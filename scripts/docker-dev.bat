@echo off
echo 🐳 Building and running Docker container for development...

rem Build the Docker image
docker build -t sri-lanka-bus-transport:dev .

rem Stop and remove existing container if running
docker stop bus-transport-dev >nul 2>nul
docker rm bus-transport-dev >nul 2>nul

rem Run the container
docker run -d ^
 --name bus-transport-dev ^
 -p 8080:8080 ^
 -e SPRING_PROFILES_ACTIVE=dev ^
 -e DB_URL=jdbc:postgresql://host.docker.internal:5432/bus_transport_db ^
 -e DB_USERNAME=postgres ^
 -e DB_PASSWORD=root ^
 -e CORS_ALLOWED_ORIGINS=http://localhost:5173 ^
 sri-lanka-bus-transport:dev

echo ✅ Container started successfully!
echo Application will be available at: http://localhost:8080
echo Health check: http://localhost:8080/actuator/health
echo.
echo To view logs: docker logs -f bus-transport-dev
echo To stop: docker stop bus-transport-dev