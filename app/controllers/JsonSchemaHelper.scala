/*
 * Copyright 2025 HM Revenue & Customs
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
import com.networknt.schema.*
import file.FileReader
import models.{ErrorResponse, FailureMessage}
import play.api.Logging
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Result
import play.api.mvc.Results.{BadRequest, InternalServerError}

import scala.collection.convert.AsScalaConverters
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

object JsonSchemaHelper extends Logging with AsScalaConverters {

  private final lazy val jsonMapper  = new ObjectMapper()
  private final lazy val jsonFactory = jsonMapper.getFactory

  private def loadRequestSchema(requestSchema: JsValue): JsonSchema = {
    val schemaMapper: ObjectMapper           = new ObjectMapper()
    val factory: JsonFactory                 = schemaMapper.getFactory
    val schemaParser: JsonParser             = factory.createParser(requestSchema.toString)
    val schemaJson: JsonNode                 = schemaMapper.readTree(schemaParser)
    val jsonSchemaFactory: JsonSchemaFactory = JsonSchemaFactory.getInstance(SpecVersionDetector.detect(schemaJson))
    jsonSchemaFactory.getSchema(schemaJson)
  }

  private def validRequest(jsonSchema: JsValue, json: Option[JsValue]): Option[Set[ValidationMessage]] =
    json.map { response =>
      val jsonParser: JsonParser         = jsonFactory.createParser(response.toString)
      val jsonNode: JsonNode             = jsonMapper.readTree(jsonParser)
      val schema: JsonSchema             = loadRequestSchema(jsonSchema)
      val result: Set[ValidationMessage] = asScala[ValidationMessage](schema.validate(jsonNode)).toSet

      result
    }

  private def applySchemaValidation(schemaPath: String, jsonBody: Option[JsValue])(
    f: => Future[Result]
  ): Future[Result] =
    retrieveJsonSchema(schemaPath) match {
      case Success(schema) =>
        val validationResult: Option[Set[ValidationMessage]] = JsonSchemaHelper.validRequest(schema, jsonBody)
        validationResult match {
          case Some(res) if res.isEmpty => f
          case Some(res)                =>
            logger.error(s"[JsonSchemaHelper][applySchemaValidation] [${res.toString}]")
            Future.successful(BadRequest(Json.toJson(ErrorResponse(List(FailureMessage.InvalidJson)))))
          case _                        => Future.successful(BadRequest(Json.toJson(ErrorResponse(List(FailureMessage.MissingBody)))))
        }
      case Failure(e)      =>
        logger.error(s"[JsonSchemaHelper][applySchemaValidation] Error: ${e.getMessage}", e)
        Future.successful(InternalServerError(""))
    }

  private def retrieveJsonSchema(schemaPath: String): Try[JsValue] = {
    logger.info(s"[JsonSchemaHelper][retrieveJsonSchema] Schema path: $schemaPath")

    val jsonSchema: Try[String] = Try(FileReader.readFile(schemaPath))
    jsonSchema.map(Json.parse)
  }

  def applySchemaValidation(schemaDir: String, schemaFilename: String, jsonBody: Option[JsValue])(
    f: => Future[Result]
  ): Future[Result] = applySchemaValidation(s"$schemaDir/$schemaFilename", jsonBody)(f)

}
