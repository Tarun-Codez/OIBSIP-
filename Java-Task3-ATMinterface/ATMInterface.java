import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ATMInterface {
	private static final Scanner SCANNER = new Scanner(System.in);
	private static final BankService BANK_SERVICE = DemoData.createService();
	private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	public static void main(String[] args) {
		new ATMInterface().run();
	}

	private void run() {
		printWelcome();

		while (true) {
			BankAccount currentAccount = login();
			if (currentAccount == null) {
				System.out.println("Goodbye!");
				return;
			}

			boolean loggedIn = true;
			while (loggedIn) {
				printMenu(currentAccount);
				int choice = readInt("Select an option: ");

				switch (choice) {
					case 1:
						showTransactions(currentAccount);
						break;
					case 2:
						withdraw(currentAccount);
						break;
					case 3:
						deposit(currentAccount);
						break;
					case 4:
						transfer(currentAccount);
						break;
					case 5:
						System.out.println("Session ended. Returning to login screen...\n");
						loggedIn = false;
						break;
					default:
						System.out.println("Invalid option. Please choose between 1 and 5.");
				}
			}
		}
	}

	private void printWelcome() {
		System.out.println("=====================================");
		System.out.println("         WELCOME TO ATM DEMO         ");
		System.out.println("=====================================");
		System.out.println("Demo credentials:");
		System.out.println("  User ID: 123456");
		System.out.println("  PIN    : 1234");
		System.out.println("  Transfer target account: 654321");
		System.out.println();
	}

	private BankAccount login() {
		while (true) {
			System.out.print("Enter user id (or type 'exit' to quit): ");
			String userId = SCANNER.nextLine().trim();
			if (userId.equalsIgnoreCase("exit")) {
				return null;
			}

			System.out.print("Enter PIN: ");
			String pin = SCANNER.nextLine().trim();

			BankAccount account = BANK_SERVICE.authenticate(userId, pin);
			if (account != null) {
				System.out.println("\nLogin successful. Welcome, " + account.getHolderName() + "!\n");
				return account;
			}

			System.out.println("Invalid user id or PIN. Please try again.\n");
		}
	}

	private void printMenu(BankAccount account) {
		System.out.println("-------------------------------------");
		System.out.println("Account : " + account.getAccountId() + "  |  Holder: " + account.getHolderName());
		System.out.println("Balance : $" + formatAmount(account.getBalance()));
		System.out.println("-------------------------------------");
		System.out.println("1. Transactions History");
		System.out.println("2. Withdraw");
		System.out.println("3. Deposit");
		System.out.println("4. Transfer");
		System.out.println("5. Quit");
	}

	private void showTransactions(BankAccount account) {
		List<Transaction> history = account.getTransactions();
		if (history.isEmpty()) {
			System.out.println("No transactions yet.\n");
			return;
		}

		System.out.println("\nTransaction History:");
		for (Transaction transaction : history) {
			System.out.println(transaction.format(TIME_FORMATTER));
		}
		System.out.println();
	}

	private void withdraw(BankAccount account) {
		double amount = readAmount("Enter amount to withdraw: $");
		if (amount <= 0) {
			System.out.println("Amount must be greater than zero.\n");
			return;
		}

		if (!BANK_SERVICE.withdraw(account, amount)) {
			System.out.println("Insufficient balance.\n");
			return;
		}

		System.out.println("Withdrawal successful. New balance: $" + formatAmount(account.getBalance()) + "\n");
	}

	private void deposit(BankAccount account) {
		double amount = readAmount("Enter amount to deposit: $");
		if (amount <= 0) {
			System.out.println("Amount must be greater than zero.\n");
			return;
		}

		BANK_SERVICE.deposit(account, amount);
		System.out.println("Deposit successful. New balance: $" + formatAmount(account.getBalance()) + "\n");
	}

	private void transfer(BankAccount fromAccount) {
		System.out.print("Enter recipient account number: ");
		String targetAccountId = SCANNER.nextLine().trim();

		if (targetAccountId.isEmpty()) {
			System.out.println("Recipient account number cannot be empty.\n");
			return;
		}

		if (targetAccountId.equals(fromAccount.getAccountId())) {
			System.out.println("You cannot transfer money to the same account.\n");
			return;
		}

		double amount = readAmount("Enter amount to transfer: $");
		if (amount <= 0) {
			System.out.println("Amount must be greater than zero.\n");
			return;
		}

		BankAccount recipient = BANK_SERVICE.findAccount(targetAccountId);
		if (recipient == null) {
			System.out.println("Recipient account not found.\n");
			return;
		}

		if (!BANK_SERVICE.transfer(fromAccount, recipient, amount)) {
			System.out.println("Insufficient balance.\n");
			return;
		}

		System.out.println("Transfer successful. New balance: $" + formatAmount(fromAccount.getBalance()) + "\n");
	}

	private int readInt(String prompt) {
		while (true) {
			System.out.print(prompt);
			String value = SCANNER.nextLine().trim();
			try {
				return Integer.parseInt(value);
			} catch (NumberFormatException ex) {
				System.out.println("Please enter a valid number.");
			}
		}
	}

	private double readAmount(String prompt) {
		while (true) {
			System.out.print(prompt);
			String value = SCANNER.nextLine().trim();
			try {
				return Double.parseDouble(value);
			} catch (NumberFormatException ex) {
				System.out.println("Please enter a valid amount.");
			}
		}
	}

	private String formatAmount(double amount) {
		return String.format("%.2f", amount);
	}
}

class BankService {
	private final Map<String, BankAccount> accounts = new HashMap<>();

	BankService(List<BankAccount> seedAccounts) {
		for (BankAccount account : seedAccounts) {
			accounts.put(account.getAccountId(), account);
		}
	}

	BankAccount authenticate(String accountId, String pin) {
		BankAccount account = accounts.get(accountId);
		if (account != null && account.getPin().equals(pin)) {
			return account;
		}
		return null;
	}

	BankAccount findAccount(String accountId) {
		return accounts.get(accountId);
	}

	boolean withdraw(BankAccount account, double amount) {
		if (account.getBalance() < amount) {
			return false;
		}

		account.adjustBalance(-amount);
		account.addTransaction(new Transaction(
				TransactionType.WITHDRAWAL,
				amount,
				account.getBalance(),
				"Cash withdrawal"));
		return true;
	}

	void deposit(BankAccount account, double amount) {
		account.adjustBalance(amount);
		account.addTransaction(new Transaction(
				TransactionType.DEPOSIT,
				amount,
				account.getBalance(),
				"Cash deposit"));
	}

	boolean transfer(BankAccount from, BankAccount to, double amount) {
		if (from.getBalance() < amount) {
			return false;
		}

		from.adjustBalance(-amount);
		to.adjustBalance(amount);

		from.addTransaction(new Transaction(
				TransactionType.TRANSFER_OUT,
				amount,
				from.getBalance(),
				"Transferred to account " + to.getAccountId()));
		to.addTransaction(new Transaction(
				TransactionType.TRANSFER_IN,
				amount,
				to.getBalance(),
				"Received from account " + from.getAccountId()));
		return true;
	}
}

class BankAccount {
	private final String accountId;
	private final String pin;
	private final String holderName;
	private double balance;
	private final List<Transaction> transactions = new ArrayList<>();

	BankAccount(String accountId, String pin, String holderName, double balance) {
		this.accountId = accountId;
		this.pin = pin;
		this.holderName = holderName;
		this.balance = balance;
		transactions.add(new Transaction(
				TransactionType.ACCOUNT_OPENED,
				balance,
				balance,
				"Account opened with starting balance"));
	}

	String getAccountId() {
		return accountId;
	}

	String getPin() {
		return pin;
	}

	String getHolderName() {
		return holderName;
	}

	double getBalance() {
		return balance;
	}

	List<Transaction> getTransactions() {
		return transactions;
	}

	void adjustBalance(double amount) {
		balance += amount;
	}

	void addTransaction(Transaction transaction) {
		transactions.add(transaction);
	}
}

class Transaction {
	private final TransactionType type;
	private final double amount;
	private final double balanceAfter;
	private final String description;
	private final LocalDateTime timestamp;

	Transaction(TransactionType type, double amount, double balanceAfter, String description) {
		this.type = type;
		this.amount = amount;
		this.balanceAfter = balanceAfter;
		this.description = description;
		this.timestamp = LocalDateTime.now();
	}

	String format(DateTimeFormatter formatter) {
		return String.format("[%s] %-14s $%8.2f | Balance: $%8.2f | %s",
				timestamp.format(formatter),
				type.getDisplayName(),
				amount,
				balanceAfter,
				description);
	}
}

enum TransactionType {
	ACCOUNT_OPENED("Account Opened"),
	DEPOSIT("Deposit"),
	WITHDRAWAL("Withdrawal"),
	TRANSFER_IN("Transfer In"),
	TRANSFER_OUT("Transfer Out");

	private final String displayName;

	TransactionType(String displayName) {
		this.displayName = displayName;
	}

	String getDisplayName() {
		return displayName;
	}
}

class DemoData {
	private DemoData() {
	}

	static BankService createService() {
		List<BankAccount> accounts = new ArrayList<>();
		accounts.add(new BankAccount("123456", "1234", "Demo User", 5000.00));
		accounts.add(new BankAccount("654321", "4321", "Savings Receiver", 2500.00));
		accounts.add(new BankAccount("111222", "1111", "Secondary User", 1000.00));
		return new BankService(accounts);
	}
}
