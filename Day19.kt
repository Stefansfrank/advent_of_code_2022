package com.sf.aoc2022
import com.sf.aoc.*
import kotlin.math.max


class Day19 : Solver {

    override fun solve(file: String) {

        // reading input
        val data = readTxtFile(file)
        val rx = ("Blueprint (\\d+): Each ore robot costs (\\d+) ore. " +
                "Each clay robot costs (\\d+) ore. " +
                "Each obsidian robot costs (\\d+) ore and (\\d+) clay. " +
                "Each geode robot costs (\\d+) ore and (\\d+) obsidian.").toRegex()

        // this is a class with 4 integers representing the four resources with some helper classes
        data class Res(val ore:Int, val clay:Int, val obs:Int, val geo:Int ) {

            // adding two resource vectors
            fun add(sum:Res) = Res(ore + sum.ore, clay + sum.clay, obs + sum.obs, geo + sum.geo)

            // subtracting two resource vectors
            fun sub(sum:Res) = Res(ore - sum.ore, clay - sum.clay, obs - sum.obs, geo - sum.geo)

            // detects whether this resource set is enough to source the vector given as a parameter 'obj'
            fun enough(obj:Res) = (ore >= obj.ore) && (clay >= obj.clay) && (obs >= obj.obs) && (geo >= obj.geo)

            // returns the 4 values by their index number
            fun get(r:Int) = when (r) { 0 -> ore; 1 -> clay; 2 -> obs; 3 -> geo; else -> -1 }
        }

        // represents a robot with it's type and both the cost and output vector
        data class Robot(val typ:Int, val cost:Res, val out:Res)

        // represents one blueprint with the robots that make it up and an id
        data class Blueprint(val id:Int, val robs:List<Robot>) {

            // determines the highest cost among the robots for each resource
            // this is used to avoid building more than necessary robots
            val maxNeed = (0 .. 3).map {
                robs.fold(0) { mx, rb -> max(mx,rb.cost.get(it)) + if (it == 3) 10_000 else 0}}
        }

        // the parsing of the input into these structures
        val blups = mutableListOf<Blueprint>()
        data.forEach { ln ->
            val mm = rx.find(ln)!!.groupValues
            blups.add(Blueprint(mm[1].toInt(), listOf(
                Robot(0, Res(mm[2].toInt(), 0, 0, 0), Res(1, 0, 0, 0)),
                Robot(1, Res(mm[3].toInt(), 0, 0, 0), Res(0, 1, 0, 0)),
                Robot(2, Res(mm[4].toInt(), mm[5].toInt(), 0, 0), Res(0, 0, 1, 0)),
                Robot(3, Res(mm[6].toInt(), 0, mm[7].toInt(), 0), Res(0, 0, 0, 1)))))
        }

        // ---------------------------------------------------------------------------------------------

        // a core class representing one production throughout the production run
        data class ProdLog(val blup:Blueprint,                            // the blueprint used for this production
                           var res:Res = Res(0,0,0,0),  // the current available resources
                           var out:Res = Res(1,0,0,0),  // the current output of all built robots

                           // this is an interesting optimization / pruning. The idea is that if I have the resources
                           // to build a robot of type A and don't do it, I block the ability to build a robot of
                           // that same type until I built a robot of a different type. The only reason to not build
                           // a robot if I can is saving resources for a different robot ...
                           var blk:MutableList<Boolean> = MutableList(4){false},
                           var log:String = "") {  // the log was used for debugging

            // this function determines which robots can currently be built. The criteria are:
            // - I have the resources to cover the cost
            // - This type of robot is currently not blocked (see above)
            // - I already have enough production capacity of this resource to buy any robot I want every round
            fun canBuild():List<Robot> = blup.robs.filter { res.enough(it.cost)
                    && !blk[it.typ]
                    && out.get(it.typ) < blup.maxNeed[it.typ]}

            // creates a real copy of itself
            fun copy() = ProdLog(blup, res, out, blk.toMutableList(), log)
        }

        // This is the main optimizer, a BFS algorithm with some optimization in pruning.
        // it takes a list of blueprints, optimizes them all and returns a list of the best results for each
        fun maxGeodes(bluePrints:List<Blueprint>, mins:Int):List<Int> {
            val result = mutableListOf<Int>()
            for (blup in bluePrints) {

                var prods = listOf(ProdLog(blup)) // the BFS queue - replaced after each minute
                var maxGeo = 0
                var maxObs = 0
                for (min in 1..mins) {
                    val newProds = mutableListOf<ProdLog>() // the BFS queue for the next minute
                    for (prod in prods) {
                        val builds = prod.canBuild()      // determine which robots I could and should build
                        prod.res = prod.res.add(prod.out) // actual production before new robots are added
                        if (builds.isNotEmpty()) {        // loop through the new robots to be build
                            prod.blk = MutableList(4) { false }
                            builds.forEach {
                                newProds.add(prod.copy().apply {
                                    out = prod.out.add(it.out)
                                    res = prod.res.sub(it.cost)
                                    log += "${min}|R${res.ore}/${res.clay}/${res.obs}/${res.geo}" +
                                            "|O${out.ore}/${out.clay}/${out.obs}/${out.geo}|B${it.typ}\n"
                                })
                            }
                        }
                        // this is described enough in the header of ProdLog - each type of robot I have build in
                        // a newly added production is now blocked to be built on this production run until a robot
                        // of a different type is built on this one.
                        builds.forEach { prod.blk[it.typ] = true }
                        prod.log += "${min}|R${prod.res.ore}/${prod.res.clay}/${prod.res.obs}/${prod.res.geo}" +
                                "|O${prod.out.ore}/${prod.out.clay}/${prod.out.obs}/${prod.out.geo}\n"

                        newProds.add(prod)
                        maxGeo = max(maxGeo, prod.res.geo)
                        maxObs = max(maxObs, prod.res.obs)
                    }

                    // another optimization - if there are already geodes produced, discard all solutions that
                    // are behind the best solution by 2 or more geodes - also discard solutions where obsidian
                    // lags behind
                    prods = newProds.filter { maxGeo - it.res.geo <= 2 || maxObs - it.res.obs <= 2}
                }
                result.add(maxGeo)
                println("Blueprint ${blup.id} over $mins minutes. Best result: $maxGeo geodes.")
            }
            return result
        }

        println("Part 1: $red$bold${maxGeodes(blups, 24).foldIndexed(0){ ix, sm, g -> sm + (ix+1)*g}}$reset")
        println("Part 2: $red$bold${maxGeodes(blups.subList(0,3), 32).fold(1){ p, g -> p*g}}$reset")
    }
}