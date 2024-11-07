package model

import java.util.concurrent.ConcurrentHashMap

class Client(val id: Int) {
    val balance = ConcurrentHashMap<String, Double>()

    init {
        balance["RUB"] = 100000.0
        balance["USD"] = 1000.0
        balance["EUR"] = 500.0
    }

    override fun toString(): String {
        return "Client $id $balance"
    }
}