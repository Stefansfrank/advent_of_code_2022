package com.sf.aoc2022
import com.sf.aoc.*

class Day22 : Solver {

    override fun solve(file: String) {

        // a class presenting on location on the map with coordinates, a symbol,
        // a list of neighbours in the four directions and
        // a list of headings in case there is a change when moving to each neighbour (part 2)
        data class Loc(val xy: XY, val c: Char,
                       var next: MutableList<XY> = MutableList(4){XY(-1,-1)},
                       var ndir: MutableList<Int> = mutableListOf(0,1,2,3)) {
            fun copy() = Loc(XY(xy.x, xy.y), c, next.toMutableList(), ndir.toMutableList())
            override fun toString() = "[$c][$xy] $next"
        }

        // a mapp alowing coordinate access to the locations
        val map = mutableMapOf<XY, Loc>()

        // a class to save current coordinates and the current heading / direction
        data class Cur(var loc: Loc, var dir: Int) {
            fun copy() = Cur(loc.copy(), dir)
        }

        // this function moves 'steps' from the location and direction given in cur
        fun go(cur:Cur, steps:Int):Cur {
            val ret = cur.copy()
            repeat(steps) {
                val tdir = ret.dir
                ret.dir = ret.loc.ndir[tdir]
                ret.loc = map[ret.loc.next[tdir]]!!
            }
            return ret
        }

        // reading input and computing some dimensions
        val data = readTxtFile(file)
        val dir = data.last()
        val mapInp = data.dropLast(2)
        val xdim = mapInp.map { it.length }.max()
        val ydim = mapInp.size

        // the start location
        var start = Loc(XY(-1,-1),'?')

        // the limits of the map in each direction at each point
        val xfrom = MutableList(ydim) {-1}
        val xto = MutableList(ydim) {-1}
        val yfrom = MutableList(xdim) {-1}
        val yto = MutableList(xdim) {-1}

        // temporary indicator helping detect when data starts and stops during the loop
        var xon: Boolean
        val yon = MutableList(xdim) {false}

        // first run through without calculation of neighbours
        mapInp.forEachIndexed() { y, ln ->
            xon = false
            ln.forEachIndexed { x, c ->
                if (c != ' ') {
                    map[XY(x,y)] = Loc(XY(x,y), c)
                    if (!xon) { xfrom[y] = x; xon = true }
                    if (!yon[x]) { yfrom[x] = y; yon[x] = true }
                    xto[y] = x
                    yto[x] = y
                    if (start.xy.x == -1 && c != '#') start = map[XY(x,y)]!!
                }
            }
        }
        yto.forEachIndexed() { ix, vl -> if (vl == -1) yto[ix] = ydim }

        // this is now setting the 4 neighbouring coordinates for each loc
        // having points point at themselves if there is a wall and wrap around
        // according to the rules of part 1
        // ----- this could be prettier in a loop with relative vectors -----
        for (loc in map.values) {
            if (loc.c == '#') continue

            var nx= loc.xy.x + 1
            if (nx > xto[loc.xy.y]) nx = xfrom[loc.xy.y]
            var next = XY(nx, loc.xy.y)
            loc.next[0] = if (map[next]!!.c != '#') map[next]!!.xy else loc.xy

            var ny= loc.xy.y + 1
            if (ny > yto[loc.xy.x]) ny = yfrom[loc.xy.x]
            next = XY(loc.xy.x, ny)
            loc.next[1] = if (map[next]!!.c != '#') map[next]!!.xy else loc.xy

            nx= loc.xy.x - 1
            if (nx < xfrom[loc.xy.y]) nx = xto[loc.xy.y]
            next = XY(nx, loc.xy.y)
            loc.next[2] = if (map[next]!!.c != '#') map[next]!!.xy else loc.xy

            ny= loc.xy.y - 1
            if (ny < yfrom[loc.xy.x]) ny = yto[loc.xy.x]
            next = XY(loc.xy.x, ny)
            loc.next[3] = if (map[next]!!.c != '#') map[next]!!.xy else loc.xy
        }

        // parsing of the directions by splitting moves and turns into two lists
        // later I use the fact that it always starts and ends with a number
        // otherwise I would have to detect that ....
        val walk = dir.split('R','L').map{it.toInt()}
        val turn = dir.toList().filter { it == 'R' || it == 'L' }

        // Part 1 is easy with the data structures I built
        var cur = Cur(start, 0)
        cur = go(cur, walk[0])
        turn.forEachIndexed { ix, c ->
            cur.dir = (cur.dir + if (c == 'L') 3 else 1) % 4
            cur = go(cur, walk[ix+1])
        }
        println("Part 1: $red$bold${1000*cur.loc.xy.y+4*cur.loc.xy.x + 1004 + cur.dir}$reset")

        // Part 2:
        // helper function to stitch together two locations by referencing each other
        // and also save the given direction changes
        // detects blocking walls preventing a crossing of the edge
        fun stitch(p1:Loc, p2:Loc, d1:Int, d2:Int) {
            if (p1.c == '#') {
                p2.next[(d2 + 2) % 4] = p2.xy
            } else if (p2.c == '#') {
                p1.next[d1] = p1.xy
            } else {
                p1.next[d1] = p2.xy
                p1.ndir[d1] = d2
                p2.next[(d2 + 2) % 4] = p1.xy
                p2.ndir[(d2 + 2) % 4] = (d1 + 2) % 4
            }
        }

        // this stitches together the 12 edges of the cube by calculating
        // the next coordinate and the new direction once there
        val ln = 50
        for (ix in 0 until ln) {
            // no stitch necessary for A0 - B2
            // no stitch necessary for A1 - C3
            stitch(map[XY(ln, ix)]!!, map[XY(0, 3*ln-1-ix)]!!, 2, 0) // A2 - D2
            stitch(map[XY(ln+ix, 0)]!!, map[XY(0, 3*ln+ix)]!!, 3, 0) // A3 - F2
            stitch(map[XY(3*ln-1, ix)]!!, map[XY(2*ln-1, 3*ln-1-ix)]!!, 0, 2) // B0 - E0
            stitch(map[XY(2*ln+ix, ln-1)]!!, map[XY(2*ln-1, ln+ix)]!!, 1, 2) // B1 - C0
            stitch(map[XY(2*ln+ix, 0)]!!, map[XY(ix, 4*ln-1)]!!, 3, 3) // B3 - F1
            // no stitch necessary for C1 - E3
            stitch(map[XY(ln, ln + ix)]!!, map[XY(ix, 2*ln)]!!, 2, 1) // C2 - D3
            // no stitch necessary for D0 - E2
            // no stitch necessary for D1 - F3
            stitch(map[XY(ln+ix, 3*ln-1)]!!, map[XY(ln-1, 3*ln+ix)]!!, 1, 2) // E1 - F0
        }

        // the actual execution is now trivial
        cur = go(Cur(start, 0), walk[0])
        turn.forEachIndexed { ix, c ->
            cur.dir = (cur.dir + if (c == 'L') 3 else 1) % 4
            cur = go(cur, walk[ix+1])
        }
        println("Part 2: $red$bold${1000*cur.loc.xy.y+4*cur.loc.xy.x + 1004 + cur.dir}$reset")
    }
}
