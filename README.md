#Identity Service

Really simple, and in need of much securing, identity service. The idea is to keep it as close to as simple as, pass in a username and password, and get a principal back.

#TODO
* Support for optionally signed requests, via a shared key (tampering)
* Trusted requesters
* HTTPS by default, with support for off-loading
* Lockout those hammering the service with continual failures (might be easier to configure on the web server with anti-DoS)
* Support for more backends