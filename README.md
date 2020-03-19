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
$ curl -s http://aee4f39d9c48b4c02acd1f313ad94374-1483124477.us-east-2.elb.amazonaws.com:8080/weather/Sofia | jq .
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

### Helm Chart
A Helm chart is also added in weatherapp folder.
You can deploy the application using the command: ` helm install --namespace=weather weatherapp ./weatherapp`

After that the application is deployed in the namespace `weather`

```
 kubectl --namespace=weather get all -o wide
NAME                              READY   STATUS    RESTARTS   AGE     IP             NODE                                        NOMINATED NODE   READINESS GATES
pod/weatherapp-564bd48d75-r9bvz   1/1     Running   0          2m47s   172.31.5.177   ip-172-31-6-95.us-east-2.compute.internal   <none>           <none>
pod/weatherapp-564bd48d75-rsdgz   1/1     Running   0          2m47s   172.31.0.192   ip-172-31-6-95.us-east-2.compute.internal   <none>           <none>

NAME                 TYPE           CLUSTER-IP     EXTERNAL-IP                                                               PORT(S)          AGE     SELECTOR
service/weatherapp   LoadBalancer   10.100.49.25   aee4f39d9c48b4c02acd1f313ad94374-1483124477.us-east-2.elb.amazonaws.com   8080:31432/TCP   2m47s   app.kubernetes.io/instance=weatherapp,app.kubernetes.io/name=weatherapp

NAME                         READY   UP-TO-DATE   AVAILABLE   AGE     CONTAINERS   IMAGES                                                          SELECTOR
deployment.apps/weatherapp   2/2     2            2           2m47s   weatherapp   733339657127.dkr.ecr.us-east-2.amazonaws.com/weatherapp:0.1.3   app.kubernetes.io/instance=weatherapp,app.kubernetes.io/name=weatherapp

NAME                                    DESIRED   CURRENT   READY   AGE     CONTAINERS   IMAGES                                                          SELECTOR
replicaset.apps/weatherapp-564bd48d75   2         2         2       2m48s   weatherapp   733339657127.dkr.ecr.us-east-2.amazonaws.com/weatherapp:0.1.3   app.kubernetes.io/instance=weatherapp,app.kubernetes.io/name=weatherapp,pod-template-hash=564bd48d75
```
