package ru.mrrex.betterium_cli.commands;

import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.Callable;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import ru.mrrex.betterium.Betterium;
import ru.mrrex.betterium.entities.client.Client;
import ru.mrrex.betterium.entities.client.ClientArguments;
import ru.mrrex.betterium.entities.client.ClientConfig;
import ru.mrrex.betterium.entities.client.ClientOptions;
import ru.mrrex.betterium.runtime.JavaRuntime;
import ru.mrrex.betterium.runtime.JvmArguments;
import ru.mrrex.betterium_cli.utils.ErrorHandler;

@Command(
    name = "client",
    description = "Creates and launches a client based on a json configuration file"
)
public class RunClientCommand implements Callable<Integer> {

    public static final String INTERNAL_CLIENT_DIRECTORY_NAME = "mounted";

    @Option(names = {"-d", "--workdir"})
    private Path workingDirectoryPath;

    @Option(names = {"-j", "--java"})
    private Path javaDirectoryPath;

    @Option(names = {"-c", "--config"}, required = true)
    private Path clientConfigPath;

    @Option(names = {"-n", "--nickname"})
    private String playerNickname;

    @Option(names = {"-u", "--uuid"})
    private UUID playerUuid;

    @Option(names = {"-g", "--gamedir"})
    private Path gameDirectoryPath;

    private Betterium createBetteriumInstance() {
        if (workingDirectoryPath == null) {
            return new Betterium();
        }

        return new Betterium(workingDirectoryPath);
    }

    private JavaRuntime createJavaRuntimeInstance(JvmArguments jvmArguments) {
        if (javaDirectoryPath == null) {
            return new JavaRuntime(jvmArguments);
        }

        return new JavaRuntime(javaDirectoryPath, jvmArguments);
    }

    private Path getGameDirectoryPath(Betterium betterium) {
        if (gameDirectoryPath != null) {
            return gameDirectoryPath;
        }

        return betterium.getWorkingDirectoryPath().resolve(INTERNAL_CLIENT_DIRECTORY_NAME);
    }

    private ClientArguments createClientArgumentsInstance(Betterium betterium) throws FileAlreadyExistsException {
        ClientArguments clientArguments = new ClientArguments();

        if (playerNickname != null) {
            int playerNicknameLength = playerNickname.length();

            if (playerNicknameLength < 3 || playerNicknameLength > 16) {
                throw new IllegalArgumentException("Player nickname length must be between 3 and 16 characters");
            }

            clientArguments.setPlayerNickname(playerNickname);
            
            if (playerUuid != null) {
                byte[] bytes = ("OfflinePlayer:" + playerNickname).getBytes(StandardCharsets.UTF_8);
                UUID generatedPlayerUuid = UUID.nameUUIDFromBytes(bytes);

                clientArguments.setPlayerUuid(generatedPlayerUuid);
            }
        }

        if (playerUuid != null) {
            clientArguments.setPlayerUuid(playerUuid);
        }

        Path gameDataDirectoryPath = getGameDirectoryPath(betterium);

        if (Files.exists(gameDataDirectoryPath)) {
            throw new FileAlreadyExistsException(gameDataDirectoryPath.toString());
        }

        clientArguments.setGameDirectoryPath(gameDataDirectoryPath);

        return clientArguments;
    }

    @Override
    public Integer call() {
        try {
            if (workingDirectoryPath != null && Files.exists(workingDirectoryPath) && !Files.isDirectory(workingDirectoryPath)) {
                throw new IllegalArgumentException("Specified betterium working directory path exists, but is not a directory");
            }

            if (javaDirectoryPath != null && Files.exists(javaDirectoryPath) && !Files.isDirectory(javaDirectoryPath)) {
                throw new IllegalArgumentException("Specified java directory path exists, but is not a directory");
            }

            if (clientConfigPath == null || !Files.exists(clientConfigPath) || !Files.isRegularFile(clientConfigPath)) {
                throw new FileNotFoundException("Client config file is missing, doesn't exist, or is not a regular file");
            }

            Betterium betterium = createBetteriumInstance();

            ClientConfig clientConfig = betterium.loadClientConfig(clientConfigPath);
            ClientOptions clientOptions = clientConfig.getOptions();

            JvmArguments jvmArguments = new JvmArguments(clientOptions.getJvmArguments());
            JavaRuntime javaRuntime = createJavaRuntimeInstance(jvmArguments);

            Client client = betterium.createClient(clientConfig);
            ClientArguments clientArguments = createClientArgumentsInstance(betterium);

            return client.run(javaRuntime, clientArguments);
        } catch (Exception exception) {
            return ErrorHandler.handle(exception);
        }
    }
}
