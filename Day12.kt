package com.sf.aoc2022
import com.sf.aoc.*
import kotlin.math.min

class Day12 : Solver {

    override fun solve(file: String) {

        // find the best path from start to end on a given map
        fun shortestPath(start:XY, end:XY, map:MapInt):Int {

            // Optimization Structures
            // - Pnt is a structure representing the distance from start for every point on the map
            //   together with helper structures for optimization (visited, coordinates of previous point)
            // - opt is a 2d map allowing to access all optimization structures by coordinates
            // - que is the queue of the optimization structures that should be looked at next
            // - box is a helper for quickly ruling out points outside the map
            data class Pnt(var loc: XY, var dist: Int, var prev: Pnt?, var vis: Boolean = false)
            val inf = map.xdim * map.ydim * 10
            val opt = Map<Pnt> (map.xdim, map.ydim) { x, y -> Pnt(XY(x,y), inf, null) }
            opt.set(start, Pnt(start,0, null))
            val que = ArrayDeque(mutableListOf(opt.get(start)))
            val box = Rect(XY(0,0),XY(map.xdim - 1 , map.ydim - 1))

            // BFS algorithm
            // since the graph is not weighted (i.e. every move of q has the same cost)
            // there is no need to resort the queue (which would make this Dijkstra)
            while (que.size > 0) {
                val cur = que.removeFirst()
                cur.vis = true
                if (cur.loc == end) return cur.dist
                (0..3).forEach {
                    val nextLoc = cur.loc.mv(it)
                    if (box.contains(nextLoc) && map.get(nextLoc) < (map.get(cur.loc) + 2) && !opt.get(nextLoc).vis) {
                        if (opt.get(nextLoc).dist > cur.dist + 1) {
                            opt.get(nextLoc).dist = cur.dist + 1
                            opt.get(nextLoc).prev = cur
                            que.addLast(opt.get(nextLoc))
                        }
                    }
                }
            }
            return inf
        }

        // reading input
        val data = readTxtFile(file)

        // parsing into map with Int (0..25) with 0 = 'a'
        val map = MapInt(data[0].length, data.size)
        var start:XY = XY(0,0)
        var end:XY = XY(0,0)
        for (y in 0 until map.ydim) {
            map.setLine(y, data[y].toList().mapIndexed { x, c ->
                if (c.code - 'a'.code >= 0) c.code - 'a'.code else
                    if (c == 'S') {
                        start = XY(x, y); 0
                    } else {
                        end = XY(x, y); 25
                    }
            })
        }

        // part 1
        println("Part 1: $red$bold${shortestPath(start, end, map)}$reset")

        // Part 2
        var minPath = map.xdim * map.ydim * 10
        for (y in 0 until map.ydim) for (x in 0 until map.xdim)
            if (map.get(x,y) == 0) minPath = min(minPath, shortestPath(XY(x,y), end, map))
        println("Part 2: $red$bold${minPath}$reset")
    }
}