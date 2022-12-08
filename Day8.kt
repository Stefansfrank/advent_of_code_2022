package com.sf.aoc2022
import com.sf.aoc.*
import kotlin.math.max

class Day8 : Solver {

    override fun solve(file: String) {

        // reading input
        val data = readTxtFile(file)

        // parsing (assuming square input)
        val dim  = data[0].length
        val woods = MapInt(dim, dim)
        data.forEachIndexed { ix, s -> woods.setLine(ix, s.map { it.code - '0'.code }) }

        // Part 1
        val vis   = Mask(dim, dim) // visibility
        for (out in 0 until dim) { // loop through the columns / rows
            var high = mutableListOf(-1, -1, -1, -1) // reset the highest tree encountered for each column / row
            for (inn in 0 until dim) {  // loop through the individual columns / rows

                // coordinates of the four trees representing the next tree in the column / row from the 4 directions
                val trees = listOf(XY(out,inn), XY(dim - inn - 1, out), XY(out,dim - inn -1), XY(inn, out ))

                // set vis mask if tree higher than current highest
                trees.forEachIndexed { ix, xy ->
                    if (woods.get(xy) > high[ix]) {
                        vis.on(xy)
                        high[ix] = woods.get(xy)
                    }
                }

                // if I hit the highest tree (9) for all directions, stop trying
                if (high.sum() == 36) break
            }
        }
        println("The number of visible trees from outside is $red$bold${vis.cnt()}$reset")

        // Part 2
        // Function calculating the scenic score
        fun scenic(loc: XY):Int {
            var totScore = 1
            for (direction in 0..3) { // go through the four directions
                var curLoc = loc.mv(direction)
                var score  = 0
                while (curLoc.x in 0 until dim && curLoc.y in 0 until dim) { // move until edge
                    score++
                    if (woods.get(curLoc) < woods.get(loc)) curLoc = curLoc.mv(direction) else break
                }
                totScore *= score
            }
            return totScore
        }

        // find maximum across woods
        var maxScore = 0
        (0 until dim).forEach{ y -> (0 until dim).forEach{ x -> maxScore = max(scenic(XY(x,y)), maxScore) }}
        println("The best location has a scenic score of $red$bold${maxScore}$reset")
    }
}