# GALOP

[![Release](https://img.shields.io/github/release/galop-proxy/galop.svg)](https://github.com/galop-proxy/galop/releases)
[![License](https://img.shields.io/github/license/galop-proxy/galop.svg)](https://github.com/galop-proxy/galop/blob/master/LICENSE)
[![Build Status](https://travis-ci.org/galop-proxy/galop.svg?branch=master)](https://travis-ci.org/galop-proxy/galop)
[![Coverage Status](https://coveralls.io/repos/github/galop-proxy/galop/badge.svg?branch=master)](https://coveralls.io/github/galop-proxy/galop?branch=master)
[![Dependency Status](https://www.versioneye.com/user/projects/590cf5e39e070f0038ec764e/badge.svg)](https://www.versioneye.com/user/projects/590cf5e39e070f0038ec764e)

GALOP (**G**raceful **A**pp**l**icati**o**n **P**roxy) is a simple reverse
proxy that accepts HTTP requests from clients and passes them to a server.
When shutting down, the proxy processes open HTTP requests before the
connections are closed. A persistent connection (keep-alive) is closed after
the current HTTP request has been answered. After this, the actual server can
be shut down without interrupting open HTTP requests.


## Features

- Simple reverse proxy for HTTP requests.
- Closes connections gracefully when shutting down by processing open HTTP
  requests before terminating a connection.
- Supports chunked transfer encoding.


## Limitations

**GALOP is in the alpha stage of development.** This means that the proxy may
be unstable and not all functions are implemented that are necessary for use in
a production environment. Please note the following limitations in the current
functionality of GALOP.

- **No secure connections:** Currently neither TLS nor SSL is supported.
- **X-Forwarded-For not supported:** Currently, the proxy can not send the
  original IP addresses of the clients to the server via the HTTP header
  X-Forwarded-For.

These limitations will be solved before the first stable release.
During the development, this list will be constantly updated.


## Installation

1. Precondition: An installed Java Runtime Environment 8.
2. Download the
   [latest release](https://github.com/galop-proxy/galop/releases/latest)
   of GALOP.
3. Extract the file galop-%version%.jar from the downloaded archive.
4. Create a configuration file for GALOP.
   The following section describes the structure of the configuration file.
5. GALOP can be started via the Java Runtime Environment.
   The path to the configuration file must be passed as an argument:
   `java -jar galop-%version%.jar configuration.properties`


## Configuration

GALOP is configured via a [.properties](https://en.wikipedia.org/wiki/.properties)
file. This is a simple text file. Each line contains a single property.
Each property consists of a name and a value, separated by an equals sign.
A configuration file for GALOP must contain at least the following three
properties.

- **proxy_port:**
  When GALOP starts, it binds to this port number and waits for incoming requests.
- **target.address:**
  Each request is forwarded to the server with this address.
  The address can be an IP address or hostname.
- **target.port:**
  Each request is forwarded to this port number of the target server.

A minimal configuration file might look like this:

```
proxy_port=80
target.address=localhost
target.port=3000
```

In addition, the following optional properties are available:

- **target.connection_timeout:**
  The maximum time that GALOP waits for the target server to establish a TCP
  connection. The time must be specified in milliseconds. The default value is
  15000 milliseconds.
- **http.connection.termination_timeout:**
  When GALOP is shut down, the open connections are not closed until open HTTP
  requests have been processed. This property can be used to configure the
  maximum time in milliseconds, after which all connections are closed even if
  HTTP requests are still open. The default value is 30000 milliseconds.
- **http.connection.log_interval:**
  In this interval, the current number of open connections is logged. The
  interval must be specified in milliseconds. The default value is 60000
  milliseconds.
- **http.header.max_size:**
  The maximum allowed size of an HTTP header in bytes. Any HTTP request or
  response that contains a larger header is rejected by GALOP. The default
  value is 8192 bytes. The smallest size that can be configured is 255 bytes.

For example, a configuration file that overwrites all default values might
look like this:

```
proxy_port=80
target.address=localhost
target.port=3000
target.connection_timeout=30000
http.connection.termination_timeout=120000
http.connection.log_interval=30000
http.header.max_size=16384
```


## Logging

GALOP uses the [Log4j 2 framework](https://logging.apache.org/log4j/2.0/) for
logging. By default, all log entries are written to the default output stream.
Read the [Log4j 2 documentation](https://logging.apache.org/log4j/2.0/manual/configuration.html)
to learn how to customize logging.


## License

GALOP is licensed under [The MIT License](https://opensource.org/licenses/MIT).
