package com.sf.aoc2022
import com.sf.aoc.*
import kotlin.math.max

class Day14 : Solver {

    override fun solve(file: String) {

        // reading input
        val data = readTxtFile(file)

        // parsing coordinates into mask
        val rx =" (\\d+),(\\d+) ".toRegex()
        val map = Mask(1001, 200)
        var low = 0
        for (ln in data) {
            val pts = rx.findAll(" $ln ").map { XY(it.groupValues[1].toInt(), it.groupValues[2].toInt()) }.toList()
            (0..pts.size - 2).forEach{ map.on(Rect(pts[it], pts[it+1])) }
            low = max(pts.map { it.y }.max(),low)
        }

        // part 1 simulation of falling sand
        // returns false if sand falls below structures
        fun fall(start:XY, map:Mask, low:Int):Boolean {
            var cur = start
            falling@while (true) {
                if (cur.y == low) return false
                for (it in listOf(cur.add(XY(0,1)), cur.add(XY(-1,1)), cur.add(XY(1,1)))) {
                    if (!map.get(it)) { cur = it; continue@falling }
                }
                map.on(cur)
                return true
            }
        }

        // Part 2 is solved by filling every reachable square from the top
        // using a BFS-like approach instead of simulating the fall of each sand portion
        fun fill(start:XY, map:Mask) {
            map.on(start)
            val que = ArrayDeque(listOf(start))
            while (que.size > 0) {
                val cur = que.removeFirst()
                listOf(cur.add(XY(0,1)), cur.add(XY(-1,1)), cur.add(XY(1,1)))
                    .forEach { if (!map.get(it)) {map.on(it); que.add(it)} }
            }
        }

        // part 1 - simulate falling sand
        var cnt1 = 0
        while (true) if (fall(XY(500,0), map, low)) cnt1++ else break
        println("Part 1: $red$bold${cnt1}$reset")

        // part 2 - fill all reachable areas
        map.on(Rect(XY(0,low+2), XY(1000, low+2)))
        val beforeFill = map.cnt() // count squares already filled by structures and part 1
        fill(XY(500, 0), map)
        println("Part 2: $red$bold${cnt1 + map.cnt() - beforeFill}$reset")
    }
}