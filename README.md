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

#Installation
* Install [SBT][http://code.google.com/p/simple-build-tool/downloads/list] verison 0.7.5
* <code>$> cd /home/you/where/you/put/the/source</code> - cd to the root of the source directory
* <code>$> sbt</code> - Starts simple build tool console
* <code>$> update</code - Pull down all the dependencies
* <code>$> jetty</code> - Run
* Go to [http://localhost:8080] and you should see a basic page
##Tests
* <code>$> sbt</code> - Starts simple build tool console
* <code>$> test</code> - Exercises all tests
## License
Identity is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
  
Identity is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  
You should have received a copy of the GNU General Public License along with Identity. If not, see http://www.gnu.org/licenses/.