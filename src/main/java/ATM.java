import java.util.Objects;

public class ATM {
    private Bank bank;
    private User currentUser;

    public ATM(Bank bank, User currentUser) {
        this.bank = bank;
        this.currentUser = currentUser;
    }

    public boolean insertCard(String userId) {
        currentUser = bank.getUserById(userId);
        return currentUser != null && !currentUser.isLocked();
    }

    public boolean enterPin(String pin) {
        if (Objects.equals(currentUser.getPin(), pin)) {
            currentUser.resetFailedAttempts();
            return true;
        } else {
            currentUser.incrementFailedAttempts();
            if (currentUser.getFailedAttempts() >= 3) {
                currentUser.lockCard();
            }
            return false;
        }
    }

    public double checkBalance() {
        return currentUser.getBalance();
    }

    public void deposit(double amount) {
        currentUser.deposit(amount);
    }

    public void withdraw(double amount) {
        if (currentUser.getBalance() >= amount) {
            currentUser.withdraw(amount);
        } else {
            throw new IllegalArgumentException("Insufficient funds");
        }
    }
}
