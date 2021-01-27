# Weather Aggregator

Service for collecting weather information from various free services with REST or SOAP protocols.

## Supported services:
1) WeatherApi.com


## Installation

- Checkout project from github repository:
```git clone https://github.com/fmaksim74/weatheraggregator.git```
- Goto ```weatheraggregator``` folder
- Build the project:
```mvn package -DskipTests```
- Copy ```./target/weatheraggregator-0.0.1-SNAPSHOT.jar/BOOT-INFclasses/application.properties``` outside of jar file into ```./target```.
- Set weatherapicom.apikey option to your API-KEY in ```application.properties``` file.
- Start service:
```java -jar ./target/weatheraggregator-0.0.1-SNAPSHOT.jar```

Now you can request to ```http://localhost:8080/avgTempC/<location>``` for average temperature.


