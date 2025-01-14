
import org.box.model.InMemoryDatabase;
import org.box.model.Transaction;
import org.box.model.TransactionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TransactionManagerTest {

    private InMemoryDatabase mockDatabase;
    private TransactionManager transactionManager;

    @BeforeEach
    void setUp() {
        mockDatabase = mock(InMemoryDatabase.class);
        transactionManager = new TransactionManager(mockDatabase);
    }

    @Test
    void testBeginTransaction() {
        transactionManager.begin();
        assertTrue(transactionManager.hasActiveTransaction(), "A transaction should be active after calling begin().");
    }



    @Test
    void testRollbackTransaction() {
        transactionManager.begin();
        Transaction mockTransaction = mock(Transaction.class);

        // Simulate changes in the transaction, replace null with a placeholder (e.g., "nullValue")
        when(mockTransaction.getLocalChanges()).thenReturn(Map.of("key1", "value1", "key2", "nullValue"));
        when(mockTransaction.getPreviousValue("key1")).thenReturn("oldValue1");
        when(mockTransaction.getPreviousValue("key2")).thenReturn(null);
        transactionManager.getCurrentTransaction().getLocalChanges().putAll(mockTransaction.getLocalChanges());

        transactionManager.rollback();

        // Verify that changes are reverted
        verify(mockDatabase, times(2)).getDataStore();


        assertFalse(transactionManager.hasActiveTransaction(), "No transaction should be active after rollback.");
    }


    @Test
    void testGetCurrentTransactionWithoutActiveTransaction() {
        IllegalStateException exception = assertThrows(IllegalStateException.class, transactionManager::getCurrentTransaction);
        assertEquals("No active transaction.", exception.getMessage());
    }

    @Test
    void testHasActiveTransaction() {
        assertFalse(transactionManager.hasActiveTransaction(), "No transaction should be active initially.");
        transactionManager.begin();
        assertTrue(transactionManager.hasActiveTransaction(), "A transaction should be active after calling begin().");
    }
}
