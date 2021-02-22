// SPDX-License-Identifier: Apache-2.0

package layered.stage.cli

import firrtl.options.Shell

trait ElkCli {
  this: Shell =>

  parser.note("Layered Firrtl Options")

  Seq(
    StartModuleNameAnnotation,
    FlattenLevelAnnotation,
    SerializeAnnotation
  ).foreach(_.addOptions(parser))
}
