package ru.mrrex.betterium_cli.commands;

import java.nio.file.Path;
import java.util.concurrent.Callable;

import picocli.CommandLine.Command;
import picocli.CommandLine.ExitCode;
import picocli.CommandLine.Option;
import ru.mrrex.betterium.Betterium;
import ru.mrrex.betterium.directories.GameDirectory;
import ru.mrrex.betterium.directories.WorkingDirectory;
import ru.mrrex.betterium_cli.utils.ErrorHandler;

@Command(
    name = "restore",
    description = "Restores files from the client unmounted game directory"
)
public class RestoreGameDirectoryCommand implements Callable<Integer> {

    @Option(names = {"-d", "--workdir"})
    private Path workingDirectoryPath;

    @Option(names = {"-g", "--gamedir"}, required = true)
    private Path gameDirectoryPath;

    private Betterium createBetteriumInstance() {
        if (workingDirectoryPath == null) {
            return new Betterium();
        }

        return new Betterium(workingDirectoryPath);
    }

    private Path getGameDirectoryPath(Betterium betterium) {
        if (gameDirectoryPath != null) {
            return gameDirectoryPath;
        }

        return betterium.getWorkingDirectoryPath().resolve(RunClientCommand.INTERNAL_CLIENT_DIRECTORY_NAME);
    }

    @Override
    public Integer call() throws Exception {
        try {
            Betterium betterium = createBetteriumInstance();
            Path targetDirectoryPath = getGameDirectoryPath(betterium);

            if (targetDirectoryPath == null) {
                throw new IllegalArgumentException("Game directory path must not be null");
            }

            WorkingDirectory workingDirectory = new WorkingDirectory(betterium.getWorkingDirectoryPath());
            GameDirectory gameDirectory = GameDirectory.restoreFromDirectory(workingDirectory, targetDirectoryPath);

            gameDirectory.unmount();

            return ExitCode.OK;
        } catch (Exception exception) {
            return ErrorHandler.handle(exception);
        }
    }
}
