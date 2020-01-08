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

package mocks

import org.scalamock.handlers.CallHandler3
import org.scalamock.scalatest.MockFactory
import play.api.libs.json.JsValue
import play.api.mvc.Result
import play.api.mvc.Results.BadRequest
import uk.gov.hmrc.play.test.UnitSpec
import utils.SchemaValidation

import scala.concurrent.Future

trait MockSchemaValidation extends UnitSpec with MockFactory {

  val mockSchemaValidation: SchemaValidation = mock[SchemaValidation]

  def mockValidateResponseJson(schemaId: String, json: Option[JsValue])(success: Boolean)
  : CallHandler3[String, Option[JsValue], Future[Result], Future[Result]] = {
    def mock = (mockSchemaValidation.validateResponseJson(_: String, _: Option[JsValue])(_: Future[Result]))
      .expects(schemaId, json, *)

    if (success) mock.onCall(_.productElement(2).asInstanceOf[() => Future[Result]]())
    else mock.returns(Future.successful(BadRequest))
  }

  def mockValidateUrlMatch(schemaId:String, url: String)(success: Boolean)
  : CallHandler3[String, String, Future[Result], Future[Result]] = {
    def mock = (mockSchemaValidation.validateUrlMatch(_: String, _: String)(_: Future[Result]))
      .expects(schemaId, url, *)

    if (success) mock.onCall(_.productElement(2).asInstanceOf[() => Future[Result]]())
    else mock.returns(Future.successful(BadRequest))
  }

  def mockValidateRequestJson(schemaId: String, json: Option[JsValue])(success: Boolean)
  : CallHandler3[String, Option[JsValue], Future[Result], Future[Result]] = {
    def mock = (mockSchemaValidation.validateRequestJson(_: String, _: Option[JsValue])(_: Future[Result]))
      .expects(schemaId, json, *)

    if (success) mock.onCall(_.productElement(2).asInstanceOf[() => Future[Result]]())
    else mock.returns(Future.successful(BadRequest))
  }

}
