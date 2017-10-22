GALOP (Graceful Application Proxy) is a simple reverse proxy that accepts HTTP requests from clients and passes them to a server.
When shutting down, the proxy processes open HTTP requests before the connections are closed.
A persistent connection (keep-alive) is closed after the current HTTP request has been answered.
After this, the actual server can be shut down without interrupting open HTTP requests.


## Features

- Simple reverse proxy for HTTP 1.1 requests.
- Closes connections gracefully when shutting down by processing open HTTP
  requests before terminating a connection.
- Supports chunked transfer encoding.


## Limitations

GALOP is in the alpha stage of development.
This means that the proxy may be unstable and not all functions are implemented that are necessary for use in a production environment.
Please note the following limitations in the current functionality of GALOP.

- **No Plugin API:** The proxy can not be extended via plugins.
- **X-Forwarded-For not supported:** The proxy can not send the original IP addresses of the clients to the server via the HTTP header X-Forwarded-For.
- **No HTTP persistent connections:** The proxy closes connections immediately after the HTTP response to a HTTP request has been submitted.
- **HTTP 1.0 not supported:** Only HTTP protocol version 1.1 is supported.
- **No secure connections:** Currently neither TLS nor SSL is supported.

These limitations will be solved before the first stable release.
During the development, this list will be constantly updated.
