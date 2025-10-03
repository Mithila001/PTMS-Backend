@REM @echo off
@REM echo 🚀 Setting up Sri Lanka Bus Transport System for development...

@REM rem Check if Java 21 is installed
@REM where java >nul 2>nul
@REM if %errorlevel% neq 0 (
@REM     echo ❌ Java is not installed. Please install Java 21.
@REM     exit /b 1
@REM )

@REM rem Check Java version
@REM for /f "tokens=3" %%i in ('java -version 2^>^&1') do (
@REM     set "java_version_full=%%i"
@REM )
@REM set "java_version=!java_version_full:~0,2!"
@REM if !java_version! lss 21 (
@REM     echo ❌ Java 21 or higher is required. Current version: !java_version!
@REM     exit /b 1
@REM )

@REM rem Check if PostgreSQL is running
@REM pg_isready -h localhost -p 5432 >nul 2>nul
@REM if %errorlevel% neq 0 (
@REM     echo ❌ PostgreSQL is not running on localhost:5432
@REM     echo Please start PostgreSQL and create database 'bus_transport_db'
@REM     exit /b 1
@REM )

@REM rem Check if database exists
@REM psql -h localhost -U postgres -lqt | find "bus_transport_db" >nul
@REM if %errorlevel% neq 0 (
@REM     echo 📦 Creating database 'bus_transport_db'...
@REM     createdb -h localhost -U postgres bus_transport_db
@REM )

@REM rem Copy environment file if it doesn't exist
@REM if not exist .env (
@REM     if exist .env.example (
@REM         copy .env.example .env
@REM         echo 📝 Created .env file from .env.example
@REM         echo Please update .env file with your local settings
@REM     )
@REM )

@REM rem Make Maven wrapper executable
@REM rem On Windows, .cmd files are executable by default, so we just check for its existence.
@REM if not exist mvnw.cmd (
@REM     echo ❌ mvnw.cmd not found. Please ensure it exists in your project root.
@REM )

@REM echo ✅ Development environment setup complete!
@REM echo.
@REM echo To run the application:
@REM echo    .\mvnw.cmd spring-boot:run
@REM echo.
@REM echo To run tests:
@REM echo    .\mvnw.cmd test
@REM echo.
@REM echo To build Docker image:
@REM echo    docker build -t sri-lanka-bus-transport .