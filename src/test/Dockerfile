FROM maven:3.9.6-eclipse-temurin-21
WORKDIR /tests
COPY . .
RUN mvn clean compile
CMD ["./run-tests.sh"]