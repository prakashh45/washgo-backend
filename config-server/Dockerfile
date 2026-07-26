# ============================================================
# Dockerfile for: config-server
# ============================================================

FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /workspace

COPY . .

RUN chmod +x gradlew

RUN ./gradlew :config-server:bootJar -x test --no-daemon

FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

RUN addgroup --system spring && adduser --system --ingroup spring spring
USER spring:spring

COPY --from=build /workspace/config-server/build/libs/*.jar app.jar

EXPOSE 8888

ENTRYPOINT ["java","-jar","app.jar"]