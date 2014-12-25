package ru.fizteh.fivt.students.kotsurba.storeable;

public class ShellDbRollback extends SimpleShellCommand {

    public ShellDbRollback(final Context newContext) {
        super("rollback", 1, "usage: rollback", newContext);
    }

    @Override
    public void run() {
        if (context.getTable() != null) {
            System.out.println(context.getTable().rollback());
        } else {
            System.out.println("no table");
        }
    }
}
