package com.sf.aoc2022
import com.sf.aoc.*
import kotlin.math.max
import kotlin.math.min

class Day17 : Solver {

    override fun solve(file: String) {

        // reading input
        val data = readTxtFile(file)[0].toList()

        data class Rock(val maxX: Int, val default: Int, val bits:List<Int>) {
            val height = bits.size
        }

        val rocks = listOf(
            Rock(3, 1, listOf(0b0001111)),
            Rock(4, 2, listOf(0b0000010, 0b0000111, 0b0000010)),
            Rock(4, 2, listOf(0b0000111, 0b0000001, 0b0000001)),
            Rock(6, 4, listOf(0b0000001, 0b0000001, 0b0000001, 0b0000001)),
            Rock(5, 3, listOf(0b0000011, 0b0000011)),
        )

        fun caveDump(cv:List<Int>) {
            cv.forEach { println(it.toString(2).padStart(7, '0')) }
            println("-------")
        }

        data class State(val fill:Int, val hash:String)
        fun simulate(n:Int):List<State> {
            // prep the cave
            val cave = mutableListOf(0b01111111)
            cave.addAll(List(7){0b0})
            var fill = 1

            // position the first rock
            var rockTyp = 0
            var rock = rocks[0]
            var rLvl = 4
            var xPos = rock.default

            // the next jet to be used
            var jetIx = 0

            // function to detect a crash of the rock into walls or other rocks
            fun crash():Boolean {
                rock.bits.forEachIndexed() { ix, it -> if ((it shl xPos) and (cave[rLvl + ix]) > 0) return true }
                return false
            }

            // prepare the state list
            val states = mutableListOf<State>()
            val front = MutableList(7){ 0 }
            fun hash(typ:Int, jet:Int, frnt:List<Int>):String = "$jet-$typ-${frnt.fold(""){s, i -> "$s$i|" }}"

            // rem 1_000_000_000_000
            for (ix in 0 until n) {
                do {
                    // move to the side and check whether it would be beyond the limits or crash into other rock
                    val oldXPos = xPos
                    xPos = if (data[jetIx] == '<') min(rock.maxX, xPos+1) else max(0, xPos-1)
                    if (crash()) xPos = oldXPos

                    // now move down
                    rLvl -= 1

                    // increment the jet
                    jetIx = (jetIx + 1) % data.size

                    // if that move down has crashed, stop falling and move back up
                } while (!crash())
                rLvl += 1

                // compute the new fill level based on the rock height and final position
                // and ensure enough headroom to run the crash logic
                val oldFill = fill
                fill = max(fill, rLvl + rock.height)
                cave.addAll(List(fill + 7 - cave.size){0b0})

                // move the relative front line
                (0 until 7).forEach { front[it] += (fill - oldFill) }

                // actually add the rock permanently to the cave
                for ((bix,b) in rock.bits.withIndex()) {
                    val new = b shl xPos
                    cave[rLvl + bix] = cave[rLvl + bix] or new

                    // add new rock to the relative front-line
                    (0 until 7).forEach {
                        if ((1 shl it) and new > 0) {
                            front[it] = fill - rLvl - 1 - bix
                        }
                    }
                }
                states.add(State(fill-1, hash(rockTyp, jetIx, front)))

                // increment and position the next rock
                rockTyp = (rockTyp + 1) % 5
                rock = rocks[rockTyp]
                rLvl = fill + 3
                xPos = rock.default

                //debug
                //caveDump(cave)
                //println("$fill - ${front}")
            }
            return(states)
        }

        val states = simulate(500_000)
        println("Part 1: $red$bold${states[2021].fill}$reset")

        // now try to find a frequency where the front line, rock type, and jet index are the same
        // looping through potential frequencies
        freq@for (freq in 30 .. 100_000) {

            // now loop over the states and try to find the first time this happens by comparing the hash
            // the limits are chosen in a way that I'd expect the first repetition to happen within 10* frequency
            for (ix in 1 until min(states.size - freq, freq*10)) {

                // this is hit!
                if (states[ix].hash == states[ix+freq].hash) {

                    // compute the fill that is added for each repetitive period
                    val delta = states[ix+freq].fill - states[ix].fill

                    // and use integer division and modulo and such to construct the fill level
                    val test  = 1_000_000_000_000L - ix
                    var total = (test / freq) * delta
                    total += states[(test % freq).toInt() + ix - 1].fill // combines the first and last bit
                    println("Part 2: $red$bold${total}$reset")
                    return
                }
            }
        }
    }
}