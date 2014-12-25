package ru.fizteh.fivt.students.kotsurba.storeable;

public final class ShellExit extends SimpleShellCommand {

    public ShellExit(final Context newContext) {
        super("exit", 1, "usage: exit", newContext);
    }

    @Override
    public void run() {
        if (context.getChanges() == 0) {
            throw new ShellExitException("Exit command");
        } else {
            System.out.println(context.getChanges() + " unsaved changes");
            System.out.println("Can't exit");
        }
    }

}
