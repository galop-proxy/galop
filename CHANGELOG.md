# Changelog


## 0.4.2 - 2017-05-30

### Fixed

- Name of the monitor thread corrected.


## 0.4.1 - 2017-05-22

### Fixed

- If an error occurs while transferring the server's HTTP response to the client,
  GALOP is trying to send a suitable HTTP status code to the client. If parts of
  the HTTP response have already been sent to the client, so far they are mixed
  with this HTTP status code. Instead, GALOP now close the connection so that no
  invalid response is sent to the client.


## 0.4.0 - 2017-05-19

### Added

- If the proxy can not establish a TCP connection with the server within a
  configured time, the proxy sends the HTTP status code 504 (Gateway Time-out)
  to the client.
- If a connection to the server can not be established, the proxy sends the
  HTTP status code 503 (Service Unavailable) to the client.
- If the client does not send a full HTTP request header to the server within
  a configured time, the proxy sends the HTTP status code 408 (Request Time-out)
  to the client.
- If the server does not send a full HTTP response header to the client within
  a configured time, the proxy sends the HTTP status code 504 (Gateway Time-out)
  to the client.
- The requested maximum number of pending connections on the proxy socket can be
  configured.
- The local address the proxy socket will bind to can be configured.
  
### Changed

- The maximum allowed size of an HTTP header can be configured separately for
  requests and responses.
- The property `security.max_http_header_size` has been replaced by the new
  properties `http.header.request.max_size` and `http.header.response.max_size`.
- The following configuration properties have been renamed:
    - `proxy_port` to `proxy.port`
    - `target_address` to `target.address`
    - `target_port` to `target.port`
    - `connection_handlers.termination_timeout` to `http.connection.termination_timeout`
    - `connection_handlers.log_interval` to `http.connection.log_interval`


## 0.3.1 - 2017-04-12

### Changed

- Apache Log4j updated to version 2.8.2.


## 0.3.0 - 2017-03-22

### Added

- Support for chunked transfer encoding.
- When GALOP is started, the following events are additional logged:
  - Loading of the configuration file and the result.
  - The initialization of the server socket.


## 0.2.0 - 2017-03-20

### Added

- If the client sends an invalid HTTP request or the server sends an invalid
  HTTP response, the proxy now sends an error message to the client.
- If an error occurred while processing the server response, the proxy sends
  the HTTP status code 502 (Bad Gateway) to the client and logs the error.


## 0.1.0 - 2017-03-19

First release!
