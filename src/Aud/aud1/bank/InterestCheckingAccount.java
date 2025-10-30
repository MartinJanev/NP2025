package Aud.aud1.bank;

public class InterestCheckingAccount extends Account implements InterestBearingAccount {

    protected static double INTEREST_RATE = 0.03;

    public InterestCheckingAccount(double balance, String accountHodler) {
        super(balance, accountHodler);
    }

    @Override
    public AccountType getAccountType() {
        return AccountType.INTEREST;
    }

    @Override
    public void addInterest() {
        super.setBalance(super.getBalance() * (1 + INTEREST_RATE));
    }
}
