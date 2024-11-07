import model.*
import monitor.Bank
import util.Logger
import worker.Cashier

fun main() {
    val bank = Bank()

    bank.addObserver(Logger())

    bank.addClient(Client(1))
    bank.addClient(Client(2))
    bank.addClient(Client(3))

    bank.addCashier(Cashier(1, bank))
    bank.addCashier(Cashier(2, bank))

    bank.addTransaction(Deposit(1, "RUB", 100.0))
    bank.addTransaction(Withdraw(2, "USD", 10.0))
    bank.addTransaction(Transfer(1, 2, "RUB", 20000.0))
    bank.addTransaction(Exchange(3, "EUR", "RUB", 50.0))


    Thread.sleep(1000)


    Thread.sleep(500)

    bank.clients.forEach {
        println(it.value)
    }
}