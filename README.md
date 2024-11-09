## Симуляция многопоточного банка

## Классы
Была использована структура классов из тз. Для определения типа транзакций были добавлены data-class'ы `Deposit`, 
`Withdraw`, `Exchange` и `Transfer`.

## Детальное пояснение
Здесь описаны детали реализации тех классов, что были дополнены относительно шаблонов, предоставленных в тз.

### [Client](src/model/Client.kt)
Хранит айди клиента и хешмап с текущим балансом клиента, где ключом является валюта, а значением - количество денег в 
представленной валюте. По умолчанию на балансе каждого клиента лежит 1000 рублей, 100 долларов и 50 евро. Класс так же
переопределяет метод `toString()` для аккуратного вывода информации о клиенте.

### [Cashier](src/worker/Cashier.kt)
Пока запущен, получает транзакции из очереди транзакций в банке. В зависимости от типа транзакции, вызывает один из 
четырёх методов `deposit()`, `withdraw()`, `transferFunds()` или `exchangeCurrency()`. При этом два последних метода
реализованы через последовательный вызов `deposit()` и `withdraw()`.

Так, например, вызов `transferFunds(1, 2, "USD", 100.0)` (клиент1 переводит клиенту2 100 долларов) означает, 
что сначала будет вызван `withdraw(1, "USD", 100.0)` (списание 100 долларов с баланса клиента1) и 
затем `deposit(2, "USD", 100.0)` (пополнение баланса клиента2 на 100 долларов). 

Каждая из четырёх операций может либо успешно выполниться, либо выбросить `IllegalArgumentException` или `IllegalStateException`,
которые будут обработаны внутри класса.

### [Bank](src/monitor/Bank.kt)
Помимо методов, представленных в тз, реализует методы для добавления клиентов, транзакций и наблюдателей в соотвествующие
структуры данных. Так же имеет методы `await()` для ожидания завершения всех транзакций и `stop()` для остановки всех 
касс и завершения обновления курса валют

## Запуск программы
Для запуска программы запустите [Main-файл](src/Main.kt), в котором уже лежит пример работы программы с указанным ожидаемым
результатом в комментариях к коду.