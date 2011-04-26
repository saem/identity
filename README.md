# Identity Service
Really simple, and in need of much securing, identity service. The idea is to keep it as close to, 'pass in a username and password', and get a principal back.

## Audience

In its current state, or what will be, Identity is meant to be used within a trusted network by trusted users. Identity and whatever family of services it's a part of would be consumed by other trusted services, who's responsibility it would be to keep the "bad" out.

# API
This is just a first pass and things are up for debate:

## URI Interface
    get / -> basic info about the server, that a consumer should know
    get /catalogues -> list of catalogues, which are groups of realms
    get /catalogues/:catalogue -> catalogue info
    get /catalogues/:catalogue/realms -> list of realms for this catalogue
    get /catalogues/:catalogue/realms/:realm -> realm info
    get /catalogues/:catalogue/realms/:realm/principals -> list of identity
    get /catalogues/:catalogue/realms/:realm/principals/:name -> identity info

Of course, there are some sane POST/PUT/DELETEs that need to be thrown in, and these need to be converted into tests, executable documentation FTW!

## Catalogue
    {
      "name":"catalogue.name",
      "token":"a SHA-1 of the name"
    }

## Realm
    {
      "name":"my.realm",
      "catalogue":{
        "catalogue":"some.catalogue", 
        "token":"a SHA-1 of the name"
      } 
    }

## Principal
    {
      "principal":"my name",
      "token":"a SHA-1 of the other values",
      {
        "name":"my.realm",
        "catalogue":{
          "catalogue":"some.catalogue",
          "token":"a SHA-1 of the name"
        } 
      }
    }

## Open Questions

* As indicated in the example above, we can have '.' in the realm and catalogue names, the question is whether to treate these in some special way and further namespace on them?

# TODO
* Moar Tests!
* Associate arbitrary meta-data with each data structure
* Use [ScalaCheck][http://code.google.com/p/scalacheck/] because it looks super neat

## Nice to Haves
* Support for optionally signed requests, via a shared key (tampering)
* Trusted requesters (chain of trust vs masturbation)
* HTTPS by default, with support for off-loading
* Lockout those hammering the service with continual failures (might be easier to configure on the web server with anti-DoS)
* Support for more backends (rather than the file backed stuff, front other systems like LDAP, etc...)

# Installation
* Install [SBT][http://code.google.com/p/simple-build-tool/downloads/list] verison 0.7.5
* <code>$> cd /home/you/where/you/put/the/source</code> - cd to the root of the source directory
* <code>$> sbt</code> - Starts simple build tool console
* <code>$> update</code - Pull down all the dependencies
* <code>$> jetty</code> - Run
* Go to [http://localhost:8080] and you should see a basic page

## Tests
* <code>$> sbt</code> - Starts simple build tool console
* <code>$> test</code> - Execute all tests

## License
Identity is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
  
Identity is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  
You should have received a copy of the GNU General Public License along with Identity. If not, see http://www.gnu.org/licenses/.

## Recommended

If you're ever working on an git+SBT project, such as this, use the following SBT processor: [https://github.com/paulp/git-sbt-processor]