import javax.swing.*
import java.awt.*



open class Acc(
    val accNum: String,
    val ownerName: String,
    private var balance: Double
) {
    open fun deposit(amount: Double) {
        if (amount <= 0) throw IllegalArgumentException("Deposit must be positive")
        balance += amount
    }

    open fun withdraw(amount: Double) {
        if (amount <= 0) throw IllegalArgumentException("Withdraw must be positive")
        else if (amount > balance) throw IllegalStateException("Insufficient balance")
        balance -= amount
    }

    fun getBalance(): Double = balance
}

class SavingAcc(accNum: String, ownerName: String, balance: Double, private val interestRate: Double) :
    Acc(accNum, ownerName, balance) {
    fun addInterest() {
        deposit(getBalance() * interestRate)
    }
}

class CheckingAcc(accNum: String, ownerName: String, balance: Double, private val fee: Double) :
    Acc(accNum, ownerName, balance) {
    override fun withdraw(amount: Double) {
        super.withdraw(amount + fee)
    }
}


fun main() {
    val accounts = mutableListOf<Acc>()

    val frame = JFrame("Mini Bank Account")
    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    frame.setSize(500, 500)
    frame.layout = FlowLayout()

    val outputArea = JTextArea(20, 40)
    outputArea.isEditable = false
    val scrollPane = JScrollPane(outputArea)

    val createBtn = JButton("Create Account")
    val depositBtn = JButton("Deposit")
    val withdrawBtn = JButton("Withdraw")
    val showBtn = JButton("Show Balance")

    frame.add(createBtn)
    frame.add(depositBtn)
    frame.add(withdrawBtn)
    frame.add(showBtn)
    frame.add(scrollPane)


    createBtn.addActionListener {
        try {
            val typeOptions = arrayOf("SAVING", "CHECKING")
            val type = JOptionPane.showInputDialog(frame, "Account Type:", "Create Account",
                JOptionPane.QUESTION_MESSAGE, null, typeOptions, "SAVING") as String

            val num = JOptionPane.showInputDialog(frame, "Account Number:")
            val name = JOptionPane.showInputDialog(frame, "Owner Name:")
            val balanceInput = JOptionPane.showInputDialog(frame, "Initial Balance:")?.toDouble() ?: 0.0

            val acc = when (type.uppercase()) {
                "SAVING" -> SavingAcc(num, name, balanceInput, 0.05)
                "CHECKING" -> CheckingAcc(num, name, balanceInput, 2.0)
                else -> throw IllegalArgumentException("Invalid account type")
            }

            accounts.add(acc)
            outputArea.append("Account Created: $num, Owner: $name\n")
        } catch (e: Exception) {
            outputArea.append("Error: ${e.message}\n")
        }
    }

    depositBtn.addActionListener {
        try {
            val num = JOptionPane.showInputDialog(frame, "Account Number:")
            val acc = accounts.find { it.accNum == num } ?: run {
                outputArea.append("Account not found\n")
                return@addActionListener
            }

            val amount = JOptionPane.showInputDialog(frame, "Deposit Amount:")?.toDouble() ?: 0.0
            acc.deposit(amount)
            outputArea.append("Deposit successful for $num\n")
        } catch (e: Exception) {
            outputArea.append("Error: ${e.message}\n")
        }
    }

    withdrawBtn.addActionListener {
        try {
            val num = JOptionPane.showInputDialog(frame, "Account Number:")
            val acc = accounts.find { it.accNum == num } ?: run {
                outputArea.append("Account not found\n")
                return@addActionListener
            }

            val amount = JOptionPane.showInputDialog(frame, "Withdraw Amount:")?.toDouble() ?: 0.0
            acc.withdraw(amount)
            outputArea.append("Withdraw successful for $num\n")
        } catch (e: Exception) {
            outputArea.append("Error: ${e.message}\n")
        }
    }

    showBtn.addActionListener {
        val num = JOptionPane.showInputDialog(frame, "Account Number:")
        val acc = accounts.find { it.accNum == num }
        if (acc == null) outputArea.append("Account not found\n")
        else outputArea.append("Balance for $num: ${acc.getBalance()}\n")
    }

    frame.isVisible = true
}
