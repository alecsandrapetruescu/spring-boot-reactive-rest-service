#### TODO: How to set up

###### docker commands

```
> cd deployment // folder for docker-compose.yml

> docker compose up

> docker compose down

> docker system prune -a // removes any unused containers 

> docker volume prune // removes any unused volumes 

> docker logs container_id // preivew container logs

> docker exec -it container_id bash // enter a container
```

###### Running mongodb with docker

- Enter the container

```
docker exec -it mongo_container bash
```

- To launch the MongoDB shell client, execute the following

```
mongosh --username <username> --authenticationDatabase admin
```

- Lists all databases

```
show dbs
```

- Select the database on which to perform an action (eg. `use database_name`)

```
use users
```

- Displays the database you are using

```
db
```

- Insert 2 documents in the collection

```
db.users.insertMany([{ username: "first entry" }, { username: "second entry"}])
```

- Search in the database one entry

```
db.users.find().limit(1)
```

- Update one document by id

```
db.users.updateOne({ _id: ObjectId("63e3959ccaa23aaa345f9f5a")}, { $set: {name: "First entry" }} )
```

- Remove all documents in a collection

```
> db.users.deleteMany({})
```
