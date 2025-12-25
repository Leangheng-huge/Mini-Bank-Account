import javax.swing.*
import javax.swing.text.SimpleAttributeSet
import javax.swing.text.StyleConstants
import javax.swing.text.StyledDocument
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

    val outputArea = JTextPane()
    outputArea.isEditable = false
    val scrollPane = JScrollPane(outputArea)
    scrollPane.setPreferredSize(Dimension(450, 300))

    val createBtn = JButton("Create Account")
    val depositBtn = JButton("Deposit")
    val withdrawBtn = JButton("Withdraw")
    val showBtn = JButton("Show Balance")

    frame.add(createBtn)
    frame.add(depositBtn)
    frame.add(withdrawBtn)
    frame.add(showBtn)
    frame.add(scrollPane)

    fun appendColoredText(text: String, color: Color) {
        val doc: StyledDocument = outputArea.styledDocument
        val style = SimpleAttributeSet()
        StyleConstants.setForeground(style, color)
        doc.insertString(doc.length, "$text\n", style)
        outputArea.caretPosition = doc.length
    }

    createBtn.addActionListener {
        try {
            val typeOptions = arrayOf("SAVING", "CHECKING")
            val type = JOptionPane.showInputDialog(
                frame, "Account Type:", "Create Account",
                JOptionPane.QUESTION_MESSAGE, null, typeOptions, "SAVING"
            ) as String

            val num = JOptionPane.showInputDialog(frame, "Account Number:")
            val name = JOptionPane.showInputDialog(frame, "Owner Name:")
            val balanceInput = JOptionPane.showInputDialog(frame, "Initial Balance:")?.toDouble() ?: 0.0

            val acc = when (type.uppercase()) {
                "SAVING" -> SavingAcc(num, name, balanceInput, 0.05)
                "CHECKING" -> CheckingAcc(num, name, balanceInput, 2.0)
                else -> throw IllegalArgumentException("Invalid account type")
            }

            accounts.add(acc)
            val color = if (acc is SavingAcc) Color(0, 128, 0) else Color.BLUE // green for saving, blue for checking
            appendColoredText("Account Created: $num, Owner: $name (${type.uppercase()})", color)
        } catch (e: Exception) {
            appendColoredText("Error: ${e.message}", Color.RED)
        }
    }

    depositBtn.addActionListener {
        try {
            val num = JOptionPane.showInputDialog(frame, "Account Number:")
            val acc = accounts.find { it.accNum == num } ?: run {
                appendColoredText("Account not found", Color.RED)
                return@addActionListener
            }

            val amount = JOptionPane.showInputDialog(frame, "Deposit Amount:")?.toDouble() ?: 0.0
            acc.deposit(amount)
            val color = if (acc is SavingAcc) Color(0, 128, 0) else Color.BLUE
            appendColoredText("Deposit successful for $num", color)
        } catch (e: Exception) {
            appendColoredText("Error: ${e.message}", Color.RED)
        }
    }

    withdrawBtn.addActionListener {
        try {
            val num = JOptionPane.showInputDialog(frame, "Account Number:")
            val acc = accounts.find { it.accNum == num } ?: run {
                appendColoredText("Account not found", Color.RED)
                return@addActionListener
            }

            val amount = JOptionPane.showInputDialog(frame, "Withdraw Amount:")?.toDouble() ?: 0.0
            acc.withdraw(amount)
            val color = if (acc is SavingAcc) Color(0, 128, 0) else Color.BLUE
            appendColoredText("Withdraw successful for $num", color)
        } catch (e: Exception) {
            appendColoredText("Error: ${e.message}", Color.RED)
        }
    }

    showBtn.addActionListener {
        val num = JOptionPane.showInputDialog(frame, "Account Number:")
        val acc = accounts.find { it.accNum == num }
        if (acc == null) appendColoredText("Account not found", Color.RED)
        else {
            val color = if (acc is SavingAcc) Color(0, 128, 0) else Color.BLUE
            appendColoredText("Balance for $num: ${acc.getBalance()}", color)
        }
    }

    frame.isVisible = true
}
