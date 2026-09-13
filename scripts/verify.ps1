$ErrorActionPreference = "Stop"

$projectRoot = Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Definition)
Push-Location $projectRoot
try {
    Write-Host "Checking Java..."
    java -version

    Write-Host "Checking Maven wrapper..."
    .\mvnw.cmd -version

    Write-Host "Checking Docker (required by Testcontainers)..."
    docker info *> $null

    Write-Host "Running tests..."
    .\mvnw.cmd clean test

    Write-Host "Packaging application..."
    .\mvnw.cmd clean package

    Write-Host "PTMS verification completed successfully."
}
finally {
    Pop-Location
}
