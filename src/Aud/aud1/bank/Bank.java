package Aud.aud1.bank;

import java.util.ArrayList;
import java.util.List;

public class Bank {


    // final - we use final to make sure that the reference to the list cannot be changed
    // vo web se koristi sekogas, za da se osigura integrity na podatocite

    private List<Account> accounts;

    public Bank(List<Account> accounts) {
        accounts = new ArrayList<>();
    }

    public void addAccount(Account account) {
        accounts.add(account);
    }

    public double totalAssets() {
//        double total = 0;
//        for (Account account : accounts) {
//            total += account.getBalance();
//        }
//        return total;

        return accounts.stream().mapToDouble(Account::getBalance).sum();
        // return accounts.stream().mapToDouble(account -> account.getBalance()).sum();
    }

    public void addInterest() {
        for (Account account : accounts) {
            if (account.getAccountType().equals(AccountType.INTEREST)) {
                ((InterestBearingAccount) account).addInterest();
            }
        }
    }
}
