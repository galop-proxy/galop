package io.github.sebastianschmidt.galop.configuration;

import java.io.IOException;
import java.nio.file.Path;

public interface ConfigurationFileLoader {

    Configuration load(Path path) throws IOException, InvalidConfigurationException;

}
