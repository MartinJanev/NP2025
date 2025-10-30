package Aud.aud1.bank;

public class UltraAccount extends InterestCheckingAccount implements InterestBearingAccount {

    public UltraAccount(double balance, String accountHodler) {
        super(balance, accountHodler);
    }

    @Override
    public void addInterest() {
        super.setBalance(super.getBalance() * (1 + INTEREST_RATE * 2));
    }

    @Override
    public AccountType getAccountType() {
        return AccountType.NON_INTEREST;
    }
}
