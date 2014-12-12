package ru.fizteh.fivt.students.kotsurba.junit;

import ru.fizteh.fivt.students.kotsurba.filemap.shell.SimpleShellCommand;

public class ShellDbCommit extends SimpleShellCommand {
    private Context context;

    public ShellDbCommit(final Context newContext) {
        context = newContext;
        setName("commit");
        setNumberOfArgs(1);
        setHint("usage: commit");
    }

    @Override
    public void run() {
        if (context.table != null) {
            System.out.println(context.table.commit());
        } else {
            System.out.println("no table");
        }
    }
}
