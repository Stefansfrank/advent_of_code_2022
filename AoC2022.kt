package com.sf.aoc2022

// This class allows me to pick the day I want to work on using a command line parameter without having to
// have all Days on the machine I am working on (I work on multiple machines)
// It uses reflection which I had to add to the gradle build script

// Each Day class is written in a way that 'solve(file)' can be renamed as 'main()'
// as long as the 'file' variable within is replaced with the filename of the input
// (if there is an input file used)
import kotlin.reflect.full.createInstance

interface Solver {
    fun solve(file: String)
}

fun main(args: Array<String>) {

    val start = System.nanoTime()

    println("\nAoC 2022 - Day $yellow$bold${args[0]}$reset - File: $yellow${bold}d${args[0]}.${args[1]}.txt$reset")
    val kClass = Class.forName("com.sf.aoc2022.Day${args[0]}").kotlin
    kClass.members.filter { it.name == "solve" }[0].call( kClass.createInstance(), "data/d${args[0]}.${args[1]}.txt" )

    println("\nElapsed time: $green$bold${"%,d".format(System.nanoTime()-start)}$reset ns")
}