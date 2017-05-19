package io.github.galop_proxy.galop.starter;

import com.google.inject.Injector;
import com.google.inject.Module;
import io.github.galop_proxy.galop.AbstractConfigurationTest;
import io.github.galop_proxy.galop.configuration.Configuration;
import io.github.galop_proxy.galop.configuration.ConfigurationFileLoader;
import io.github.galop_proxy.galop.configuration.InvalidConfigurationException;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.mockito.Mockito.*;

/**
 * Tests the class {@link LoaderImpl}.
 */
public class LoaderImplTest extends AbstractConfigurationTest {

    private ConfigurationFileLoader configurationFileLoader;
    private Runtime runtime;
    private Injector injector;
    private Loader loader;

    @Before
    public void setUp() {
        configurationFileLoader = mock(ConfigurationFileLoader.class);
        runtime = mock(Runtime.class);
        injector = mock(Injector.class);
        loader = new LoaderImpl(configurationFileLoader, runtime, injector);
    }

    // Valid arguments:

    @Test
    public void start_withPathToValidConfigurationFile_startsGalop() throws Exception {

        final Configuration configuration = mock(Configuration.class);
        when(configurationFileLoader.load(any())).thenReturn(configuration);

        final Injector childInjector = mock(Injector.class);
        final Starter starter = mock(Starter.class);
        when(injector.createChildInjector(any(Module[].class))).thenReturn(childInjector);
        when(childInjector.getInstance(Starter.class)).thenReturn(starter);

        loader.load(new String[] { "./valid.properties" });

        verify(childInjector).getInstance(Starter.class);
        verify(starter).start();

    }

    @Test
    public void start_withHelpOption_printsHelpAndReturns() {
        loader.load(new String[] { "-help" });
        loader.load(new String[] { "--help" });
    }

    // Invalid arguments or invalid configuration file:

    @Test
    public void start_withoutPathToConfigurationFile_exitsApplication() {
        loader.load(new String[0]);
        verify(runtime).exit(1);
    }

    @Test
    public void start_withNotExistingConfigurationFile_exitsApplication() throws Exception {
        doThrow(IOException.class).when(configurationFileLoader).load(any());
        loader.load(new String[] { "./not-existing-file.properties" });
        verify(runtime).exit(1);
    }

    @Test
    public void start_withInvalidConfigurationFile_exitsApplication() throws Exception {
        doThrow(InvalidConfigurationException.class).when(configurationFileLoader).load(any());
        loader.load(new String[] { "./invalid.properties" });
        verify(runtime).exit(1);
    }
    
    // constructor:
    
    @Test(expected = NullPointerException.class)
    public void constructor_withoutConfigurationFileLoader_throwsNullPointerException() {
        new LoaderImpl(null, runtime, injector);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_withoutRuntime_throwsNullPointerException() {
        new LoaderImpl(configurationFileLoader, null, injector);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_withoutInjector_throwsNullPointerException() {
        new LoaderImpl(configurationFileLoader, runtime, null);
    }

}
