package com.sf.aoc2022
import com.sf.aoc.*
import kotlin.math.min

class Day13 : Solver {

    override fun solve(file: String) {

        // list splitter (assumes well-formed lists)
        // determines delimiter chars ('[', ']', ',') on the top level and returns the substrings between them
        fun splitList(inp: String):List<String> {
            var level = 0
            val delimiters = mutableListOf(0)
            inp.forEachIndexed {ix, char -> when (char) {
                    '[' -> level++
                    ',' -> if (level == 1) delimiters.add(ix)
                    ']' -> level--
            } }
            delimiters.add(inp.length - 1)
            return (0 until delimiters.size - 1).map { inp.substring(delimiters[it]+1, delimiters[it+1]) }
        }

        // The class representing a packet.
        // It has either a list of packets if isList is true or a value if isList is false.
        // It implements Kotlin's Comparable, so I can sort / compare packages using build in operations
        class Packet(val isList:Boolean, val items:List<Packet>, val value:Int):Comparable<Packet> {

            // creates a List version of a value packet
            fun asList() = Packet(true, listOf(this), 0)

            // this is the core puzzle implementation - the definition of the order -
            // implemented as a Kotlin.Comparator so I can use regular sorting / comparison operations
            override fun compareTo(other: Packet):Int {

                if (!isList) {
                    return if (!other.isList) value.compareTo(other.value) // both are values and can resolve
                    else this.asList().compareTo(other) // one is a list and the other a value ->
                }                                       // converting the value to a list and recurse list comparison
                if (!other.isList) return this.compareTo(other.asList()) // again one is a list and the other a value

                // both are lists, so we go through the first n elements of both lists
                // until a difference is detected or one list is exhausted
                val listOverlap = min(items.size, other.items.size)
                for (ix in 0 until listOverlap) {
                    val cmp = items[ix].compareTo(other.items[ix])
                    if (cmp != 0) return cmp
                }

                // when no difference is detected in the elements
                // the relative length of the lists represents the outcome
                return items.size.compareTo(other.items.size)
            }
        }

        // recursive packet parser
        fun parsePackets(inp: String):Packet {
            if (inp.isEmpty()) return Packet(true, listOf<Packet>(), 0)
            if (inp[0] == '[') return Packet(true, splitList(inp).map { parsePackets(it) }, 0)
            return Packet(false, listOf<Packet>(), inp.toInt())
        }

        // reading input
        val data = readTxtFile(file)

        // part 1
        print("Part 1: $red$bold")
        print(data.chunked(3).foldIndexed(0) { ix, sum, pair ->
           sum + if (parsePackets(pair[0]) < parsePackets(pair[1])) ix + 1 else 0 })
        println(reset)

        // part 2
        val d1 = parsePackets("[[2]]")
        val d2 = parsePackets("[[6]]")
        val packets = (data.filter { it.isNotEmpty() }.map{ parsePackets(it) } + d1 + d2).sorted()
        println("Part 2: $red$bold${(packets.indexOf(d1)+1)*(packets.indexOf(d2)+1)}$reset")
    }
}
