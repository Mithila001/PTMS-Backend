# run-prod.ps1
# ------------------------------------------------------------------------
# A robust script to run the Spring Boot application in 'prod' profile.
# It performs pre-flight checks for mandatory database properties.
# ------------------------------------------------------------------------

# Define mandatory properties
$DB_URL = "jdbc:postgresql://localhost:5433/postgres"
$DB_USERNAME = "ptms_user_docker"
$DB_PASSWORD = "secret_docker"
$PROFILE = "prod"

# 1. Environment Variable Check
if (-not $DB_URL -or -not $DB_USERNAME -or -not $DB_PASSWORD) {
    Write-Error "❌ FATAL OS CHECK: Missing one or more critical database variables (DB_URL, DB_USERNAME, DB_PASSWORD)."
    exit 1
}

Write-Host "✅ OS Check: All mandatory database properties are defined."
Write-Host "   - DB_URL: $DB_URL"
Write-Host "   - DB_USERNAME: $DB_USERNAME"
Write-Host "   - Profile: $PROFILE"

$mavenProps = @(
    # Maven standard arguments (used by pom.xml forwarding)
    "-DDB_URL=$DB_URL"
    "-DDB_USERNAME=$DB_USERNAME"
    "-DDB_PASSWORD=$DB_PASSWORD"
    
    # Specific Spring Boot Maven plugin properties
    "-Dspring-boot.run.profiles=$PROFILE"
    "-Dlogging.level.root=DEBUG"
)

$mavenGoal = "spring-boot:run"

# Execute the command: Maven properties first, then the goal.
# The pom.xml will ensure these are correctly routed to the application environment.
& ".\mvnw" $mavenProps $mavenGoal

# Check the exit code of the mvnw process
if ($LASTEXITCODE -ne 0) {
    Write-Error "💥 Application failed during startup. Check the logs above for the specific Java exception."
} else {
    Write-Host "🎉 Application started successfully!"
}