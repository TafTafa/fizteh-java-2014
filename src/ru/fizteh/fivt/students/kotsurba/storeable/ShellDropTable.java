package ru.fizteh.fivt.students.kotsurba.storeable;

import java.io.IOException;

public class ShellDropTable extends SimpleShellCommand {

    public ShellDropTable(Context newContext) {
        super("drop", 2, "usage: drop <table name>", newContext);
    }

    public void run() {
        try {
            if ((context.getTable() != null) && (context.getTable().getName().equals(getArg(1)))) {
                context.setTable(null);
            }

            context.getProvider().removeTable(getArg(1));
            System.out.println("dropped");
        } catch (IllegalStateException e) {
            System.out.println(getArg(1) + " not exists");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
