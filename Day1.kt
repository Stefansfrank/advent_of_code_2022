package com.sf.aoc2022

import com.sf.aoc.*

class Day1 : Solver {

    override fun solve(file: String) {

        // parsing input
        val data = readTxtFile(file)

        // build a list of elf's calories counts
        var elfCals = mutableListOf(0)
        data.forEach { ln -> if (ln == "") {
                elfCals.add(0)
            } else {
                elfCals[elfCals.size - 1] += ln.toInt()
            }
        }

        // sorted version of this list with highest count first
        val sortedElfs = elfCals.sortedDescending()

        println("\nThe elf with the most calories has $red$bold${sortedElfs[0]}$reset calories")
        println("The top three together have $red$bold${sortedElfs.take(3).sum()}$reset calories")
    }
}