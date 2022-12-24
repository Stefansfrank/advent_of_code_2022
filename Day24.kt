package com.sf.aoc2022
import com.sf.aoc.*

class Day24 : Solver {

    override fun solve(file: String) {

        // reading input
        val data = readTxtFile(file)

        // Blizzards with a relatively quick move class (could be faster if I would write more code)
        // note that blizzards internally move in only the inner coordinate system, thus they add (1,1) at the end
        // this is necessary for the modulo to work
        class Blizz(init:XY, xdim:Int, ydim:Int, c:Char) {
            var pos = init.add(-1,-1)
            val vert = (c == 'v' || c == '^')
            val up = (c == 'v' || c == '>')
            var mod = (if (vert) ydim else xdim) - 2
            var add = if (up) 1 else (mod - 1)

            fun move():XY {
                pos = if (vert) XY(pos.x, (pos.y + add) % mod) else XY((pos.x + add) % mod, pos.y)
                return pos.add(1,1)
            }
        }

        // parsing the input into Blizzard objects, start & end and a cage mask with the walls
        // that cage mask is going to be used as a template for the occupied mask computed each clock tick
        val blizzes = mutableListOf<Blizz>()
        val cage = Mask(data[0].length, data.size)
        var start = XY(-1,-1)
        var end = XY(-1,-1)
        for ((y, ln) in data.withIndex()) {
            for ((x, c) in ln.withIndex()) {
                when (c) {
                    '<', '>', 'v', '^' -> blizzes.add(Blizz(XY(x, y), cage.xdim, cage.ydim, c))
                    '#' -> cage.on(x, y)
                    '.' -> {
                        if (y == 0) start = XY(x, y)
                        if (y == cage.ydim - 1) end = XY(x, y)
                    }
                }
            }
        }

        // finds the path by computing an occupied map for each clock tick as the blizzards are moving
        // on each clock tick a new set is created with all locations the group can be at that clock tick
        // the set keeps duplicates in check
        fun findPath(from: XY, to: XY):Int {
            var clock = 0
            var locs = setOf(from)
            while(true) {

                // compute blocked squares after blizzards move
                val occ = Mask(cage)
                blizzes.forEach { occ.on(it.move()) }

                // compute all locations reachable in the next round
                val nLocs = mutableSetOf<XY>()
                for (loc in locs) {
                    if (!occ.get(loc)) nLocs.add(loc)
                    loc.neighbors(false).forEach { if (!occ.getSafe(it, true)) nLocs.add(it) }
                }

                // rinse and repeat
                clock++
                if (nLocs.contains(to)) return clock
                locs = nLocs
            }
        }

        val p1 = findPath(start, end)
        println("Part 1: $red$bold$p1$reset")
        println("Part 2: $red$bold${p1 + findPath(end,start) + findPath(start,end)}$reset")
    }
}
