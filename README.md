# Fetch Rewards Coding Exercise 
For the Backend Software Engineering Apprenticeship

## Table of Contents
- [Technologies Used](#tech_used)
- [Getting Started](#get_started)
  - [Run using Docker](#docker)
  - [Run using Maven](#maven)
- [REST API docs](#docs)
  - [Open Endpoints](#open_end)
    - [Add transaction](#transaction)
    - [Spend points](#spend)
    - [Check balance](#balance)
  - [Endpoints for testing and debugging](#debugging)
    - [Show Account](#show_acc)
    - [Delete History](#del_history)

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

<br>Use Docker to build an image. Replace `<image_name>` with whatever name you would like for the image. Don't forget the `.` for the current working directory. The build process will take a few minutes.
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
- For demonstration purposes, there is no authentication.
- The base URL for the web application is `http://localhost:8080/`
- All quotes must be escaped (i.e. `\"payer\"`) only when using cURL
### Open Endpoints <a name="open_end"></a>
- Add transaction : Add a transaction to an account.
- Spend points : Spend an account's points.
- Check balance : Check an account's points balance.
#### Add transaction <a name="transaction"></a>
URL : `/api/{id}/transaction/`<br>
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
```json
{ 
  "payer": "DANNON", 
  "points": 1000, 
  "timestamp": "2020-11-02T14:00:00Z"
}
```
<br>Note: Break lines in terminal with `\` for Linux and `^` for Windows 
<br>cURL example :
```shell
curl -X POST -H "Content-Type: application/json" \
-d  "{\"payer\": \"DANNON\", \"points\": 1000, \"timestamp\": \"2020-11-02T14:00:00Z\"}" \
http://localhost:8080/api/1/addTransaction/
```
Condition : No fields are zero, null, empty, or blank. Timestamp is of ISO 8601 format.
<br><br>
Success Response example :
```json
{
  "points": 1000,
  "timestamp": "2020-11-02T14:00:00Z",
  "payer": "DANNON",
  "_links": {
    "self": {
      "href": "http://localhost:8080/api/1/transaction/"
    }
  }
}
```
#### Spend points <a name="spend"></a>
URL : `/api/{id}/spend/`<br>
Method : `PUT`<br>
Data constraints :
```
{
  "points": [integer] 
}
```
Data example : 
```json
{
  "points": 5000  
}
```
Condition : The sum of payer points must greater than or equal to the points to spend. Points must not be zero or negative. 
<br><br>
cURL example :
```shell
curl -X PUT -H "Content-Type: application/json" \
-d  "{\"points\": 6000}" http://localhost:8080/api/1/spend/
```
Success Response example :
```json
{
  "_embedded": {
    "transactionImplList": [
      {
        "points": -100,
        "timestamp": "2022-07-13T19:51:45Z",
        "payer": "DANNON"
      },
      {
        "points": -200,
        "timestamp": "2022-07-13T19:51:45Z",
        "payer": "UNILEVER"
      },
      {
        "points": -4700,
        "timestamp": "2022-07-13T19:51:45Z",
        "payer": "MILLER COORS"
      }
    ]
  },
  "_links": {
    "self": {
      "href": "http://localhost:8080/api/1/spend/"
    }
  }
}
```
#### Check balance <a name="balance"></a>
URL : `/api/{id}/balance/`<br>
Method : `GET`<br>
No data constraints<br>
cURL example :
```shell
curl http://localhost:8080/api/1/balance/
```
Success Response example :
```json
{
  "_embedded": {
    "payerImplList": [
      {
        "points": 1000,
        "name": "DANNON"
      },
      {
        "points": 0,
        "name": "UNILEVER"
      },
      {
        "points": 5300,
        "name": "MILLER COORS"
      }
    ]
  },
  "_links": {
    "self": {
      "href": "http://localhost:8080/api/1/balance/"
    }
  }
}
```
### Endpoints for testing and debugging <a name="debugging"></a>
- Show Account : see the current payers and transactions associated with the account
- Delete History : delete all payers and transactions associated with the account
#### Show Account <a name="show_acc"></a>
URL : `/api/{id}/account/`<br>
Method : `GET`<br>
No data constraints<br>
cURL example :
```shell
curl http://localhost:8080/api/1/account/
```
Response example :
```json
{
  "points": 11300,
  "name": "root",
  "payers": [
    {
      "points": 1100,
      "name": "DANNON"
    },
    {
      "points": 200,
      "name": "UNILEVER"
    },
    {
      "points": 10000,
      "name": "MILLER COORS"
    }
  ],
  "transactions": [
    {
      "points": 1000,
      "timestamp": "2020-11-02T14:00:00Z",
      "payer": "DANNON"
    },
    {
      "points": 200,
      "timestamp": "2020-10-31T11:00:00Z",
      "payer": "UNILEVER"
    },
    {
      "points": -200,
      "timestamp": "2020-10-31T15:00:00Z",
      "payer": "DANNON"
    },
    {
      "points": 300,
      "timestamp": "2020-10-31T10:00:00Z",
      "payer": "DANNON"
    },
    {
      "points": 10000,
      "timestamp": "2020-11-01T14:00:00Z",
      "payer": "MILLER COORS"
    }
  ],
  "_links": {
    "self": {
      "href": "http://localhost:8080/api/1/account/"
    }
  }
}
```
#### Delete History <a name="del_history"></a>
URL : `/api/{id}/delete/`<br>
Method : `DELETE`<br>
No data constraints<br>
cURL example :
```shell
curl -X DELETE http://localhost:8080/api/1/delete/
```
Response example :
```json
{
  "points": 0,
  "name": "root",
  "payers": [],
  "transactions": [],
  "_links": {
    "self": {
      "href": "http://localhost:8080/api/1/account/"
    }
  }
}
```