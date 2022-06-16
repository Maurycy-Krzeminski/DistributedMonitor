package org.maurycy.monitor.example

import org.maurycy.monitor.required.IntList
import org.maurycy.monitor.required.Monitor


fun main() {
    val monitor = Monitor(::IntList, aId = 2, aPeers = peers)
    var sum = 0
    repeat(50) {
        monitor.run({
            data.isNotEmpty() && data[0] % 2 == 1
        }) {
            val local = data.removeFirst()
            sum += local
            println("Received value: $local")
        }
    }
    println("Sum: $sum")
    monitor.fin()
}