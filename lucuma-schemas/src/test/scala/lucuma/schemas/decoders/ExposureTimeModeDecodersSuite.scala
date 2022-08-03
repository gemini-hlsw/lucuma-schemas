// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.decoders

import eu.timepit.refined._
import eu.timepit.refined.numeric.Positive
import lucuma.core.model.ExposureTimeMode
import lucuma.core.model.NonNegDuration
import lucuma.refined._

import java.time.Duration

class ExposureTimeModeDecodersSuite extends InputStreamSuite {
  test("SignalToNoise decoder") {
    val expected: ExposureTimeMode =
      ExposureTimeMode.SignalToNoise(value =
        refineV[Positive](BigDecimal(1.23)).getOrElse(sys.error("Cannot happen"))
      )
    assertParsedStreamEquals("/signalToNoise.json", expected)
  }

  test("FixedExposure decoder") {
    val expected: ExposureTimeMode =
      ExposureTimeMode.FixedExposure(count = 99.refined,
                                     NonNegDuration.unsafeFrom(Duration.ofNanos(47000))
      )
    assertParsedStreamEquals("/fixedExposure.json", expected)
  }
}