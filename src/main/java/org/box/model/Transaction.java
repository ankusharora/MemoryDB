package org.box.model;

import java.util.HashMap;
import java.util.Map;

public class Transaction {
    private final Map<String, String> localChanges; // Tracks key-value changes in this transaction
    private final Map<String, String> previousValues; // Tracks original values before changes
    private final Map<String, Integer> localValueCount; // Tracks occurrences of values locally in this transaction

    public Transaction() {
        this.localChanges = new HashMap<>();
        this.previousValues = new HashMap<>();
        this.localValueCount = new HashMap<>();
    }

    public void setLocalChange(String key, String newValue, String previousValue) {
        localChanges.put(key, newValue);
        if (!previousValues.containsKey(key)) {
            previousValues.put(key, previousValue); // Store the original value only once
        }

        // Adjust local value counts for the new value
        if (newValue != null) {
            localValueCount.put(newValue, localValueCount.getOrDefault(newValue, 0) + 1);
        }

        // Adjust local value counts for the previous value (decrement or remove)
        if (previousValue != null) {
            localValueCount.put(previousValue, localValueCount.getOrDefault(previousValue, 0) - 1);
            if (localValueCount.get(previousValue) == 0) {
                localValueCount.remove(previousValue);
            }
        }
    }

    public Map<String, String> getLocalChanges() {
        return localChanges;
    }

    public String getPreviousValue(String key) {
        return previousValues.get(key);
    }

    public int getLocalValueCount(String value) {
        return localValueCount.getOrDefault(value, 0);
    }
}
