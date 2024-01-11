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

import com.github.fge.jsonschema.core.report.ProcessingReport
import com.github.fge.jsonschema.main.{JsonSchema, JsonSchemaFactory}
import play.api.libs.json.{JsValue, Json}
import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import com.fasterxml.jackson.core.JsonParser
import scala.concurrent.Future
import play.api.Logging
import play.api.mvc._
import play.api.mvc.Results._
import scala.util.{Failure, Success, Try}
import scala.io.Source
import models.{ErrorResponse, FailureMessage}
import config._
import scala.concurrent._

object JsonSchemaHelper extends Logging {

  private final lazy val jsonMapper  = new ObjectMapper()
  private final lazy val jsonFactory = jsonMapper.getFactory

  private final lazy val correlationIdRegex =
    "^[0-9a-fA-F]{8}[-][0-9a-fA-F]{4}[-][0-9a-fA-F]{4}[-][0-9a-fA-F]{4}[-][0-9a-fA-F]{12}$"

  private def loadRequestSchema(requestSchema: JsValue): JsonSchema = {
    val schemaMapper             = new ObjectMapper()
    val factory                  = schemaMapper.getFactory
    val schemaParser: JsonParser = factory.createParser(requestSchema.toString)
    val schemaJson: JsonNode     = schemaMapper.readTree(schemaParser)
    JsonSchemaFactory.byDefault().getJsonSchema(schemaJson)
  }

  private def validRequest(jsonSchema: JsValue, json: Option[JsValue]): Option[ProcessingReport] =
    json.map { response =>
      val jsonParser               = jsonFactory.createParser(response.toString)
      val jsonNode: JsonNode       = jsonMapper.readTree(jsonParser)
      val result: ProcessingReport = loadRequestSchema(jsonSchema).validate(jsonNode)
      result
    }

  def applySchemaValidation(schemaPath: String, jsonBody: Option[JsValue])(f: => Future[Result]): Future[Result] =
    retrieveJsonSchema(schemaPath) match {
      case Success(schema) =>
        val validationResult = JsonSchemaHelper.validRequest(schema, jsonBody)
        validationResult match {
          case Some(res) if res.isSuccess => f
          case Some(res)                  =>
            logger.info(s"[INTEREST-RESTRICTION-RETURN-DYNAMIC-STUB][VALIDATION-ERROR][${res.toString}]")
            Future.successful(BadRequest(Json.toJson(ErrorResponse(List(FailureMessage.InvalidJson)))))
          case _                          => Future.successful(BadRequest(Json.toJson(ErrorResponse(List(FailureMessage.MissingBody)))))
        }
      case Failure(e)      =>
        logger.error(s"Error: ${e.getMessage}", e)
        Future.successful(InternalServerError(""))
    }

  private def retrieveJsonSchema(schemaPath: String): Try[JsValue] = {
    val jsonSchema = Try(Source.fromInputStream(getClass.getResourceAsStream(schemaPath)).mkString)
    jsonSchema.map(Json.parse)
  }

  def applySchemaHeaderValidation(
    headers: Headers
  )(f: => Future[Result])(implicit ec: ExecutionContext): Future[Result] = {
    val maybeCorrelationId: Option[String] = headers.get(HeaderKeys.correlationId)
    val maybeEnvironment: Option[String]   = headers.get(HeaderKeys.environment)

    val correlationIdResult = maybeCorrelationId match {
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
