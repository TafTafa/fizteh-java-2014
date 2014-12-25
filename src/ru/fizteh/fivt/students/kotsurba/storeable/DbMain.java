package ru.fizteh.fivt.students.kotsurba.storeable;

import java.io.IOException;

import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.kotsurba.filemap.shell.Shell;

public class DbMain {
    private static Shell shell;

    private static void checkDbDir() {
        if (!System.getProperties().containsKey("fizteh.db.dir")) {
            System.err.println("Please set database directory!");
            System.err.println("-Dfizteh.db.dir=<directory name>");
            System.exit(1);
        }
    }

    private static void initShell() {
        try {
            shell = new Shell();

            TableProviderFactory factory = new MyTableProviderFactory();
            Context context = new Context(factory.create(System.getProperty("fizteh.db.dir")));

            shell.addCommand(new ShellDbPut(context));
            shell.addCommand(new ShellExit(context));
            shell.addCommand(new ShellDbGet(context));
            shell.addCommand(new ShellDbRemove(context));
            shell.addCommand(new ShellCreateTable(context));
            shell.addCommand(new ShellDropTable(context));
            shell.addCommand(new ShellUseTable(context));
            shell.addCommand(new ShellDbSize(context));
            shell.addCommand(new ShellDbCommit(context));
            shell.addCommand(new ShellDbRollback(context));
            shell.addCommand(new ShellDbList(context));
            shell.addCommand(new ShellDbShow(context));

        } catch (IOException e) {
            System.out.println("init shell failed!");
            System.exit(1);
        }
    }

    public static void main(final String[] args) {
        try {
            checkDbDir();
            initShell();

            if (args.length > 0) {
                ShellRunner.packetRun(shell, args);
            } else {
                ShellRunner.interactiveRun(shell);
            }

        } catch (DataBaseException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (ShellExitException e) {
            System.exit(0);
        } finally {
            System.exit(0);
        }
    }
}

