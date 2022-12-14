package com.sf.aoc2022
import com.sf.aoc.*

class Day12 : Solver {

    override fun solve(file: String) {

        // find the best path on the given map for part 1 & 2
        fun shortestPath(start:XY, end:XY, map:MapInt, part:Int):Int {

            // Optimization Structures
            // - OptPnt is a structure class representing each point on the map with the distance from start
            //   together with helper structures for optimization (visited, coordinates of previous point)
            // - optMap is a 2d map of OptPnt structures allowing access by coordinates
            // - que is the queue of the OptPnt structures that should be looked at next
            data class OptPnt(var loc: XY, var dist: Int, var prev: OptPnt?, var vis: Boolean = false)
            val inf = map.xdim * map.ydim * 10
            val optMap = Map(map.xdim, map.ydim) { x, y -> OptPnt(XY(x,y), inf, null) }
            val first = if (part == 1) start else end // for part 2, we start from the end
            optMap.set(first, OptPnt(first,0, null, true))
            val que = ArrayDeque(mutableListOf(optMap.get(first)))
            val boundingBox = Rect(XY(0,0),XY(map.xdim - 1 , map.ydim - 1))

            // BFS algorithm
            // Since the graph is not weighted (i.e. every move to another point has the same cost)
            // BFS ensures that every point is reached with min cost when seen the first time
            while (que.size > 0) {
                val cur = que.removeFirst()
                if ((part == 1 && cur.loc == end) ||                      // part 1: stop when reaching the end point
                    (part == 2 && map.get(cur.loc) == 0)) return cur.dist // part 2: stop when finding an 'a'
                for (direction in 0..3) {
                    val nextLoc = cur.loc.mv(direction)
                    if (boundingBox.contains(nextLoc) &&     // next point is on the map
                        !optMap.get(nextLoc).vis &&          // next point not yet visited
                        ((part == 1 && map.get(nextLoc) < (map.get(cur.loc) + 2)) ||   // the climb-up limits for p1
                                (part == 2 && map.get(nextLoc) > (map.get(cur.loc) - 2)))) {  // reverse for p2
                        optMap.get(nextLoc).dist = cur.dist + 1
                        optMap.get(nextLoc).vis  = true
                        optMap.get(nextLoc).prev = cur
                        que.add(optMap.get(nextLoc))
                    }
                }

            }
            return inf
        }

        // reading input
        val data = readTxtFile(file)

        // parsing into map with Int (0..25) with 0 = 'a'
        val map = MapInt(data[0].length, data.size)
        var start = XY(0,0)
        var end = XY(0,0)
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

        // Part 1
        println("Part 1: $red$bold${shortestPath(start, end, map, 1)}$reset")

        // Part 2
        println("Part 2: $red$bold${shortestPath(start, end, map, 2)}$reset")
    }
}