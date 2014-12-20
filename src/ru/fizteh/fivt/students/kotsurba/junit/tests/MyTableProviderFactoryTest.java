package ru.fizteh.fivt.students.kotsurba.junit.tests;

import org.junit.*;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.kotsurba.junit.MyTableProviderFactory;

import java.io.IOException;

public class MyTableProviderFactoryTest {
    TableProviderFactory factory;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void before() throws IOException {
        factory = new MyTableProviderFactory();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateNull() {
        factory.create(null);
    }

    @Test
    public void testCreateNotNull() throws IOException {
        Assert.assertNotNull(factory.create(folder.newFolder("folder").getCanonicalPath()));
    }
}
