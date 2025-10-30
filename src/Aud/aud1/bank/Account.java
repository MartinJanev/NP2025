package Aud.aud1.bank;

public abstract class Account {

    private static long ACCOUNT_NUMBER = 1L;

    private String accountHodler;
    private long accountNumber;
    private double balance;
    private AccountType accountType;

    public Account(double balance, String accountHodler) {
        this.balance = balance;
        this.accountNumber = ACCOUNT_NUMBER++;
        this.accountHodler = accountHodler;
    }

    public double deposit(double amount) {
        return balance += amount;
    }

    private double withdraw(double amount) {
        if (balance >= amount)
            balance -= amount;

        return amount;
    }

    public String getAccountHodler() {
        return accountHodler;
    }

    public void setAccountHodler(String accountHodler) {
        this.accountHodler = accountHodler;
    }

    public long getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(long accountNumber) {
        this.accountNumber = accountNumber;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public abstract AccountType getAccountType();

    @Override
    public String toString() {
        return "Account{" +
                "accountHodler='" + accountHodler + '\'' +
                ", accountNumber=" + accountNumber +
                ", balance=" + balance +
                '}';
    }

    public abstract void addInterest();
}
