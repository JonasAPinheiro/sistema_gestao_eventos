# Etapa 1: Build da aplicação com Maven
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Imagem final leve para execução
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

# Limita a memória da JVM para não ultrapassar a cota de 512MB do Render
ENV JAVA_OPTS="-Xmx384m -Xss256k -XX:+UseContainerSupport"
EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]