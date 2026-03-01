# JobRadar

Simple job ingestion and analysis service built with Java \| Spring Boot \| Maven.

## Summary

JobRadar crawls ATS job boards, stores job postings, and analyzes job descriptions via an external Python service. The scheduled ingestion runs periodically and saves job postings and analysis results.

## Tech stack

- Java 17
- Spring Boot
- Hibernate / JPA
- Maven
- Python analysis service (external)
- MySQL (or other JDBC compatible DB)
- IntelliJ IDEA (development)

## Requirements

- JDK 17
- Maven 3.x
- Running database (configure in `application.properties`)
- Python analysis service endpoint (configure client URL)

## Build & run

- Build:
    - `mvn clean install`

- Run (from project root):
    - `mvn spring-boot:run`
    - Or run the generated jar: `java -jar target/jobradar-1.0-SNAPSHOT.jar`

## Configuration

Configure database and Python client in `src/main/resources/application.properties` (or environment variables):

- `spring.datasource.url`
- `spring.datasource.username`
- `spring.datasource.password`
- `jobradar.python.client.url` (or similar property used by `PythonClient`)

Keep secrets out of the repo (use environment variables or a secrets store).

## How ingestion works (high level)

- `JobScheduler` triggers periodic runs.
- `JobIngestionService` loads active `CompanyAts` mappings, uses a `CrawlerFactory` to obtain an `AtsCrawler`, crawls job postings and persists them.
- Newly saved jobs are sent to the Python analysis service; results are persisted to `JobAnalysis`.
- Stale jobs are marked inactive if not present in the current crawl.

## Common issues & fixes

- Maven error: `dependencies.dependency.version for org.jsoup:jsoup:jar is missing`
    - Cause: dependency declared without a version and not covered by dependency management.
    - Fix: add a version or property in `pom.xml`, e.g.:
      ```xml
      <properties>
        <jsoup.version>1.16.2</jsoup.version>
      </properties>
      ...
      <dependency>
        <groupId>org.jsoup</groupId>
        <artifactId>jsoup</artifactId>
        <version>${jsoup.version}</version>
      </dependency>
      ```

- `org.hibernate.LazyInitializationException: could not initialize proxy ... no Session`
    - Cause: lazy association accessed outside of an active transaction / session (often in scheduled tasks).
    - Fixes:
        - Load associations inside a transaction (annotate service method with `@Transactional`).
        - Or fetch required associations with a fetch\-join query (e.g. repository method using `join fetch`).
        - Avoid switching relationships to `EAGER` unless appropriate.

- URL normalization
    - Ensure crawlers normalize ATS URLs by trimming trailing slashes before extracting path segments:
      ```java
      if (url != null && url.endsWith("/")) {
        url = url.substring(0, url.length() - 1);
      }
      ```

## Developer tips

- Use `CompanyAtsRepository.findActiveWithAssociations()` (or similar) to fetch required associations in the same query.
- Annotate long-running scheduled logic with transactions where entity lazy-loading is needed.
- Keep `application.properties` out of source control if it contains secrets.

## Repository

- See `pom.xml` for dependencies and Maven configuration.
- Add a `.gitignore` to exclude IDE files, `target/`, `.env` and other local artifacts.

## License

Project license as appropriate (add a `LICENSE` file).