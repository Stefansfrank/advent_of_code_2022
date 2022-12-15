package com.sf.aoc

import kotlin.math.min
import kotlin.math.max

// A list of ranges is reduced to the minimum list of non-overlapping ranges withe same coverage
// recursively tries to merge two ranges in the list
fun reduceRngs(rngs:List<Rng>):List<Rng> {
    if (rngs.size < 2) return rngs
    for (a in 0 until rngs.size - 1) {
        for (b in a+1 until rngs.size) {
            if (rngs[a].overlap(rngs[b]) || rngs[a].adjoint(rngs[b])) {
                val ret = (0 until a).map { rngs[it] }.toMutableList()
                ret.addAll((a+1 until b).map { rngs[it] })
                ret.addAll((b+1 until rngs.size).map { rngs[it] })
                ret.add(rngs[a].mergeUnchecked(rngs[b]))
                return reduceRngs(ret)
            }
        }
    }
    return rngs
}

// a range of numbers only defined by its limits
// with some functions to help manipulating multiple ranges
// without expanding them into lists
data class Rng(val from:Int, val to:Int) {

    // do two ranges overlap?
    fun overlap(oth:Rng) = (from <= oth.to && oth.from <= to)

    // are two ranges right next to each other?
    fun adjoint(oth:Rng) = (from == oth.to + 1 || oth.from == to + 1)

    // merge two ranges into one if possible
    fun merge(oth: Rng):List<Rng> = if (overlap(oth) || adjoint(oth))
        listOf(Rng(min(from, oth.from), max(to, oth.to))) else listOf(this, oth)

    // merge two ranges into one without checking for overlap / adjoint
    fun mergeUnchecked(oth: Rng):Rng = Rng(min(from, oth.from), max(to, oth.to))

    // determine the overlap as range
    fun cross(oth: Rng):List<Rng> = if (overlap(oth))
        listOf(Rng(max(from, oth.from), min(to, oth.to))) else listOf()

    // the length of the range
    fun length() = to - from + 1
}