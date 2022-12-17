package com.sf.aoc2022
import com.sf.aoc.*
import kotlin.math.max

class Day16 : Solver {

    override fun solve(file: String) {

        // reading input
        val data = readTxtFile(file)

        // A connection to a cave with the distance getting there
        // In the reduced view, I only look at caves with working valves
        // thus the distance can include some caves with non-working valves
        // it also indicates whether the target cave is on the elephant's list (for part 2)
        data class Conn(val dst: Int, val cave: String, var eleph: Boolean = false)

        // A cave with a name and a flow rate for the valve
        // conn is a list of caves it connects to
        // rconn is a list of caves with working valves and the distance to get there
        data class Cave( val name: String, val flow: Int,
            val conn: List<Conn>, val rConn: MutableList<Conn> = mutableListOf()) {

            // this function goes over all connections and finds any direct connection to
            // all caves with working valves and the distance to get there this is saved in rConn
            fun reduce(map: HashMap<String, Cave>, limit: Int) {

                var que = listOf(this)
                var distance = 0
                val found = mutableMapOf(name to true)

                // work the queue of this BFS
                while(true) {
                    val newQue = mutableListOf<Cave>()
                    distance += 1
                    for (cv in que) {
                        for (nCv in cv.conn) {
                            val nextCv = map[nCv.cave]!!
                            if (nextCv.flow > 0 && !found.getOrDefault(nextCv.name, false)) {
                                this.rConn.add(Conn(distance, nextCv.name))
                                found[nextCv.name] = true
                                if (this.rConn.size == limit) return
                            }
                            newQue.add(nextCv)
                        }
                    }
                    que = newQue
                }
            }
        }

        // the map of all caves and the reduced map of only caves with working valves
        val map = HashMap<String, Cave>()
        val rMap = HashMap<String, Cave>()

        // Parsing of the input into map and filling rMap
        val rx = "Valve ([A-Z]+) has flow rate=(\\d+); tunnels? leads? to valves? ([A-Z, ]*)\$".toRegex()
        for (ln in data) {
            val mm = rx.find(ln)?.groupValues
            if (mm != null)
                map[mm[1]] = Cave(mm[1], mm[2].toInt(), mm[3].split(',').map { Conn(1, it.trim()) })
        }

        // Build a list of only caves with working valves and compute the connections to this reduced set
        map.forEach { (ix, cv) -> if (cv.flow > 0) rMap[ix] = cv }
        rMap.forEach { (_, cv) -> cv.reduce(map, rMap.size - 1) }
        map["AA"]!!.reduce(map, rMap.size)

        // The logic for finding the best path using BFS on possible paths
        data class Path(
            val cur: String,  // the name of the current location
            val psi: Int,     // the pressure already relieved (calculated for the end of the period already)
            val time: Int,    // the time already spent
            val opened: MutableMap<String, Boolean>,  // a hashmap for the names of valves already opened
            val log: String   // for debugging - it shows the caves visited
        )

        // The actual BFS algorithm operating on caves with working valves
        // it takes the available time and whether an elephant or a human is looked at
        // The latter  plays a role in part 2 where I determine which caves are visited by the elephant before pathfinding
        fun bestPath(time: Int, eleph: Boolean): Path {
            val paths = ArrayDeque(listOf(Path("AA", 0, 0, mutableMapOf("AA" to true), "AA-")))
            var bestPath = paths.first()
            while (paths.size > 0) {
                val path = paths.removeFirst()
                val curCave = map[path.cur]!!
                for (it in curCave.rConn) {
                    if (it.eleph != eleph) continue
                    val openTime = path.time + it.dst + 1
                    if (openTime < time && !path.opened.getOrDefault(it.cave, false)) {
                        val newVisited = path.opened.toMutableMap()
                        newVisited[it.cave] = true
                        val newPsi = path.psi + (time - openTime) * map[it.cave]!!.flow
                        paths.add(Path(it.cave, newPsi, openTime, newVisited, path.log + "${it.cave}-"))
                    }

                }
                if (path.psi > bestPath.psi) bestPath = path
            }
            return bestPath
        }

        // Part 1 is just calling the path finding algo
        println("Part 1: $red$bold${bestPath(30, false).psi}$reset")

        // Part 2 is solved by assuming that for the best result, the human and the elephant are handling
        // half of the working valves each. Si I loop through all permutations of splitting the working valve
        // caves in 2 equally sized list and mark them accordingly in all the rConn lists on each cave
        // I then run the pathfinding completely independent for the human and the elephant with drastically
        // complexity by ignoring all potential next caves that are not on the according list.
        val rList = rMap.map { it.value }           // all working valves in an indexed list, so they are numbered
        val elIx = mutableMapOf<String, Boolean>()  // a map that is true for all caves on the elephants list
        var bestTotal = 0

        // this is a bit crude implementation of all permutations of splitting the list of working valves into 2
        // I basically count up through an Int until 2 to the power of the amount of working valves and
        // immediately discard numbers that do not have half of the bits sets
        for (db in 0 until (1 shl rList.size)) {
            if (db.countOneBits() != rMap.size / 2) continue

            rList.forEachIndexed { ix, cv -> elIx[cv.name] = ((db and (1 shl ix)) != 0) } // creating the elephant list
            rList.forEach { cv -> cv.rConn.forEach { it.eleph = elIx[it.cave]!! } }  // mark all the elephant caves
            map["AA"]!!.rConn.forEach { it.eleph = elIx[it.cave]!! } // do that also for the target caves of AA

            val bestHumanPath = bestPath(26, false)
            val bestElephantPath = bestPath(26, true)

            bestTotal = max(bestTotal, bestHumanPath.psi + bestElephantPath.psi)
        }
        println("Part 2: $red$bold$bestTotal$reset")
    }
}