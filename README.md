# Board Game Web Application

Demo Mangala/Khala like web application.

## Installation
Application uses 8080 port. It can be configured in "application.properties" file.


* The application is Dockerized. To spin up run :

```bash
docker-compese up
```
* The application is Maven application. To build run :

```bash
mvn clean install
```

## Details
* Application Backend is implemented in Java with SpringBoot. 
* Frontend is implemented in Javascript with Vuejs.
* Maven build merges the two layer and packages into "jar" file. This produced file can bu run as "java -jar"

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

## License
[MIT](https://choosealicense.com/licenses/mit/)
