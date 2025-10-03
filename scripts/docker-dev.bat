@REM @echo off
@REM echo 🐳 Building and running Docker container for development...

@REM rem Build the Docker image
@REM docker build -t sri-lanka-bus-transport:dev .

@REM rem Stop and remove existing container if running
@REM docker stop bus-transport-dev >nul 2>nul
@REM docker rm bus-transport-dev >nul 2>nul

@REM rem Run the container
@REM docker run -d ^
@REM  --name bus-transport-dev ^
@REM  -p 8080:8080 ^
@REM  -e SPRING_PROFILES_ACTIVE=dev ^
@REM  -e DB_URL=jdbc:postgresql://host.docker.internal:5432/bus_transport_db ^
@REM  -e DB_USERNAME=postgres ^
@REM  -e DB_PASSWORD=root ^
@REM  -e CORS_ALLOWED_ORIGINS=http://localhost:5173 ^
@REM  sri-lanka-bus-transport:dev

@REM echo ✅ Container started successfully!
@REM echo Application will be available at: http://localhost:8080
@REM echo Health check: http://localhost:8080/actuator/health
@REM echo.
@REM echo To view logs: docker logs -f bus-transport-dev
@REM echo To stop: docker stop bus-transport-dev