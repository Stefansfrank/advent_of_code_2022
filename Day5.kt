package com.sf.aoc2022
import com.sf.aoc.*

// This is a template to start new days quickly
class Day5 : Solver {

    override fun solve(file: String) {

        // reading input
        val data = readTxtFile(file)

        // regex for instructions
        val rx = "move (\\d+) from (\\d+) to (\\d+)".toRegex()

        // stacks are prepared for both parts at once
        val numStacks = (data[0].length + 1) / 4
        val stacks1 = (0 until numStacks).map { mutableListOf<Char>() }.toMutableList()
        val stacks2 = (0 until numStacks).map { mutableListOf<Char>() }.toMutableList()

        // the data class and list holding moving instructions
        data class Instr(val amt: Int, val from: Int, val to: Int)
        val instructions = mutableListOf<Instr>()

        // the actual moving logic for both cranes (only difference is the .reverse() call)
        fun move(i: Instr) {
            stacks1[i.to]   = (stacks1[i.from].take(i.amt).reversed() + stacks1[i.to]).toMutableList()
            stacks1[i.from] =  stacks1[i.from].drop(i.amt).toMutableList()
            stacks2[i.to]   = (stacks2[i.from].take(i.amt) + stacks2[i.to]).toMutableList()
            stacks2[i.from] =  stacks2[i.from].drop(i.amt).toMutableList()
        }

        // parse the text file and create two (initially identical) stack sets and one set of instructions
        for (ln in data) {
            if (ln.contains('[')) {
                ln.chunked(4).forEachIndexed { ix, s -> if (s[1] != ' ') {
                    stacks1[ix].add(s[1])
                    stacks2[ix].add(s[1])
                } }
            } else if (ln.contains('m')) {
                val pl = rx.find(ln)!!.groupValues
                instructions.add( Instr( pl[1].toInt() , pl[2].toInt() - 1, pl[3].toInt() - 1 ) )
            }
        }

        // actually move crates
        instructions.forEach { move(it) }

        // using inline fold to display the top crate (index 0 on each stack)
        println("\nCrane 9000: $red$bold${stacks1.fold("") { res , s -> res + s[0] }}$reset")
        println("Crane 9001: $red$bold${stacks2.fold("") { res , s -> res + s[0] }}$reset")
    }
}