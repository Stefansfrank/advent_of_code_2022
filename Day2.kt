package com.sf.aoc2022

import com.sf.aoc.*

class Day2 : Solver {

    override fun solve(file: String) {

        // the scoring function for a round expressed by a point XY
        // with x = opponent play and y = my play and
        // 0 = rock, 1 = paper, 2 = scissors
        fun rpsScore(rnd: XY): Int {
            return ((4 + rnd.y - rnd.x) % 3) * 3 + rnd.y + 1
        }

        // loading the input file
        val data = readTxtFile(file)

        // part 1 & part 2 lists of rounds
        // expressed by a list of XY data structures (see above)
        val p1rnds = mutableListOf<XY>()
        val p2rnds = mutableListOf<XY>()
        data.forEach {
            val c1 = it.first().code - 'A'.code
            val c2 = it.last().code  - 'X'.code
            p1rnds.add(XY(c1, c2))
            p2rnds.add(XY(c1, (c1 + c2 + 2) % 3 ))
        }

        //
        println("\nThe game result is $red$bold${p1rnds.fold(0) { sum, it -> sum + rpsScore(it) }}$reset")
        println("The modified game result is $red$bold${p2rnds.fold(0) { sum, it -> sum + rpsScore(it) }}$reset")
    }

}