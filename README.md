# Account Server

A sample containerized project that provides API, for handling transactions.

In this branch you'll find:
* API provided by **Spring Boot MVC** server that processes data stored in **Postgres** database. 
* Containerization with the help of **docker compose**. 
* Decreased boilerplate code with **Lombok**.
* Mock MVC tests written with **Mockito**.

## Compilation
To compile project use either `VS Code` tasks in following sequence: `maven package`,  `docker-compose restart`. 
Or CLI comands:
- `mvn clean package -DskipTests`
- `docker compose -f "./docker-compose.debug.yml" up -d --build`
