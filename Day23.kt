package com.sf.aoc2022
import com.sf.aoc.*

class Day23 : Solver {

    override fun solve(file: String) {

        // the elfs
        val elfs = mutableSetOf<XY>()

        // this is the rotating que of directions
        val dirs = ArrayDeque(listOf(0, 2, 3, 1))
        val dirCheck = listOf(listOf(7, 0, 1), listOf(1, 2, 3), listOf(3, 4, 5), listOf(5, 6, 7))

        // reading input
        val data = readTxtFile(file)
        data.forEachIndexed() { y, ln -> ln.forEachIndexed { x, c -> if (c == '#') elfs.add(XY(x,y)) } }

        // creates a proposal (or null)
        fun prop(elf:XY):XY? {
            val ping = elf.neighbors(true).map { elfs.contains(it) }
            if (!ping.contains(true)) return null // no elves around
            dir@for (dir in dirs) {
                for (cDir in dirCheck[dir]) if (ping[cDir]) continue@dir
                return elf.mv(dir) // this direction is free
            }
            return null // no free direction
        }

        // moves an elf
        fun move(from:XY, to:XY):Boolean {
            elfs.remove(from)
            elfs.add(to)
            return true
        }

        // on tick in time - returns whether a change happened
        fun tick():Boolean {

            // proposal stage
            data class Prop(var valid:Boolean, val elf:XY)
            val props = mutableMapOf<XY, Prop>()
            elfs.forEach {
                val prop = prop(it)
                if (prop != null) {
                    if (props[prop] != null) {
                        props[prop]!!.valid = false
                    } else {
                        props[prop] = Prop(true, it)
                    }
                }
            }

            // the move
            var moved = false
            props.forEach { (ploc, prop) -> if (prop.valid) moved = move(prop.elf, ploc) }

            // rotate global direction sequence
            dirs.add(dirs.removeFirst())
            return moved
        }

        // gets the bounding box of elves
        fun getBox():Rect {
            val elX = elfs.map { it.x }
            val elY = elfs.map { it.y }
            return Rect(XY(elX.min(), elY.min()), XY(elX.max(), elY.max()))
        }

        // counts the empty space
        fun count() = getBox().size() - elfs.size

        // Part 1
        repeat(10) { tick() }
        println("Part 1: $red$bold${count()}$reset")

        // Part 2 (continuing)
        var cnt = 10
        while (tick()) cnt++
        println("Part 2: $red$bold${cnt+1}$reset")
    }
}
