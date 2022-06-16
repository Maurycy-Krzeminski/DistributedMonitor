package org.maurycy.monitor.required

@kotlinx.serialization.Serializable
data class Token(
    var queue: MutableList<Int> = mutableListOf(1, 2, 3),
    var LN: MutableList<Int> = mutableListOf(1, 2, 3)
) {
    fun set(token: Token) {
        this.queue = token.queue
        this.LN = token.LN

    }
}