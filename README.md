# Fetch Rewards Coding Exercise 
For the Backend Software Engineering Apprenticeship

## Table of Contents
- [Technologies Used](#tech_used)
- [Getting Started](#get_started)
  - [Run using Docker](#docker)
  - [Run using Maven](#maven)
- [REST API docs](#docs)

## Technologies Used <a name="tech_used"> </a>
- Java 17
- Maven
- Spring Framework
  - Dependencies
    - Spring Web
    - Spring Data JPA
    - Project Lombok
    - H2 Database
    - HATEOAS
## Getting Started <a name="get_started"></a>
This project can be run using either Maven or Docker, but Docker is the simplest to set up.
### Run using Docker <a name="docker"></a>
If you don't have Docker Desktop installed, follow the instructions for download according to your OS [here](https://docs.docker.com/get-docker/) and launch the client after installation.<br>

Assuming you have git installed, clone the repository. `git clone https://github.com/Jtmonument/fetch-rewards-exercise.git`

Switch to the working directory. `cd fetch-rewards-exercise`

<br>Use Docker to build an image. Replace `<image_name>` with whatever name you would like for the image. The build process will take a few minutes.
```
docker build -t <image_name> .
```

<br>Finally, run the image as a container with port 8080 open to your machine.
```
docker run -p 8080:8080 <image_name>
```
### Run using Maven <a name="maven"><a/>
Make sure you have [JDK 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) installed.
If you do not already have Maven installed, download it [here](https://maven.apache.org/install.html) and follow the instructions.<br><br>
After installing everything correctly, launch the terminal, switch to the working directory, and run the application.
```
cd fetch-rewards-exercise
mvn spring-boot:run
```
## REST API docs <a name="docs"></a>
Note: 
- For demonstration purposes, there is only one user with id=1. 
- The base URL for the web application is `http://localhost:8080/`.
- For demonstration purposes, there is no authentication.
- All quotes in the payload must be escaped (i.e. \"payer\") for the time being.
- For cURL commands, create line breaks on Linux/Unix systems with `\` and on Windows with `^`
### Open Endpoints
- Add transaction : Add a transaction to an account.
- Spend points : Spend an account's points.
- Check balance : Check an account's points balance.
#### Add transaction
URL : `/api/{id}/addTransaction/`<br>
Method : `POST`<br>
Data constraints :
```
{
  "payer": [string],
  "points": [integer],
  "timestamp": [timestamp in ISO 8601 format]
}
```
Data example :
```
{ 
  "payer": "DANNON", 
  "points": 1000, 
  "timestamp": "2020-11-02T14:00:00Z"
}
```
Success Response
Condition : No fields are zero, null, empty, or blank. Timestamp is of ISO 8601 format.
Content: Returns the same payload back to the client.
cURL example :
```
curl -X POST -H "Content-Type: application/json" \
-d  "{\"payer\": \"DANNON\", \"points\": 1000, \"timestamp\": \"2020-11-02T14:00:00Z\"}" \
http://localhost:8080/api/1/addTransaction/
```
#### Spend points
URL : `/api/{id}/spend/`<br>
Method : `PUT`<br>
Data constraints :
```
{
  "points": [integer] 
}
```
Data example : 
```
{
  "points": 5000  
}
```
cURL example :
Success Response
#### Check balance
URL : `/api/{id}/balance/`<br>
Method : `GET`<br>
No data constraints<br>
cURL example :
Success Response
