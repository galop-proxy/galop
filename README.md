# GALOP

[![Build Status](https://travis-ci.org/SebastianSchmidt/galop.svg?branch=master)](https://travis-ci.org/SebastianSchmidt/galop)
[![Coverage Status](https://coveralls.io/repos/github/SebastianSchmidt/galop/badge.svg?branch=master)](https://coveralls.io/github/SebastianSchmidt/galop?branch=master)
[![Codebeat Status](https://codebeat.co/badges/3265f32b-9bec-419e-a421-f00cc87f0e65)](https://codebeat.co/projects/github-com-sebastianschmidt-galop-master)

GALOP (**G**raceful **A**pp**l**icati**o**n **P**roxy) is a simple reverse
proxy that accepts HTTP requests from clients and passes them to a server.
When shutting down, the proxy processes open HTTP requests before the
connections are closed. A persistent connection (HTTP keep-alive) is closed
after the current HTTP request has been answered. After this, the actual server
can be shut down without interrupting open HTTP requests.
