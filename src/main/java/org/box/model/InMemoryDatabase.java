package org.box.model;

import java.util.HashMap;
import java.util.Map;

public class InMemoryDatabase {
    private final Map<String, String> dataStore; // Main key-value store
    private final Map<String, Integer> valueCount; // Tracks occurrences of values
    private final TransactionManager transactionManager; // Handles transactions

    public InMemoryDatabase() {
        this.dataStore = new HashMap<>();
        this.valueCount = new HashMap<>();
        this.transactionManager = new TransactionManager(this);
    }

    public Map<String, String> getDataStore() {
        return dataStore;
    }

    public void applyChanges(String key, String value) {
        String oldValue = dataStore.get(key);

        // Update value counts
        if (oldValue != null) {
            decrementValueCount(oldValue);
        }
        if (value != null) {
            incrementValueCount(value);
        }

        // Apply the key-value change to the data store
        dataStore.put(key, value);
    }

    public String get(String key) {
        return dataStore.getOrDefault(key, null);
    }

    public void setOri(String key, String value) {
        String oldValue = dataStore.get(key);

        if (oldValue != null) {
            decrementValueCount(oldValue);
        }

        dataStore.put(key, value);
        incrementValueCount(value);
    }

    // InMemoryDatabase class

    public void set(String key, String value) {
        if (transactionManager.hasActiveTransaction()) {
            // If a transaction is active, record the change in the transaction
            String previousValue = dataStore.get(key); // Store the previous value
            transactionManager.getCurrentTransaction().setLocalChange(key, value, previousValue);
        } else {
            // If no transaction is active, apply the change directly to the main database
            String oldValue = dataStore.get(key);

            if (oldValue != null) {
                decrementValueCount(oldValue);
            }

            dataStore.put(key, value);
            incrementValueCount(value);
        }
    }


    public int getCountOfValue(String value) {
        return valueCount.getOrDefault(value, 0);
    }

    public void incrementValueCount(String value) {
        valueCount.put(value, valueCount.getOrDefault(value, 0) + 1);
    }

    public void decrementValueCount(String value) {
        int count = valueCount.getOrDefault(value, 0);
        if (count > 1) {
            valueCount.put(value, count - 1);
        } else {
            valueCount.remove(value);
        }
    }

    public TransactionManager getTransactionManager() {
        return transactionManager;
    }
}
