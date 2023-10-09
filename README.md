# Account Server

A sample containerized project that provides API, for handling transactions. This project also showcases how to use view-based and hierarchical representation.

In this branch you'll find:
* API provided by **Spring Boot MVC** server that processes data stored in **Postgres** database. 
* Containerization of a server and database with the help of **Docker**. 
* Decreased boilerplate code with **Lombok**.
* Mock MVC tests written with **Mockito**.

## Compilation
To compile project use either `VS Code` tasks in following sequence: `maven package`,  `docker-compose restart`. 
Or CLI comands:
- `mvn clean package -DskipTests`
- `docker compose -f "./docker-compose.debug.yml" up -d --build`
