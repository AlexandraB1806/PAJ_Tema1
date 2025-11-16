package com.luxoft.bankapp.domain;

import java.util.*;
import java.util.stream.Collectors;

public class BankReportStreams implements BankReportInterface {
    @Override
    public int getNumberOfClients(Bank bank) {
        return bank.getClients().size();
    }

    public int getNumberOfAccounts(Bank bank) {
        return bank.getClients().stream().mapToInt(client -> client.getAccounts().size()).sum();
    }

    @Override
    public SortedSet<Client> getClientsSorted(Bank bank) {

        Comparator<Client> byNameThenGender = Comparator
                .comparing(Client::getName, Comparator.nullsFirst(Comparator.naturalOrder()))
                .thenComparing(Client::getGender, Comparator.nullsFirst(Comparator.naturalOrder()));

        return bank.getClients().stream().sorted(byNameThenGender).collect(Collectors.toCollection(() -> new TreeSet<>(byNameThenGender)));
    }

    @Override
    public double getTotalSumInAccounts(Bank bank) {
        return bank.getClients().stream().mapToDouble(client ->
            client.getAccounts().stream().mapToDouble(Account::getBalance).sum()).sum();
    }

    @Override
    public SortedSet<Account> getAccountsSortedBySum(Bank bank) {
        Comparator<Account> byBalance = Comparator.comparing(Account::getBalance);

        return bank.getClients().stream()
                .flatMap(client -> client.getAccounts().stream())
                .collect(Collectors.toCollection(() -> new TreeSet<>(byBalance)));
    }

    @Override
    public double getBankCreditSum(Bank bank) {
        return bank.getClients().stream()
                .flatMap(client -> client.getAccounts().stream())
                .filter(account -> account instanceof CheckingAccount && account.getBalance() < 0)
                .mapToDouble(Account::getBalance)
                .map(Math::abs)
                .sum();
    }

    @Override
    public Map<Client, Collection<Account>> getCustomerAccounts(Bank bank) {
        return bank.getClients().stream()
                .collect(Collectors.toMap(
                        client -> client,
                        Client::getAccounts
                ));
    }

    @Override
    public Map<String, List<Client>> getClientsByCity(Bank bank) {
        return bank.getClients().stream()
                .collect(Collectors.groupingBy(
                        Client::getCity,
                        () -> new TreeMap<>(String::compareTo),
                        Collectors.toList()
                ));
    }
}
