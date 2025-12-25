package exercise

open class Acc(
    val accNum: String,
    val ownerName: String,
    private var balance: Double
) {
    open fun deposit(amount: Double) {
        if (amount <= 0) {
            throw IllegalArgumentException("Deposit must be positive.")
        }
        balance += amount
    }

    open fun withdraw(amount: Double) {
        if (amount <= 0) {
            throw IllegalArgumentException("Withdraw must be positive.")
        } else if (amount > balance) {
            throw IllegalStateException("Insufficient balance.")
        }
        balance -= amount
    }

    fun getBalance(): Double = balance
}

class SavingAcc(
    accNum: String,
    ownerName: String,
    balance: Double,
    private val interestRate: Double
) : Acc(accNum, ownerName, balance) {
    fun addInterest() {
        deposit(getBalance() * interestRate)
    }
}

class CheckingAcc(
    accNum: String,
    ownerName: String,
    balance: Double,
    private val fee: Double
) : Acc(accNum, ownerName, balance) {
    override fun withdraw(amount: Double) {
        super.withdraw(amount + fee)
    }
}

enum class AccTypes {
    SAVING, CHECKING
}

fun main() {
    val accounts = mutableListOf<Acc>()

    while (true) {
        println("""
            1. Create Account
            2. Deposit
            3. Withdraw
            4. Show Balance
            5. Exit
        """.trimIndent())

        when (readln().toInt()) {

            1 -> {
                println("Account type (SAVING / CHECKING):")
                val type = AccTypes.valueOf(readln().uppercase())

                println("Account Number:")
                val num = readln()

                println("Owner Name:")
                val name = readln()

                println("Initial Balance:")
                val balance = readln().toDouble()

                val acc = when (type) {
                    AccTypes.SAVING -> SavingAcc(num, name, balance, 0.05)
                    AccTypes.CHECKING -> CheckingAcc(num, name, balance, 2.0)
                }

                accounts.add(acc)
                println("Account Created ✔️")
            }

            2 -> {
                println("Account Number:")
                val num = readln()
                val acc = accounts.find { it.accNum == num }

                if (acc == null) {
                    println("Account not found")
                    continue
                }

                println("Amount:")
                acc.deposit(readln().toDouble())
                println("Deposit successful")
            }

            3 -> {
                println("Account Number:")
                val num = readln()
                val acc = accounts.find { it.accNum == num }

                if (acc == null) {
                    println("Account not found")
                    continue
                }

                println("Amount:")
                acc.withdraw(readln().toDouble())
                println("Withdraw successful")
            }

            4 -> {
                println("Account Number:")
                val num = readln()
                val acc = accounts.find { it.accNum == num }

                if (acc == null) {
                    println("Account not found")
                } else {
                    println("Balance: ${acc.getBalance()}")
                }
            }

            5 -> {
                println("Goodbye 👋")
                break
            }
        }
    }
}
