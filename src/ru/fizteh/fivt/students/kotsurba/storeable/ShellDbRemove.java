package ru.fizteh.fivt.students.kotsurba.storeable;

public final class ShellDbRemove extends SimpleShellCommand {

    public ShellDbRemove(Context newContext) {
        super("remove", 2, "usage: remove <key>", newContext);
    }

    @Override
    public void run() {
        if (context.getTable() == null) {
            System.out.println("no table");
            return;
        }
        if (context.getTable().remove(getArg(1)) == null) {
            System.out.println("not found");
        } else {
            System.out.println("removed");
        }
    }
}
