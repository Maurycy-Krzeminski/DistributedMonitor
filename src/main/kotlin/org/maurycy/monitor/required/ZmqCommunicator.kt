package org.maurycy.monitor.required

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.zeromq.SocketType
import org.zeromq.ZMQ

class ZmqCommunicator(
    address: String,
    peers: List<String>
) {
    private val format = Json { classDiscriminator = "#class" }
    private val pubSocket: ZMQ.Socket = ZMQ.context(1).socket(SocketType.PUB)
    private val subSocket: ZMQ.Socket = ZMQ.context(1).socket(SocketType.SUB)

    init {
        this.subSocket.subscribe("".toByteArray())
        this.pubSocket.bind(address)
        this.subSocket.conflate = true
        peers.forEach { it ->
            if (it != address) {
                subSocket.connect(it)
            }
        }
    }

    fun receive(): String = subSocket.recvStr()
    fun sendRequestMessage(aSenderNumber: Int, aRN: Int) {
        pubSocket.send(format.encodeToString(Message(id = 0, aSenderNumber + 1, aRN, token = null, state = null)))
    }

    fun sendTokenMessage(aToken: Token?, aProcessNumber: Int, aState: State) {
        aToken?.let {
            pubSocket.send(composeTokenMessage(aToken, aProcessNumber, aState))
        }
    }

    private fun composeTokenMessage(aToken: Token, aProcessNumber: Int, aState: State): String {
        println(
            "composeTokenMessage" + format.encodeToString(
                Message(
                    id = aProcessNumber,
                    senderNumber = null,
                    requestNumber = null,
                    token = aToken,
                    state = aState.serialize()
                )
            )
        )
        return format.encodeToString(
            Message(
                id = aProcessNumber,
                senderNumber = null,
                requestNumber = null,
                token = aToken,
                state = aState.serialize()
            )
        )
    }

}