package com.sf.aoc2022
import com.sf.aoc.*

// This is a template to start new days quickly
class Day7 : Solver {

    override fun solve(file: String) {

        // class representing a file
        data class File(val name:String, val size:Int)

        // class representing a directory
        // somewhat over-engineered maintaining path and file names etc.
        // not all info is needed in order to solve the problem
        data class Dir(val name: String, val path: String, val parent: Dir?) {
            val files = mutableListOf<File>()
            val subDirs = HashMap<String, Dir>()
            var size = 0        // the total size of files directly in this directory
            var rollupSize = 0  // the total size of files in lower directories
            var total = 0       // the sum of the above

            // size increases recompute totals
            fun addSize(value: Int) {
                size  += value
                total += value
            }

            // rollupSize increases recompute totals
            fun addRollupSize(value: Int) {
                rollupSize += value
                total += value
            }

            // recursive function to rollup native sizes
            fun addUpTree(value: Int) {
                if (this.parent != null) {
                    this.parent.addRollupSize(value)
                    this.parent.addUpTree(value)
                }
            }
        }

        // reading input
        val data = readTxtFile(file)

        // central representations (tree root and flat list)
        val root = Dir("", "/", null)
        val dict = mutableListOf(root)

        // parsing the input
        var curDir  = root // current directory
        for (ln in data) {

            // take the first 4 letters to distinguish the type of line
            when (ln.take(4)) {

                // change directory
                "\$ cd"  -> curDir = when (val d = ln.substringAfter("cd ")) {
                    "/"  -> root
                    ".." -> curDir.parent!!     // this !! assumes we never issue "cd.." on root
                    else -> curDir.subDirs[d]!! // this !! assumes we only issue cd to directories already seen with ls
                }

                // ls is a NOP line in terms of understanding the system
                "\$ ls"  -> Unit

                // a subdirectory is encountered and added to current directory
                "dir "   -> {
                    val nm = ln.substringAfter(' ')
                    curDir.subDirs[nm] = Dir(nm, curDir.path + nm + "/", curDir)
                    dict.add(curDir.subDirs[nm]!!)
                }

                // a file is encountered and added to current directory
                else -> {
                    curDir.files.add(File(ln.substringAfter(' '), ln.substringBefore(' ').toInt()))
                    curDir.addSize(curDir.files.last().size)
                }
            }
        }

        // after parsing the whole tree, I rollup directory level sizes to nested sizes of the parent directories
        dict.forEach { it.addUpTree(it.size) }

        // Part 1
        val res1 = dict.filter { it.total <= 100_000 }.sumOf { it.total }
        println("Directories with less than 100,000 in size add up to $red$bold${res1}$reset")

        // Part 2
        val min  = dict[0].total - 40_000_000
        val res2 = dict.filter { it.total >= min }.minOf { it.total }
        println("The smallest directory freeing enough room has size $red$bold${res2}$reset")
    }
}
