package io.github.galop_proxy.galop.http;

@FunctionalInterface
interface Callable<V, E extends Exception> {

    V call() throws E;

}
