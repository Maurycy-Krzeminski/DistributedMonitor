package org.maurycy.monitor.required

@kotlinx.serialization.Serializable
data class Message(val id: Int, val senderNumber: Int?, val requestNumber: Int?, val token: Token?, val state: String?)
