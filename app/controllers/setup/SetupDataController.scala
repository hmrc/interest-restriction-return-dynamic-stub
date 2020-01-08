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

import controllers.BaseController
import javax.inject.{Inject, Singleton}
import models.DataModel
import models.HttpMethod._
import play.api.libs.json.Json.JsValueWrapper
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import repositories.DataRepository
import utils.{MongoSugar, SchemaValidation}

import scala.concurrent.Future

@Singleton
class SetupDataController @Inject()(schemaValidation: SchemaValidation,
                                    dataRepository: DataRepository,
                                    override val controllerComponents: ControllerComponents) extends BaseController with MongoSugar {

  val addData: Action[JsValue] = Action.async(parse.json) { implicit request =>
    withJsonBody[DataModel] { json =>
      json._id.method.toUpperCase match {
        case GET | POST | PUT =>
          schemaValidation.validateUrlMatch(json.schemaId, json._id.url) {
            schemaValidation.validateResponseJson(json.schemaId, json.response) {
              insert(dataRepository)(json)
            }
          }
        case x => Future.successful(BadRequest(s"The method: $x is currently unsupported"))
      }
    }
  }

  val removeData: String => Action[AnyContent] = url => Action.async { implicit request =>
    val query: (String, JsValueWrapper) = "_id" -> Json.obj("url" -> url)
    remove(dataRepository)(query)
  }

  val removeAllData: Action[AnyContent] = Action.async { implicit request =>
    removeAll(dataRepository)
  }
}
