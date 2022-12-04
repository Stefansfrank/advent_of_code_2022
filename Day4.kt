package com.sf.aoc2022
import  com.sf.aoc.*

class Day4 : Solver {

    override fun solve(file: String) {

        // result counters
        var sum1 = 0
        var sum2 = 0

        // reading input
        val data = readTxtFile(file)
        val rgx = "(\\d+)-(\\d+),(\\d+)-(\\d+)".toRegex()
        data.forEach { ln ->

            // parses the 4 limit values into limits[]
            val limits = rgx.find(ln)!!.groupValues.drop(1).map { it.toInt() }

            // computes the intersection of both
            val inter = (limits[0]..limits[1]).intersect(limits[2] .. limits[3])

            // checks whether there is an intersection (part 2)
            if (inter.isNotEmpty()) {
                sum2++

                // checks whether that intersection has the same size as either range (part 1)
                if ((inter.size == (limits[0]..limits[1]).count()) ||
                    (inter.size == (limits[2]..limits[3]).count())) sum1++
            }

        }

        println("\nPairs where one completely contains the other: $red$bold${sum1}$reset")
        println("Pairs with some overlap: $red$bold${sum2}$reset")
    }
}
