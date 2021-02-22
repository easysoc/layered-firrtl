// SPDX-License-Identifier: Apache-2.0

package layered.stage.phase

import java.io.File

import layered.stage.ElkException
import firrtl.options.{Dependency, Phase, TargetDirAnnotation}
import firrtl.stage.FirrtlCircuitAnnotation
import firrtl.{AnnotationSeq, FileUtils}

class OptionallyBuildTargetDirPhase extends Phase {
  override def prerequisites = Seq(Dependency[GetFirrtlCircuitPhase])

  override def optionalPrerequisites = Seq.empty

  override def optionalPrerequisiteOf = Seq.empty

  override def invalidates(a: Phase) = false

  override def transform(annotationSeq: AnnotationSeq): AnnotationSeq = {
    // TargetDirAnnotation(".") is added by default by AddDefaults Phase
    val dir = annotationSeq.collectFirst { case TargetDirAnnotation(targetDir) => targetDir }.get

    val newAnnotations: AnnotationSeq = if (dir != ".") {
      annotationSeq
    } else {
      val circuit = annotationSeq.collectFirst { case FirrtlCircuitAnnotation(circuit) => circuit }.get
      val targetDir = s"test_run_dir/${circuit.main}"
      val newSeq = annotationSeq.filterNot {
        case _: TargetDirAnnotation => true
        case _ => false
      }
      newSeq :+ TargetDirAnnotation(targetDir)
    }

    newAnnotations.foreach {
      case TargetDirAnnotation(targetDir) =>
        val targetDirFile = new File(targetDir)
        if (targetDirFile.exists()) {
          if (!targetDirFile.isDirectory) {
            throw new ElkException(s"Error: Target dir ${targetDir} exists and is not a directory")
          }
        } else {
          FileUtils.makeDirectory(targetDir)
          if (!targetDirFile.exists()) {
            throw new ElkException(s"Error: Target dir ${targetDir} exists and is not a directory")
          }
        }
      case _ =>
    }
    newAnnotations
  }
}
