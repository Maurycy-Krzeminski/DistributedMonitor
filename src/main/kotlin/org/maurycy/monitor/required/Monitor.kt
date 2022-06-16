package org.maurycy.monitor.required

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class Monitor<T : State>(
    aConstructor: () -> T,
    aPeers: List<String>,
    private val aId: Int,
    private val aFinishTimeout: Long = 2000
) {
    private val numberOfProcesses = aPeers.size
    private val rn: MutableList<Int> = MutableList(numberOfProcesses) { 0 }
    private val format = Json { classDiscriminator = "#class" }
    private val token: MutableList<Token?> = MutableList(1){null}

    private var state: T = aConstructor()

    private val lock = ReentrantLock()
    private val condition = lock.newCondition()
    private val zmqCommunicator = ZmqCommunicator(address = aPeers[aId], peers = aPeers)
    private val processMessages = ProcessMessages()

    init {
        if (this.aId == 0) {
            token[0]=Token(mutableListOf(), MutableList(numberOfProcesses) { 0 })
        }

        CoroutineScope(Dispatchers.IO).launch{
            start()
        }
    }

    private suspend fun start() = coroutineScope {
        launch {
            while (true) {
                val string = zmqCommunicator.receive()
                val message = format.decodeFromString<Message>(string)
                if (message.id == 0) {
                    processMessages.processRequestMessage(message,rn)
                } else if (message.id.minus(1) == this@Monitor.aId) {
                    lock.withLock {
                        processMessages.processTokenMessage(message,token,state)
                        condition.signalAll()
                    }
                }
            }
        }
    }

    fun run(aCanTaskBeExecuted: T.() -> Boolean = { true }, aTask: T.() -> Unit) {
        var executed = false
        while (!executed) {
            if (token[0] == null) {
                Thread.sleep(1000)
                rn[this.aId]++
                zmqCommunicator.sendRequestMessage(this.aId, this.rn[this.aId])
            }
            lock.withLock {
                while (token[0] == null) {
                    condition.await()
                }

                if (state.aCanTaskBeExecuted()) {
                    state.apply(aTask)
                    executed = true
                }
                updateQueueAndTryToSendToken()
            }
        }
    }
    private fun updateQueueAndTryToSendToken() {
        token[0]!!.LN[this.aId] = rn[this.aId]

        for (i in 0 until numberOfProcesses) {
            val isIndexInQueue = token[0]!!.queue.contains(i + 1)
            val isWaitingForCriticalSection = token[0]!!.LN[i] < rn[i]
            if (!isIndexInQueue && isWaitingForCriticalSection) {
                token[0]!!.queue.add(i + 1)
            }
        }

        val processNumber = token[0]!!.queue.removeFirstOrNull()
        if (processNumber != null) {
            zmqCommunicator.sendTokenMessage(aProcessNumber = processNumber, aToken = token[0], aState = state)
            token[0] = null
        }
    }
    fun fin() {
        var remainingTimeout = aFinishTimeout
        while (token[0] != null && remainingTimeout > 0) {
            updateQueueAndTryToSendToken()
            Thread.sleep(50)
            remainingTimeout -= 50
        }
    }
}