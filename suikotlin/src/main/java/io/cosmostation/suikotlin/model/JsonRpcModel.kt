package io.cosmostation.suikotlin.model

import java.util.*
import kotlin.math.abs

data class JsonRpcRequest(
    val method: String, val jsonrpc: String, val id: Long, val params: List<Any?>
) {
    constructor(method: String, params: List<Any?>) : this(
        method, "2.0", abs(Random().nextInt()).toLong(), params
    )
}

data class JsonRpcResponse(
    val result: Any, val jsonrpc: String, val id: Long
)