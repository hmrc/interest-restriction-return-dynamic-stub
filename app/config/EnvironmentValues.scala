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

package config

object EnvironmentValues {
  private val environmentIst0  = "ist0"
  private val environmentClone = "clone"
  private val environmentLive  = "live"
  val environmentDev           = "dev"

  val all: Set[String] = Set(environmentIst0, environmentClone, environmentLive, environmentDev)
}
