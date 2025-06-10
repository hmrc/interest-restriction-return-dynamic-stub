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

package models.cir

object ErrorMessages {

  val BAD_REQUEST: ErrorResponseHip = ErrorResponseHip(
    origin = "HIP",
    response = PlatformFailureMessages(
      Seq(
        PlatformFailureMessage(
          `type` = "BAD_REQUEST",
          reason = "The request is invalid or malformed."
        )
      )
    )
  )

  val INTERNAL_SERVER_ERROR: ErrorResponseHip = ErrorResponseHip(
    origin = "HIP",
    response = PlatformFailureMessages(
      Seq(
        PlatformFailureMessage(
          `type` = "INTERNAL_SERVER_ERROR",
          reason = "An unexpected error occurred on the server."
        )
      )
    )
  )

  val SERVER_UNAVAILABLE: ErrorResponseHip = ErrorResponseHip(
    origin = "HIP",
    response = PlatformFailureMessages(
      Seq(
        PlatformFailureMessage(
          `type` = "SERVICE_UNAVAILABLE",
          reason = "The service is currently unavailable."
        )
      )
    )
  )

}
