<img src="http://wcm.io/images/favicon-16@2x.png"/> wcm.io Caravan Testing
======
[![Build Status](https://travis-ci.org/wcm-io-caravan/caravan-testing.png?branch=develop)](https://travis-ci.org/wcm-io-caravan/caravan-testing)

wcm.io Caravan - JSON Data Pipelining Infrastructure

![Caravan](https://github.com/wcm-io-caravan/caravan-tooling/blob/master/public_site/src/site/resources/images/caravan.gif)

Testing support for Caravan-based Micro Services.

Documentation: http://caravan.wcm.io/testing/<br/>
Issues: https://wcm-io.atlassian.net/<br/>
Wiki: https://wcm-io.atlassian.net/wiki/<br/>
Continuous Integration: https://travis-ci.org/wcm-io-caravan/caravan-testing/


## Build from sources

If you want to build wcm.io from sources make sure you have configured all [Maven Repositories](http://caravan.wcm.io/maven.html) in your settings.xml.

See [Travis Maven settings.xml](https://github.com/wcm-io-caravan/caravan-testing/blob/master/.travis.maven-settings.xml) for an example with a full configuration.

Then you can build using

```
mvn clean install
```
