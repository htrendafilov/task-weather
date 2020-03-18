# Weather App Task

## Task definition
Implement a Java based REST API for the Weather, which:
- Uses a 3rd party weather forecast provider e.g. https://openweathermap.org/current
- Provides a 3-day forecast for a requested location
- Is deployed on a public cloud provider
- Is containerized and deployed in k8s through a Helm chart
- Is publicly exposed(ingress based)
- Is higly available, spread over AZs
- Supports update through helm

The API may support some of the following features:
- Provides a 2-day weather statistics for a requested location(pick 3 locations)
- Is basic auth protected(optional)

## Steps to implememt
### Spring Boot + Uber Jar Simple Service
As deployment part is heavier, initially make a very simple Spring service, that is in fact only a proxy from: 
 ```http://<BASE_SERVICE_URL>/weather/{city}```
to: ```api.openweathermap.org/data/2.5/forecast?appid=....&units=metric&q={city}```
... well this will give us 5 days forecast instead of 3, but ... good enough for a start.

To build the service use: ```mvn clean install```

### Packing it in a container
