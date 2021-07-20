# Darkan Game Server
Darkan is a 2012 pre-EOC remake of RuneScape using 727. It was originally created from a fresh de-obfuscation of the 727 client, a fresh 727 cache and the matrix base. The code source is currently being updated and re-factored to allow contributors to append to source code while keeping the integrity of the game. For this reason the lobby server will not be posted on github. This way people do not leech or edit player files. 

<img src="https://media.discordapp.net/attachments/460272484608901120/858576715475451954/unknown.png">

Cache link: https://mega.nz/#!1pc2mCSA!0YGah4Ou46TeZPlVWNf5Tt_rzV0BXfs72N-BI_Y7Tz0

Images link (for public/images/items): https://mega.nz/#!wodDTKwD!CwMVEU1ctVy1ZFhmHN4TVeT_TaPpMrHsHiHcmVxzx6Y

Related repos
https://github.com/titandino/darkan-cache
https://github.com/titandino/darkan-core
https://github.com/titandino/darkan-world-server
https://github.com/titandino/darkan-game-client

## How to set up your local source
### Pre-requisites
- [Git-LFS](https://git-lfs.github.com/ "Git-LFS")
- [MongoDB](https://docs.mongodb.com/manual/installation/ "MongoDB")
- [Gradle](https://gradle.org/install/ "Gradle")

### Project Setup
- Create a new folder on your machine called darkan wherever you want.
- Git clone the following repositories into it with these console commands:
```
git clone git@github.com:titandino/darkan-cache.git
git clone git@github.com:titandino/darkan-core.git
git clone git@github.com:titandino/darkan-world-server.git
git clone git@github.com:titandino/darkan-game-client.git
```
- Run the command `git lfs pull` within the darkan-cache project to get the packed information data file.

### Running and Testing
- (Optional if not using the test-lobby-db) Make sure you have a MongoDB server running on your local machine or somewhere else (the connection URL for the Mongo server can be configured through the serverConfig.json file that gets generated after trying to run the world server once)
- Create an account on the test lobby server using the following CURL command:
`curl -X POST -H "Content-Type: application/json" -d '{ "username": "DESIRED_USERNAME", "password": "DESIRED_PASSWORD", "email": "ANY_EMAIL_ADDRESS" }' http://testlobby.darkan.org:4040/api/createaccount`
- Run the world server with the command `gradle run` within the darkan-world-server project.
- Load up the client and login with the account details you created. (You login with the username, not the email address)

### Extra notes
If using Eclipse to edit the projects, be sure to import them all as new Gradle projects.

If you don't know how to run a mongodb instance, running using these exact steps will allow you to run the world server without making any changes to the default config file:
- Create a new folder called `mongo` somewhere and create a `mongod.conf` file with the following contents:
```
systemLog:
   destination: file
   path: "./mongod.log"
   logAppend: true
net:
   bindIp: "0.0.0.0"
   port: 27017
storage:
   dbPath: "./baserino/"
```

- You can then create a `start.sh` (Unix) or `start.bat` (Windows) file and add the following command to it to easily start up the Mongo server whenever you want:
 `mongod --config ./mongod.conf`
- You may also need to create the `baserino` data folder within that `mongo` folder as well. I am not sure if MongoDB creates it automatically or not.

## Contributing to Darkan
Pending refactor(Make fork then PR)
