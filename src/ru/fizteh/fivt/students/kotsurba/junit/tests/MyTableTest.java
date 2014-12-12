package ru.fizteh.fivt.students.kotsurba.junit.tests;

import org.junit.*;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.kotsurba.junit.MyTableProviderFactory;

import java.io.IOException;
import java.util.*;

public class MyTableTest {
    static Table table;
    static TableProviderFactory factory;
    static TableProvider provider;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();


    @BeforeClass
    public static void beforeClass() {
        factory = new MyTableProviderFactory();
    }

    @Before
    public void beforeTest() throws IOException {
        provider = factory.create(folder.newFolder("folder").getCanonicalPath());
        table = provider.createTable("new");
    }

    @After
    public void afterTest() {
        provider.removeTable("new");
    }


    @Test
    public void testGetName() {
        Assert.assertEquals(table.getName(), "new");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNull() {
        table.get(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutNull() {
        table.put(null, "test");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveNull() {
        table.remove(null);
    }

    @Test
    public void testPutGetRemoveEnglish() {
        Assert.assertNull(table.put("key", "value"));
        Assert.assertEquals(table.get("key"), "value");
        Assert.assertNull(table.get("value"));
        Assert.assertEquals(table.put("key", "value_new"), "value");
        Assert.assertEquals(table.remove("key"), "value_new");
    }

    @Test
    public void testPutGetRemoveRussian() {
        Assert.assertNull(table.put("МАМА", "ПАПА"));
        Assert.assertEquals(table.get("МАМА"), "ПАПА");
        Assert.assertNull(table.get("ПАПА"));
        Assert.assertEquals(table.put("МАМА", "БРАТ"), "ПАПА");
        Assert.assertEquals(table.remove("МАМА"), "БРАТ");
    }

    @Test
    public void testSizeCommit() {
        Assert.assertEquals(table.size(), 0);
        int count = (Math.abs(new Random().nextInt()) % 255) + 100;
        for (int i = 0; i < count; ++i) {
            Assert.assertNull(table.put(Integer.toString(i), "size test"));
        }
        Assert.assertEquals(table.size(), count);
        Assert.assertEquals(table.commit(), count);
        for (int i = 0; i < count; ++i) {
            Assert.assertEquals(table.remove(Integer.toString(i)), "size test");
        }
        Assert.assertEquals(table.size(), 0);
        Assert.assertEquals(table.commit(), count);
    }

    @Test
    public void testRollback() {
        Assert.assertEquals(table.size(), 0);
        int count = (Math.abs(new Random().nextInt()) % 255) + 100;
        for (int i = 0; i < count; ++i) {
            Assert.assertNull(table.put(Integer.toString(i), "rollback test"));
        }
        Assert.assertEquals(table.rollback(), count);
    }

    @Test
    public void testRollbackWithNoChanges() {
        Assert.assertNull(table.put("no_changes", "will_be_deleted_soon"));
        Assert.assertEquals(table.remove("no_changes"), "will_be_deleted_soon");
        Assert.assertEquals(table.rollback(), 0);

        Assert.assertNull(table.put("key", "value"));
        Assert.assertEquals(table.commit(), 1);
        Assert.assertEquals(table.put("key", "value_new"), "value");
        Assert.assertEquals(table.put("key", "value"), "value_new");
        Assert.assertEquals(table.rollback(), 0);
    }

    @Test
    public void testCommitWithNoChanges() {
        Assert.assertNull(table.put("no_changes", "will_be_deleted_soon"));
        Assert.assertEquals(table.remove("no_changes"), "will_be_deleted_soon");
        Assert.assertEquals(table.commit(), 0);

        Assert.assertNull(table.put("key", "value"));
        Assert.assertEquals(table.commit(), 1);
        Assert.assertEquals(table.put("key", "value_new"), "value");
        Assert.assertEquals(table.put("key", "value"), "value_new");
        Assert.assertEquals(table.commit(), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyKey() {
        table.put("   ", "empty_key");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyValue() {
        table.put("empty_value", "   ");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullValue() {
        table.put("null_value", null);
    }

    @Test
    public void testWorkWithTable() {
        Assert.assertNull(table.put("work", "with"));
        Assert.assertEquals(table.get("work"), "with");
        Assert.assertEquals(table.remove("work"), "with");
        Assert.assertEquals(table.remove("work"), null);
        Assert.assertEquals(table.commit(), 0);
        Assert.assertEquals(table.rollback(), 0);
        Assert.assertEquals(table.size(), 0);
    }

    @Test
    public void testCommitRollback() {
        Assert.assertNull(table.put("commit", "rollback"));
        Assert.assertEquals(table.get("commit"), "rollback");
        Assert.assertEquals(table.rollback(), 1);
        Assert.assertNull(table.get("commit"));
        Assert.assertNull(table.put("commit", "rollback"));
        Assert.assertEquals(table.get("commit"), "rollback");
        Assert.assertEquals(table.commit(), 1);
        Assert.assertEquals(table.remove("commit"), "rollback");
        Assert.assertNull(table.put("commit", "rollback1"));
        Assert.assertEquals(table.commit(), 1);
        Assert.assertEquals(table.get("commit"), "rollback1");
    }

    @Test
    public void testList() {
        Assert.assertEquals(table.list().size(), 0);
        table.put("14", "88");
        table.put("22", "8");
        table.put("42", "42");
        List<String> actual = table.list();
        Set<String> expected = new HashSet<String>(3);
        expected.add("14");
        expected.add("22");
        expected.add("42");
        for (String s : actual) {
            Assert.assertTrue(expected.contains(s));
        }
    }
}
