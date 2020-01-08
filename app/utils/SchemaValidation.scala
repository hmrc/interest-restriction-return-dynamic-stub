/*
 * Copyright 2020 HM Revenue & Customs
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

package utils

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import com.github.fge.jsonschema.core.report.ProcessingReport
import com.github.fge.jsonschema.main.{JsonSchema, JsonSchemaFactory}
import javax.inject.{Inject, Singleton}
import models.SchemaModel
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Result
import play.api.mvc.Results.BadRequest
import repositories.SchemaRepository

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class SchemaValidation @Inject()(repository: SchemaRepository) extends MongoSugar {

  private[utils] final lazy val jsonMapper = new ObjectMapper()
  private[utils] final lazy val jsonFactory = jsonMapper.getFactory

  def validateResponseJson(schemaId: String, json: Option[JsValue])(f: => Future[Result]): Future[Result] = {
    validResponse(schemaId, json).flatMap {
      case true => f
      case _ => Future.successful(BadRequest(s"The Json Body did not validate against the Schema Definition"))
    }
  }

  def validateUrlMatch(schemaId: String, url: String)(f: => Future[Result]): Future[Result] =
    validateUrl(schemaId, url).flatMap {
      case Left(err) => Future.successful(err)
      case Right(_) => f
    }


  def validateRequestJson(schemaId: String, json: Option[JsValue])(f: => Future[Result]): Future[Result] = {
    findById(repository)(schemaId) { schema =>
      if (validRequest(schema, json)) f
      else Future.successful(BadRequest(Json.obj("code" -> "400", "reason" -> "Request did not validate against schema")))
    }
  }

  private[utils] def validResponse(schemaId: String, json: Option[JsValue]): Future[Boolean] = {
    json.fold(Future.successful(true)) { response =>
      loadResponseSchema(schemaId).map { schema =>
        val jsonParser = jsonFactory.createParser(response.toString)
        val jsonNode: JsonNode = jsonMapper.readTree(jsonParser)
        schema.validate(jsonNode).isSuccess
      }
    }
  }

  private[utils] def loadResponseSchema(schemaId: String): Future[JsonSchema] = {
    val schemaMapper = new ObjectMapper()
    val factory = schemaMapper.getFactory
    repository.findById(schemaId).map {
      case Some(response) => {
        val schemaParser: JsonParser = factory.createParser(response.responseSchema.toString)
        val schemaJson: JsonNode = schemaMapper.readTree(schemaParser)
        JsonSchemaFactory.byDefault().getJsonSchema(schemaJson)
      }
      case _ => throw new Exception("Schema could not be retrieved/found in MongoDB")
    }
  }

  private[utils] def loadRequestSchema(requestSchema: JsValue): JsonSchema = {
    val schemaMapper = new ObjectMapper()
    val factory = schemaMapper.getFactory
    val schemaParser: JsonParser = factory.createParser(requestSchema.toString)
    val schemaJson: JsonNode = schemaMapper.readTree(schemaParser)
    JsonSchemaFactory.byDefault().getJsonSchema(schemaJson)
  }

  private[utils] def validateUrl(schemaId: String, url: String): Future[Either[Result, Boolean]] =
    loadUrlRegex(schemaId).map { regex =>
      if (url.matches(regex)) Right(true)
      else Left(BadRequest(s"URL $url did not match the Schema Definition Regex $regex"))
    }

  private[utils] def loadUrlRegex(schemaId: String): Future[String] =
    repository.findById(schemaId).map {
      case Some(model) => model.url
      case _ => throw new Exception("Schema could not be retrieved/found in MongoDB")
    }

  private[utils] def validRequest(schema: SchemaModel, json: Option[JsValue]): Boolean = {
    schema.requestSchema.fold(true) { jsonSchema =>
      json.fold(true) { response =>
        val jsonParser = jsonFactory.createParser(response.toString)
        val jsonNode: JsonNode = jsonMapper.readTree(jsonParser)
        val result: ProcessingReport = loadRequestSchema(jsonSchema).validate(jsonNode)
        result.isSuccess
      }
    }
  }
}
