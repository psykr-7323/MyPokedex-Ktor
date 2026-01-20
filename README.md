# MyPokedex Backend API

A secure, multi-tenant REST API built with Kotlin and Ktor.
This application allows users to register, log in, and manage their own private collection of Pokémon. It demonstrates full CRUD operations, Authentication, and Persistent Storage using a local H2 database.

## Technology Stack

* **Language:** Kotlin
* **Framework:** Ktor (Server Engine)
* **Database:** H2 (Embedded SQL)
* **ORM:** JetBrains Exposed
* **Security:** BCrypt (Hashing) + Session Authentication (Cookies)
* **Build Tool:** Gradle (Kotlin DSL)

## Key Features

* **Secure Authentication:** Implementation of session-based authentication. User passwords are salted and hashed using BCrypt before storage.
* **Multi-Tenancy:** Strict data isolation ensures users can only access and modify their own records.
* **Data Persistence:** Utilizes a file-based H2 database (`data.mv.db`) to ensure data survives server restarts.
* **CRUD Operations:**
    * **Create:** Capture new entities.
    * **Read:** Retrieve personal collections.
    * **Update:** Modify existing entity attributes (e.g., power level).
    * **Delete:** Remove entities from the database.

## API Reference

### Public Routes

| Method | Endpoint    | Description                             | Payload Example                            |
| :----- | :---------- | :-------------------------------------- | :----------------------------------------- |
| `POST` | `/register` | Create a new user account               | `{"username": "Ash", "password": "123"}`   |
| `POST` | `/login`    | Authenticate and receive session cookie | `{"username": "Ash", "password": "123"}`   |

### Protected Routes
*Note: These endpoints require a valid session cookie.*

| Method   | Endpoint          | Description                     | Payload Example                                                |
| :------- | :---------------- | :------------------------------ | :------------------------------------------------------------- |
| `POST`   | `/catch`          | Add a new Pokemon to collection | `{"name": "Pikachu", "type": "Electric", "powerLevel": 50}`    |
| `GET`    | `/pokedex`        | Retrieve user's collection      | *None* |
| `PATCH`  | `/train/{name}`   | Increase power level by 10      | *None* (Target specified in URL)                               |
| `DELETE` | `/release/{name}` | Remove Pokemon from collection  | *None* (Target specified in URL)                               |

## Getting Started

1.  **Clone the repository** to your local machine.
2.  Open the project in **IntelliJ IDEA**.
3.  Sync Gradle dependencies.
4.  Run `Main.kt`.
5.  The server will initialize at `http://0.0.0.0:8080`.
