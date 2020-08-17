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

package controllers.predicates

import config.AppConfig
import config.featureSwitch.{FeatureSwitching, UseStaticCannedResponse, UseTimeBasedCannedResponse}
import javax.inject.Inject
import org.joda.time.{DateTime,DateTimeZone}
import play.api.libs.json.Json
import play.api.mvc.Results.Ok
import play.api.mvc._

import scala.concurrent.Future

class CannedResponsePredicate @Inject()(implicit appConfig: AppConfig) extends FeatureSwitching {

  def apply(f: => Future[Result]): Future[Result] = {
    if(isEnabled(UseStaticCannedResponse)) {
      Future.successful(Ok(Json.obj("acknowledgementReference" -> "ackRef1234")))
    } else if(isEnabled(UseTimeBasedCannedResponse)) {
      val now = DateTime.now(DateTimeZone.UTC).getMillis()
      Future.successful(Ok(Json.obj("acknowledgementReference" -> now.toString)))
    } else {
      f
    }
  }

}
