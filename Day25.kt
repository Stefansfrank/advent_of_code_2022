package com.sf.aoc2022
import com.sf.aoc.*

class Day25: Solver {

    override fun solve(file: String) {

        // reading input
        val data = readTxtFile(file)

        // parse so that every SNAFU number is a list of ints [-2,-1,0,1,2]
        // starting with the lowest digit
        val nums = mutableListOf<List<Int>>()
        for (s in data) {
            nums.add( s.reversed().map { when (it) {
                '2' -> 2
                '1' -> 1
                '0' -> 0
                '-' -> -1
                '=' -> -2
                else -> 0
            } })
        }

        // super simple conversion to dec
        fun dec(snaf:List<Int>):Long {
            var mult = 1L
            return snaf.fold(0L) { p, d -> (p + d * mult).also { mult *= 5 }}
        }

        // sum the input
        var sum = nums.fold(0L){ sm, sn -> (sm + dec(sn))}

        // a standard algo for base 5 conversion with one addition (below)
        var digit = 0
        val result = MutableList(30) { 0L } // to lazy to determine digits needed
        repeat(30) {
            result[digit] += sum % 5
            // that's the line taking care of the specialty here ...
            if (result[digit] > 2L) { result[digit] -= 5L; result[digit+1] = 1 }
            sum /= 5
            digit ++
        }

        // inline conversion from List<Int> to a string
        print("Part 1: $red$bold")
        print(result.reversed().map{ when (it) {
            2L -> '2'
            1L -> '1'
            0L -> '0'
            -1L -> '-'
            -2L -> '='
            else -> '?'
        } }.fold(""){ s, c -> s + c }.trimStart('0'))
        println(reset)
    }
}
