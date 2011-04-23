#Identity Service

Really simple, and in need of much securing, identity service. The idea is to keep it as close to as simple as, pass in a username and password, and get a principal back.

#API
This is just a first pass and things are up for debate:

get / -> basic info about the server, that a consumer should know
get /catalogues -> list of catalogues, which are groups of realms
get /catalogues/:name -> catalogue info
get /catalogues/:name/realms -> list of realms for this catalogue
get /catalogues/:name/realms/:name -> realm info
get /catalogues/:name/realms/:name/identities -> list of identity
get /catalogues/:name/realms/:name/identities/:name -> identity info

Of course, there are some sane POST/PUT/DELETEs that need to be thrown in, and these need to be converted into tests, executable documentation FTW!

#TODO
* Tests
* Support for optionally signed requests, via a shared key (tampering)
* Trusted requesters (chain of trust vs masturbation)
* HTTPS by default, with support for off-loading
* Lockout those hammering the service with continual failures (might be easier to configure on the web server with anti-DoS)
* Support for more backends (rather than the file backed stuff, front other systems like LDAP, etc...)