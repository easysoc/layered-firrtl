// SPDX-License-Identifier: Apache-2.0

package layered

import chisel3._
import chisel3.stage.ChiselStage
import firrtl.options.TargetDirAnnotation
import firrtl.stage.FirrtlSourceAnnotation
import layered.stage.ElkStage

import scala.language.reflectiveCalls

class MyManyDynamicElementVecFir(length: Int) extends Module {
  //noinspection TypeAnnotation
  val io = IO(new Bundle {
    val in = Input(UInt(8.W))
    val out = Output(UInt(8.W))
    val consts = Input(Vec(length, UInt(8.W)))
  })

  val taps: Seq[UInt] = Seq(io.in) ++ Seq.fill(io.consts.length - 1)(RegInit(0.U(8.W)))
  taps.zip(taps.tail).foreach { case (a, b) => b := a }

  io.out := taps.zip(io.consts).map { case (a, b) => a * b }.reduce(_ + _)
}

object FirExample extends App {

  val targetDir = "test_run_dir/fir_example"

  val firrtl = ChiselStage.emitFirrtl(
    new MyManyDynamicElementVecFir(length = 10)
  )

  val annos = Seq(
    TargetDirAnnotation(targetDir),
    FirrtlSourceAnnotation(firrtl)
  )
  (new ElkStage).transform(annos)
}
