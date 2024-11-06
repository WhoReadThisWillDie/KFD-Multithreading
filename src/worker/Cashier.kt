package worker

import Bank

class Cashier(val id: Int, val bank: Bank) : Thread() {
    override fun run() {
        while (true) {
            val transaction = bank.transactionQueue.take()

        }
    }

    fun deposit(clientId: Int, amount: Double) {
        require(amount > 0) { "Amount must be positive" }
        val client = bank.clients[clientId] ?: throw IllegalArgumentException("Client $clientId does not exist")

        client.balance += amount
    }

    fun withdraw(clientId: Int, amount: Double) {
        require(amount > 0) { "Amount must be positive" }
        val client = bank.clients[clientId] ?: throw IllegalStateException("Client $clientId does not exist")

        client.balance -= amount
    }

    fun exchangeCurrency(clientId: Int, fromCurrency: String, toCurrency: String, amount: Double) {
        require(amount > 0) { "Amount must be positive" }

        require(
            bank.exchangeRates.contains("$fromCurrency/$toCurrency") ||
                    bank.exchangeRates.contains("$toCurrency/$fromCurrency")
        ) { "Currency pair $fromCurrency/$toCurrency does not exist" }

        val client = bank.clients[clientId] ?: throw IllegalArgumentException("Client $clientId does not exist")
        require(client.currency == fromCurrency) { "Client $clientId does not have money of the specified currency" }

        if (bank.exchangeRates.contains("$fromCurrency/$toCurrency")) {
            client.balance = amount * bank.exchangeRates["$fromCurrency/$toCurrency"]!!
        }
        else if (bank.exchangeRates.contains("$toCurrency/$fromCurrency")) {
            client.balance = amount
        }
    }

    fun transferFunds(senderId: Int, receiverId: Int, amount: Double) {
        require(amount > 0) { "Amount must be positive" }

        val sender = bank.clients[senderId] ?: throw IllegalArgumentException("Sender $senderId does not exist")
        val receiver = bank.clients[receiverId] ?: throw IllegalArgumentException("Receiver $receiverId does not exist")

        if (sender.balance < amount) {
            throw IllegalStateException("Sender with id $senderId does not have enough money")
        }

        sender.balance -= amount
        receiver.balance += amount
    }
}