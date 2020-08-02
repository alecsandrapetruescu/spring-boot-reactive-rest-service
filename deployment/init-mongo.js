//use admin;
db.createUser(
    {
        user : "user",
        pwd : "user",
        roles : [
            {
                role : "dbAdmin",
                db : "users"
            },
            {
               role : "readWrite",
               db : "users"
            }
        ]
    }
);

//db.users.save({ username: "test@gmail.com", lastname: "Raboy" });
//db.users.save({ firstname: "Maria", lastname: "Raboy" });