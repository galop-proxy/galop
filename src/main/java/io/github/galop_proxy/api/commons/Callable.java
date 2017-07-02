package io.github.galop_proxy.api.commons;

@FunctionalInterface
public interface Callable<V, E extends Exception> {

    V call() throws E;

}
