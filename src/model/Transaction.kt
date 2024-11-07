package model

sealed class Transaction()

data class Deposit(val clientId: Int, val currency: String, val amount: Double) : Transaction()
data class Withdraw(val clientId: Int, val currency: String, val amount: Double) : Transaction()
data class Exchange(val clientId: Int, val fromCurrency: String, val toCurrency: String, val amount: Double) :
    Transaction()
data class Transfer(val senderId: Int, val receiverId: Int, val currency: String, val amount: Double) : Transaction()