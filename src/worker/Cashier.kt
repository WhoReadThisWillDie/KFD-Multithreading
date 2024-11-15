package worker

import model.Deposit
import model.Exchange
import model.Transfer
import model.Withdraw
import monitor.Bank

class Cashier(private val id: Int, private val bank: Bank) : Thread() {
    override fun run() {

        while (true) {
            try {
                when (val transaction = bank.transactionQueue.take()) {
                    is Deposit -> deposit(transaction.clientId, transaction.currency, transaction.amount)
                    is Withdraw -> withdraw(transaction.clientId, transaction.currency, transaction.amount)
                    is Exchange -> exchangeCurrency(transaction.clientId, transaction.fromCurrency, transaction.toCurrency, transaction.amount)
                    is Transfer -> transferFunds(transaction.senderId, transaction.receiverId, transaction.currency, transaction.amount)
                }
            } catch (e: IllegalArgumentException) {
                bank.notifyObservers(e.message!!)
            } catch (e: IllegalStateException) {
                bank.notifyObservers(e.message!!)
            } catch (e: InterruptedException) {
                break
            }
        }
    }

    private fun deposit(clientId: Int, currency: String, amount: Double) {
        require(amount > 0) { "Amount must be positive" }
        val client = bank.clients[clientId] ?: throw IllegalArgumentException("Client $clientId does not exist")

            client.balance.computeIfPresent(currency) { _, value -> value + amount }
                ?: throw IllegalStateException("Currency $currency does not exist")

        bank.notifyObservers("Successful deposit for client $clientId, amount: ${String.format("%.2f", amount)} $currency")
    }

    private fun withdraw(clientId: Int, currency: String, amount: Double) {
        require(amount > 0) { "Amount must be positive" }
        val client = bank.clients[clientId] ?: throw IllegalArgumentException("Client $clientId does not exist")

            client.balance.computeIfPresent(currency) { _, value ->
                if (value >= amount) value - amount
                else throw IllegalStateException("Client $clientId does not have enough money to withdraw $amount $currency")
            } ?: throw IllegalStateException("Currency $currency does not exist")

        bank.notifyObservers("Successful withdraw for client $clientId, amount: ${String.format("%.2f", amount)} $currency")
    }

    private fun exchangeCurrency(clientId: Int, fromCurrency: String, toCurrency: String, amount: Double) {
        bank.notifyObservers("Starting currency exchange for client $clientId, ${String.format("%.2f", amount)} $fromCurrency to $toCurrency")

        if (bank.exchangeRates.keys.contains("$fromCurrency/$toCurrency")) {
            withdraw(clientId, fromCurrency, amount)
            deposit(clientId, toCurrency, amount * bank.exchangeRates["$fromCurrency/$toCurrency"]!!)
        } else if (bank.exchangeRates.keys.contains("$toCurrency/$fromCurrency")) {
            withdraw(clientId, fromCurrency, amount)
            deposit(clientId, toCurrency, amount / bank.exchangeRates["$toCurrency/$fromCurrency"]!!)
        }

        bank.notifyObservers("Successful currency exchange for client $clientId, ${String.format("%.2f", amount)} $fromCurrency to $toCurrency")
    }

    private fun transferFunds(senderId: Int, receiverId: Int, currency: String, amount: Double) {
        bank.notifyObservers("Starting transfer from client $senderId client $receiverId, amount: ${String.format("%.2f", amount)} $currency")
        require(bank.clients.contains(receiverId)) { "Client $receiverId does not exist" }

        withdraw(senderId, currency, amount)
        deposit(receiverId, currency, amount)

        bank.notifyObservers("Successful transfer from client $senderId to client $receiverId, amount: ${String.format("%.2f", amount)} $currency")
    }
}
