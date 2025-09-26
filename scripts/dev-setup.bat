@echo off
echo 🚀 Setting up Sri Lanka Bus Transport System for development...

rem Check if Java 21 is installed
where java >nul 2>nul
if %errorlevel% neq 0 (
    echo ❌ Java is not installed. Please install Java 21.
    exit /b 1
)

rem Check Java version
for /f "tokens=3" %%i in ('java -version 2^>^&1') do (
    set "java_version_full=%%i"
)
set "java_version=!java_version_full:~0,2!"
if !java_version! lss 21 (
    echo ❌ Java 21 or higher is required. Current version: !java_version!
    exit /b 1
)

rem Check if PostgreSQL is running
pg_isready -h localhost -p 5432 >nul 2>nul
if %errorlevel% neq 0 (
    echo ❌ PostgreSQL is not running on localhost:5432
    echo Please start PostgreSQL and create database 'bus_transport_db'
    exit /b 1
)

rem Check if database exists
psql -h localhost -U postgres -lqt | find "bus_transport_db" >nul
if %errorlevel% neq 0 (
    echo 📦 Creating database 'bus_transport_db'...
    createdb -h localhost -U postgres bus_transport_db
)

rem Copy environment file if it doesn't exist
if not exist .env (
    if exist .env.example (
        copy .env.example .env
        echo 📝 Created .env file from .env.example
        echo Please update .env file with your local settings
    )
)

rem Make Maven wrapper executable
rem On Windows, .cmd files are executable by default, so we just check for its existence.
if not exist mvnw.cmd (
    echo ❌ mvnw.cmd not found. Please ensure it exists in your project root.
)

echo ✅ Development environment setup complete!
echo.
echo To run the application:
echo    .\mvnw.cmd spring-boot:run
echo.
echo To run tests:
echo    .\mvnw.cmd test
echo.
echo To build Docker image:
echo    docker build -t sri-lanka-bus-transport .