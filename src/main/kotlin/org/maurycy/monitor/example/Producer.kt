package org.maurycy.monitor.example

import org.maurycy.monitor.required.Monitor


fun main() {
    val monitor = Monitor(::IntList, aId = 0, aPeers = peers)

    (1..101).forEach {
        monitor.run({
            data.size < 5
        }) {
            println("producing: $it")
            data.add(it)
        }
    }

    monitor.fin()
}