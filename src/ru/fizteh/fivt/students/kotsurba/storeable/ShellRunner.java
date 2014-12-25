package ru.fizteh.fivt.students.kotsurba.storeable;

import ru.fizteh.fivt.students.kotsurba.filemap.shell.CommandParser;
import ru.fizteh.fivt.students.kotsurba.filemap.shell.InvalidCommandException;
import ru.fizteh.fivt.students.kotsurba.shell.ShellMain;
import ru.fizteh.fivt.students.kotsurba.filemap.shell.Shell;

import java.util.Scanner;

public class ShellRunner {

    public static final String PROMPT = " $ ";

    public static void packetRun(Shell shell, final String[] args) {
        try {
            CommandParser parser = new CommandParser(args);
            while (!parser.isEmpty()) {
                shell.executeCommand(parser.getCommand());
            }
        } catch (InvalidCommandException | MultiDataBaseException | DataBaseWrongFileFormat | RuntimeException e) {
            System.err.println(e.getMessage());
        }
    }

    public static void interactiveRun(Shell shell) {
        Scanner scanner = new Scanner(System.in);
        System.out.print(PROMPT);
        while (true) {
            try {
                if (!scanner.hasNextLine()) {
                    throw new ShellExitException("Ctrl + D exit!");
                }

                String command = scanner.nextLine();

                if (ShellMain.hasTerminalSymbol(command)) {
                    throw new ShellExitException("Ctrl + D exit or EOF!");
                }

                CommandParser parser = new CommandParser(command);
                if (!parser.isEmpty()) {
                    shell.executeCommand(parser.getCommand());
                }
            } catch (ShellExitException e) {
                throw new ShellExitException(e.getMessage());
            } catch (DataBaseException e) {
                throw new DataBaseException(e.getMessage());
            } catch (MultiDataBaseException | DataBaseWrongFileFormat | InvalidCommandException | RuntimeException e) {
                System.err.println(e.getMessage());
            } finally {
                System.out.print(PROMPT);
            }
        }
    }
}
