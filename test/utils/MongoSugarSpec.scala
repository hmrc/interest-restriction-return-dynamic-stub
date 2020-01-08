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

import org.mockito.Matchers
import org.mockito.Mockito.{reset, when}
import org.mockito.stubbing.OngoingStubbing
import play.api.http.Status
import play.api.libs.json.Json.JsValueWrapper
import play.api.libs.json.{Format, Json}
import play.api.mvc.Results._
import reactivemongo.api.commands.{DefaultWriteResult, WriteError, WriteResult}
import uk.gov.hmrc.mongo.ReactiveRepository

import scala.concurrent.Future

class MongoSugarSpec extends TestSupport {

  val successWriteResult = DefaultWriteResult(ok = true, n = 1, writeErrors = Seq(), None, None, None)
  val errorWriteResult = DefaultWriteResult(ok = false, n = 1, writeErrors = Seq(WriteError(1,1,"Error")), None, None, None)
  val testId = "foo"

  case class TestModel(foo: String)
  object TestModel {
    implicit val fmt: Format[TestModel] = Json.format[TestModel]
  }

  lazy val mockRepo: ReactiveRepository[TestModel, String] = mock[ReactiveRepository[TestModel, String]]

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockRepo)
  }

  def mockFindById(id: String)(response: Future[Option[TestModel]] = Future.successful(None)): OngoingStubbing[Future[Option[TestModel]]] = {
    when(mockRepo.findById(Matchers.eq(id), Matchers.any())(Matchers.any()))
      .thenReturn(response)
  }

  def mockAdd(model: TestModel)(response: Future[WriteResult]): OngoingStubbing[Future[WriteResult]] =
    when(mockRepo.insert(Matchers.eq(model))(Matchers.any()))
      .thenReturn(Future.successful(response))

  def mockRemove(query: (String, JsValueWrapper))(response: Future[WriteResult]): OngoingStubbing[Future[WriteResult]] = {
    when(mockRepo.remove(Matchers.any())(Matchers.any()))
      .thenReturn(Future.successful(response))
  }

  def mockRemoveAll(response: Future[WriteResult]): OngoingStubbing[Future[WriteResult]] =
    when(mockRepo.removeAll(Matchers.any())(Matchers.any()))
      .thenReturn(Future.successful(response))

  object TestMongoSugar extends MongoSugar

  "The .findById() method" should {

    "when finding a document successfully" should {

      lazy val model = TestModel("bar")
      lazy val result = TestMongoSugar.findById(mockRepo)(testId) {
        model => Future.successful(Ok(Json.toJson(model)))
      }

      "return OK" in {
        mockFindById(testId)(Some(model))
        status(result) shouldBe Status.OK
      }

      "body of result should be model" in {
        jsonBodyOf(await(result)) shouldBe Json.toJson(model)
      }
    }

    "when NOT finding a document" should {

      lazy val result = TestMongoSugar.findById(mockRepo)(testId) {
        model => Future.successful(Ok(Json.toJson(model)))
      }

      "return OK" in {
        mockFindById(testId)()
        status(result) shouldBe Status.NOT_FOUND
      }

      "body of result should be not found error" in {
        bodyOf(await(result)) shouldBe "Could not find TestModel with _ID: foo"
      }
    }

    "when an unexpected error occurs" should {

      lazy val result = TestMongoSugar.findById(mockRepo)(testId) {
        model => Future.successful(Ok(Json.toJson(model)))
      }

      "return OK" in {
        mockFindById(testId)(Future.failed(new Exception("Err")))
        status(result) shouldBe Status.SERVICE_UNAVAILABLE
      }

      "body of result should be not found error" in {
        bodyOf(await(result)) shouldBe "An unexpected error occurred when communicating with the MongoDB repository"
      }
    }
  }

  "The .insert() method" should {

    lazy val model = TestModel("bar")

    "when successfully storing the document" should {

      lazy val result = TestMongoSugar.insert(mockRepo)(model)

      "return OK if the mongo response is successful" in {
        mockAdd(model)(successWriteResult)
        status(result) shouldBe Status.OK
      }
    }

    "when an unexpected error occurs" should {

      lazy val result = TestMongoSugar.insert(mockRepo)(model)

      "return OK" in {
        mockAdd(model)(Future.failed(new Exception("Err")))
        status(result) shouldBe Status.SERVICE_UNAVAILABLE
      }

      "body of result should be not found error" in {
        bodyOf(await(result)) shouldBe "An unexpected error occurred when communicating with the MongoDB repository"
      }
    }
  }

  "The .remove() method" should {

    lazy val query: (String, JsValueWrapper) = "String" -> Json.obj("_id" -> "foo")

    "when successfully storing the document" should {

      lazy val result = TestMongoSugar.remove(mockRepo)(query)

      "return OK if the mongo response is successful" in {
        mockRemove(query)(successWriteResult)
        status(result) shouldBe Status.OK
      }
    }

    "when an unexpected error occurs" should {

      lazy val result = TestMongoSugar.remove(mockRepo)(query)

      "return OK" in {
        mockRemove(query)(Future.failed(new Exception("Err")))
        status(result) shouldBe Status.SERVICE_UNAVAILABLE
      }

      "body of result should be not found error" in {
        bodyOf(await(result)) shouldBe "An unexpected error occurred when communicating with the MongoDB repository"
      }
    }
  }

  "The .removeAll() method" should {

    "When successfully removing all documents" should {

      lazy val result = TestMongoSugar.removeAll(mockRepo)

      "return OK if the mongo response is successful" in {
        mockRemoveAll(successWriteResult)
        status(result) shouldBe Status.OK
      }
    }

    "when an unexpected error occurs" should {

      lazy val result = TestMongoSugar.removeAll(mockRepo)

      "return OK" in {
        mockRemoveAll(Future.failed(new Exception("Err")))
        status(result) shouldBe Status.SERVICE_UNAVAILABLE
      }

      "body of result should be not found error" in {
        bodyOf(await(result)) shouldBe "An unexpected error occurred when communicating with the MongoDB repository"
      }
    }
  }
}
