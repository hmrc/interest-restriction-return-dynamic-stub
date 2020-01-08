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

import models.{DataIdModel, DataModel}
import org.mockito.Matchers
import org.mockito.Mockito.{reset, when}
import org.mockito.stubbing.OngoingStubbing
import org.scalatest.mockito.MockitoSugar
import play.api.Logger
import play.api.libs.json.Json
import play.api.libs.json.Json.JsValueWrapper
import reactivemongo.api.commands.{DefaultWriteResult, WriteError, WriteResult}
import repositories.DataRepository
import utils.TestSupport

import scala.concurrent.Future

trait MockDataRepository extends TestSupport with MockitoSugar {

  val successWriteResult = DefaultWriteResult(ok = true, n = 1, writeErrors = Seq(), None, None, None)
  val errorWriteResult = DefaultWriteResult(ok = false, n = 1, writeErrors = Seq(WriteError(1,1,"Error")), None, None, None)

  lazy val mockDataRepository: DataRepository = mock[DataRepository]

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockDataRepository)
  }

  def mockAddEntry(document: DataModel)(response: WriteResult): OngoingStubbing[Future[WriteResult]] = {
    when(mockDataRepository.insert(Matchers.any())(Matchers.any())).thenReturn(Future.successful(response))
  }

  def mockRemoveById(url: String)(response: WriteResult): OngoingStubbing[Future[WriteResult]] = {
    val query: (String, JsValueWrapper) = "_id" -> Json.obj("url" -> url)
    when(mockDataRepository.remove(Matchers.any())(Matchers.any()))
      .thenReturn(Future.successful(response))
  }

  def mockRemoveAll()(response: WriteResult): OngoingStubbing[Future[WriteResult]] = {
    when(mockDataRepository.removeAll(Matchers.any())(Matchers.any())).thenReturn(Future.successful(response))
  }

  def mockFindById(id: DataIdModel)(response: Option[DataModel] = None): OngoingStubbing[Future[Option[DataModel]]] = {
    when(mockDataRepository.findById(Matchers.any(), Matchers.any())(Matchers.any()))
      .thenReturn(Future.successful(response))
  }

}
