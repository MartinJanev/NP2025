
package Aud.aud1.bank;

public class PlatinumCheckingAccount extends InterestCheckingAccount {
    public PlatinumCheckingAccount(String accountHodler, double balance) {
        super(balance, accountHodler);
    }

    @Override
    public void addInterest() {
        super.setBalance(super.getBalance() * (1 + 2 * INTEREST_RATE));
    }
}