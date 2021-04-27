// SPDX-License-Identifier: Apache-2.0

package layered.transforms

import firrtl.options.TargetDirAnnotation
import firrtl.{CircuitState, DependencyAPIMigration, Transform}
import layered.stage.cli.{SerializeAnnotation, StartModuleNameAnnotation}

import scala.collection.mutable

class MakeGroup extends Transform with DependencyAPIMigration {
  override def prerequisites = Seq.empty

  override def optionalPrerequisites = Seq.empty

  override def optionalPrerequisiteOf = Seq.empty

  override def invalidates(a: Transform) = false

  /**
    * Creates a series of elk graphs starting with the startModule and continuing
    * through all descendant sub-modules.
    * @param state the state to be diagrammed
    * @return
    */

  override def execute(state: CircuitState): CircuitState = {

    val startModuleName = state.annotations.collectFirst {
      case StartModuleNameAnnotation(moduleName) => moduleName
    }.getOrElse(state.circuit.main)

    val serialize = state.annotations.collectFirst {
      case SerializeAnnotation => true
    }.getOrElse(false)

    if (serialize) {
      val targetDir = state.annotations.collectFirst { case TargetDirAnnotation(dir) => dir }.get.stripSuffix("/")
      import java.io.BufferedWriter
      import java.io.FileWriter
      val bufferedWriter = new BufferedWriter(new FileWriter(s"$targetDir/$startModuleName.lo.fir"))
      try {
        bufferedWriter.write(state.circuit.serialize)
      } finally {
        if (bufferedWriter != null) bufferedWriter.close()
      }
    }

    val queue = new mutable.Queue[String]()
    val modulesSeen = new mutable.HashSet[String]()

    queue += startModuleName // set top level of diagram tree

    while (queue.nonEmpty) {
      val moduleName = queue.dequeue()
      if (!modulesSeen.contains(moduleName)) {

        val updatedAnnotations = {
          state.annotations.filterNot { x =>
            x.isInstanceOf[StartModuleNameAnnotation]
          } :+ StartModuleNameAnnotation(moduleName)
        }
        val stateToDiagram = CircuitState(state.circuit, state.form, updatedAnnotations)

        val pass = new MakeOne()
        pass.execute(stateToDiagram)

        queue ++= pass.subModulesFound.map(module => module.name)
      }
      modulesSeen += moduleName
    }

    // we return the original state, all transform work is just in the interest of diagramming
    state
  }
}
