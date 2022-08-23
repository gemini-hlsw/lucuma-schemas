// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.model

import cats.Eq
import cats.derived.*
import cats.syntax.all.given
import eu.timepit.refined.cats.given
import lucuma.core.enums.StepQaState
import lucuma.core.model.NonNegDuration
import lucuma.core.model.sequence.DynamicConfig
import lucuma.core.model.sequence.Step
import lucuma.core.model.sequence.StepConfig
import org.typelevel.cats.time.given

import java.time.Instant

sealed trait StepRecord derives Eq {
  def created: Instant
  def stepConfig: StepConfig
  def stepEvents: List[StepEvent]
  def startTime: Option[Instant]
  def duration: Option[NonNegDuration]
}

object StepRecord {
  final case class GmosNorth protected[schemas] (
    id:               Step.Id,
    created:          Instant,
    startTime:        Option[Instant],
    endTime:          Option[Instant],
    duration:         Option[NonNegDuration],
    instrumentConfig: DynamicConfig.GmosNorth,
    stepConfig:       StepConfig,
    stepEvents:       List[StepEvent],
    stepQaState:      Option[StepQaState],
    datasetEvents:    List[DatasetEvent],
    datasets:         List[Dataset]
  ) extends StepRecord
      derives Eq

  final case class GmosSouth protected[schemas] (
    id:               Step.Id,
    created:          Instant,
    startTime:        Option[Instant],
    endTime:          Option[Instant],
    duration:         Option[NonNegDuration],
    instrumentConfig: DynamicConfig.GmosSouth,
    stepConfig:       StepConfig,
    stepEvents:       List[StepEvent],
    stepQaState:      Option[StepQaState],
    datasetEvents:    List[DatasetEvent],
    datasets:         List[Dataset]
  ) extends StepRecord
      derives Eq
}
