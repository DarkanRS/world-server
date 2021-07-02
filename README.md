# Darkan World Server
The world server for Darkan that integrates with the lobby server.

## Setup steps

### Pre-requisites
- [Git-LFS](https://git-lfs.github.com/ "Git-LFS")
- [MongoDB](https://docs.mongodb.com/manual/installation/ "MongoDB")
- [Gradle](https://gradle.org/install/ "Gradle")

### Project Setup
- Create a new folder on your machine called darkan wherever you want.
- Git clone the following repositories into it with these console commands:
  ```bash
git clone git@github.com:titandino/darkan-cache.git
git clone git@github.com:titandino/darkan-core.git
git clone git@github.com:titandino/darkan-world-server.git
git clone git@github.com:titandino/darkan-game-client.git
```
- Run the command `git lfs pull` within the darkan-cache project to get the packed information data file.

### Running and Testing
- Create an account on the test lobby server using the following CURL command:
`curl -X POST -H "Content-Type: application/json" -d '{ "username": "DESIRED_USERNAME", "password": "DESIRED_PASSWORD", "email": "ANY_EMAIL_ADDRESS" }' http://testlobby.darkan.org:4040/api/createaccount`
- Run the world server with the command `gradle run` within the darkan-world-server project.
- Load up the client and login with the account details you created. (You login with the username, not the email address)

### Extra notes
If using Eclipse to edit the projects, be sure to import them all as new Gradle projects.
