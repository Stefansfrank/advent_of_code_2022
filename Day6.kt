package com.sf.aoc2022
import com.sf.aoc.*

// This is a template to start new days quickly
class Day6 : Solver {

    override fun solve(file: String) {

        // parsing input
        val data = readTxtFile(file)[0].toList()

        // Part 1 using windowing and
        // the length of the window converted to a set
        val wdata1 = data.windowed(4)
        for (ix in wdata1.indices) {
            if (wdata1[ix].toSet().size == 4) {
                println("\nPart 1: $red$bold${ix+4}$reset")
                break
            }
        }

        // Part 2 using windowing and
        // the length of the window converted to a set
        val wdata2 = data.windowed(14)
        for (ix in wdata1.indices) {
            if (wdata2[ix].toSet().size == 14) {
                println("Part 2: $red$bold${ix+14}$reset")
                break
            }
        }
    }
}