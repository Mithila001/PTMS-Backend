# Brief Explanation: This script reliably loads variables from .env.dev 
# into the environment before starting the Spring Boot application.

# 1. Define the project root path reliably
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Definition
$projectRoot = Split-Path -Parent $scriptDir
$envFile = Join-Path $projectRoot ".env.dev"

# 2. Check if the .env file exists
if (-not (Test-Path $envFile)) {
    Write-Error "Error: .env.dev file not found at $envFile. Cannot load environment variables."
    exit 1
}

# 3. Read the file and set environment variables
Write-Host "Loading environment variables from $envFile..."
Get-Content $envFile | ForEach-Object {
    $line = $_.Trim()
    if ($line -like "#*" -or [string]::IsNullOrWhiteSpace($line)) {
        return
    }

    $parts = $line -split '=', 2

    if ($parts.Length -eq 2) {
        $key = $parts[0].Trim()
        $value = $parts[1].Trim().Trim('"').Trim("'")
        
        # Set the environment variable for the current session
        Set-Item -Path Env:\$key -Value $value
        Write-Host "Set variable: $key"
    }
}

Write-Host "Starting Spring Boot application with 'dev' profile..."

# 4. Run the application from the project root. Spring Boot will automatically detect
# the active profile from the SPRING_PROFILES_ACTIVE environment variable.
pushd $projectRoot
.\mvnw spring-boot:run
popd