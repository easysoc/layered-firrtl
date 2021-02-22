// SPDX-License-Identifier: Apache-2.0

package layered

import chisel3._
import chisel3.stage.ChiselGeneratorAnnotation
import layered.stage.ElkStage

class GCD extends Module {
  //noinspection TypeAnnotation
  val io = IO(new Bundle {
    val a = Input(UInt(16.W))
    val b = Input(UInt(16.W))
    val e = Input(Bool())
    val z = Output(UInt(16.W))
    val v = Output(Bool())
  })
//  io.z := io.a
//  io.v := DontCare
  val x = Reg(UInt())
  val y = Reg(UInt())

  when(x > y) { x := x - y }.otherwise { y := y - x }

  when(io.e) { x := io.a; y := io.b }
  io.z := x
  io.v := y === 0.U
}

object GCDTester extends App {

  val targetDir = "test_run_dir/gcd"

  (new ElkStage).execute(
    Array("--target-dir", targetDir, "--serialize"),
    Seq(ChiselGeneratorAnnotation(() => new GCD))
  )

//    def getLowFirrtl(gen: => RawModule) = {
//      (new chisel3.stage.ChiselStage).execute(Array("-td", targetDir, "-X", "low"),
//        Seq(ChiselGeneratorAnnotation(() => gen)))
//    }
//    getLowFirrtl(new GCD)
}
