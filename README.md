# Home Search Service
Home Stack Search Service

## Build
### Set JAVA_HOME (in case mvn run through terminal)
```shell
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home
export PATH=$JAVA_HOME/bin:$PATH
```
### Build Application image

### How to run
````
java -jar target/home-search-service-1.0.0.jar
````

#### Build
1. Maven Package
   ```shell
   mvn clean package -DskipTests
   ```
2. Docker Build, Push & Run
   ```shell
   docker build -t alokkusingh/home-search-service:latest -t alokkusingh/home-search-service:1.0.0 --build-arg JAR_FILE=target/home-search-service-1.0.0.jar .
   ```
   ```shell
   docker push alokkusingh/home-search-service:latest
   ```
   ```shell
   docker push alokkusingh/home-search-service:1.0.0
   ```
   ```shell
   docker run -d -p 8081:8081 --rm --name home-search-service alokkusingh/home-search-service
   ```
### Test
```shell
curl -X GET http://localhost:8081/home/search/transactions?description=avinash
```