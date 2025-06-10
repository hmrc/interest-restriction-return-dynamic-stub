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

import base.BaseSpec

class FileReaderSpec extends BaseSpec {

  "FileReader" when {

    "successfully read a file from resources" in {
      val result = FileReader.readFile("resources/cir/examples/example_abbreviated_body.json")
      result should not be empty
    }

    "successfully read a file from classpath" in {
      val result = FileReader.readFile("/application.conf")
      result should not be empty
    }

    "throw exception when file doesn't exist in any location" in {
      val nonExistentFile = "non-existent-file.txt"

      val exception = intercept[RuntimeException] {
        FileReader.readFile(nonExistentFile)
      }

      exception.getMessage shouldBe s"File not found: $nonExistentFile"
    }

  }
}
