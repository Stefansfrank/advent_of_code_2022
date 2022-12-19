package com.sf.aoc

import kotlin.math.abs

data class XYZ(val x: Int, val y: Int, val z: Int) {
    fun add(vec:XYZ) = XYZ(x + vec.x, y + vec.y, z + vec.z)
}

fun xyzFromList(inp: List<Int>) = XYZ(inp[0],inp[1],inp[2])

// a 3D mutable list of Booleans of dimensions xDim, yDim
// used for positive coordinate systems starting at 0,0
class Mask3D(val xdim:Int, val ydim:Int, val zdim:Int, private val default:Boolean = false) {

    // the underlying mask accessible with [z][y][x] sequence
    val msk = mutableListOf<MutableList<MutableList<Boolean>>>().apply {
        repeat(zdim){ this.add(mutableListOf<MutableList<Boolean>>().apply {
            repeat(ydim) { this.add( MutableList(xdim) { default })} }) }
    }

    // simple getters and setters using XYZ as coordinates
    fun set(loc:XYZ, value:Boolean) { msk[loc.z][loc.y][loc.x] = value }
    fun get(loc:XYZ) = msk[loc.z][loc.y][loc.x]
    fun on(loc:XYZ)  = set(loc, true)
    fun off(loc:XYZ) = set(loc, false)
    fun tgl(loc:XYZ) = set(loc, !get(loc))

    // same with individual coordinates
    fun set(x:Int, y:Int, z:Int, value:Boolean) { msk[z][y][x] = value }
    fun get(x:Int, y:Int, z:Int) = msk[z][y][x]
    fun on(x:Int, y:Int, z:Int)  = set(x, y, z, true)
    fun off(x:Int, y:Int, z:Int) = set(x, y, z,false)
    fun tgl(x:Int, y:Int, z:Int) = set(x, y, z, !get(x, y, z))
}

// a class representing one face a cube
data class Face(val loc:XYZ, val ix: Int) {

    // a marker that can be used to mark a face as visited / outside / free - whatever
    var mark:Boolean = false

    // this returns four lists (for each of the edges) of three elements each
    // (for each of the three surfaces that could potentially connect at the given edge)
    // it's not pretty but involved a piece of paper and such
    fun adjunctFaces():List<List<Face>> {
        return when (ix) {
            0 -> listOf(
                listOf(Face(loc.add(XYZ(-1,0,1)), 4), Face(loc.add(XYZ(0,0,1)), 0), Face(loc, 5)),
                listOf(Face(loc.add(XYZ(-1,1,0)), 2), Face(loc.add(XYZ(0,1,0)), 0), Face(loc, 3)),
                listOf(Face(loc.add(XYZ(-1,0,-1)), 5), Face(loc.add(XYZ(0,0,-1)), 0), Face(loc, 4)),
                listOf(Face(loc.add(XYZ(-1,-1,0)), 3), Face(loc.add(XYZ(0,-1,0)), 0), Face(loc, 2)))
            1 -> listOf(
                listOf(Face(loc.add(XYZ(1,0,1)), 4), Face(loc.add(XYZ(0,0,1)), 1), Face(loc, 5)),
                listOf(Face(loc.add(XYZ(1,1,0)), 2), Face(loc.add(XYZ(0,1,0)), 1), Face(loc, 3)),
                listOf(Face(loc.add(XYZ(1,0,-1)), 5), Face(loc.add(XYZ(0,0,-1)), 1), Face(loc, 4)),
                listOf(Face(loc.add(XYZ(1,-1,0)), 3), Face(loc.add(XYZ(0,-1,0)), 1), Face(loc, 2)))
            2 -> listOf(
                listOf(Face(loc.add(XYZ(0,-1,1)), 4), Face(loc.add(XYZ(0,0,1)), 2), Face(loc, 5)),
                listOf(Face(loc.add(XYZ(1,-1,0)), 0), Face(loc.add(XYZ(1,0,0)), 2), Face(loc, 1)),
                listOf(Face(loc.add(XYZ(0,-1,-1)), 5), Face(loc.add(XYZ(0,0,-1)), 2), Face(loc, 4)),
                listOf(Face(loc.add(XYZ(-1,-1,0)), 1), Face(loc.add(XYZ(-1,0,0)), 2), Face(loc, 0)))
            3 -> listOf(
                listOf(Face(loc.add(XYZ(0,1,1)), 4), Face(loc.add(XYZ(0,0,1)), 3), Face(loc, 5)),
                listOf(Face(loc.add(XYZ(1,1,0)), 0), Face(loc.add(XYZ(1,0,0)), 3), Face(loc, 1)),
                listOf(Face(loc.add(XYZ(0,1,-1)), 5), Face(loc.add(XYZ(0,0,-1)), 3), Face(loc, 4)),
                listOf(Face(loc.add(XYZ(-1,1,0)), 1), Face(loc.add(XYZ(-1,0,0)), 3), Face(loc, 0)))
            4 -> listOf(
                listOf(Face(loc.add(XYZ(1,0,-1)), 0), Face(loc.add(XYZ(1,0,0)), 4), Face(loc, 1)),
                listOf(Face(loc.add(XYZ(0,1,-1)), 2), Face(loc.add(XYZ(0,1,0)), 4), Face(loc, 3)),
                listOf(Face(loc.add(XYZ(-1,0,-1)), 1), Face(loc.add(XYZ(-1,0,0)), 4), Face(loc, 0)),
                listOf(Face(loc.add(XYZ(0,-1,-1)), 3), Face(loc.add(XYZ(0,-1,0)), 4), Face(loc, 2)))
            5 -> listOf(
                listOf(Face(loc.add(XYZ(1,0,1)), 0), Face(loc.add(XYZ(1,0,0)), 5), Face(loc, 1)),
                listOf(Face(loc.add(XYZ(0,1,1)), 2), Face(loc.add(XYZ(0,1,0)), 5), Face(loc, 3)),
                listOf(Face(loc.add(XYZ(-1,0,1)), 1), Face(loc.add(XYZ(-1,0,0)), 5), Face(loc, 0)),
                listOf(Face(loc.add(XYZ(0,-1,1)), 3), Face(loc.add(XYZ(0,-1,0)), 5), Face(loc, 2)))
            else -> listOf(listOf())
        }
    }
}

// Face index: 0,1 in x direction 2,3 in y direction and 4,5 in z direction - lower one at lower coordinate
data class Cube(val loc:XYZ) {
    val faces = MutableList(6){ Face(loc, it)}

    // computes the amount of shared faces of two cubes (returns 2 for two neighboring cubes)
    fun sharedFaces(p:Cube):Int {
        val total = abs(loc.x-p.loc.x) + abs(loc.y - p.loc.y) + abs(loc.z - p.loc.z)
        return if (total > 1) 0 else if (total == 1) 2 else 6
    }
}


