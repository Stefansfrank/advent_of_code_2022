package com.sf.aoc

// a double linked list with a Long value
data class Link(val value:Long, var next:Link?, var prev:Link?) {

    // take this element out of the chain (leaving its own prev/next untouched)
    fun takeOut() {
        next?.prev = prev
        prev?.next = next
    }

    // inserts this after the given link
    fun insert(after:Link?) {
        val before = after?.next
        before?.prev = this
        after?.next = this
        this.prev = after
        this.next = before
    }

    // moves the Link forward in the chain
    fun moveFw(n:Int) { this.takeOut() ; this.insert(this.atFw(n)) }

    // moves the Link backwards in the chain
    fun moveBw(n:Int) { this.takeOut() ; this.insert(this.atBw(n).prev) }

    // returns the item n links ahead in the chain
    fun atFw(n:Int):Link {
        var ret:Link = this
        repeat(n){ ret = ret.next ?: ret }
        return ret
    }

    // returns the item n links behind in the chain
    fun atBw(n:Int):Link {
        var ret = this
        repeat(n){ ret = ret.prev ?: ret }
        return ret
    }

    // overriding toString since it runs into stack overflow for a cyclic list
    override fun toString(): String {
        return "[value: $value ,Prev:${prev?.value} ,Next:${next?.value} ]"
    }
}
