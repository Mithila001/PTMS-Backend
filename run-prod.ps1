# run-prod.ps1
# ------------------------------------------------------------------------
# A robust script to run the Spring Boot application in 'prod' profile.
# It performs pre-flight checks for mandatory database properties.
# ------------------------------------------------------------------------

# Define mandatory properties as ENVIRONMENT VARIABLES for the Java app to read
$env:DB_URL = "jdbc:postgresql://localhost:5433/ptms_docker_db"
$env:DB_USERNAME = "ptms_user_docker"
$env:DB_PASSWORD = "secret_docker"
$env:PROFILE = "prod"
$env:DEV_DATA_LOADER_ENABLED = $true
$env:SHOULD_CREATE_INITIAL_USERS = $true

# 1. Environment Variable Check
# Note: Checking the script's local variables first is fine for pre-flight.
if (-not $env:DB_URL -or -not $env:DB_USERNAME -or -not $env:DB_PASSWORD) {
 Write-Error "❌ FATAL OS CHECK: Missing one or more critical database variables (DB_URL, DB_USERNAME, DB_PASSWORD)."
 exit 1
}

Write-Host "✅ OS Check: All mandatory database properties are defined."
Write-Host "- DB_URL: $env:DB_URL"
Write-Host "- DB_USERNAME: $env:DB_USERNAME"
Write-Host "- Profile: $env:PROFILE"
Write-Host "- DEV_DATA_LOADER_ENABLED: $env:DEV_DATA_LOADER_ENABLED"
Write-Host "- SHOULD_CREATE_INITIAL_USERS: $env:SHOULD_CREATE_INITIAL_USERS"


$mavenGoal = "spring-boot:run"

# Execute the command with the essential SSL override.
# -Dspring-boot.run.profiles=$env:PROFILE : Sets the 'prod' profile.
# -Dlogging.level.root=DEBUG : Enables debug logging for troubleshooting.
# -Dspring.datasource.hikari.ssl-mode=disable : CRITICAL OVERRIDE for local Docker connection.
& ".\mvnw.cmd" "-Dspring-boot.run.profiles=$env:PROFILE" "-Dlogging.level.root=DEBUG" "-Dspring.datasource.hikari.ssl-mode=disable" $mavenGoal

# Check the exit code of the mvnw process
if ($LASTEXITCODE -ne 0) {
    Write-Error "💥 Application failed during startup. Check the logs above for the specific Java exception."
} else {
    Write-Host "🎉 Application started successfully!"
}