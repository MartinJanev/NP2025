package Aud.aud1.bank;


public class NonInterestCheckingAccount extends Account {
    public NonInterestCheckingAccount(double balance, String accountHodler) {
        super(balance, accountHodler);
    }

    @Override
    public AccountType getAccountType() {
        return AccountType.NON_INTEREST;
    }

    @Override
    public void addInterest() {
        // Do nothing, as this is a non-interest checking account
    }

}
