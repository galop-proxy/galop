package io.github.galop_proxy.galop.administration;

public interface Monitor extends Runnable {

    void start();

    void interrupt();

    boolean isAlive();

}
