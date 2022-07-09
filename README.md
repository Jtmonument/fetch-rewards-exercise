# Fetch Rewards Coding Exercise 
For the Backend Software Engineering Apprenticeship

## Table of Contents
- [Technologies Used](#tech_used)
- [Getting Started](#get_started)
  - [Running using Docker](#docker)

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
### Running using Docker <a name="docker"></a>
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
### Running using Maven
Make sure you have [JDK 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) installed.<br><br>
If you do not already have Maven installed, download it [here](https://maven.apache.org/install.html) and follow the instructions.<br><br>
After installing everything correctly, launch the terminal in Linux or cmd if you are on Windows
```
cd fetch-rewards-exercise
```
