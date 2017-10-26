<p align="center">
    <img src="logo.png" alt="GALOP" />
</p>

<p align="center">
    <strong>
        Simple reverse proxy for HTTP requests.<br />
        Closes connections gracefully when shutting down.
    </strong>
</p>

<p align="center">
    <a href="https://github.com/galop-proxy/galop/releases">
        <img src="https://img.shields.io/github/release/galop-proxy/galop.svg" alt="Release" />
    </a>
    <a href="https://github.com/galop-proxy/galop/blob/master/LICENSE">
        <img src="https://img.shields.io/github/license/galop-proxy/galop.svg" alt="License" />
    </a>
</p>

---

GALOP (**G**raceful **A**pp**l**icati**o**n **P**roxy) is a simple reverse proxy that accepts HTTP requests from clients and passes them to a server.
When shutting down, the proxy processes open HTTP requests before the connections are closed.
A persistent connection (keep-alive) is closed after the current HTTP request has been answered.
After this, the actual server can be shut down without interrupting open HTTP requests.


## Features

- Simple reverse proxy for HTTP 1.1 requests.
- Closes connections gracefully when shutting down by processing open HTTP
  requests before terminating a connection.
- Supports chunked transfer encoding.


## Limitations

**GALOP is in the alpha stage of development.**
This means that the proxy may be unstable and not all functions are implemented that are necessary for use in a production environment.
Please note the following limitations in the current functionality of GALOP.

- **No Plugin API:** The proxy can not be extended via plugins.
  *(Plugin API scheduled for version 0.9.0)*
- **X-Forwarded-For not supported:** The proxy can not send the original
  IP addresses of the clients to the server via the HTTP header X-Forwarded-For.
  *(Support scheduled for version 0.10.0)*
- **No HTTP persistent connections:** The proxy closes connections
  immediately after the HTTP response to a HTTP request has been submitted.
  *(Support scheduled for version 0.10.0)*
- **HTTP 1.0 not supported:** Only HTTP protocol version 1.1 is supported.
  *(HTTP 1.0 support scheduled for version 0.11.0)*
- **No secure connections:** Currently neither TLS nor SSL is supported.

These limitations will be solved before the first stable release.
During the development, this list will be constantly updated.


## User Guide

* [Installation](src/site/markdown/user-guide/installation.md)
* [Configuration](src/site/markdown/user-guide/configuration.md)
* [Changelog](CHANGELOG.md)


## License

GALOP is licensed under [The MIT License](https://opensource.org/licenses/MIT).
