import model.Client
import model.Transaction
import util.Observer
import worker.Cashier
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

class Bank {
    val clients = ConcurrentHashMap<Int, Client>()
    val cashiers = ArrayList<Cashier>()
    val exchangeRates = ConcurrentHashMap<String, Double>()

    val transactionQueue = LinkedBlockingQueue<Transaction>()
    private val observers = mutableListOf<Observer>()

    init {
        exchangeRates["USD/RUB"] = 97.0
        exchangeRates["EUR/RUB"] = 104.0
        exchangeRates["EUR/USD"] = 1.1

        val executor = ScheduledThreadPoolExecutor(1)
        executor.scheduleAtFixedRate({
            for (currency in exchangeRates.keys()) {
                exchangeRates[currency] = getRandomExchangeRate(
                    exchangeRates[currency] ?: throw IllegalStateException("This currency does not exist")
                )
            }
        }, 0, 5, TimeUnit.SECONDS)
    }

    private fun getRandomExchangeRate(currentRate: Double): Double {
        return currentRate * (Random().nextDouble() * 0.1 + 0.95)
    }

    private fun addObserver(observer: Observer) {
        observers.add(observer)
    }

    private fun notifyObservers(message: String) {
        observers.forEach {
            it.update(message)
        }
    }
}