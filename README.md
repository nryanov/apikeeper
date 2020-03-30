# apikeeper
Apikeeper - is an application which allows you to create, read and modify dependency graphs of your services.

## Build
To build project from sources you will need `npm` (10.+) and `sbt` (1.+).
You can use shell script located in the `bin` folder:
```shell script
git clone https://github.com/nryanov/apikeeper
cd ./apikeeper
./bin/install
``` 

When script finish you will find an executable jar `apikeeper.jar` in the project root folder.

## Run
To be able to run project you need an existing [Neo4j](https://neo4j.com/) instance with version 4.0.0 or higher.
By default project assumes that instance of Neo4j located on `localhost:7687/db/neo4j`.
```text
docker run --name=neo4j -p 7474:7474 -p 7687:7687 -e NEO4J_AUTH=none -d neo4j:4.0.0
```

To change this you can use environment variables or change path to the config:
```shell script
java jar -Dconfig.file=<PATH TO application.conf> apikeeper.jar
```

For more info about configuration see [Configuration](#configuration).

## Configuration
Project is configured using [HOCON](https://github.com/lightbend/config) files.

Default configuration:
```text
apikeeper {
    neo4jSettings {
        username = "neo4j"
        username = ${?APIKEEPER_NEO4J_USERNAME}
        password = "neo4j"
        password = ${?APIKEEPER_NEO4J_PASSWORD}
        uri = "bolt://localhost:7687/db/neo4j"
        uri = ${?APIKEEPER_NEO4J_URI}
    }

    serverSettings {
        port = 8080
        port = ${?APIKEEPER_LISTENING_PORT}
        host = "localhost"
        host = ${?APIKEEPER_HOST}
    }
}
```

## Features
Using simple Web-UI you can create and read service entities which can be `Service`, `Storage` or `MessageQueue`.
Each service entity may have many dependencies (`Downstream` and `Upstream`) on other entities. 

Sometimes it is hard to memorize all of them. Apikeeper allows you not only to save all you service entities with descriptions, but also to visualise dependency graph of each of them. 

## API
- index - host/index
- swagger doc - host/docs 
- api endpoints host/api/v1/{endpoint}
