// SPDX-License-Identifier: Apache-2.0

package layered.stage.cli

import firrtl.annotations.{Annotation, NoTargetAnnotation}
import firrtl.options.{HasShellOptions, ShellOption, Unserializable}

sealed trait ElkAnnotation {
  this: Annotation =>
}

case class StartModuleNameAnnotation(name: String)
    extends ElkAnnotation
    with NoTargetAnnotation
    with Unserializable

object StartModuleNameAnnotation extends HasShellOptions {
  val options = Seq(
    new ShellOption[String](
      longOption = "module-name",
      toAnnotationSeq = (a: String) => Seq(StartModuleNameAnnotation(a)),
      helpText = "The module in the hierarchy to start, default is the circuit top"
    )
  )
}

case class FlattenLevelAnnotation(depth: Int)
  extends ElkAnnotation
    with NoTargetAnnotation
    with Unserializable

object FlattenLevelAnnotation extends HasShellOptions {
  val options = Seq(
    new ShellOption[Int](
      longOption = "flatten",
      toAnnotationSeq = (a: Int) => Seq(FlattenLevelAnnotation(a)),
      helpText = "The maxDepth of the flatten levels",
      helpValueName = Some("depth")
    )
  )
}

case object SerializeAnnotation
    extends NoTargetAnnotation
    with ElkAnnotation
    with HasShellOptions
    with Unserializable {
  val options = Seq(
    new ShellOption[Unit](
      longOption = "serialize",
      toAnnotationSeq = _ => Seq(SerializeAnnotation),
      helpText = "Serialize the loFirrtl circuit state to a lo.fir file"
    )
  )
}
