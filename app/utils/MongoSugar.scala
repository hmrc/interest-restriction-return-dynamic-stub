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

import play.api.Logger
import play.api.libs.json.{Format, Json, Writes}
import play.api.mvc.Result
import play.api.mvc.Results._
import reactivemongo.api.commands.WriteResult
import uk.gov.hmrc.mongo.ReactiveRepository

import scala.concurrent.{ExecutionContext, Future}

trait MongoSugar {


  def findById[T,I](repo: => ReactiveRepository[T,I])(id: I)(f: => T => Future[Result])
  (implicit ec: ExecutionContext, m: Manifest[T], writes: Writes[T]): Future[Result] = repo.findById(id).flatMap {
    case Some(value) =>
      Logger.debug(s"[MongoSugar][findById] Found ${m.runtimeClass.getSimpleName}: \n\n${Json.toJson(value)}\n\n")
      f(value)
    case _ =>
      Logger.error(s"[MongoSugar][findById] Could not find ${m.runtimeClass.getSimpleName} with _ID: $id")
      Future.successful(NotFound(s"Could not find ${m.runtimeClass.getSimpleName} with _ID: $id"))
  }.recover(handleMongoErr)

  def remove[T,I](repo: => ReactiveRepository[T,I])(query: (String, Json.JsValueWrapper))
                 (implicit ec: ExecutionContext, m: Manifest[T]): Future[Result] =
    repo.remove(query).flatMap(handleWriteResult(
      Future.successful(Ok(s"Deleted ${m.runtimeClass.getSimpleName} document matching query $query successfully")))
    ).recover(handleMongoErr)

  def removeAll[T,I](repo: => ReactiveRepository[T,I])(implicit ec: ExecutionContext, m: Manifest[T]): Future[Result] =
    repo.removeAll().flatMap(handleWriteResult(
      Future.successful(Ok(s"All ${m.runtimeClass.getSimpleName} documents removed successfully")))
    ).recover(handleMongoErr)

  def insert[T,I](repo: => ReactiveRepository[T,I])(data: T)
               (implicit ec: ExecutionContext, m: Manifest[T], fmt: Format[T]): Future[Result] = {
    Logger.debug(s"[MongoSugar][insert] ${m.runtimeClass.getSimpleName}: $data")
    repo.insert(data).flatMap(handleWriteResult(
      Future.successful(Ok(s"The following ${m.runtimeClass.getSimpleName} document JSON was added to the stub:\n\n${Json.toJson(data)}")))
    ).recover(handleMongoErr)
  }

  private def handleWriteResult(f: => Future[Result]): WriteResult => Future[Result] = {
    case result if result.ok => f
    case err =>
      Logger.error(s"[MongoSugar][handleWriteResult] Mongo Errors: ${err.writeErrors.map(_.errmsg)}")
      Future.successful(InternalServerError("An error was returned from the MongoDB repository"))
  }


  private val handleMongoErr: PartialFunction[Throwable, Result] = {
    case e =>
      Logger.error(s"[MongoSugar][handleMongoErr] Mongo Errors: ${e.getMessage}")
      ServiceUnavailable("An unexpected error occurred when communicating with the MongoDB repository")
  }
}
