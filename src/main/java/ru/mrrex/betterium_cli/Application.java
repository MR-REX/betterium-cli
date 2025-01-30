package ru.mrrex.betterium_cli;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import ru.mrrex.betterium_cli.commands.RestoreGameDirectoryCommand;
import ru.mrrex.betterium_cli.commands.RunClientCommand;

@Command(
    name = "betterium-cli",
    description = "A simple betterium based CLI game launcher",
    subcommands = {
        RunClientCommand.class,
        RestoreGameDirectoryCommand.class
    }
)
public class Application {

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Application()).execute(args);
        System.exit(exitCode);
    }
}
