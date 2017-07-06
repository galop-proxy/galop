package io.github.galop_proxy.system_test;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static io.github.galop_proxy.system_test.ConsoleUtils.*;

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
            testRunSuccessful(result);
        } else {
            testRunFailed(result);
        }

        println("\n");

    }

    private void testRunSuccessful(final Result result) {
        print("Result:");
        printlnSuccessful("All " + result.getRunCount()
                + " test cases were successfully executed in "
                + formatTime(result.getRunTime()) + ".");
    }

    private void testRunFailed(final Result result) {

        print("Result:");

        if (result.getFailureCount() > 1) {
            printlnError(result.getFailureCount() + " test cases failed.");
        } else {
            printlnError("1 test case failed.");
        }

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
        println(simplifyMethodName(lastMethodName) + "\b: " + failure.getMessage());

    }

    @Override
    public void testIgnored(final Description description) throws Exception {
        lastMethodName = description.getMethodName();
        printWarning("IGNORED   ");
        println(simplifyMethodName(lastMethodName));
    }

    // Helper methods:

    private String simplifyMethodName(final String methodName) {
        return methodName.replace('_', ' ') + ".";
    }

    private String formatTime(final long milliseconds) {
        final long seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds);
        final long millisecondsWithoutSeconds = milliseconds - TimeUnit.SECONDS.toMillis(seconds);
        return seconds + "." + millisecondsWithoutSeconds + " seconds";
    }

    private String simplifyClassName(final Class<?> clazz) {
        final String name = clazz.getSimpleName();
        final String nameWithoutTest = name.substring(0, name.length() - 4);
        return addSpaces(nameWithoutTest);
    }

    private String addSpaces(final String s) {

        final StringBuilder builder = new StringBuilder();

        boolean firstChar = true;

        for (final char c : s.toCharArray()) {

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

}
