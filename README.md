## User Administration - Restful Reactive Application
A demo project that provides:
* reactive CRUD APIs
* reactive authentication using a JWT token

#### Motivated by
* [The Reactive Manifesto](https://www.reactivemanifesto.org/)
* [Reactive Systems](https://www.youtube.com/watch?v=f-voUsRxw6c)

#### Technologies used
* Spring Boot - 2.4.0-SNAPSHOT
* Spring WebFlux
* Spring Security
* Spring Data Reactive MongoDb
* Spring Actuator
* [MongoDb](https://docs.mongodb.com/) - 4.2.8

#### Prerequisites
* [Java 8]() - 1.8.*
* [Docker](https://www.docker.com/) (Build and push images [install guide](https://docs.docker.com/install/))
    * Engine 19.03.8, 
    * Compose: 1.25.5

#### TODO: How to set up 

###### docker commands
```
> cd deployment // folder for docker-compose.yml

> docker-compose up

> docker-compose down

> docker system prune -a // removes any unused containers 

> docker volume prune // removes any unused volumes 

> docker logs container_id // preivew container logs

> docker exec -it container_id bash // enter a container
```

###### Running mongodb with docker
```
> docker exec -it mongodb bash
To launch the MongoDB shell client, execute the following
> mongo
> mongo -u <username> -p <password> --authenticationDatabase <database name>

mongodb://username:password@127.0.0.1:27017/database-name

> show dbs
> use database_name
> db
> db.users.save({ username: "test@gmail.com", lastname: "Raboy" })
> db.users.save({ firstname: "Maria", lastname: "Raboy" })

db.users.find({ firstname: "Nic" })

remove all documents in a collection
> db.users.remove({})
```