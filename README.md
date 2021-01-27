# Weather Aggregator

Service for collecting weather information from various free services with REST or SOAP protocols.

## Supported services:
1) WeatherApi.com


## Instalation

- Checkout project from github repository:
```git clone https://github.com/fmaksim74/weatheraggregator.git```

- Build the project:
```mvn package -DskipTests```

- Copy ```/target/weatheraggregator*.jar/.../application.properties``` outside of jar file.
- Set weatherapicom.apikey option to your API-KEY.
- Start service:
```java -jar /target/....```

Now you can request to http://localhpst:8080/avgTempC/<location> for average temperature.


