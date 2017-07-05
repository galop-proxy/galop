package io.github.galop_proxy.system_test;

final class ConsoleUtils {

    static void println(final String message) {
        print(message + "\n");
    }

    static void print(final String message) {
        System.out.print(" " + message);
    }

    static void printlnSuccessful(final String message) {
        printSuccessful(message + "\n");
    }

    static void printSuccessful(final String message) {
        print((char) 27 + "[32m" + message + (char) 27 + "[0m");
    }

    static void printWarning(final String message) {
        print((char) 27 + "[33m" + message + (char) 27 + "[0m");
    }

    static void printlnError(final String message) {
        printError(message + "\n");
    }

    static void printError(final String message) {
        print((char) 27 + "[31m" + message + (char) 27 + "[0m");
    }
    
}
