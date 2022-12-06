package com.sf.aoc2022
import com.sf.aoc.*

// This is a template to start new days quickly
class Day6 : Solver {

    override fun solve(file: String) {

        // reading input
        val data = readTxtFile(file)[0].toList()

        // using windowing and
        // the length of the corresponding set
        for ((part, len) in listOf(4,14).withIndex()) {
            for ((ix, window) in data.windowed(len).withIndex()) {
                if (window.toSet().size == len) {
                    println("Part ${part + 1}: $red$bold${ix + len}$reset")
                    break
                }
            }
        }
    }
}