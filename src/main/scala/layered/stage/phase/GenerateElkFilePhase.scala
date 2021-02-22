// SPDX-License-Identifier: Apache-2.0

package layered.stage.phase

import firrtl.options.{Dependency, Phase}
import firrtl.stage.FirrtlCircuitAnnotation
import firrtl.{AnnotationSeq, CircuitState}
import layered.transforms.MakeGroup

class GenerateElkFilePhase extends Phase {
  override def prerequisites = Seq(Dependency[OptionallyBuildTargetDirPhase])

  override def optionalPrerequisites = Seq.empty

  override def optionalPrerequisiteOf = Seq.empty

  override def invalidates(a: Phase) = false

  override def transform(annotationSeq: AnnotationSeq): AnnotationSeq = {
    val firrtlCircuit = annotationSeq.collectFirst { case FirrtlCircuitAnnotation(circuit) => circuit }.get
    val circuitState = CircuitState(firrtlCircuit, annotationSeq)

    val transform = new MakeGroup()
    transform.execute(circuitState)

    annotationSeq
  }
}
