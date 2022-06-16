package org.maurycy.monitor.required

@kotlinx.serialization.Serializable
sealed interface State {

    fun serialize(): String
    fun deserialize(aInput: String)

}
