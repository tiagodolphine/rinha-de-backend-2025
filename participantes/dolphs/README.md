# 🚀 Rinha de Backend 2025

A backend project for the Rinha de Backend 2025 challenge.

## 🛠️ Stack

- Java 21 (GraalVM)
- Spring Boot 3.5.3
- PostgreSQL
- Maven
- Docker & Docker Compose

## 📦 Project Structure

- `src/` — Source code
- `docker-compose.yaml` — Docker Compose setup
- `README.md` — Project documentation

## 🚀 Getting Started

### Prerequisites

- Docker & Docker Compose v2+
- Java 21 (GraalVM)
- Maven

### Build and Run

1. **Clone the repository:**
   ```sh
   git clone git@github.com:tiagodolphine/rinha-de-backend-2025.git
   cd rinha-de-backend-2025

## ⚡ Native Build

This project supports building a native executable with GraalVM Native Image.

### Steps to Build Native Image
   ```sh
    mvn -Pnative spring-boot:build-image -Dspring-boot.build-image.imageName=tiagodolphine/rinha-2025-payment-service:22