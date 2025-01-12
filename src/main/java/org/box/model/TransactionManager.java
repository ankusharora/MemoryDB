package org.box.model;

import java.util.ArrayDeque;
import java.util.Deque;

public class TransactionManager {
    private final InMemoryDatabase database;
    private final Deque<Transaction> transactionStack;

    public TransactionManager(InMemoryDatabase database) {
        this.database = database;
        this.transactionStack = new ArrayDeque<>();
    }

    public void begin() {
        transactionStack.push(new Transaction());
    }

    // TransactionManager class

    public Transaction getCurrentTransaction() {
        if (transactionStack.isEmpty()) {
            throw new IllegalStateException("No active transaction.");
        }
        return transactionStack.peek(); // Peek to get the current transaction without removing it
    }


    public void commit() {
        if (transactionStack.isEmpty()) {
            throw new IllegalStateException("No transaction to commit.");
        }

        Transaction currentTransaction = transactionStack.pop();

        // Apply the changes in the current transaction to the database
        currentTransaction.getLocalChanges().forEach((key, value) -> {
            database.applyChanges(key, value);
        });

        System.out.println("Transaction committed.");
    }

    public void rollback() {
        if (transactionStack.isEmpty()) {
            throw new IllegalStateException("No transaction to rollback.");
        }

        // Pop the most recent transaction
        Transaction currentTransaction = transactionStack.pop();

        // Revert all changes made in the current transaction
        currentTransaction.getLocalChanges().forEach((key, newValue) -> {
            String previousValue = currentTransaction.getPreviousValue(key);

            if (newValue == null) {
                // Key was deleted in the transaction, restore it
                database.getDataStore().put(key, previousValue);
                database.incrementValueCount(previousValue);
            } else {
                // Key was updated or added in the transaction, revert it
                if (previousValue == null) {
                    // Key was newly added, remove it
                    database.getDataStore().remove(key);
                    database.decrementValueCount(newValue);
                } else {
                    // Key was updated, revert to the previous value
                    database.getDataStore().put(key, previousValue);
                    database.decrementValueCount(newValue);
                    database.incrementValueCount(previousValue);
                }
            }
        });

        System.out.println("Transaction rolled back.");
    }

    public boolean hasActiveTransaction() {
        return !transactionStack.isEmpty();
    }
}
