package com.sf.aoc2022
import com.sf.aoc.*
import kotlin.math.abs
import kotlin.math.log10
import kotlin.math.sign

class Day21 : Solver {

    override fun solve(file: String) {

        // reading input
        val data = readTxtFile(file)

        // I wanted to understand class inheritance in Kotlin
        // thus I created a bunch of classes describing monkeys
        abstract class Monkey (val typ:Boolean) {
            abstract fun eval():Long
            open fun setVal(n: Long) {}
            open fun get1(): String = ""
            open fun get2(): String = ""
        }

        abstract class Formula(open val op1:String, open val op2:String,
                               open val dir:Map<String, Monkey>):Monkey(false) {
            override fun get1():String = op1
            override fun get2():String = op2
        }

        class Val(var value:Long):Monkey(true) {
            override fun eval() = value
            override fun setVal(n:Long) { value = n }
        }

        class Add(override val op1:String, override val op2:String,
                  override val dir:Map<String, Monkey>):Formula(op1, op2, dir) {
            override fun eval() = dir[op1]!!.eval() + dir[op2]!!.eval()
        }

        class Sub(override val op1:String, override val op2:String,
                  override val dir:Map<String, Monkey>):Formula(op1, op2, dir) {
            override fun eval() = dir[op1]!!.eval() - dir[op2]!!.eval()
        }

        class Mul(override val op1:String, override val op2:String,
                  override val dir:Map<String, Monkey>):Formula(op1, op2, dir) {
            override fun eval() = dir[op1]!!.eval() * dir[op2]!!.eval()
        }

        class Div(override val op1:String, override val op2:String,
                  override val dir:Map<String, Monkey>):Formula(op1, op2, dir) {
            override fun eval() = dir[op1]!!.eval() / dir[op2]!!.eval()
        }

        // Parse the input into these classes
        val rx = "([a-z]+): ([a-z]+) ([+\\-*/]) ([a-z]+)$".toRegex()
        val dir = mutableMapOf<String, Monkey>()
        for (ln in data) {
            val mm = rx.find(ln)?.groupValues
            if (mm != null) {
                when (mm[3]) {
                    "+" -> dir[mm[1]] = Add(mm[2], mm[4], dir)
                    "-" -> dir[mm[1]] = Sub(mm[2], mm[4], dir)
                    "*" -> dir[mm[1]] = Mul(mm[2], mm[4], dir)
                    "/" -> dir[mm[1]] = Div(mm[2], mm[4], dir)
                }
           } else dir[ln.substringBefore(':')] =
                    Val(ln.substringAfter(':').trim().toLong())
        }

        // Part1: the recursive eval is super simple
        println("Part 1: $red$bold${dir["root"]!!.eval().toLong()}$reset")

        // Part 2
        // helper computes the difference between the two operands of "root" with n as "humn"
        fun diff(n:Long):Long {
            dir["humn"]!!.setVal(n)
            return dir[dir["root"]!!.get1()]!!.eval() -
                    dir[dir["root"]!!.get2()]!!.eval()
        }

        // the sign of the original difference between the two operands of "root"
        val baseSign = diff(0L).sign

        // detects whether the guesses should be positive or negative
        // building on monotone dependence between the difference and the "humn" value
        var guess = (abs(diff(0L)) - abs(diff(10L))).sign * 10L

        // detect the amount of digits needed to flip the sign and sets the starting guess
        while (diff(guess).sign == baseSign) guess *= 10
        var sum = guess
        guess /= 10
        val digits = log10(guess.toDouble()).toInt()

        // now try to count up each digit starting with the highest until the sign flips
        // there will be multiple solution due to integer division and I want the smallest
        var solution = 0L
        dig@for (d in digits downTo 0) {
            sum /= 10
            for (s in 1 .. 9) {
                guess += sum
                val nSig = diff(guess).sign
                if (nSig != baseSign) {
                    if (nSig == 0) solution = guess
                    guess -= sum
                    continue@dig
                }
            }
        }
        println("Part 2: $red$bold${solution}$reset")
    }
}
