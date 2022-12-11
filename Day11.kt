package com.sf.aoc2022
import com.sf.aoc.*

class Day11 : Solver {

    override fun solve(file: String) {

        // The monkey class with the various values. The operation is expressed by a polynomial i.e.
        // new = p0*old*old + p1*old + p2. Thus, I can express all operations as parameter triples of one formula.
        // items: the worry values / op: the polynomial parameter triple / test: the division test value
        // target: the monkey throw target for true and false / cnt: the inspection counter
        data class Monkey (var items: MutableList<Long>, val op: Triple<Long, Long, Long>,
                           val test: Long, val target: Pair<Int, Int>, var cnt:Long = 0) {
            fun op(ix: Int): Long {
                return op.first*items[ix]*items[ix] + op.second*items[ix] + op.third
            }
        }

        // The parsing is not super elegant but Regex would be similar in length and this seems more readable
        fun parseMonkeys(data:List<String>):List<Monkey> {
            val monkeys = mutableListOf<Monkey>()
            data.chunked(7).forEach { def ->
                // simple elements
                val items = def[1].substringAfter(':').split(',').map { it.trim().toLong() }
                val test = def[3].substringAfter("by ").toLong()
                val target = Pair(def[4].substringAfter("ey ").toInt(),
                    def[5].substringAfter("ey ").toInt())

                // the operation formula parsing
                val fm = def[2].substringAfter("= old ")
                val op = if (fm[0] == '+') Triple(0L, 1L, fm.substring(2).toLong())
                    else if (fm[2] == 'o') Triple(1L, 0L, 0L)
                    else Triple(0L, fm.substring(2).toLong(), 0L)

                monkeys.add(Monkey(items.toMutableList(), op, test, target))
            }
            return monkeys
        }

        // reading input
        val data = readTxtFile(file)

        // Both parts are nearly identical code
        for (part in 1 ..2) {
            val monkeys = parseMonkeys(data)

            // the trick for part 2 - compute the product of the division test parameter of each monkey
            // now all worry values can be limited to the value modulo that product
            // as this will not change the division tests on any monkey
            val cm = monkeys.fold(1) { p:Long, it -> p * it.test}
            repeat (if (part == 1) 20 else 10_000) {
                for (mk in monkeys) {
                    mk.items.indices.forEach {
                        var new = mk.op(it)
                        new = if (part == 1) new / 3 else new % cm
                        if (new % mk.test == 0L)
                            monkeys[mk.target.first].items.add(new) else monkeys[mk.target.second].items.add(new)
                        mk.cnt++
                    }
                    // all items are thrown elsewhere thus the item list for this monkey is empty at the end
                    mk.items = mutableListOf<Long>()
                }
            }

            // Print result
            print("Part ${part}: $red$bold")
            print(monkeys.sortedByDescending { it.cnt }.take(2).fold(1) { p: Long, it -> p * it.cnt })
            println(reset)
        }
    }
}