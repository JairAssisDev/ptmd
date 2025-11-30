# The 'openjdk' repository is deprecated. We use Eclipse Temurin instead.
# eclipse-temurin:17-jdk-alpine is the direct replacement.
FROM eclipse-temurin:17-jdk-alpine AS base

WORKDIR /app

# Install Maven (still works because this is an Alpine Linux image)
RUN apk add --no-cache maven

FROM base AS build

COPY . .

RUN mvn clean package -DskipTests

# Final stage: Use JRE (Java Runtime Environment) to keep the image smaller, 
# or use 17-jdk-alpine if you prefer.
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

RUN mkdir -p /app/uploads

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]