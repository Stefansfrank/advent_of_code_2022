package com.sf.aoc2022
import com.sf.aoc.*

class Day10 : Solver {

    override fun solve(file: String) {

        // reading input
        val data = readTxtFile(file)

        // parse and create time series for register X
        var curX = 1
        val time = mutableListOf<Int>()
        for (ln in data) {
            if (ln[0] == 'a') {
                time.add(curX)
                time.add(curX)
                curX += ln.substringAfter(' ').toInt()
            } else {
                time.add(curX)
            }
        }

        // sum for part 1
        println("Part 1: $red$bold${listOf(20,60,100,140,180,220).fold(0) { sm, it -> sm + time[it-1]*it }}$reset")

        // define crt as 2D mask and loop through
        val crt = Mask(40, 6)
        for (x in 0 until 40) {
            for (y in 0 until 6) {
                val totIx = x + y*40
                if (x in time[totIx]-1 .. time[totIx]+1) crt.on(XY(x,y))
            }
        }

        // print crt
        println("Part 2:$red$bold")
        crt.print()
        print(reset)
    }
}