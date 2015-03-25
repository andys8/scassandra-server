/*
 * Copyright (C) 2014 Christopher Batey and Dogan Narinc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.scassandra.server.priming.routes

import java.util.concurrent.TimeUnit

import org.scalatest.{Matchers, FunSuite}
import org.scassandra.server.priming._
import org.scassandra.server.priming.query.{Prime, Then, When, PrimeQuerySingle}

import scala.concurrent.duration.FiniteDuration

/**
 * PrimeQueryResultExtractor was "extracted" and thus is primarily tested
 * via the PrimeQueryRoute.
 *
 * Starting to add direct tests as the PrimingQueryRoute test is getting large
 */
class PrimeQueryResultExtractorTest extends FunSuite with Matchers {

  test("Should default fixedDelay to None") {
    val when = When()
    val then = Then(None, None, None, fixedDelay = None)
    val primeRequest: PrimeQuerySingle = PrimeQuerySingle(when, then)

    val primeResult: Prime = PrimeQueryResultExtractor.extractPrimeResult(primeRequest)

    primeResult.fixedDelay should equal(None)
  }

  test("Should record fixedDelay if present") {
    val when = When()
    val fixedDelay: Some[Long] = Some(500)
    val then = Then(None, None, None, fixedDelay)
    val primeRequest: PrimeQuerySingle = PrimeQuerySingle(when, then)

    val primeResult: Prime = PrimeQueryResultExtractor.extractPrimeResult(primeRequest)

    primeResult.fixedDelay should equal(Some(FiniteDuration(500, TimeUnit.MILLISECONDS)))
  }

  test("Extracting Success result") {
    val when = When()
    val then = Then(None, Some(Success), None, fixedDelay = None)
    val primeRequest: PrimeQuerySingle = PrimeQuerySingle(when, then)

    val primeResult: Prime = PrimeQueryResultExtractor.extractPrimeResult(primeRequest)

    primeResult.result should equal(SuccessResult)
  }

  test("Extracting ReadRequestTimeout result") {
    val when = When()
    val properties = Map[String, String](
      ErrorConstants.ReceivedResponse -> "2",
      ErrorConstants.RequiredResponse -> "3",
      ErrorConstants.DataPresent -> "true")
    val then = Then(None, Some(ReadTimeout), config = properties)
    val primeRequest: PrimeQuerySingle = PrimeQuerySingle(when, then)

    val primeResult: Prime = PrimeQueryResultExtractor.extractPrimeResult(primeRequest)

    primeResult.result should equal(ReadRequestTimeoutResult(2, 3, dataPresent = true))
  }

  test("Extracting WriteRequestTimeout result with no extra params") {
    val when = When()
    val then = Then(None, Some(WriteTimeout))
    val primeRequest: PrimeQuerySingle = PrimeQuerySingle(when, then)

    val primeResult: Prime = PrimeQueryResultExtractor.extractPrimeResult(primeRequest)

    primeResult.result should equal(WriteRequestTimeoutResult())
  }

  test("Extracting Unavailable result with no extra params") {
    val when = When()
    val then = Then(None, Some(Unavailable))
    val primeRequest: PrimeQuerySingle = PrimeQuerySingle(when, then)

    val primeResult: Prime = PrimeQueryResultExtractor.extractPrimeResult(primeRequest)

    primeResult.result should equal(UnavailableResult())
  }

  //todo errors params for write and unavailable

}