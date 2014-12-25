package ru.fizteh.fivt.students.kotsurba.storeable;

import java.io.IOException;

public class ShellDbCommit extends SimpleShellCommand {

    public ShellDbCommit(final Context newContext) {
        super("commit", 1, "usage: commit", newContext);
    }

    @Override
    public void run() {
        try {
            if (context.getTable() != null) {
                System.out.println(context.getTable().commit());
            } else {
                System.out.println("no table");
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
