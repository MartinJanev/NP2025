package Lab.One;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

class Account {
    private String name;
    private long id;
    private double balance;

    public Account(String name, double balance) {
        this.name = name;
        this.balance = balance;
        this.id = new Random().nextLong();
    }

    public String getName() {
        return name;
    }

    public long getId() {
        return id;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public double getBalance() {
        return balance;
    }

    @Override
    public String toString() {
        return String.format("Name: %s\nBalance: %.2f$", name, balance);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Account o = (Account) obj;
        return this.id == o.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

abstract class Transaction {
    private final long fromId, toId;
    private final String description;
    private final double amount;

    public Transaction(long fromId, long toId, String description, double amount) {
        this.fromId = fromId;
        this.toId = toId;
        this.description = description;
        this.amount = amount;
    }

    public double getAmount() {
        return amount;
    }

    public long getToId() {
        return toId;
    }

    public long getFromId() {
        return fromId;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fromId, toId, description, amount);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Transaction o = (Transaction) obj;
        return this.fromId == o.fromId &&
                this.toId == o.toId &&
                this.description.equals(o.description) &&
                Double.compare(this.amount, o.amount) == 0;
    }
}

class FlatAmountProvisionTransaction extends Transaction {

    private final double flatAmount;

    public FlatAmountProvisionTransaction(long fromId, long toId, double amount, double flatProvision) {
        super(fromId, toId, "FlatAmount", amount);
        this.flatAmount = flatProvision;
    }

    public double getFlatAmount() {
        return flatAmount;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        FlatAmountProvisionTransaction o = (FlatAmountProvisionTransaction) obj;
        return this.getFromId() == o.getFromId() &&
                this.getToId() == o.getToId() &&
                Double.compare(this.getAmount(), o.getAmount()) == 0 &&
                Double.compare(this.getFlatAmount(), o.getFlatAmount()) == 0;

    }

    @Override
    public int hashCode() {
        return Objects.hash(getFromId(), getToId(), getAmount(), getFlatAmount());
    }
}


class FlatPercentProvisionTransaction extends Transaction {

    private final int percent;

    public FlatPercentProvisionTransaction(long fromId, long toId, double amount, int centsPerDollar) {
        super(fromId, toId, "FlatPercent", amount);
        this.percent = centsPerDollar;
    }

    public int getPercent() {
        return percent;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        FlatPercentProvisionTransaction o = (FlatPercentProvisionTransaction) obj;
        return this.getFromId() == o.getFromId() &&
                this.getToId() == o.getToId() &&
                Double.compare(this.getAmount(), o.getAmount()) == 0 &&
                this.getPercent() == o.getPercent();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFromId(), getToId(), getAmount(), getPercent());
    }
}

class Bank {
    private String name;
    private Account[] accounts;
    private List<Transaction> transactions;
    private double totalTransfers, totalProvision;

    public Bank(String name, Account[] accounts) {
        this.name = name;
        this.accounts = Arrays.copyOf(accounts, accounts.length);
        this.transactions = new ArrayList<>();
        this.totalTransfers = 0.0;
        this.totalProvision = 0.0;
    }

    public Account[] getAccounts() {
        return accounts;
    }

    private Account findAccountById(long id) {
        return Arrays.stream(accounts).filter(acc -> acc.getId() == id).findFirst().orElse(null);
    }

    boolean makeTransaction(Transaction t) {
        Account from = findAccountById(t.getFromId());
        Account to = findAccountById(t.getToId());

        if (from == null || to == null) {
            return false;
        }

        double amount = t.getAmount();
        if (amount < 0) return false;

        double provision = 0.0;

        if (t instanceof FlatAmountProvisionTransaction) {
            provision = ((FlatAmountProvisionTransaction) t).getFlatAmount();
        } else if (t instanceof FlatPercentProvisionTransaction) {
            provision = amount * ((FlatPercentProvisionTransaction) t).getPercent() / 100.0;
        }
        double totalDebit = amount + provision;
        if (from.getBalance() < totalDebit) {
            return false;
        }
        from.setBalance(from.getBalance() - totalDebit);
        to.setBalance(to.getBalance() + amount);
        totalProvision += provision;
        totalTransfers += amount;
        transactions.add(t);

        return true;
    }

    double totalTransfers() {
        return totalTransfers;
    }

    double totalProvision() {
        return totalProvision;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Name: ").append(name).append("\n\n");
        for (Account account : accounts) {
            sb.append(account.toString()).append("\n");
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                name,
                new HashSet<>(Arrays.asList(accounts)),
                totalTransfers,
                totalProvision,
                transactions
        );
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Bank o = (Bank) obj;
        if (!this.name.equals(o.name) || this.accounts.length != o.accounts.length) return false;
        for (int i = 0; i < this.accounts.length; i++) {
            if (!this.accounts[i].equals(o.accounts[i])) {
                return false;
            }
        }
        if (Double.compare(this.totalTransfers, o.totalTransfers) != 0) return false;
        if (Double.compare(this.totalProvision, o.totalProvision) != 0) return false;
        return this.transactions.equals(o.transactions);
    }

    public void forEachConditional(Predicate<Account> predicate, Consumer<Account> consumer) {
        for (Account account : accounts) {
            if (predicate.test(account)) {
                consumer.accept(account);
            }
        }
    }
}


public class BankTester {

    public static void main(String[] args) {
        Scanner jin = new Scanner(System.in);
        String test_type = jin.nextLine();
        switch (test_type) {
            case "typical_usage":
                testTypicalUsage(jin);
                break;
            case "equals":
                testEquals();
                break;
        }
        jin.close();
    }

    private static double parseAmount(String amount) {
        return Double.parseDouble(amount.replace("$", ""));
    }

    private static void testEquals() {
        Account a1 = new Account("Andrej", 20.0);
        Account a2 = new Account("Andrej", 20.0);
        Account a3 = new Account("Andrej", 30.0);
        Account a4 = new Account("Gajduk", 20.0);
        List<Account> all = Arrays.asList(a1, a2, a3, a4);
        if (!(a1.equals(a1) && !a1.equals(a2) && !a2.equals(a1) && !a3.equals(a1)
                && !a4.equals(a1)
                && !a1.equals(null))) {
            System.out.println("Your account equals method does not work properly.");
            return;
        }
        Set<Long> ids = all.stream().map(Account::getId).collect(Collectors.toSet());
        if (ids.size() != all.size()) {
            System.out.println("Different accounts have the same IDS. This is not allowed");
            return;
        }
        FlatAmountProvisionTransaction fa1 = new FlatAmountProvisionTransaction(10, 20, 20.0, 10.0);
        FlatAmountProvisionTransaction fa2 = new FlatAmountProvisionTransaction(20, 20, 20.0, 10.0);
        FlatAmountProvisionTransaction fa3 = new FlatAmountProvisionTransaction(20, 10, 20.0, 10.0);
        FlatAmountProvisionTransaction fa4 = new FlatAmountProvisionTransaction(10, 20, 50.0, 50.0);
        FlatAmountProvisionTransaction fa5 = new FlatAmountProvisionTransaction(30, 40, 20.0, 10.0);
        FlatPercentProvisionTransaction fp1 = new FlatPercentProvisionTransaction(10, 20, 20.0, 10);
        FlatPercentProvisionTransaction fp2 = new FlatPercentProvisionTransaction(10, 20, 20.0, 10);
        FlatPercentProvisionTransaction fp3 = new FlatPercentProvisionTransaction(10, 10, 20.0, 10);
        FlatPercentProvisionTransaction fp4 = new FlatPercentProvisionTransaction(10, 20, 50.0, 10);
        FlatPercentProvisionTransaction fp5 = new FlatPercentProvisionTransaction(10, 20, 20.0, 30);
        FlatPercentProvisionTransaction fp6 = new FlatPercentProvisionTransaction(30, 40, 20.0, 10);
        if (fa1.equals(fa1) &&
                !fa2.equals(null) &&
                fa2.equals(fa1) &&
                fa1.equals(fa2) &&
                fa1.equals(fa3) &&
                !fa1.equals(fa4) &&
                !fa1.equals(fa5) &&
                !fa1.equals(fp1) &&
                fp1.equals(fp1) &&
                !fp2.equals(null) &&
                fp2.equals(fp1) &&
                fp1.equals(fp2) &&
                fp1.equals(fp3) &&
                !fp1.equals(fp4) &&
                !fp1.equals(fp5) &&
                !fp1.equals(fp6)) {
            System.out.println("Your transactions equals methods do not work properly.");
            return;
        }
        Account accounts[] = new Account[]{a1, a2, a3, a4};
        Account accounts1[] = new Account[]{a2, a1, a3, a4};
        Account accounts2[] = new Account[]{a1, a2, a3};
        Account accounts3[] = new Account[]{a1, a2, a3, a4};

        Bank b1 = new Bank("Test", accounts);
        Bank b2 = new Bank("Test", accounts1);
        Bank b3 = new Bank("Test", accounts2);
        Bank b4 = new Bank("Sample", accounts);
        Bank b5 = new Bank("Test", accounts3);

        if (!(b1.equals(b1) &&
                !b1.equals(null) &&
                !b1.equals(b2) &&
                !b2.equals(b1) &&
                !b1.equals(b3) &&
                !b3.equals(b1) &&
                !b1.equals(b4) &&
                b1.equals(b5))) {
            System.out.println("Your bank equals method do not work properly.");
            return;
        }
        accounts[2] = a1;
        if (!b1.equals(b5)) {
            System.out.println("Your bank equals method do not work properly.");
            return;
        }
        long from_id = a2.getId();
        long to_id = a3.getId();
        Transaction t = new FlatAmountProvisionTransaction(from_id, to_id, 3.0, 3.0);
        b1.makeTransaction(t);
        if (b1.equals(b5)) {
            System.out.println("Your bank equals method do not work properly.");
            return;
        }
        b5.makeTransaction(t);
        if (!b1.equals(b5)) {
            System.out.println("Your bank equals method do not work properly.");
            return;
        }
        System.out.println("All your equals methods work properly.");
    }

    private static void testTypicalUsage(Scanner jin) {
        String bank_name = jin.nextLine();
        int num_accounts = jin.nextInt();
        jin.nextLine();
        Account accounts[] = new Account[num_accounts];
        for (int i = 0; i < num_accounts; ++i)
            accounts[i] = new Account(jin.nextLine(), parseAmount(jin.nextLine()));
        Bank bank = new Bank(bank_name, accounts);

//        bank.forEachConditional(a -> a.getBalance() > 10000,a -> a.setBalance(a.getBalance() + 100));
        while (true) {
            String line = jin.nextLine();
            switch (line) {
                case "stop":
                    return;
                case "transaction":
                    String descrption = jin.nextLine();
                    double amount = parseAmount(jin.nextLine());
                    double parameter = parseAmount(jin.nextLine());
                    int from_idx = jin.nextInt();
                    int to_idx = jin.nextInt();
                    jin.nextLine();
                    Transaction t = getTransaction(descrption, from_idx, to_idx, amount, parameter, bank);
                    System.out.println("Transaction amount: " + String.format("%.2f$", t.getAmount()));
                    System.out.println("Transaction description: " + t.getDescription());
                    System.out.println("Transaction successful? " + bank.makeTransaction(t));
                    break;
                case "print":
                    System.out.println(bank.toString());
                    System.out.println("Total provisions: " + String.format("%.2f$", bank.totalProvision()));
                    System.out.println("Total transfers: " + String.format("%.2f$", bank.totalTransfers()));
                    System.out.println();
                    break;
            }
        }
    }

    private static Transaction getTransaction(String description, int from_idx, int to_idx, double amount, double o, Bank bank) {
        switch (description) {
            case "FlatAmount":
                return new FlatAmountProvisionTransaction(bank.getAccounts()[from_idx].getId(),
                        bank.getAccounts()[to_idx].getId(), amount, o);
            case "FlatPercent":
                return new FlatPercentProvisionTransaction(bank.getAccounts()[from_idx].getId(),
                        bank.getAccounts()[to_idx].getId(), amount, (int) o);
        }
        return null;
    }


}
