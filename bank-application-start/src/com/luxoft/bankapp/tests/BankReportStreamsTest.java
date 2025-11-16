package com.luxoft.bankapp.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.luxoft.bankapp.domain.*;
import com.luxoft.bankapp.exceptions.ClientExistsException;
import com.luxoft.bankapp.exceptions.OverdraftLimitExceededException;
import com.luxoft.bankapp.service.BankService;
import com.luxoft.bankapp.service.EmailService;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.*;

public class BankReportStreamsTest {

    private BankReportStreams bankReport;
    private Bank bank;
    private EmailService emailService;

    @Before
    public void setUp() {
        bankReport = new BankReportStreams();
        emailService = new EmailService();
        bank = new Bank(emailService);
    }

    @Test
    public void testGetNumberOfClients() throws ClientExistsException {
        Client client1 = new Client("John Doe", Gender.MALE);
        client1.addAccount(new SavingAccount(1, 1000.0));
        Client client2 = new Client("Jane Smith", Gender.FEMALE);
        client2.addAccount(new SavingAccount(2, 2000.0));
        Client client3 = new Client("Bob Johnson", Gender.MALE);
        client3.addAccount(new SavingAccount(3, 1500.0));

        BankService.addClient(bank, client1);
        BankService.addClient(bank, client2);
        BankService.addClient(bank, client3);

        assertEquals(3, bankReport.getNumberOfClients(bank));
    }

    @Test
    public void testGetNumberOfAccounts() throws ClientExistsException {
        Client client1 = new Client("John Doe", Gender.MALE);
        client1.addAccount(new SavingAccount(1, 1000.0));
        client1.addAccount(new CheckingAccount(2, 2000.0, 100.0));

        Client client2 = new Client("Jane Smith", Gender.FEMALE);
        client2.addAccount(new SavingAccount(3, 1500.0));

        BankService.addClient(bank, client1);
        BankService.addClient(bank, client2);

        assertEquals(3, bankReport.getNumberOfAccounts(bank));
    }

    @Test
    public void testGetClientsSorted() throws ClientExistsException {
        Client client1 = new Client("Charlie Brown", Gender.MALE);
        client1.addAccount(new SavingAccount(1, 1000.0));
        Client client2 = new Client("Alice White", Gender.FEMALE);
        client2.addAccount(new SavingAccount(2, 2000.0));
        Client client3 = new Client("Bob Green", Gender.MALE);
        client3.addAccount(new SavingAccount(3, 1500.0));

        BankService.addClient(bank, client1);
        BankService.addClient(bank, client2);
        BankService.addClient(bank, client3);

        SortedSet<Client> sorted = bankReport.getClientsSorted(bank);
        List<Client> sortedList = new ArrayList<>(sorted);

        assertEquals(3, sorted.size());
        assertEquals("Alice White", sortedList.get(0).getName());
        assertEquals("Bob Green", sortedList.get(1).getName());
        assertEquals("Charlie Brown", sortedList.get(2).getName());
    }

    @Test
    public void testGetTotalSumInAccounts() throws ClientExistsException {
        Client client1 = new Client("John Doe", Gender.MALE);
        client1.addAccount(new SavingAccount(1, 1000.0));
        client1.addAccount(new CheckingAccount(2, 2000.0, 100.0));

        Client client2 = new Client("Jane Smith", Gender.FEMALE);
        client2.addAccount(new SavingAccount(3, 1500.0));
        client2.addAccount(new CheckingAccount(4, -500.0, 100.0));

        BankService.addClient(bank, client1);
        BankService.addClient(bank, client2);

        double expected = 1000.0 + 2000.0 + 1500.0 + (-500.0);
        assertEquals(expected, bankReport.getTotalSumInAccounts(bank), 0.001);
    }

    @Test
    public void testGetAccountsSortedBySum() throws ClientExistsException {
        Client client1 = new Client("John Doe", Gender.MALE);
        SavingAccount account1 = new SavingAccount(1, 3000.0);
        CheckingAccount account2 = new CheckingAccount(2, 1000.0, 100.0);
        client1.addAccount(account1);
        client1.addAccount(account2);

        Client client2 = new Client("Jane Smith", Gender.FEMALE);
        SavingAccount account3 = new SavingAccount(3, 2000.0);
        client2.addAccount(account3);

        BankService.addClient(bank, client1);
        BankService.addClient(bank, client2);

        SortedSet<Account> sorted = bankReport.getAccountsSortedBySum(bank);
        List<Account> sortedList = new ArrayList<>(sorted);

        assertEquals(3, sorted.size());
        assertEquals(1000.0, sortedList.get(0).getBalance(), 0.001);
        assertEquals(2000.0, sortedList.get(1).getBalance(), 0.001);
        assertEquals(3000.0, sortedList.get(2).getBalance(), 0.001);
    }

    @Test
    public void testGetBankCreditSum() throws ClientExistsException, OverdraftLimitExceededException {
        Client client1 = new Client("John Doe", Gender.MALE);
        CheckingAccount account1 = new CheckingAccount(1, 1000.0, 200.0);
        account1.withdraw(1200.0); // Balance becomes -200
        client1.addAccount(account1);

        Client client2 = new Client("Jane Smith", Gender.FEMALE);
        CheckingAccount account2 = new CheckingAccount(2, 500.0, 100.0);
        account2.withdraw(550.0); // Balance becomes -50
        client2.addAccount(account2);

        BankService.addClient(bank, client1);
        BankService.addClient(bank, client2);

        double expected = 200.0 + 50.0; // Only checking accounts with negative balances
        assertEquals(expected, bankReport.getBankCreditSum(bank), 0.001);
    }

    @Test
    public void testGetCustomerAccounts() throws ClientExistsException {
        Client client1 = new Client("John Doe", Gender.MALE);
        SavingAccount account1 = new SavingAccount(1, 1000.0);
        CheckingAccount account2 = new CheckingAccount(2, 2000.0, 100.0);
        client1.addAccount(account1);
        client1.addAccount(account2);

        Client client2 = new Client("Jane Smith", Gender.FEMALE);
        SavingAccount account3 = new SavingAccount(3, 1500.0);
        client2.addAccount(account3);

        BankService.addClient(bank, client1);
        BankService.addClient(bank, client2);

        Map<Client, Collection<Account>> result = bankReport.getCustomerAccounts(bank);

        assertEquals(2, result.size());
        assertTrue(result.containsKey(client1));
        assertTrue(result.containsKey(client2));
        assertEquals(2, result.get(client1).size());
        assertEquals(1, result.get(client2).size());
        assertTrue(result.get(client1).contains(account1));
        assertTrue(result.get(client1).contains(account2));
        assertTrue(result.get(client2).contains(account3));
    }

    @Test
    public void testGetClientsByCity() throws ClientExistsException, NoSuchFieldException, IllegalAccessException {
        Client client1 = new Client("John Doe", Gender.MALE);
        client1.addAccount(new SavingAccount(1, 1000.0));
        setCity(client1, "New York");
        Client client2 = new Client("Jane Smith", Gender.FEMALE);
        client2.addAccount(new SavingAccount(2, 2000.0));
        setCity(client2, "Los Angeles");
        Client client3 = new Client("Bob Johnson", Gender.MALE);
        client3.addAccount(new SavingAccount(3, 1500.0));
        setCity(client3, "New York");

        BankService.addClient(bank, client1);
        BankService.addClient(bank, client2);
        BankService.addClient(bank, client3);

        Map<String, List<Client>> result = bankReport.getClientsByCity(bank);

        assertEquals(2, result.size());
        assertTrue(result.containsKey("Los Angeles"));
        assertTrue(result.containsKey("New York"));
        assertEquals(1, result.get("Los Angeles").size());
        assertEquals(2, result.get("New York").size());
        assertTrue(result.get("New York").contains(client1));
        assertTrue(result.get("New York").contains(client3));
        assertTrue(result.get("Los Angeles").contains(client2));
    }

    // Helper method to set city using reflection
    private void setCity(Client client, String city) throws NoSuchFieldException, IllegalAccessException {
        Field cityField = Client.class.getDeclaredField("city");
        cityField.setAccessible(true);
        cityField.set(client, city);
    }
}
