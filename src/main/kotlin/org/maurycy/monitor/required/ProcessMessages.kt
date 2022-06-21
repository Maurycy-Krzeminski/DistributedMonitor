package org.maurycy.monitor.required

import java.util.concurrent.ArrayBlockingQueue

class ProcessMessages {
    fun processRequestMessage(aMessage: Message, aRN: MutableList<Int>) {
        val senderNumber = aMessage.senderNumber!!
        val requestNumber = aMessage.requestNumber!!
        aRN[senderNumber - 1] = maxOf(aRN[senderNumber - 1], requestNumber)
    }

    fun processTokenMessage(aMessage: Message, aToken: ArrayBlockingQueue<Token>, aState: State) {
        aMessage.state?.let {
            aState.deserialize(it)
            aMessage.token?.let { tok ->
                aToken.clear()
                aToken.add(tok)
            }

        }
    }

}