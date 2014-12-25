package ru.fizteh.fivt.students.kotsurba.storeable;

public class ShellDbSize extends SimpleShellCommand {

    public ShellDbSize(final Context newContext) {
        super("size", 1, "usage: size", newContext);
    }

    @Override
    public void run() {
        if (context.getClass() != null) {
            System.out.println(context.getTable().size());
        } else {
            System.out.println("no table");
        }
    }
}
