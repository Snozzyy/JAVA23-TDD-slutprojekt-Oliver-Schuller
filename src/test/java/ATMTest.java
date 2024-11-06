import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ATMTest {
    Bank mockBank;
    User mockUser;
    ATM atm;

    @BeforeEach
    void setUp() {
        mockBank = mock(Bank.class);
        mockUser = mock(User.class);
        atm = new ATM(mockBank, mockUser);
    }

    @Test
    @DisplayName("Test if user exists")
    public void testUserExists() {
        when(mockBank.getUserById("123")).thenReturn(mockUser);
        assertTrue(atm.insertCard("123"), "User should exist");
        verify(mockBank).getUserById("123");
    }

    @Test
    @DisplayName("Test if user does not exist")
    public void testUserNotExist() {
        when(mockBank.getUserById("123")).thenReturn(null);
        assertFalse(atm.insertCard("123"), "User should not exist");
        verify(mockBank).getUserById("123");
    }

    @Test
    @DisplayName("Test locked card")
    public void testLockedCard() {
        when(mockBank.getUserById("123")).thenReturn(mockUser);
        when(mockUser.isLocked()).thenReturn(true);
        assertFalse(atm.insertCard("123"), "Card should be locked");
        verify(mockUser).isLocked();
    }

    @Test
    @DisplayName("Test card not locked")
    public void testCardNotLocked() {
        when(mockBank.getUserById("123")).thenReturn(mockUser);
        when(mockUser.isLocked()).thenReturn(false);
        assertTrue(atm.insertCard("123"), "Card should not be locked");
        verify(mockUser).isLocked();
    }

    @Test
    @DisplayName("Test correct pin")
    public void testCorrectPin() {
        when(mockUser.getPin()).thenReturn("1234");
        assertTrue(atm.enterPin("1234"), "Pin is correct");
        verify(mockUser).getPin();
        verify(mockUser).resetFailedAttempts();
    }

    @Test
    @DisplayName("Test incorrect pin")
    public void testIncorrectPin() {
        when(mockUser.getPin()).thenReturn("1234");
        assertFalse(atm.enterPin("4321"), "Pin is incorrect");
        verify(mockUser).getPin();
        verify(mockUser).incrementFailedAttempts();
    }

    @Test
    @DisplayName("Test PIN after 3 attempts are done")
    public void testPinAfterThreeAttempts() {
        when(mockUser.getFailedAttempts()).thenReturn(3);
        assertFalse(atm.enterPin("1234"), "Card gets locked after three attempts");
        verify(mockUser).lockCard();
    }

    @Test
    @DisplayName("Test entering wrong pin three times")
    public void testWrongPinThreeTimes() {
        when(mockUser.getPin()).thenReturn("1234");
        when(mockUser.getFailedAttempts()).thenReturn(1)
            .thenReturn(2)
            .thenReturn(3);

        atm.enterPin("4321");
        atm.enterPin("4321");
        assertFalse(atm.enterPin("4321"), "Card gets locked after three attempts");
        verify(mockUser).lockCard();
        verify(mockUser, times(3)).incrementFailedAttempts();
    }

    @Test
    @DisplayName("Test checking balance")
    public void testCheckBalance() {
        when(mockUser.getBalance()).thenReturn(100.0);
        assertEquals(100.0, atm.checkBalance(), "Balance should be 100.0");
        verify(mockUser).getBalance();
    }

    @Test
    @DisplayName("Test depositing money")
    public void testDeposit() {
        atm.deposit(100);
        verify(mockUser).deposit(100.0);
    }

    @Test
    @DisplayName("Test withdrawing money")
    public void testWithdraw() {
        when(mockUser.getBalance()).thenReturn(100.0);
        atm.withdraw(50);
        verify(mockUser).withdraw(50.0);
    }

    @Test
    @DisplayName("Test withdraw too much money")
    public void testWithdrawTooMuch() {
        when(mockUser.getBalance()).thenReturn(100.0);
        assertThrows(IllegalArgumentException.class, () -> atm.withdraw(150));
        verify(mockUser, never()).withdraw(150);
    }

    @Test
    @DisplayName("Get bank name")
    public void testGetBankName() {
        assertEquals("MockBank", Bank.getBankName());
    }
}