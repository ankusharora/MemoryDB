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
        // Track the change for rollback
        localChanges.put(key, newValue);

        // Only store the original value once, during the first change
        if (!previousValues.containsKey(key)) {
            previousValues.put(key, previousValue);
        }

        // Update local value counts
        updateValueCount(newValue, 1);  // Increment count for the new value
        updateValueCount(previousValue, -1); // Decrement count for the previous value
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

    private void updateValueCount(String value, int delta) {
        if (value == null) return; // No adjustment needed for null values
        int updatedCount = localValueCount.getOrDefault(value, 0) + delta;
        if (updatedCount > 0) {
            localValueCount.put(value, updatedCount);
        } else {
            localValueCount.remove(value); // Remove the entry if count drops to zero
        }
    }
}
