param([switch]$SelfTest)

$ErrorActionPreference = "Stop"

function Invoke-CheckedCommand([string]$Name, [scriptblock]$Action) {
    & $Action
    $commandExitCode = $global:LASTEXITCODE
    if ($commandExitCode -ne 0) {
        throw "$Name failed with exit code $commandExitCode."
    }
}

if ($SelfTest) {
    $failureDetected = $false
    try {
        Invoke-CheckedCommand "Stub command" { $global:LASTEXITCODE = 7 }
    } catch {
        $failureDetected = $true
    }
    if (-not $failureDetected) {
        throw "Verification failed to detect the stub command error."
    }
    Write-Host "Verification failure handling passed."
    exit 0
}

$projectRoot = Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Definition)
Push-Location $projectRoot
try {
    Write-Host "Checking Java..."
    Invoke-CheckedCommand "Java" { java -version }

    Write-Host "Checking Maven wrapper..."
    Invoke-CheckedCommand "Maven wrapper" { .\mvnw.cmd -version }

    Write-Host "Checking Docker (required by Testcontainers)..."
    Invoke-CheckedCommand "Docker" { docker info *> $null }

    Write-Host "Running tests and packaging application..."
    Invoke-CheckedCommand "Maven clean package" { .\mvnw.cmd clean package }

    Write-Host "PTMS verification completed successfully."
}
finally {
    Pop-Location
}
