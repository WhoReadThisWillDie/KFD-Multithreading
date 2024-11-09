package monitor

import model.Client
import model.Transaction
import util.Observer
import worker.Cashier
import java.util.*
import java.util.concurrent.*

class Bank {
    val clients = ConcurrentHashMap<Int, Client>()
    private val cashiers = ArrayList<Cashier>()
    val exchangeRates = ConcurrentHashMap<String, Double>()

    val transactionQueue = LinkedBlockingQueue<Transaction>()
    private val observers = mutableListOf<Observer>()
    private val executor = ScheduledThreadPoolExecutor(1)

    private val logLock = Any()

    init {
        exchangeRates["USD/RUB"] = 97.0
        exchangeRates["EUR/RUB"] = 104.0
        exchangeRates["EUR/USD"] = 1.1

        executor.scheduleAtFixedRate({
            synchronized(logLock) {
                for (currency in exchangeRates.keys()) {
                    exchangeRates[currency] = getRandomExchangeRate(
                        exchangeRates[currency] ?: throw IllegalStateException("This currency does not exist")
                    )
                    notifyObservers("$currency rate changed to ${String.format("%.2f", exchangeRates[currency])}")
                }
            }
        }, 0, 10, TimeUnit.SECONDS)
    }

    private fun getRandomExchangeRate(currentRate: Double): Double {
        return currentRate * (Random().nextDouble() * 0.1 + 0.95)
    }

    fun addObserver(observer: Observer) {
        synchronized(observers) {
            observers.add(observer)
        }
    }

    fun notifyObservers(message: String) {
        synchronized(logLock) {
            observers.forEach {
                it.update(message)
            }
        }
    }

    fun addClient(client: Client) {
        clients[client.id] = client
    }

    fun addCashier(cashier: Cashier) {
        synchronized(cashiers) {
            cashiers.add(cashier)
            cashier.start()
        }
    }

    fun addTransaction(transaction: Transaction) {
        transactionQueue.add(transaction)
    }

    fun await() {
        while (!transactionQueue.isEmpty()) Thread.sleep(100)
    }

    fun stop() {
        await()

        cashiers.forEach {
            it.interrupt()
        }
        executor.shutdown()
    }
}