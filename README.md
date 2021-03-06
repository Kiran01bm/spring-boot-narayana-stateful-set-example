# Description

This application demonstrates usage of Narayana inside of a Spring Boot application running on OpenShift.

Standard Narayan Spring Boot application has its own transaction and recovery managers. However, because of volatile OpenShift pods it is not safe to have such a setup. In a scenario where pod is killed in a middle of a transaction, there would be impossible to recover uncompleted transaction. For that reason, this example extends current Narayana integration and requires application to run on an OpenShift stateful set. As a result, only a first pod will run recovery manager which will be responsible for recovering transactions of all other pods. It will access their transaction records from JDBC object store configured in a separate PostgreSQL deployment.

# Execution steps
1. Deploy database
```
oc new-app \
    -p POSTGRESQL_USER=narayana \
    -p POSTGRESQL_PASSWORD=narayana \
    -p POSTGRESQL_DATABASE=narayana \
    -p DATABASE_SERVICE_NAME=narayana-database \
    --name=narayana-database \
    --template=postgresql-persistent
```
2. Set environment variable
```
POSTGRESQL_MAX_PREPARED_TRANSACTIONS=100
```
3. Deploy this application to OpenShift
```
mvn clean fabric8:deploy -Dfabric8.mode=kubernetes
```
4. Scale-up application
```
oc scale statefulsets spring-boot-narayana-stateful-set-example --replicas=2
```
5. Get entries
```
curl http://spring-boot-narayana-stateful-set-example-myproject.192.168.64.3.nip.io/
```
6. Create new entry
```
curl -X POST http://spring-boot-narayana-stateful-set-example-myproject.192.168.64.3.nip.io/?entry=hello
```
7. Crash when creating entry
```
curl -X POST http://spring-boot-narayana-stateful-set-example-myproject.192.168.64.3.nip.io/?entry=kill
```
New entry 'kill' should appear after pod is restarted and recovery completes. Try killing different containers (requests are routed interchangeably between two pods).

# Undeploy application
```
oc delete statefulsets/spring-boot-narayana-stateful-set-example
```

# Check object store
1. Open PostgreSQL pod terminal
2. Connect (password: narayana)
```
psql -h narayana-database -U narayana
```
3. Select object store entries
```
select * from actionjbosststxtable;
```

# Clear object store
1. Open PostgreSQL pod terminal
2. Connect (password: narayana)
```
psql -h narayana-database -U narayana
```
3. Select object store entries
```
delete from actionjbosststxtable;
```
