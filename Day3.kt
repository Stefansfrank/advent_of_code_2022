package com.sf.aoc2022

import com.sf.aoc.*

class Day3 : Solver {

    override fun solve(file: String) {

        // the priority function
        fun prio(c: Char): Int {
            return if (c.code < 97) c.code - 'A'.code + 27 else c.code - 'a'.code + 1
        }

        // reading the data as Char lists
        val data = readTxtFile(file).map { it.toList() }

        // using Kotlin's intersect function to find the overlap
        // Part 1
        var sum = 0
        for (rs in data)
            rs.take(rs.size/2).intersect(rs.takeLast(rs.size/2).toSet()).forEach { sum += prio(it) }
        println("\nThe sum of overlapping item priorities is $red$bold$sum$reset")

        // Part 2
        sum = 0
        for (i in data.indices step 3)
            data[i].intersect(data[i+1].toSet()).intersect(data[i+2].toSet()).forEach { sum += prio(it) }
        println("The sum of group badges is $red$bold$sum$reset")
    }

}