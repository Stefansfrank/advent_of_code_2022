package com.sf.aoc2022
import com.sf.aoc.*
import kotlin.math.sign

class Day20 : Solver {

    override fun solve(file: String) {
        
        // sets the prev/next links on the Links with the order given in data
        // returns the element with value 0L
        fun crossLink(data:List<Link>):Link {
            val dlen = data.size
            var ret = data[0]
            data.forEachIndexed { ix, it ->
                data[(ix + dlen - 1) % dlen].next = it
                data[(ix + dlen + 1) % dlen].prev = it
                if (it.value == 0L) ret = it
            }
            return ret
        }

        // moving things around
        fun move(n:Int, data:List<Link>) {
            val dmod = data.size - 1
            repeat(n) {
                data.forEach {
                    when (it.value.sign) {
                         1 -> it.moveFw((it.value % dmod).toInt())
                        -1 -> it.moveBw((-it.value % dmod).toInt())
                    }
                }
            }
        }

        // read input
        val input = readTxtFile(file)
        val dlen = input.size

        // Part 1
        var data = input.map { Link(it.toLong(), null, null) }
        var zero = crossLink(data)
        move(1, data)

        print("Part 1: $red$bold")
        print(zero.atFw(1000 % dlen).value + zero.atFw(2000 % dlen).value + zero.atFw(3000 % dlen).value)
        println(reset)

        // Part 2
        val enc = 811589153L
        data = input.map { Link(it.toLong() * enc, null, null) }
        zero = crossLink(data)
        move(10,data)

        print("Part 2: $red$bold")
        print(zero.atFw(1000 % dlen).value + zero.atFw(2000 % dlen).value + zero.atFw(3000 % dlen).value)
        println(reset)
    }
}
