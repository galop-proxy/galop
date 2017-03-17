package io.github.sebastianschmidt.galop.administration;

import io.github.sebastianschmidt.galop.configuration.Configuration;

public interface MonitorFactory {

    Thread create(Configuration configuration);

}
