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


## License

GALOP is licensed under [The MIT License](https://opensource.org/licenses/MIT).
