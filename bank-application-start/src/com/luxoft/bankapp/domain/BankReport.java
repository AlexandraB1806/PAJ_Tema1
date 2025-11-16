package com.luxoft.bankapp.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

public class BankReport implements BankReportInterface {
    @Override
    public int getNumberOfClients(Bank bank) {
        return bank.getClients().size();
    }

    public int getNumberOfAccounts(Bank bank) {
        int total = 0;

        for (Client client : bank.getClients()) {
            total += client.getAccounts().size();
        }

        return total;
    }

    @Override
    public SortedSet<Client> getClientsSorted(Bank bank) {
        Comparator<Client> byNameThenGender = Comparator
                .comparing(Client::getName, Comparator.nullsFirst(Comparator.naturalOrder()))
                .thenComparing(Client::getGender, Comparator.nullsFirst(Comparator.naturalOrder()));

        SortedSet<Client> sorted = new TreeSet<>(byNameThenGender);
        sorted.addAll(bank.getClients());

        return sorted;
    }

    @Override
    public double getTotalSumInAccounts(Bank bank) {
        double totalSum = 0;

        for (Client client : bank.getClients()) {
            for (Account account : client.getAccounts()) {
                totalSum += account.getBalance();
            }
        }

        return totalSum;
    }

    @Override
    public SortedSet<Account> getAccountsSortedBySum(Bank bank) {
        Comparator<Account> byBalance = Comparator.comparing(Account::getBalance);
        SortedSet<Account> sorted = new TreeSet<>(byBalance);

        Set<Account> allAccounts = new HashSet<>();
        for (Client client : bank.getClients()) {
            allAccounts.addAll(client.getAccounts());
        }

        sorted.addAll(allAccounts);

        return sorted;
    }

    @Override
    public double getBankCreditSum(Bank bank) {
        double total = 0;
        double balance;

        for (Client client : bank.getClients()) {
            for (Account account : client.getAccounts()) {
                if (account instanceof CheckingAccount) {
                    balance = account.getBalance();
                    if (balance < 0) {
                        total += Math.abs(balance);
                    }
                }
            }
        }

        return total;
    }

    @Override
    public Map<Client, Collection<Account>> getCustomerAccounts(Bank bank) {
        Map<Client, Collection<Account>> result = new HashMap<>();

        for (Client client : bank.getClients()) {
            result.put(client, client.getAccounts());
        }

        return result;
    }

    @Override
    public Map<String, List<Client>> getClientsByCity(Bank bank) {
        Map<String, List<Client>> result = new TreeMap<>(String::compareTo);

        for (Client client : bank.getClients()) {
            // If key is not found, create a new array list where you put the client
            result.computeIfAbsent(client.getCity(), _ -> new ArrayList<>()).add(client);
        }

        return result;
    }
}
