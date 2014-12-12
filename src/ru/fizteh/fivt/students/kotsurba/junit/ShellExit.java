package ru.fizteh.fivt.students.kotsurba.junit;

import ru.fizteh.fivt.students.kotsurba.filemap.shell.SimpleShellCommand;

public final class ShellExit extends SimpleShellCommand {
    private Context context;

    public ShellExit(final Context newContext) {
        context = newContext;
        setName("exit");
        setNumberOfArgs(1);
        setHint("usage: exit");
    }

    @Override
    public void run() {
        if (context.getChanges() == 0) {
            throw new ShellExitException("Exit command");
        } else {
            System.out.println(context.getChanges() + " unsaved changes");
        }
    }

}