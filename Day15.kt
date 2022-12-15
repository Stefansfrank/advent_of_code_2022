package com.sf.aoc2022
import com.sf.aoc.*
import kotlin.math.abs
import kotlin.math.max

class Day15 : Solver {

    override fun solve(file: String) {

        // reading input
        val data = readTxtFile(file)

        // pair class representing a sensor and nearest beacon
        data class SBpair(val sensor: XY, val beacon: XY) {

            // the Manhattan distance
            val dist = sensor.mDist(beacon)

            // calculates how many spaces can not have a beacon
            // for a given line due to this pair
            fun blockedInLine(y:Int):Rng?{
                val del = abs(sensor.y - y)
                if (del > dist) return null
                return Rng(sensor.x - dist + del, sensor.x + dist - del)
            }
        }

        // parsing
        val rx = " x=(-?\\d+), y=(-?\\d+):".toRegex()
        val pairs = mutableListOf<SBpair>()
        data.forEach { ln ->
            val matches = rx.findAll("$ln:").toList()
            pairs.add(SBpair(XY(matches[0].groupValues[1].toInt(), matches[0].groupValues[2].toInt()),
                XY(matches[1].groupValues[1].toInt(), matches[1].groupValues[2].toInt())))
        }

        // In order to be fast, this solution uses ranges defined by from/to but not expanded into a list
        // the logic of merging such ranges and detecting overlap etc is defined in Rng.kt
        var ranges = mutableListOf<Rng>()
        val lineNo = 2_000_000

        // collecting the ranges blocked in the given line for all sensor / beacon pairs
        pairs.forEach { it.blockedInLine(lineNo)?.let { rng -> ranges.add(rng) } }

        // detect unique beacons already in this line
        val beacons = pairs.map { it.beacon }.filter { it.y == lineNo }.toSet()

        // reduceRngs merges the individual ranges in a list into as few as possible
        // needed here to avoid double counting overlap
        println("Part 1: $red$bold${reduceRngs(ranges).fold(0){sum, r -> sum + r.length()} - beacons.size}$reset")

        // for part 2 I assume that if I encounter a line that can not be reduced to one range
        // I found my location - which works if the input does not show fragmentation outside the valid range
        print("Part 2: $red$bold")
        for (y in 0 .. 4_000_000) {
            ranges = mutableListOf()
            pairs.forEach { it.blockedInLine(y)?.let { rng -> ranges.add(rng) } }
            val rr = reduceRngs(ranges)
            if (rr.size == 2) {
                println("${4_000_000L * (max(rr[0].from, rr[1].from) - 1) + y}$reset")
                break
            }
        }
    }
}
