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

import com.github.fge.jsonschema.main.JsonSchema
import mocks.MockSchemaRepository
import models.SchemaModel
import play.api.http.Status
import play.api.libs.json.Json
import play.api.mvc.Results.Ok

import scala.concurrent.Future

class SchemaValidationSpec extends TestSupport with MockSchemaRepository {

  val testSchema = SchemaModel("testSchema","/test","GET", responseSchema = schema)

  def setupMocks(schemaModel: Option[SchemaModel]): SchemaValidation = {
    mockFindById(testSchema._id)(schemaModel)
    new SchemaValidation(mockSchemaRepository)
  }

  val schema = Json.obj(
    "title" -> "Person",
    "type" -> "object",
    "properties" -> Json.obj(
      "firstName" -> Json.obj(
        "type" -> "string"
      ),
      "lastName" -> Json.obj(
        "type" -> "string"
      )
    ),
    "required" -> Json.arr("firstName", "lastName")
  )

  "Calling .loadResponseSchema" should {

    "with a matching schema in mongo" should {
      lazy val validation = setupMocks(Some(SchemaModel("testSchema","/test","GET", responseSchema = schema)))

      "return a json schema" in {
        lazy val result = validation.loadResponseSchema("testSchema")
        await(result).isInstanceOf[JsonSchema]
      }
    }

    "without a matching schema in mongo" should {

      "throw an exception" in {
        val validation = setupMocks(None)

        val ex = intercept[Exception] {
          await(validation.loadResponseSchema("testSchema"))
        }
        ex.getMessage shouldEqual "Schema could not be retrieved/found in MongoDB"
      }
    }
  }

  "Calling .validateResponseJson" should {

    "with a valid json body" should {

      "return true" in {
        val validation = setupMocks(Some(SchemaModel("testSchema","/test","GET", responseSchema = schema)))
        val json = Json.obj( "firstName" -> "Bob", "lastName" -> "Bobson")
        val result = validation.validateResponseJson("testSchema", Some(json))(Future.successful(Ok))
        status(result) shouldBe Status.OK
      }
    }

    "with an invalid json body" should {

      lazy val validation = setupMocks(Some(SchemaModel("testSchema","/test","GET", responseSchema = schema)))
      val json = Json.obj( "firstName" -> "Bob")

      lazy val result = validation.validateResponseJson("testSchema", Some(json))(Future.successful(Ok))

      "return false" in {
        status(result) shouldBe Status.BAD_REQUEST
      }
    }
  }

  "Calling .loadUrlRegex" should {
    lazy val validation = setupMocks(Some(SchemaModel("testSchema","/test","GET", responseSchema = schema)))

    "return the url of the SchemaModel" in {
      lazy val result = validation.loadUrlRegex("testSchema")
      await(result) shouldEqual "/test"
    }
  }

  "Calling .validateUrlMatch" should {
    lazy val validation = setupMocks(Some(SchemaModel("testSchema","/test","GET", responseSchema = schema)))
    "return 'true' if the urls match" in {
      lazy val result = validation.validateUrlMatch("testSchema", "/test")(Future.successful(Ok))
      status(result) shouldBe Status.OK
    }

  }



  val postSchema = Json.obj(
    "title" -> "Person",
    "type" -> "object",
    "properties" -> Json.obj(
      "firstName" -> Json.obj(
        "type" -> "string"
      ),
      "lastName" -> Json.obj(
        "type" -> "string"
      )
    ),
    "required" -> Json.arr("firstName", "lastName")
  )

  "Calling .loadRequestSchema" should {
    "with a matching schema in mongo" should {
      lazy val validation = setupMocks(Some(SchemaModel("testSchema","/test","GET", responseSchema = schema, requestSchema = Some(postSchema))))

      "return a json schema" in {
        lazy val result = validation.loadRequestSchema(postSchema)
        await(result).isInstanceOf[JsonSchema]
      }
    }
  }

  "Calling .validateRequestJson" should {

    "with a valid json body" should {

      "return true" in {
        val validation = setupMocks(Some(SchemaModel("testSchema","/test","GET", responseSchema = schema, requestSchema = Some(postSchema))))
        val json = Json.obj( "firstName" -> "Bob", "lastName" -> "Bobson")
        val result = validation.validateRequestJson("testSchema", Some(json))(Future.successful(Ok))
        status(result) shouldBe Status.OK
      }
    }

    "with an invalid json body" should {

      lazy val validation = setupMocks(Some(SchemaModel("testSchema","/test","GET", responseSchema = schema, requestSchema = Some(postSchema))))
      val json = Json.obj( "firstName" -> "Bob")

      lazy val result = validation.validateRequestJson("testSchema", Some(json))(Future.successful(Ok))

      "return false" in {
        status(result) shouldBe Status.BAD_REQUEST
      }
    }
  }
}
