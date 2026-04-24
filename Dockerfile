# ---------- STAGE 1: BUILD ----------
FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /build

# Copia apenas arquivos necessários primeiro (cache eficiente)
COPY pom.xml .
COPY paas-domain/pom.xml paas-domain/
COPY paas-application/pom.xml paas-application/
COPY paas-infrastructure/pom.xml paas-infrastructure/
COPY paas-web/pom.xml paas-web/
COPY paas-bootstrap/pom.xml paas-bootstrap/

# Baixa dependências
RUN mvn -B -q -e -DskipTests dependency:go-offline

# Copia o restante do código
COPY . .

# Build da aplicação
RUN mvn clean package -DskipTests

# ---------- STAGE 2: RUNTIME ----------
FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

# Copia o JAR gerado (ajuste se necessário)
COPY --from=builder /build/paas-bootstrap/target/*.jar app.jar

# Porta padrão
EXPOSE 8080

# Variáveis padrão (podem ser sobrescritas)
ENV SPRING_PROFILES_ACTIVE=prod

# Comando de inicialização
ENTRYPOINT ["java", "-jar", "app.jar"]