# Changelog


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
  the HTTP status code 502 Bad Gateway to the client and logs the error.


## 0.1.0 - 2017-03-19

First pre-release!