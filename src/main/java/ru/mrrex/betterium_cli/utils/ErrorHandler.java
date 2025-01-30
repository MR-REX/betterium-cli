package ru.mrrex.betterium_cli.utils;

import picocli.CommandLine.ExitCode;

public class ErrorHandler {

    private static final String LINE_SEPARATOR = System.lineSeparator();

    private static final int LINE_TEXT_LENGTH = 64;
    private static final char LINE_TEXT_CHARACTER = '=';

    private static String getLineText(String text) {
        StringBuilder stringBuilder = new StringBuilder(LINE_TEXT_LENGTH);

        stringBuilder.append(LINE_TEXT_CHARACTER);
        stringBuilder.append(' ');

        stringBuilder.append(text);
        stringBuilder.append(' ');

        int characterLineLength = LINE_TEXT_LENGTH - stringBuilder.length();
        String characterLine = new String(new char[characterLineLength]).replace('\0', '=');

        stringBuilder.append(characterLine);

        return stringBuilder.toString();
    }

    private static String getErrorMessage(Throwable throwable) {
        String errorMessage = throwable.getLocalizedMessage();

        if (errorMessage == null) {
            return "Error object does not contain an error message";
        }

        return errorMessage;
    }

    public static int handle(Throwable throwable) {
        String errorMessage = getErrorMessage(throwable);

        if (!errorMessage.endsWith(".")) {
            errorMessage += ".";
        }

        System.err.println(getLineText("An error has occurred") + LINE_SEPARATOR);
        System.err.println(errorMessage + LINE_SEPARATOR);
        
        System.err.println(getLineText("Stack trace") + LINE_SEPARATOR);
        throwable.printStackTrace();

        return ExitCode.SOFTWARE;
    }
}
