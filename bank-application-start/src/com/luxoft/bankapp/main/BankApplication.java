package com.luxoft.bankapp.main;

import com.luxoft.bankapp.domain.*;
import com.luxoft.bankapp.exceptions.ClientExistsException;
import com.luxoft.bankapp.exceptions.NotEnoughFundsException;
import com.luxoft.bankapp.exceptions.OverdraftLimitExceededException;
import com.luxoft.bankapp.service.BankService;
import com.luxoft.bankapp.service.EmailService;

import java.util.Scanner;
import java.util.SortedSet;

public class BankApplication {
	
	private static Bank bank;
	
	public static void main(String[] args) {
		EmailService emailService = new EmailService();
		bank = new Bank(emailService);

		if (args != null && args.length > 0 && "-statistics".equalsIgnoreCase(args[0])) {
			modifyBank();
			runStatisticsMode();
			emailService.close();

			return;
		}

		modifyBank();
		printBalance();
		BankService.printMaximumAmountToWithdraw(bank);

		emailService.close();
	}
	
	private static void modifyBank() {
		Client client1 = new Client("John", Gender.MALE);
		Account account1 = new SavingAccount(1, 100);
		Account account2 = new CheckingAccount(2, 100, 20);
		client1.addAccount(account1);
		client1.addAccount(account2);
		
		try {
		   BankService.addClient(bank, client1);
		} catch(ClientExistsException e) {
			System.out.format("Cannot add an already existing client: %s%n", client1.getName());
	    } 

		account1.deposit(100);
		try {
		  account1.withdraw(10);
		} catch (OverdraftLimitExceededException e) {
	    	System.out.format("Not enough funds for account %d, balance: %.2f, overdraft: %.2f, tried to extract amount: %.2f%n", e.getId(), e.getBalance(), e.getOverdraft(), e.getAmount());
	    } catch (NotEnoughFundsException e) {
	    	System.out.format("Not enough funds for account %d, balance: %.2f, tried to extract amount: %.2f%n", e.getId(), e.getBalance(), e.getAmount());
	    }
		
		try {
		  account2.withdraw(90);
		} catch (OverdraftLimitExceededException e) {
	      System.out.format("Not enough funds for account %d, balance: %.2f, overdraft: %.2f, tried to extract amount: %.2f%n", e.getId(), e.getBalance(), e.getOverdraft(), e.getAmount());
	    } catch (NotEnoughFundsException e) {
	      System.out.format("Not enough funds for account %d, balance: %.2f, tried to extract amount: %.2f%n", e.getId(), e.getBalance(), e.getAmount());
	    }
		
		try {
		  account2.withdraw(100);
		} catch (OverdraftLimitExceededException e) {
	      System.out.format("Not enough funds for account %d, balance: %.2f, overdraft: %.2f, tried to extract amount: %.2f%n", e.getId(), e.getBalance(), e.getOverdraft(), e.getAmount());
	    } catch (NotEnoughFundsException e) {
	      System.out.format("Not enough funds for account %d, balance: %.2f, tried to extract amount: %.2f%n", e.getId(), e.getBalance(), e.getAmount());
	    }
		
		try {
		  BankService.addClient(bank, client1);
		} catch(ClientExistsException e) {
		  System.out.format("Cannot add an already existing client: %s%n", client1);
	    } 
	}
	
	private static void printBalance() {
		System.out.format("%nPrint balance for all clients%n");
		for(Client client : bank.getClients()) {
			System.out.println("Client: " + client);
			for (Account account : client.getAccounts()) {
				System.out.format("Account %d : %.2f%n", account.getId(), account.getBalance());
			}
		}
	}

	private static void runStatisticsMode() {
		System.out.println("Statistics mode. Type 'display statistics' to show current bank statistics.");
		Scanner scanner = new Scanner(System.in);
		String line = scanner.nextLine();
		if ("display statistics".equalsIgnoreCase(line)) {
			printStatistics();
		} else {
			System.out.println("Unknown command!");
		}
	}

	private static void printStatistics() {
		BankReport report = new BankReport();

		System.out.format("%nBank statistics%n");
		System.out.println("Number of clients: " + report.getNumberOfClients(bank));
		System.out.println("Number of accounts: " + report.getNumberOfAccounts(bank));
		System.out.printf("Total sum in accounts: %.2f%n", report.getTotalSumInAccounts(bank));
		System.out.printf("Total bank credit used: %.2f%n", report.getBankCreditSum(bank));

		System.out.println("\nClients sorted:");
		SortedSet<Client> clientsSorted = report.getClientsSorted(bank);
		for (Client c : clientsSorted) {
			System.out.println("- " + c.getClientGreeting());
		}

		System.out.println("\nAccounts sorted by balance:");
		SortedSet<Account> accountsSorted = report.getAccountsSortedBySum(bank);
		for (Account a : accountsSorted) {
			System.out.printf("- Account %d: %.2f%n", a.getId(), a.getBalance());
		}
	}
}
