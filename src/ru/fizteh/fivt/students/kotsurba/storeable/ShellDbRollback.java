package ru.fizteh.fivt.students.kotsurba.storeable;

import ru.fizteh.fivt.students.kotsurba.filemap.shell.SimpleShellCommand;

public class ShellDbRollback extends SimpleShellCommand {
    private Context context;

    public ShellDbRollback(final Context newContext) {
        context = newContext;
        setName("rollback");
        setNumberOfArgs(1);
        setHint("usage: rollback");
    }

    @Override
    public void run() {
        if (context.table != null) {
            System.out.println(context.table.rollback());
        } else {
            System.out.println("no table");
        }
    }
}
