package com.sf.aoc2022
import com.sf.aoc.*

class Day18 : Solver {

    override fun solve(file: String) {

        // reading input as XYZ points
        val data = readTxtFile(file).map{ xyz -> xyzFromList( xyz.split(',').map{ it.toInt() })}

        // create cube objects from these coordinates and detect the cube with the lowest x coordinate
        // (that cube's face 0 i.e. the face facing x with the lower x - coordinate is fur sure on the outside
        val cubes = mutableMapOf<XYZ, Cube>()
        var minXCube = Cube(XYZ(10000,0,0))
        for (p in data) {
            cubes[p] = Cube(p)
            if (p.x < minXCube.loc.x) minXCube = cubes[p]!!
        }

        // Part 1 - simple loop
        var total = 0
        for ((ix, p1) in data.dropLast(1).withIndex()) {
            for (p2 in data.drop(ix + 1)) {
                total += Cube(p1).sharedFaces(Cube(p2))
            }
        }
        println("Part 1: $red$bold${data.size*6 - total}$reset")

        // Part 2 - BFS traversing of the surface starting with the surface detected above
        // the adjunctFaces call will return all possible surfaces that could continue the given surface
        val que = ArrayDeque(listOf(minXCube.faces[0]))
        var surface = 0
        while (que.size > 0) {
            val face = que.removeFirst()

            // there are situation where faces are added multiple times, so I have to prune here as well
            if (face.mark) continue
            face.mark = true // visited
            surface++

            // find the next adjacent surfaces for each of the 4 edges
            // for each edge there are 3 possible continuations (run through in the inner loop)
            val nextFaces = face.adjunctFaces()
            edges@for (edge in nextFaces) {
                for (nFace in edge) {
                    if (cubes[nFace.loc] == null) continue                       // no cube there
                    if (cubes[nFace.loc]!!.faces[nFace.ix].mark) continue@edges  // visited
                    que.add(cubes[nFace.loc]!!.faces[nFace.ix])                  // add to queue
                    continue@edges                                               // and stop adding for this edge
                }
            }
        }
        println("Part 2: $red$bold${surface}$reset")
    }
}
