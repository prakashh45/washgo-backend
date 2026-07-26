# ============================================================
# Dockerfile for: auth-service
# ============================================================

FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /workspace

COPY . .

RUN chmod +x gradlew

RUN ./gradlew :auth-service:bootJar -x test --no-daemon

FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

RUN addgroup --system spring && adduser --system --ingroup spring spring
USER spring:spring

COPY --from=build /workspace/auth-service/build/libs/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java","-jar","app.jar"]