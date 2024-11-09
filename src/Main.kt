import model.*
import monitor.Bank
import util.Logger
import worker.Cashier

fun main() {
    val bank = Bank()

    val client1 = Client(1)
    val client2 = Client(2)
    bank.addClient(client1)
    bank.addClient(client2)

    val cashier1 = Cashier(1, bank)
    val cashier2 = Cashier(2, bank)
    bank.addCashier(cashier1)
    bank.addCashier(cashier2)
    // bank.addObserver(Logger())

    repeat(100) {
        bank.addTransaction(Deposit(1, "USD", 1.0))
        bank.addTransaction(Withdraw(1, "USD", 1.0))
    }
    bank.await()

    println(client1) // баланс не изменится - 100 USD, 50 EUR, 1000 RUB

    repeat(100) {
        bank.addTransaction(Transfer(1, 2, "USD", 1.0))
        bank.addTransaction(Transfer(2, 1, "USD", 1.0))
    }
    bank.await()

    println(client1) // баланс не изменится - 100 USD, 50 EUR, 1000 RUB
    println(client2) // баланс не изменится - 100 USD, 50 EUR, 1000 RUB

    repeat(100) {
        bank.addTransaction(Exchange(1, "USD", "EUR", 1.0))
        bank.addTransaction(Exchange(1, "USD", "RUB", 1.0))
        bank.addTransaction(Exchange(2, "USD", "RUB", 1.0))
    }
    bank.await()

    println(client1) // 0 USD, (50 + 50 / курс EUR/USD) EUR, (1000 + 50 * курс USD/RUB) RUB
    println(client2) // 0 USD, 50 EUR, (1000 + 100 * курс USD/RUB) RUB
    bank.stop()
}