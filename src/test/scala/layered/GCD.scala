// SPDX-License-Identifier: Apache-2.0

package layered

import chisel3._
import chisel3.stage.ChiselGeneratorAnnotation
import layered.stage.ElkStage

class GCD extends Module {
  val io = IO(new Bundle {
    val value1        = Input(UInt(16.W))
    val value2        = Input(UInt(16.W))
    val loadingValues = Input(Bool())
    val outputGCD     = Output(UInt(16.W))
    val outputValid   = Output(Bool())
  })

  val x  = RegInit(0.U(16.W))
  val y  = RegInit(0.U(16.W))

  when(x > y) { x := x - y }
    .otherwise { y := y - x }

  when(io.loadingValues) {
    x := io.value1
    y := io.value2
  }

  io.outputGCD := x
  io.outputValid := y === 0.U
}

object GCDTester extends App {

  val targetDir = "test_run_dir/gcd"

  (new ElkStage).execute(
    Array("--target-dir", targetDir, "--lowFir"),
    Seq(ChiselGeneratorAnnotation(() => new GCD))
  )

//    def getLowFirrtl(gen: => RawModule) = {
//      (new chisel3.stage.ChiselStage).execute(Array("-td", targetDir, "-X", "low"),
//        Seq(ChiselGeneratorAnnotation(() => gen)))
//    }
//    getLowFirrtl(new GCD)
  (new chisel3.stage.ChiselStage).emitVerilog(new GCD,Array("-td", targetDir) )
}
