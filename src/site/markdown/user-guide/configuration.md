GALOP is configured via a [.properties](https://en.wikipedia.org/wiki/.properties)
file. This is a simple text file. Each line contains a single property.
Each property consists of a name and a value, separated by an equals sign.
A configuration file for GALOP must contain at least the following three
properties.

- **proxy.port:**
  When GALOP starts, it binds to this port number and waits for incoming requests.

- **target.address:**
  Each request is forwarded to the server with this address.
  The address can be an IP address or hostname.

- **target.port:**
  Each request is forwarded to this port number of the target server.

A minimal configuration file might look like this:

```
proxy.port=80
target.address=localhost
target.port=3000
```

In addition, the following optional properties are available.


## Proxy and target

- **proxy.backlog_size:**
  The requested maximum number of pending connections on the proxy socket.
  If a connection indication arrives when the maximum number is exceeded, the connection is refused.
  The value must be greater than 0.
  The default value is 50.
  The largest value that can be configured is 2147483647.
  The actual maximum number is platform-dependent and may be smaller.

- **proxy.bind_address:**
  The local address the proxy will bind to. If the address is set, the proxy
  will only accept connect requests to this address. If no address is set, the
  proxy will accept connections on all local addresses. By default no address
  is set. The address can be an IP address or hostname.

- **target.connection_timeout:**
  The maximum time that GALOP waits for the target server to establish a TCP connection.
  The time must be specified in milliseconds.
  The default value is 15000 milliseconds.
  If the value is set to zero, GALOP waits an unlimited time.
  The largest value that can be configured is 2147483647 milliseconds.


## Connections

- **http.connection.termination_timeout:**
  When GALOP is shut down, open connections are not closed until all open HTTP requests have been processed.
  This property can be used to configure the maximum time in milliseconds,
  after which all connections are closed even if HTTP requests are still open.
  The default value is 30000 milliseconds.
  If the value is set to zero, all open connections are immediately closed.
  The largest value that can be configured is 2147483647 milliseconds.

- **http.connection.log_interval:**
  In this interval, the current number of open connections is logged.
  The interval must be specified in milliseconds.
  The default value is 60000 milliseconds.
  The smallest value than can be configured is one millisecond.
  The largest value that can be configured is 2147483647 milliseconds.


## Request headers

- **http.header.request.receive_timeout:**
  The maximum time for the client to send a complete HTTP request header to the
  client. If the time is exceeded, the HTTP status code 408 (Request Time-out)
  will be sent to the client. The time must be specified in milliseconds.
  The default value is 60000 milliseconds.

- **http.header.request.request_line_size_limit:**
  The maximum allowed size of an HTTP request line in bytes. Any HTTP request
  that contains a larger request line is rejected by GALOP. The default value
  is 8192 bytes. The smallest size that can be configured is 64 bytes. The
  largest size that can be configured is 65536 bytes.

- **http.header.request.fields_limit:**
  The maximum allowed number of HTTP request header fields. Any HTTP request
  that contains more header fields is rejected by GALOP. The default value
  is 100. The smallest number that can be configured is 1. The largest number
  that can be configured is 65536.

- **http.header.request.field_size_limit:** The maximum allowed size of an HTTP
  request header field in bytes. The field size includes the length of the name,
  the value, and the separators between the name and the value. Any HTTP request
  that contains a larger header field is rejected by GALOP. The default value is
  8192 bytes. The smallest size that can be configured is 64 bytes. The largest
  size that can be configured is 65536 bytes.


## Response headers

- **http.header.response.receive_timeout:**
  The maximum time for the server to send a complete HTTP response header to
  the client. If the time is exceeded, the HTTP status code
  504 (Gateway Time-out) will be sent to the client. The time must be specified
  in milliseconds. The default value is 90000 milliseconds.

- **http.header.response.status_line_size_limit:**
  The maximum allowed size of an HTTP status line in bytes. Any HTTP response
  that contains a larger status line is rejected by GALOP. The default value is
  8192 bytes. The smallest size that can be configured is 64 bytes. The largest
  size that can be configured is 65536 bytes.

- **http.header.response.fields_limit:**
  The maximum allowed number of HTTP response header fields. Any HTTP response
  that contains more header fields is rejected by GALOP. The default value
  is 100. The smallest number that can be configured is 1. The largest number
  that can be configured is 65536.

- **http.header.response.field_size_limit:**
  The maximum allowed size of an HTTP response header field in bytes. The field
  size includes the length of the name, the value, and the separators between
  the name and the value. Any HTTP response that contains a larger header field
  is rejected by GALOP. The default value is 8192 bytes. The smallest size that
  can be configured is 64 bytes. The largest size that can be configured is
  65536 bytes.


## Example configuration

For example, a configuration file that overwrites all default values might
look like this:

```
proxy.port=80
proxy.backlog_size=100
proxy.bind_address=localhost

target.address=localhost
target.port=3000
target.connection_timeout=30000

http.connection.termination_timeout=120000
http.connection.log_interval=30000

http.header.request.receive_timeout=100000
http.header.request.request_line_size_limit=16384
http.header.request.fields_limit=50
http.header.request.field_size_limit=4096

http.header.response.receive_timeout=180000
http.header.response.status_line_size_limit=16384
http.header.response.fields_limit=75
http.header.response.field_size_limit=16384
```


## Logging

GALOP uses the [Log4j 2 framework](https://logging.apache.org/log4j/2.0/) for logging.
By default, all log entries are written to the default output stream.
Read the [Log4j 2 documentation](https://logging.apache.org/log4j/2.0/manual/configuration.html) to learn how to customize logging.
