1. Precondition: An installed Java Runtime Environment 8.

2. Download the [latest release](https://github.com/galop-proxy/galop/releases/latest) of GALOP.

3. Extract the file galop-%version%.jar from the downloaded archive.

4. Create a configuration file for GALOP.
   The following section describes the [structure of the configuration file](configuration.md).

5. GALOP can be started via the Java Runtime Environment.
   The path to the configuration file must be passed as an argument:
   `java -jar galop-%version%.jar configuration.properties`