# GALOP

[![Release](https://img.shields.io/github/release/SebastianSchmidt/galop.svg)](https://github.com/SebastianSchmidt/galop/releases)
[![License](https://img.shields.io/github/license/SebastianSchmidt/galop.svg)](https://github.com/SebastianSchmidt/galop/blob/master/LICENSE)
[![Build Status](https://travis-ci.org/SebastianSchmidt/galop.svg?branch=master)](https://travis-ci.org/SebastianSchmidt/galop)
[![Coverage Status](https://coveralls.io/repos/github/SebastianSchmidt/galop/badge.svg?branch=master)](https://coveralls.io/github/SebastianSchmidt/galop?branch=master)
[![Dependency Status](https://www.versioneye.com/user/projects/58cc80f86893fd004792c788/badge.svg)](https://www.versioneye.com/user/projects/58cc80f86893fd004792c788)

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


## Limitations

**GALOP is in the alpha stage of development.** This means that the proxy may
be unstable and not all functions are implemented that are necessary for use in
a production environment. Please note the following limitations in the current
functionality of GALOP.

- **No secure connections**: Currently neither TLS nor SSL is supported.
- **No error messages**: If the proxy does not understand an HTTP request or can
  not process an HTTP response, the connection is silently closed. Currently,
  the proxy does not send any error messages as HTTP responses to the client.
- **Chunked transfer encoding not supported**: Currently, the proxy can process
  only HTTP requests and responses with a known Content-Length or without an
  HTTP entity.
- **X-Forwarded-For not supported**: Currently, the proxy can not send the
  original IP addresses of the clients to the server via the HTTP header
  X-Forwarded-For.

These limitations will be solved before the first stable release.
During the development, this list will be constantly updated.


## License

GALOP is licensed under [The MIT License](https://opensource.org/licenses/MIT).
