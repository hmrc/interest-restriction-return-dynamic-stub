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

import mocks.{MockDataRepository, MockSchemaValidation}
import models.{DataIdModel, DataModel}
import play.api.libs.json.Json
import play.api.test.{FakeRequest, Helpers}
import play.mvc.Http.Status
import utils.TestSupport

class SetupDataControllerSpec extends TestSupport with MockSchemaValidation with MockDataRepository{

  object TestSetupDataController extends SetupDataController(mockSchemaValidation, mockDataRepository, Helpers.stubControllerComponents())

  "SetupDataController.addData" when {

    val model: DataModel = DataModel(
      _id = DataIdModel("1234", method = "GET", None),
      schemaId = "2345",
      response = Some(Json.parse("{}")),
      status = Status.OK)

    lazy val request = FakeRequest().withBody(Json.toJson(model)).withHeaders(("Content-Type", "application/json"))

    "validateUrlMatch returns 'true'" should {

      "validateUrlMatch returns 'true'" should {

        "when validateResponseJson returns 'true'" should {

          "return Status OK (200) if data successfully added to stub" in {
            mockValidateUrlMatch(model.schemaId, model._id.url)(success = true)
            mockValidateResponseJson(model.schemaId, Some(Json.parse("{}")))(success = true)
            mockAddEntry(model)(successWriteResult)
            status(TestSetupDataController.addData(request)) shouldBe Status.OK
          }

          "return Status InternalServerError (500) if unable to add data to the stub" in {
            mockValidateUrlMatch(model.schemaId, model._id.url)(success = true)
            mockValidateResponseJson(model.schemaId, Some(Json.parse("{}")))(success = true)
            mockAddEntry(model)(errorWriteResult)
            status(TestSetupDataController.addData(request)) shouldBe Status.INTERNAL_SERVER_ERROR
          }
        }

        "return Status BadRequest (400) when validateResponseJson returns 'false'" in {
          mockValidateUrlMatch(model.schemaId, model._id.url)(success = true)
          mockValidateResponseJson(model.schemaId, Some(Json.parse("""{}""")))(success = false)
          status(TestSetupDataController.addData(request)) shouldBe Status.BAD_REQUEST
        }
      }

      "validateUrlMatch returns 'false'" should {

        "return Status BadRequest (400)" in {
          mockValidateUrlMatch(model.schemaId, model._id.url)(success = false)
          status(TestSetupDataController.addData(request)) shouldBe Status.BAD_REQUEST
        }
      }

      "not a GET request" should {

        val model: DataModel = DataModel(
          _id = DataIdModel("1234", method = "BLOB", None),
          schemaId = "2345",
          response = Some(Json.parse("{}")),
          status = Status.OK)

        lazy val request = FakeRequest().withBody(Json.toJson(model)).withHeaders(("Content-Type", "application/json"))

        "return Status BadRequest (400)" in {
          status(TestSetupDataController.addData(request)) shouldBe Status.BAD_REQUEST
        }
      }
    }

    "SetupDataController.removeData" should {

      "return Status OK (200) on successful removal of data from the stub" in {
        mockRemoveById("someUrl")(successWriteResult)
        status(TestSetupDataController.removeData("someUrl")(FakeRequest())) shouldBe Status.OK
      }

      "return Status InternalServerError (500) on unsuccessful removal of data from the stub" in {
        mockRemoveById("someUrl")(errorWriteResult)
        status(TestSetupDataController.removeData("someUrl")(FakeRequest())) shouldBe Status.INTERNAL_SERVER_ERROR
      }

    }

    "SetupDataController.removeAllData" should {

      "return Status OK (200) on successful removal of all stubbed data" in {
        mockRemoveAll()(successWriteResult)
        status(TestSetupDataController.removeAllData()(FakeRequest())) shouldBe Status.OK
      }

      "return Status InternalServerError (500) on successful removal of all stubbed data" in {
        mockRemoveAll()(errorWriteResult)
        status(TestSetupDataController.removeAllData()(FakeRequest())) shouldBe Status.INTERNAL_SERVER_ERROR
      }
    }
  }
}
