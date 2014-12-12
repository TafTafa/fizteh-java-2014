package ru.fizteh.fivt.students.kotsurba.junit;

import ru.fizteh.fivt.students.kotsurba.filemap.shell.SimpleShellCommand;

public class ShellDropTable extends SimpleShellCommand {
    private Context context;

    public ShellDropTable(Context newContext) {
        context = newContext;
        setName("drop");
        setNumberOfArgs(2);
        setHint("usage: drop <table name>");
    }

    public void run() {
        try {
            if ((context.table != null) && (context.table.getName().equals(getArg(1)))) {
                context.table = null;
            }

            context.provider.removeTable(getArg(1));
            System.out.println("dropped");
        } catch (IllegalStateException e) {
            System.out.println(getArg(1) + " not exists");
        }
    }
}

