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
package org.scassandra.priming.routes

import java.util.concurrent.TimeUnit

import org.scalatest.{Matchers, FunSuite}
import org.scassandra.priming.query.{Prime, Then, When, PrimeQuerySingle}

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
}
