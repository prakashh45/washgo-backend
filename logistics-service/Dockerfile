# ============================================================
# Dockerfile for: logistics-service
# ============================================================

FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /workspace

COPY . .

RUN chmod +x gradlew

RUN ./gradlew :logistics-service:bootJar -x test --no-daemon

FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

RUN addgroup --system spring && adduser --system --ingroup spring spring
USER spring:spring

COPY --from=build /workspace/logistics-service/build/libs/*.jar app.jar

EXPOSE 8085

ENTRYPOINT ["java","-jar","app.jar"]