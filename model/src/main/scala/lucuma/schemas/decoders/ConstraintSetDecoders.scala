// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.decoders

import io.circe.Decoder
import io.circe.generic.semiauto
import io.circe.refined.*
import lucuma.core.model.ConstraintSet
import lucuma.core.model.ElevationRange

trait ConstraintSetDecoders {
  given Decoder[ElevationRange.AirMass] = c =>
    for {
      min <- c.downField("min").as[ElevationRange.AirMass.DecimalValue]
      max <- c.downField("max").as[ElevationRange.AirMass.DecimalValue]
    } yield ElevationRange.AirMass.fromDecimalValues.get((min, max))

  given Decoder[ElevationRange.HourAngle] = c =>
    for {
      min <- c.downField("minHours").as[ElevationRange.HourAngle.DecimalHour]
      max <- c.downField("maxHours").as[ElevationRange.HourAngle.DecimalHour]
    } yield ElevationRange.HourAngle.fromDecimalHours.get((min, max))

  given Decoder[ElevationRange] = c =>
    c.downField("airMass")
      .as[ElevationRange.AirMass]
      .orElse(c.downField("hourAngle").as[ElevationRange.HourAngle])

  given Decoder[ConstraintSet] = semiauto.deriveDecoder
}
