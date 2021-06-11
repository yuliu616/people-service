# People Service

> code name: `people-service`.

A micro-service for managing people info.

## Framework used

- Spring Boot 2.3
- MyBatis 3.5

## Environment (dependency)

- Java 8+ (runtime)
- MySQL 5.7+ (service = hellodb:3306)
  - database table name prefix: `ppl_`.
  - db user: `ppl_dbuser`.
  - schema files: `/db`.
- service:
  - auth-service@1.0.x (service = auth-service:8080)

## Development Setup

- maven

## Configuration

> this project use spring-boot based app config,
> the config file is defined in `/src/main/resources/application.yaml`

Some key configuration parameter:

- `people-service.options.enable-debug-endpoint`: if true, expose debug-use(high risk) endpoint.
- `people-service.options.accept-expired-auth-token`: if true, auth token expiry time will be ignored.
- `auth-service.jwt.public-key-PEM`: public key (in PEM) for verifying JWT token.

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

# Docker support

## building docker image

- after building the app (maven), run this:

```sh
docker build -t people-service:1.0 .
```
