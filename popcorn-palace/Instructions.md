# 🧾 Instructions.md

## 🎬 Popcorn Palace - Movie Ticket Booking System

Welcome to the backend system for **Popcorn Palace**, a ticket booking service designed to manage movies, showtimes, and bookings.  
This guide walks you through setting up and running the application locally.
Make sure you read .md

---

## Assumptions
- `Assumptions.md` is a document serves to explain the assumptions that has been made during the development, so as additional information that was important to note.

## 📦 Tech Stack

- **Java 21**
- **Spring Boot 3.4.2**
- **PostgreSQL (via Docker)**
- **Maven** – Build & Dependency Management
- **JUnit + Mockito** – Unit & Integration Testing

---
## Prerequisite
1. Java SDK - https://www.oracle.com/java/technologies/downloads/#java21
2. Java IDE - https://www.jetbrains.com/idea/download
3. Docker - https://www.docker.com/products/docker-desktop/


## 📥 Cloning the Project from Git

To get started, clone the project from the Git repository:

```bash
git clone https://github.com/NaorCohenQ/Cinema-Booking
cd popcorn-palace
```


## 🐳 Docker Setup (PostgreSQL)

This project includes a `compose.yml` file to easily spin up a PostgreSQL container.

### 🔧 Start the Database

1. Open Docker Desktop
2. Open terminal and navigate to the project root (cd popcorn-palace):

```bash
docker-compose -f compose.yml up -d
```

3. To enter the PostgreSQL shell, run:
```bash
docker ps        
```

4. Now you should find the container-id, here is an output for example:
```bash
CONTAINER ID   IMAGE      COMMAND                  CREATED              STATUS              PORTS                    NAMES
387106961f73   postgres   "docker-entrypoint.s…"   About a minute ago   Up About a minute   0.0.0.0:5432->5432/tcp   popcorn-palace-db-1
```
5. now copy the CONTAINER ID and run:
```bash
docker exec -it <container-id> psql -U popcorn-palace
```
---

## 🚀 Running the Application

Ensure Docker is running and the previous step worked well, then start the app with:

**Option 1:**
Open your terminal (Git Bash recommended) than run:
```bash
./mvnw spring-boot:run
```

**Option 2:**
Run it manually through the IDE (SHIFT + F10 in intelliJ)

---

## 🧪 Running Tests
**Option 1:**
Open git bash than run unit and integration tests:

```bash
./mvnw test
```

**Option 2:**
Run it manually through the IDE. open the following folder : test->java the right click on java folder and press "Run 'All Tests'"

---

---

## 📥 Example: Add a Movie (Using cURL)

After running the server you can use requests yourself, doing the following:
Open git bash (in another terminal), inside popcorn-palace. Now you can, for example, add a movie:

### Request

```bash
curl -i -X POST http://localhost:8080/movies -H "Content-Type: application/json" -d '{"title": "Gladiator 2", "genre": "Action", "duration": 165, "rating": 8.3, "releaseYear": 2024}'
```

Now you should receive the following response:

HTTP/1.1 200
Content-Type: application/json
Transfer-Encoding: chunked
Date: Thu, 27 Mar 2025 23:25:49 GMT

{"id":6,"title":"Gladiator 2","genre":"Action","duration":165,"rating":8.3,"releaseYear":2024}


## 📬 API Endpoints

Full API details are documented in the `README.md`.Including Movies API , Showtimes API and Bookings API.

---

## 💡 Notes

- PostgreSQL DB configuration is already set in `application.yaml`
- The data has been initialized as can bee seen in data.sql
- A new endpoints `GET /showtimes/all` and `GET /movies/{title}` had been added to the API's.
- make sure you clone the repository before running,as some original files has been modified (pom.xml for example)
- `Assumptions.md` highlights some important issues and reasoning for assumptions that has been made.
