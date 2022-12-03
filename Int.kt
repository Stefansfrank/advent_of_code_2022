package com.sf.aoc

import kotlin.math.sqrt

// Sieve of Atkins computing all prime numbers up to 'limit'
fun primes(limit: Int): List<Int> {

    val primes = mutableListOf<Int>()

    if (limit > 2) primes.add(2)
    if (limit > 3) primes.add(3)

    val sieve = BooleanArray(limit){ false }
    val lm = sqrt(limit.toDouble()).toInt()
    for (x in 1..lm) {
        for (y in 1 .. lm) {
            // Atkin's rules
            var n = 4 * x * x + y * y
            if (n <= limit && (n % 12 == 1 || n % 12 == 5)) sieve[n] = sieve[n] xor true
            n = 3 * x * x + y * y
            if (n <= limit && n % 12 == 7) sieve[n] = sieve[n] xor true
            n = 3 * x * x - y * y
            if (x > y && n <= limit && n % 12 == 11) sieve[n] = sieve[n] xor true
        }
    }

    for (r in 5..lm) {
        if (sieve[r]) {
            var i = r * r
            while (i < limit) {
                sieve[i] = false
                i += r * r
            }
        }
    }

    for (a in 5 until limit) if (sieve[a]) primes.add(a)
    return primes
}

// Returns a list of the prime factors of the given number.
// Expects a table of prime numbers up to sqrt(num) or throws
// an index out of range exception!!
fun primeFac(num: Int, primes: List<Int>):List<Int> {
    val fac = mutableListOf<Int>()
    val lm = sqrt(num.toDouble()).toInt()
    var i = 0
    var tnum = num
    while(primes[i] <= lm) {
        if (tnum % primes[i++] == 0) {
            tnum /= primes[--i]
            fac.add(primes[i])
            if (tnum == 1) break
            i = 0
        }
    }
    if (tnum > 1) fac.add(tnum)
    return fac
}

// recursive function creating a number's divisors from its prime factorization
// ix, cur and divs parameters are for recursion control and can be ignored for initial call
fun divisors(fac: List<Int>, ix: Int = 0, curr: Int = 1, divs: MutableSet<Int> = mutableSetOf<Int>()):MutableSet<Int> {
    if (ix == fac.size) { divs.add(curr); return divs }
    divisors(fac, ix+1, curr * fac[ix], divs)
    divisors(fac, ix+1, curr, divs)
    return divs
}
