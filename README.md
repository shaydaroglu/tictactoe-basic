# Tic Tac Toe API

Small Spring Boot project for playing Tic Tac Toe through a REST API.

It supports:
- creating a game
- making moves
- reading the current board state
- persisting game data in H2
- basic protection against concurrent updates

## Tech Stack

- Java 21
- Spring Boot
- Spring Web MVC
- Spring Data JPA
- H2
- springdoc OpenAPI / Swagger UI

## Running the Project

From the project root:

```bash
./mvnw spring-boot:run
```

If you just want to run the tests:

```bash
./mvnw test
```

## Swagger UI

Once the application is running:

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- H2 Console: `http://localhost:8080/h2-console`

## Main Endpoints

Create a game:

```http
POST /api/v1/game
Content-Type: application/json

{
  "playerOneSymbol": "X"
}
```

Make a move:

```http
POST /api/v1/game/{id}/moves
Content-Type: application/json

{
  "player": "PLAYER_1",
  "position": 0
}
```

Get a game:

```http
GET /api/v1/game/{id}
```

## Notes

- Board positions go from `0` to `8`
- `PLAYER_1` chooses the starting symbol, `PLAYER_2` gets the other one
- A game ends with a win or a draw after the ninth move

## Testing

The test suite covers:
- game and move service logic
- controller and error handling
- mapper behavior
- persistence constraints
- optimistic locking
- end-to-end game flow through Spring MVC

## Project Structure

- `adapter/in/web` for controllers and API DTOs
- `application/service` for game logic
- `adapter/out/persistence` for JPA persistence
- `domain/model` for core domain types

This was built as a take-home style exercise, so the code aims to stay simple and readable.
