package io.github.galop_proxy.system_test;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

final class ExecutionListener extends RunListener {

    private Class<?> lastClassName;
    private String lastMethodName;

    @Override
    public void testRunStarted(final Description description) throws Exception {

        println("\n");

        println("------------------------------------------------------------------------");
        println("                              System test                              ");
        println("------------------------------------------------------------------------\n");

        println("Number of test cases to be executed: " + description.testCount());

    }

    @Override
    public void testRunFinished(final Result result) throws Exception {

        println("\n");

        if (result.wasSuccessful()) {
            print("Result:");
            printlnSuccessful("All " + result.getRunCount()
                    + " test cases were successfully executed in "
                    + formatTime(result.getRunTime()) + ".");
        } else {

            print("Result:");

            if (result.getFailureCount() > 1) {
                printlnError(result.getFailureCount() + " test cases failed.");
            } else {
                printlnError("1 test case failed.");
            }

        }

        println("\n");

    }

    @Override
    public void testStarted(final Description description) throws Exception {

        if (!Objects.equals(lastClassName, description.getTestClass())) {

            lastClassName = description.getTestClass();
            lastMethodName = null;

            println("\n");

            println(simplifyClassName(lastClassName) + "\n");

        }

    }

    @Override
    public void testFinished(final Description description) throws Exception {

        if (!Objects.equals(lastMethodName, description.getMethodName())) {
            lastMethodName = description.getMethodName();
            printSuccessful("SUCCESSFUL");
            println(simplifyMethodName(lastMethodName));
        }

    }

    @Override
    public void testFailure(final Failure failure) throws Exception {

        lastMethodName = failure.getDescription().getMethodName();

        printError("FAILED    ");
        println(simplifyMethodName(lastMethodName) + ": " + failure.getMessage());

    }

    @Override
    public void testIgnored(final Description description) throws Exception {
        lastMethodName = description.getMethodName();
        printWarning("IGNORED   ");
        println(simplifyMethodName(lastMethodName));
    }

    // Helper methods:

    private void println(final String message) {
        print(message + "\n");
    }

    private void print(final String message) {
        System.out.print(" " + message);
    }

    private void printlnSuccessful(final String message) {
        printSuccessful(message + "\n");
    }

    private void printSuccessful(final String message) {
        print((char) 27 + "[32m" + message + (char) 27 + "[0m");
    }

    private void printWarning(final String message) {
        print((char) 27 + "[33m" + message + (char) 27 + "[0m");
    }

    private void printlnError(final String message) {
        printError(message + "\n");
    }

    private void printError(final String message) {
        print((char) 27 + "[31m" + message + (char) 27 + "[0m");
    }

    private String simplifyClassName(final Class<?> clazz) {

        final String name = clazz.getSimpleName();

        // Remove "Tests" suffix
        final String nameWithoutTest = name.substring(0, name.length() - 4);

        final StringBuilder builder = new StringBuilder();

        boolean firstChar = true;

        for (final char c : nameWithoutTest.toCharArray()) {

            if (!firstChar && Character.isUpperCase(c)) {
                builder.append(' ');
                builder.append(Character.toLowerCase(c));
            } else {
                builder.append(c);
            }

            firstChar = false;

        }

        return builder.toString();
    }

    private String simplifyMethodName(final String methodName) {
        return methodName.replace('_', ' ') + ".";
    }

    private String formatTime(final long milliseconds) {
        final long seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds);
        final long millisecondsWithoutSeconds = milliseconds - TimeUnit.SECONDS.toMillis(seconds);
        return seconds + "." + millisecondsWithoutSeconds + " seconds";
    }

}
