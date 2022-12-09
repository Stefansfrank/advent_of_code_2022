package com.sf.aoc2022
import com.sf.aoc.*

class Day9 : Solver {

    override fun solve(file: String) {

        // reading input
        val data = readTxtFile(file)

        // the rope simulator
        // using sign to compute movement vector of tails
        class Rope (val num:Int, var knts:Array<XY> = Array(num) {_ -> XY(0,0)}) {
            fun mv(dir: Int) {
                knts[0] = knts[0].mv(dir)
                (1 until num).forEach {
                    val vec = knts[it - 1].sub(knts[it])
                    if (vec.maxAbs() > 1) knts[it] = knts[it].add(vec.sign())
                }
            }
        }

        // prep structures
        val conv   = hashMapOf('U' to 0, 'R' to 1, 'D' to 2, 'L' to 3)
        val ropes  = listOf(Rope(2), Rope(10))
        val traces = listOf(mutableSetOf(XY(0,0)), mutableSetOf(XY(0,0)))

        // parse input
        for (ln in data) {
            val dir = conv[ln[0]]!!
            val cnt = ln.substringAfter(' ').toInt()

            // move and trace tail
            (1 .. cnt).forEach { _ ->
                ropes.forEachIndexed() { ix, rp ->
                    rp.mv(dir)
                    traces[ix].add(rp.knts.last())
                }
            }
        }

        println(" 2 knot rope tail visited $red$bold${traces[0].size}$reset locations")
        println("10 knot rope tail visited $red$bold${traces[1].size}$reset locations")
    }
}