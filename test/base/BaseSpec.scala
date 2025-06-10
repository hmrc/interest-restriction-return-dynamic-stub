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

package base

import actions.{AuthenticatedAction, CorrelationIdAction, EnvironmentAction}
import config.{EnvironmentValues, HeaderKeys}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.HeaderNames
import play.api.mvc.{AnyContentAsEmpty, BodyParsers}
import play.api.test.FakeRequest

import java.util.UUID

abstract class BaseSpec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite {

  given ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

  lazy val bodyParsers: BodyParsers.Default         = app.injector.instanceOf[BodyParsers.Default]
  lazy val authenticatedAction: AuthenticatedAction = new AuthenticatedAction(ec, bodyParsers)
  lazy val correlationIdAction: CorrelationIdAction = new CorrelationIdAction(ec, bodyParsers)
  lazy val environmentAction: EnvironmentAction     = new EnvironmentAction(ec, bodyParsers)

  val fakeRequestWithHeaders: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest("POST", "/")
      .withHeaders(
        HeaderNames.AUTHORIZATION -> "Bearer 1234",
        HeaderKeys.correlationId  -> UUID.randomUUID().toString,
        HeaderKeys.environment    -> EnvironmentValues.all.head
      )

}
