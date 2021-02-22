// SPDX-License-Identifier: Apache-2.0

package layered.stage.phase

import chisel3.stage.{ChiselGeneratorAnnotation, ChiselStage}
import layered.ToLoFirrtl
import layered.stage.ElkException
import firrtl.options.{Dependency, Phase}
import firrtl.stage.{FirrtlCircuitAnnotation, FirrtlFileAnnotation, FirrtlSourceAnnotation}
import firrtl.{AnnotationSeq, FileUtils}

class GetFirrtlCircuitPhase extends Phase {
  override def prerequisites = Seq(Dependency[CheckPhase])
  override def optionalPrerequisites = Seq.empty
  override def optionalPrerequisiteOf = Seq.empty
  override def invalidates(a: Phase) = false

  def buildFirrtlCircuitAnnotation(firrtlText: String): FirrtlCircuitAnnotation = {
    val rawFirrtl = firrtl.Parser.parse(firrtlText)
    val processedFirrtlCircuit = (new ToLoFirrtl)
      .transform(Seq(FirrtlCircuitAnnotation(rawFirrtl)))
      .collectFirst {
        case circuitAnnotation: FirrtlCircuitAnnotation => circuitAnnotation
      }
      .getOrElse {
        throw new ElkException("Error: Could not lower firrtl circuit")
      }
    processedFirrtlCircuit
  }

  override def transform(annotations: AnnotationSeq): AnnotationSeq = {
    annotations.map {
      case FirrtlSourceAnnotation(source) =>
        buildFirrtlCircuitAnnotation(source)
      case FirrtlFileAnnotation(fileName) =>
        val source = FileUtils.getText(fileName)
        buildFirrtlCircuitAnnotation(source)
      case ChiselGeneratorAnnotation(gen) =>
//        val filteredAnnos = annotations.filterNot(_.isInstanceOf[ChiselGeneratorAnnotation])
//        val source = (new ChiselStage).emitFirrtl(gen(), annotations = filteredAnnos)
        val source = ChiselStage.emitFirrtl(gen())
        buildFirrtlCircuitAnnotation(source)
      case anno =>
        anno
    }
  }
}
