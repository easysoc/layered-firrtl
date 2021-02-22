// SPDX-License-Identifier: Apache-2.0

package layered

import firrtl._
import layered.stage.ElkStage

/**
  * This library implements the conversion of Chisel / Firrtl to Eclipse Layout Kernel Graph.
  */
object Elk {
  def main(args: Array[String]): Unit = {
    (new ElkStage).execute(args, Seq.empty)
  }
}
