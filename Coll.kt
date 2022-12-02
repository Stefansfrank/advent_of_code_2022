package com.sf.aoc2022

// this returns all possible subsets of 'list' with length 'len'
fun <T> combinations(list: List<T>, len: Int):List<List<T>> {
    val res = mutableListOf<List<T>>()
    if (len == 1) return list.map{ listOf(it) }
    for (ix in 0..list.size - len) {
        val sub = combinations(list.drop(ix+1), len-1)
        sub.forEach { res.add(it + list[ix]) }
    }
    return res
}

// returns all permutations of a list of Int
fun <T> permutations(lst: List<T>):List<List<T>> {
    if (lst.size == 1) return listOf(listOf(lst[0]))
    val perms = mutableListOf<List<T>>()
    for (l in lst) {
        val subList = permutations(lst.filter { it != l })
        for (sl in subList) {
            perms.add(sl + l)
        }
    }
    return perms
}


