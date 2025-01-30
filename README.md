# Betterium CLI

Betterium CLI is a lightweight command-line launcher for starting the game with the "Better Than Adventure"
modification using the [Betterium](https://github.com/MR-REX/betterium) library. It provides an easy way to manage client initialization.

## Features

- **Simple client launching**
    - Start the game using a single command.

- **Automated dependency management**
    - Powered by the Betterium library.

- **Built with Picocli**
    - Provides a flexible and user-friendly CLI interface.

## Getting Started

### Dependencies

> [!NOTE]
> The program requires Java 8 or higher.

|Group ID|Artifact ID|Version|
|--------|-----------|-------|
|ru.mrrex|betterium  |1.0.0+ |
|info.picocli|picocli|4.7.6+ |

### Usage

#### Launching the Client

To start the game client:
```bash
java -cp "launcher/*" ru.mrrex.betterium_cli.Application client --config client-config.json --nickname MyNickname
```

This command will initialize and launch the client based on the configuration managed by Betterium.

#### Restoring game directory files

If the client directory was not properly unmounted due to an error, use the restore command:
```bash
java -cp "launcher/*" ru.mrrex.betterium_cli.Application restore --gamedir "/client-temp"
```

This will return all necessary files to their correct locations.

## License

Betterium is licensed under the **MIT License**.