package util

class Logger : Observer {
    override fun update(message: String) {
        println("Log: $message")
    }
}