package model

import java.util.concurrent.ConcurrentHashMap

class Client(val id: Int) {
    val balance = ConcurrentHashMap<String, Double>()

    init {
        balance["RUB"] = 1000.0
        balance["USD"] = 100.0
        balance["EUR"] = 50.0
    }

    override fun toString(): String {
        val builder = StringBuilder()
        builder.append("Client $id, balance: {\n")

        for ((key, value) in balance) {
            builder.append("\t$key: ${String.format("%.2f", value)}\n")
        }

        builder.append("}")
        return builder.toString()
    }
}