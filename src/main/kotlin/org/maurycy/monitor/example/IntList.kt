package org.maurycy.monitor.example

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.maurycy.monitor.required.State

@kotlinx.serialization.Serializable
class IntList : State() {
    var data = mutableListOf<Int>()
    override fun serialize(): String {
        return Json.encodeToString(data)
    }

    override fun deserialize(aInput: String) {
        data = Json.decodeFromString(aInput)
    }


}