# Darkan World Server
The world server for Darkan that integrates with the lobby server.

[![discord][discord-badge]][discord-link] [![license][license-badge]][gnu-gpl-link] [![open-bugs][bug-badge]][bug-link] [![darkan](https://snapcraft.io/darkan/badge.svg)](https://snapcraft.io/darkan)

[discord-link]: https://discord.gg/Z32ggEB
[discord-badge]: https://img.shields.io/discord/118102728026095623?label=discord&logo=discord

[gnu-gpl-link]: https://www.gnu.org/licenses/gpl-3.0.en.html
[license-badge]: https://img.shields.io/badge/license-GPLv3-blue.svg

[bug-link]: https://github.com/titandino/darkan-world-server/issues
[bug-badge]: https://img.shields.io/github/issues-raw/titandino/darkan-world-server/bug?label=open%20bugs

## Setup steps

### Pre-requisites
- [Git](https://git-scm.com/download/win) (if on Windows, otherwise just use a package-manager like `pacman -S git` on Unix)
- [JDK-19](https://jdk.java.net/19/)
- [Git-LFS](https://git-lfs.github.com/ "Git-LFS")
- [MongoDB](https://docs.mongodb.com/manual/installation/ "MongoDB")
- [Gradle](https://gradle.org/install/ "Gradle")

### Project Setup
- Create a new folder on your machine called darkan wherever you want.
- Git clone the following repositories into it with these console commands:
```
git clone git@github.com:DarkanRS/cache.git
git clone git@github.com:DarkanRS/world-server.git
```
- Run the command `git lfs pull` within the darkan-cache project to get the packed information data file.
- You can optionally clone `git clone git@github.com:DarkanRS/client.git` too if you don't want to use the client loader.

### Running and Testing
- (Optional if not using the test-lobby-db) Make sure you have a MongoDB server running on your local machine or somewhere else (the connection URL for the Mongo server can be configured through the serverConfig.json file that gets generated after trying to run the world server once)
- Create an account on the test lobby server by running the client and clicking "Create Account Now" button at the bottom of the login screen.
- Run the world server with the command `./gradlew run` within the darkan-world-server project.
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
