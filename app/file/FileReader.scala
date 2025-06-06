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

package file

import file.FileReader.{getClass, logger}
import play.api.Logging
import play.api.libs.json.{JsValue, Json}

import scala.util.Try

object FileReader extends Logging {

  private def readFileFromFileSystem(filePath: String): String = {
    val source = scala.io.Source.fromFile(filePath)
    try source.mkString
    finally source.close()
  }

  private def readFileFromResources(filePath: String): String = {
    val source = scala.io.Source.fromResource(filePath)
    try source.mkString
    finally source.close()
  }

  private def readFileFromClasspath(filePath: String): String = {
    val source = scala.io.Source.fromInputStream(getClass.getResourceAsStream(filePath))
    try source.mkString
    finally source.close()
  }

  def readFile(filePath: String): String = {
    val fs = Try(readFileFromFileSystem(filePath))
    if (fs.isSuccess) return fs.get

    val res = Try(readFileFromResources(filePath))
    if (res.isSuccess) return res.get

    val cp = Try(readFileFromClasspath(filePath))
    if (cp.isSuccess) return cp.get

    logger.error(s"Failed to read file from all sources: $filePath")
    throw new RuntimeException(s"File not found: $filePath")
  }

  def readFileAsJson(filePath: String): JsValue =
    Json.parse(readFile(filePath))

}
