
## Samples

### Launch in Dev Mode

Create a property file somewhere in your home directory, e.g. ``/home/user/eolaire-service.properties``:

```
# Make shutdown much faster, server will wait 100 milliseconds before stopping serving
# pending connections and shutting down
brikar.settings.gracefulShutdownMillis=100

# Turn off basic auth
brikar.dev.disableSecurity=true

# Uncomment to also fetch static content directly from your build folder
#brikar.dev.overrideStaticPath=/File_path_to/eolaire-app/eolaire-static-content/target/release/eolaireService/web
```

Then add VM property, e.g.: ``-Dbrikar.settings.path=file:/home/user/eolaire-service.properties``.

### Health Check

```
curl -u testonly:test -X POST 127.0.0.1:8080/rest/health
```

Should result in

```
OK
```

### Fetching Server Configuration

```
curl -u testonly:test -X POST 127.0.0.1:8080/g/admin/config
```
### Lookup

#### Item

```
curl -u testonly:test -H 'Accept: application/json; charset=UTF-8' -H 'Content-Type: application/json; charset=UTF-8' -X GET 127.0.0.1:8080/rest/eolaire/item/150 -s | python -mjson.tool
```


## How to create a file-based database

Create database and fill it with test data:

```
java -cp ~/.m2/repository/com/h2database/h2/1.4.183/h2-1.4.183.jar org.h2.tools.RunScript -url jdbc:h2:/tmp/eolairedb -user sa -script eolaire-server/src/main/resources/eolaireService/sql/eolaire-schema.sql
java -cp ~/.m2/repository/com/h2database/h2/1.4.183/h2-1.4.183.jar org.h2.tools.RunScript -url jdbc:h2:/tmp/eolairedb -user sa -script eolaire-server/src/main/resources/eolaireService/sql/eolaire-fixture.sql
```


Then you can connect to it using h2 shell (remove rlwrap if you don't want to use readline):

```
rlwrap java -cp ~/.m2/repository/com/h2database/h2/1.4.183/h2-1.4.183.jar org.h2.tools.Shell -url jdbc:h2:/tmp/eolairedb -user sa
```

### OS X Grunt Build Failure

Increase ``ulimit`` if you get ``>>> Error: EMFILE ...``.

```
ulimit -n 512
```
