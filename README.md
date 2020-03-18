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
 `http://<BASE_SERVICE_URL>/weather/{city}` to: `api.openweathermap.org/data/2.5/forecast?appid=....&units=metric&q={city}`
... well this will give us 5 days forecast instead of 3, but ... good enough for a start.

To build the service use: `mvn clean install`

### Packing it in a container
 - Use an official OpenJRE as a parent image (alpine based)
 - Pack the Uber Jar and run it on container startup
 
To build the container use: `docker build --tag=weather-app .`

### Deployment
I chose AWS EKS for my needs.
A standard cluster:
![](https://raw.githubusercontent.com/htrendafilov/task-weather/master/images/screen1.png)
With 2 nodes t3.micro:
![](https://raw.githubusercontent.com/htrendafilov/task-weather/master/images/screen2.png)

After pushing the image to **ECR** we can use `kubectl apply ...` to deploy the deployment, including 2 pods and a service to expose them.

### Test it
```
$ curl -s http://ae94f46cf522f48d6b46eab22da28b60-1816265173.us-east-2.elb.amazonaws.com:8080/weather/Sofia | jq .
{
  "cod": "200",
  "message": 0,
  "cnt": 40,
  "list": [
    {
      "dt": 1584576000,
      "main": {
        "temp": -2.41,
        "feels_like": -5.8,
        "temp_min": -2.41,
        "temp_max": -0.02,
        "pressure": 1031,
        "sea_level": 1031,
        "grnd_level": 914,
        "humidity": 78,
        "temp_kf": -2.39
      },
      "weather": [
        {
          "id": 800,
          "main": "Clear",
          "description": "clear sky",
          "icon": "01n"
        }
      ],
      "clouds": {
        "all": 0
      },
      "wind": {
        "speed": 1.01,
        "deg": 325
      },
      "sys": {
        "pod": "n"
      },
      "dt_txt": "2020-03-19 00:00:00"
    },
    {
      "dt": 1584586800,
      "main": {
        "temp": -2.09,
        "feels_like": -5.49,
        "temp_min": -2.09,
        "temp_max": -0.3,
        "pressure": 1030,
        "sea_level": 1030,

```
