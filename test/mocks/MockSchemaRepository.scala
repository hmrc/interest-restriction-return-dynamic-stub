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

import models.SchemaModel
import org.mockito.Matchers
import org.mockito.Mockito._
import org.mockito.stubbing.OngoingStubbing
import play.api.libs.json.Json
import play.api.libs.json.Json.JsValueWrapper
import reactivemongo.api.commands.{DefaultWriteResult, WriteError, WriteResult}
import repositories.SchemaRepository
import utils.TestSupport

import scala.concurrent.Future

trait MockSchemaRepository extends TestSupport {

  val successWriteResult = DefaultWriteResult(ok = true, n = 1, writeErrors = Seq(), None, None, None)
  val errorWriteResult = DefaultWriteResult(ok = false, n = 1, writeErrors = Seq(WriteError(1,1,"Error")), None, None, None)

  lazy val mockSchemaRepository: SchemaRepository = mock[SchemaRepository]

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockSchemaRepository)
  }

  def mockFindById(id: String)(response: Option[SchemaModel] = None): OngoingStubbing[Future[Option[SchemaModel]]] = {
    when(mockSchemaRepository.findById(Matchers.eq(id), Matchers.any())(Matchers.any()))
      .thenReturn(response)
  }

  def setupMockAddSchema(model: SchemaModel)(response: WriteResult): OngoingStubbing[Future[WriteResult]] =
    when(mockSchemaRepository.insert(Matchers.eq(model))(Matchers.any()))
      .thenReturn(Future.successful(response))

  def setupMockRemoveSchema(id: String)(response: WriteResult): OngoingStubbing[Future[WriteResult]] = {
    val query: (String, JsValueWrapper) = "_id" -> Json.obj("url" -> id)
    when(mockSchemaRepository.remove(Matchers.any())(Matchers.any()))
      .thenReturn(Future.successful(response))
  }

  def setupMockRemoveAllSchemas(response: WriteResult): OngoingStubbing[Future[WriteResult]] =
    when(mockSchemaRepository.removeAll(Matchers.any())(Matchers.any()))
      .thenReturn(Future.successful(response))

}
