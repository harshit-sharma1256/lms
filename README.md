# Library Management System

This Library Management System is a RESTful application built using Java 17 and Micronaut 3.10.1.The application allows for managing a collection of books, tracking borrowing and returning of books, and providing simple search and reporting functionalities. The project utilizes **Gradle** as the build tool and is connected to a **PostgreSQL** database and has been designed with a package structure that separates controllers, services, repositories, entities, and exception handling.

## Features

- **Book Management**: Add, update, delete, and search for books.
- **Search Capabilities**: Find books by author, title, publication year range, or a combination of title and year range.
- **Validation**: Input validation for book details, with warnings and error handling for specific cases (e.g., zero quantity).
- **Report Generation**: Allows for categorizing books by genre and generating reports of borrowed books.



## Endpoints
<img width="837" alt="Screenshot 2024-11-26 at 1 21 29 AM" src="https://github.com/user-attachments/assets/16206f1f-9d0a-4dfd-8524-a8d2ded16812">




## Technology Stack

- **Java 17**
- **Micronaut 3.10.1**
- **Gradle**: Build tool
- **JUnit**: Unit testing framework
- **Mockito**: Mocking framework for testing

## Prerequisites

- **Java**: Ensure Java 17 is installed.
- **Docker**: Used to run PostgreSQL instances (configured to run in Colima).
- **Micronaut CLI**: Recommended for easier setup and project management.

## Project Structure

The project is organized as follows:

```
src
├── main
│   ├── java
│   │   └── com
│   │       └── harshit
│   │           ├── Application.java
│   │           ├── controller
│   │           │   ├── BookController.java
│   │           │   ├── BorrowingController.java
│   │           │   └── MemberController.java
│   │           ├── entity
│   │           │   ├── Book.java
│   │           │   ├── Borrowing.java
│   │           │   └── Member.java
│   │           ├── exception
│   │           │   ├── BookNotAvailableException.java
│   │           │   └── EntityNotFoundException.java
│   │           ├── repository
│   │           │   ├── BookRepository.java
│   │           │   ├── BorrowingRepository.java
│   │           │   └── MemberRepository.java
│   │           └── service
│   │               ├── BookService.java
│   │               ├── BorrowingService.java
│   │               └── MemberService.java
│   └── resources
│       ├── application.yml
│       └── simplelogger.properties
└── test
    └── java
        └── com
            └── harshit
                ├── LibraryTest.java
                ├── controller
                ├── entity
                ├── exception
                ├── repository  
                └── service
                    ....
```

## Getting Started

### 1. Clone the Repository

```bash
git clone https://git.target.com/Z00FB39/library_management_system_3.git
```

### 2. Set Up PostgreSQL with Docker
Follow these steps to set up a PostgreSQL container using Docker and Colima:

1. **Start Colima**  
   Ensure Colima is running with Docker enabled:
   ```bash
   colima start --with-docker
   ```

2. **Run PostgreSQL Container**  
   Launch a PostgreSQL container with Docker:
   ```bash
   docker run --name postgres -e POSTGRES_USER=your_username -e POSTGRES_PASSWORD=your_password -p 5432:5432 -d postgres
   ```
   - Replace `your_username` and `your_password` with your desired credentials.
   - The `-p 5432:5432` option maps PostgreSQL’s default port.

3. **Verify Container is Running**  
   Confirm that the PostgreSQL container is active:
   ```bash
   docker ps
   ```
   You should see your PostgreSQL container in the list of running containers.

4. **Access PostgreSQL**  
   To access the PostgreSQL database from the command line, use:
   ```bash
   docker exec -it postgres psql -U your_username
   ```
   Replace `your_username` with the username you set in Step 2.

5. **(Optional) Connect via Database GUI**  
   Use a GUI like DBeaver or Beekeeper Studio to connect to PostgreSQL with the following details:
   - **Host**: `localhost`
   - **Port**: `5432`
   - **Username**: `your_username`
   - **Password**: `your_password`
   - **Database**: `postgres`

6. **Starting and Stopping PostgreSQL**  
   - To stop the PostgreSQL container:
     ```bash
     docker stop postgres
     ```
   - To start it again:
     ```bash
     docker start postgres
     ```

---

Once PostgreSQL is running in Docker with Colima, you’re ready to build and run the Library Management System application.

### 3. Build the Project

```bash
./gradlew build
```

### 4. Run the Application

```bash
./gradlew run
```

### 5. Access the Application

By default, the application will be accessible at `http://localhost:8080/swagger-ui`.
## Running Tests

Unit tests are provided in `BookServiceTest.java`. To run the tests:

```bash
./gradlew test
```

The tests include both positive and negative scenarios to verify the functionality of `BookService`.

## Future Enhancements

- **Advanced Report Generation**: Expanding report generation features.
- **Enhanced Role-Based Access**: Further segregation of roles for finer access control.
- **Additional Search Criteria**: More ways to filter and categorize books.

## Contributing

Feel free to open issues or submit pull requests for enhancements and bug fixes.

---

## Micronaut 3.10.1 Documentation

- [User Guide](https://docs.micronaut.io/3.10.1/guide/index.html)
- [API Reference](https://docs.micronaut.io/3.10.1/api/index.html)
- [Configuration Reference](https://docs.micronaut.io/3.10.1/guide/configurationreference.html)
- [Micronaut Guides](https://guides.micronaut.io/index.html)
---

- [Shadow Gradle Plugin](https://plugins.gradle.org/plugin/com.github.johnrengelman.shadow)
- [Micronaut Gradle Plugin documentation](https://micronaut-projects.github.io/micronaut-gradle-plugin/latest/)
- [GraalVM Gradle Plugin documentation](https://graalvm.github.io/native-build-tools/latest/gradle-plugin.html)

