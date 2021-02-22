// SPDX-License-Identifier: Apache-2.0

package layered.stage

import layered.stage.phase.{
  CheckPhase,
  GenerateElkFilePhase,
  GetFirrtlCircuitPhase,
  OptionallyBuildTargetDirPhase
}
import firrtl.options.phases.DeletedWrapper
import firrtl.options.{Dependency, Phase, PhaseManager}

class ElkPhase extends PhaseManager(ElkPhase.targets) {

  override val wrappers = Seq((a: Phase) => DeletedWrapper(a))

}

object ElkPhase {

  val targets: Seq[PhaseManager.PhaseDependency] = Seq(
    Dependency[CheckPhase],
    Dependency[GetFirrtlCircuitPhase],
    Dependency[OptionallyBuildTargetDirPhase],
    Dependency[GenerateElkFilePhase]
  )
}
