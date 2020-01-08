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

package controllers.setup

import mocks.MockSchemaRepository
import models.SchemaModel
import play.api.http.Status
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.test.{FakeRequest, Helpers}
import utils.TestSupport

import scala.concurrent.Future

class SetupSchemaControllerSpec extends TestSupport with MockSchemaRepository {

  object TestSetupSchemaController extends SetupSchemaController(mockSchemaRepository, Helpers.stubControllerComponents())

  "The SetupSchemaController" when {

    "a request to add a valid schema is successful" should {

      lazy val successModel = SchemaModel(
        _id = "test",
        url = "/test",
        method = "GET",
        responseSchema = Json.parse("{}")
      )
      lazy val request = FakeRequest().withBody(Json.toJson(successModel)).withHeaders(("Content-Type","application/json"))
      lazy val result: Future[Result] = TestSetupSchemaController.addSchema(request)

      "Return a status 200 (OK)" in {
        setupMockAddSchema(successModel)(successWriteResult)
        status(result) shouldBe Status.OK
      }

      s"Return the correct Result Body" in {
        setupMockAddSchema(successModel)(successWriteResult)
        await(bodyOf(result)) shouldBe s"The following SchemaModel document JSON was added to the stub:\n\n${Json.toJson(successModel)}"
      }
    }

    "a request to add a valid schema is unsuccessful" should {

      lazy val successModel = SchemaModel(
        _id = "test",
        url = "/test",
        method = "GET",
        responseSchema = Json.parse("{}")
      )

      lazy val errorModel = SchemaModel(
        _id = "test",
        url = "/test",
        method = "GET",
        responseSchema = Json.parse("{}")
      )

      lazy val request = FakeRequest().withBody(Json.toJson(successModel)).withHeaders(("Content-Type","application/json"))
      lazy val result = TestSetupSchemaController.addSchema(request)

      "Return a status 500 (ISE)" in {
        setupMockAddSchema(successModel)(errorWriteResult)
        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
      }

      s"Result Body 'An error was returned from the MongoDB repository'" in {
        setupMockAddSchema(successModel)(errorWriteResult)
        await(bodyOf(result)) shouldBe "An error was returned from the MongoDB repository"
      }
    }

    "removing a schema is successful" should {
      "Return a status 200 (OK)" in {
        lazy val request = FakeRequest()
        lazy val result = TestSetupSchemaController.removeSchema("someId")(request)

        setupMockRemoveSchema("someId")(successWriteResult)
        status(result) shouldBe Status.OK
      }
    }

    "removing a schema is unsuccessful" should {
      "Return a status 500 (ISE)" in {
        lazy val request = FakeRequest()
        lazy val result = TestSetupSchemaController.removeSchema("someId")(request)

        setupMockRemoveSchema("someId")(errorWriteResult)
        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
      }
    }

    "removing all schemas is successful" should {
      "Return a status 200 (OK)" in {
        lazy val request = FakeRequest()
        lazy val result = TestSetupSchemaController.removeAllSchemas()(request)

        setupMockRemoveAllSchemas(successWriteResult)
        status(result) shouldBe Status.OK
      }
    }

    "removing all schemas is unsuccessful" should {
      "Return a status 500 (ISE)" in {
        lazy val request = FakeRequest()
        lazy val result = TestSetupSchemaController.removeAllSchemas()(request)

        setupMockRemoveAllSchemas(errorWriteResult)
        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
      }
    }
  }
}
