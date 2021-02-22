// SPDX-License-Identifier: Apache-2.0

package layered.stage

import chisel3.stage.ChiselCli
import firrtl.AnnotationSeq
import firrtl.options.{Shell, Stage}
import firrtl.stage.FirrtlCli
import layered.stage.cli.ElkCli

/** The firrtl circuit can be in any one of the following forms
  * ChiselGeneratorAnnotation(() => new DUT())  a chisel DUT that is used to generate firrtl
  * FirrtlFileAnnotation(fileName)              a file name that contains firrtl source
  * FirrtlSourceAnnotation                      in-line firrtl source
  */
class ElkStage extends Stage {
  val shell: Shell = new Shell("elk") with ElkCli with ChiselCli with FirrtlCli

  def run(annotations: AnnotationSeq): AnnotationSeq = {
    (new ElkPhase).transform(annotations)
  }
}
