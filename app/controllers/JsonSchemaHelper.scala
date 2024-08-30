/*
 * Copyright 2024 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers

import com.fasterxml.jackson.core.{JsonFactory, JsonParser}
import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import com.networknt.schema._
import config.{EnvironmentValues, HeaderKeys}
import models.{ErrorResponse, FailureMessage}
import play.api.Logging
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Headers, Result}
import play.api.mvc.Results.{BadRequest, InternalServerError}

import java.util
import scala.concurrent.{ExecutionContext, Future}
import scala.io.Source
import scala.util.{Failure, Success, Try}

object JsonSchemaHelper extends Logging {

  private final lazy val jsonMapper  = new ObjectMapper()
  private final lazy val jsonFactory = jsonMapper.getFactory

  private final lazy val correlationIdRegex =
    "^[0-9a-fA-F]{8}[-][0-9a-fA-F]{4}[-][0-9a-fA-F]{4}[-][0-9a-fA-F]{4}[-][0-9a-fA-F]{12}$"

  private def loadRequestSchema(requestSchema: JsValue): JsonSchema = {
    val schemaMapper: ObjectMapper           = new ObjectMapper()
    val factory: JsonFactory                 = schemaMapper.getFactory
    val schemaParser: JsonParser             = factory.createParser(requestSchema.toString)
    val schemaJson: JsonNode                 = schemaMapper.readTree(schemaParser)
    val jsonSchemaFactory: JsonSchemaFactory = JsonSchemaFactory.getInstance(SpecVersionDetector.detect(schemaJson))
    jsonSchemaFactory.getSchema(schemaJson)
  }

  private def validRequest(jsonSchema: JsValue, json: Option[JsValue]): Option[util.Set[ValidationMessage]] =
    json.map { response =>
      val jsonParser: JsonParser              = jsonFactory.createParser(response.toString)
      val jsonNode: JsonNode                  = jsonMapper.readTree(jsonParser)
      val result: util.Set[ValidationMessage] = loadRequestSchema(jsonSchema).validate(jsonNode)

      result
    }

  def applySchemaValidation(schemaPath: String, jsonBody: Option[JsValue])(f: => Future[Result]): Future[Result] =
    retrieveJsonSchema(schemaPath) match {
      case Success(schema) =>
        val validationResult: Option[util.Set[ValidationMessage]] = JsonSchemaHelper.validRequest(schema, jsonBody)
        validationResult match {
          case Some(res) if res.isEmpty => f
          case Some(res)                =>
            logger.info(s"[INTEREST-RESTRICTION-RETURN-DYNAMIC-STUB][VALIDATION-ERROR][${res.toString}]")
            Future.successful(BadRequest(Json.toJson(ErrorResponse(List(FailureMessage.InvalidJson)))))
          case _                        => Future.successful(BadRequest(Json.toJson(ErrorResponse(List(FailureMessage.MissingBody)))))
        }
      case Failure(e)      =>
        logger.error(s"Error: ${e.getMessage}", e)
        Future.successful(InternalServerError(""))
    }

  private def retrieveJsonSchema(schemaPath: String): Try[JsValue] = {
    val jsonSchema: Try[String] = Try(Source.fromInputStream(getClass.getResourceAsStream(schemaPath)).mkString)
    jsonSchema.map(Json.parse)
  }

  def applySchemaHeaderValidation(
    headers: Headers
  )(f: => Future[Result])(using ec: ExecutionContext): Future[Result] = {
    val maybeCorrelationId: Option[String] = headers.get(HeaderKeys.correlationId)
    val maybeEnvironment: Option[String]   = headers.get(HeaderKeys.environment)

    val correlationIdResult: Future[Result] = maybeCorrelationId match {
      case Some(correlationId) if isValidCorrelationId(correlationId) =>
        f.map(_.withHeaders(HeaderKeys.correlationId -> correlationId))
      case Some(_)                                                    =>
        Future.successful(BadRequest(Json.toJson(ErrorResponse(List(FailureMessage.InvalidCorrelationId)))))
      case _                                                          => f
    }

    maybeEnvironment match {
      case Some(environment) if isValidEnvironment(environment) => correlationIdResult
      case Some(_)                                              =>
        Future.successful(BadRequest(Json.toJson(ErrorResponse(List(FailureMessage.InvalidEnvironment)))))
      case _                                                    => correlationIdResult
    }

  }

  private def isValidEnvironment(environment: String): Boolean =
    EnvironmentValues.all.contains(environment)

  private def isValidCorrelationId(correlationId: String): Boolean =
    correlationId.matches(correlationIdRegex)
}
