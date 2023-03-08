// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.schemas.odb

import clue.GraphQLSubquery
import clue.annotation.*
import lucuma.schemas.ObservationDB
// gql: import io.circe.refined.*

@GraphQL
abstract class FluxDensitySubquery extends GraphQLSubquery[ObservationDB]("FluxDensityEntry"):
  // FIXME Replace wavelength when subqueries can contain subqueries
  override val subquery: String = """
        {
          wavelength {
            picometers
          }
          density
        }
      """

@GraphQLStub
object FluxDensitiesSubquery