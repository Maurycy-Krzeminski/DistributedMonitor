package org.maurycy.monitor.required

class ProcessMessages {
    fun processRequestMessage(aMessage: Message, aRN: MutableList<Int>) {
        val senderNumber = aMessage.senderNumber!!
        val requestNumber = aMessage.requestNumber!!
        aRN[senderNumber - 1] = maxOf(aRN[senderNumber - 1], requestNumber)
    }

    fun processTokenMessage(aMessage: Message, aToken: MutableList<Token?>, aState: State) {
        aMessage.state?.let {
            aState.deserialize(it)
            aMessage.token?.let { tok -> aToken.set(0, tok) }

        }
    }

}