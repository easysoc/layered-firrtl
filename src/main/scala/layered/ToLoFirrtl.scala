// SPDX-License-Identifier: Apache-2.0

package layered

import firrtl._
import firrtl.options.{Dependency, Phase}
import firrtl.stage.{FirrtlCircuitAnnotation, Forms}

/**
  * Use these lowering transforms to prepare circuit for compiling
  */
class ToLoFirrtl extends Phase {
  private val targets = Forms.LowFormOptimized ++ Seq(
    Dependency(passes.RemoveEmpty)
  )

  private def compiler = new firrtl.stage.transforms.Compiler(targets, currentState = Nil)
  private val transforms = compiler.flattenedTransformOrder

  override def transform(annotationSeq: AnnotationSeq): AnnotationSeq = {

    annotationSeq.flatMap {
      case FirrtlCircuitAnnotation(circuit) =>
        val state = CircuitState(circuit, annotationSeq)
        val newState = transforms.foldLeft(state) {
          case (prevState, transform) => transform.runTransform(prevState)
        }
        Some(FirrtlCircuitAnnotation(newState.circuit))
      case other =>
        Some(other)
    }
  }
}