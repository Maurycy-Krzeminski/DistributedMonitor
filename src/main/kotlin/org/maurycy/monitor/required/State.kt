package org.maurycy.monitor.required

//@kotlinx.serialization.Serializable
abstract class State {

    abstract fun serialize(): String
    abstract fun deserialize(aInput: String)

}
