# People Service

> code name: `people-service`.

A mirco-service for managing people info.

## Framework used

- Spring Boot 2.3
- MyBatis 3.5

## Environment (dependency)

- Java 8+
- MySQL 5.7+
  - database table name prefix: `ppl_`.
  - db user: `ppl_dbuser`.
  - schema files: `/db`.
- service:
  - auth-service@1.0.x

## Development Setup

- maven

## Configuration

> this project use spring-boot based app config,
> the config file is defined in `/src/main/resources/application.yaml`

Some key configuration parameter:

- `peopleService.options.enableDebugEndpoint`: if true, expose debug-use(high risk) endpoint.
- `peopleService.options.acceptExpiredAuthToken`: if true, auth token expiry time will be ignored.
- `authService.jwt.publicKey_PEM`: public key (in PEM) for verifying JWT token.

## Debugging

```
mvn spring-boot:run
```

## Building

```
mvn package
```

## Running (production)

```
java -jar target/people-service-1.0.0.jar 
```

## Entry points

> detail refer to `/doc/people-service.raml`

## Health checking

```
curl 'http://127.0.0.1:8082/api/about'
```
