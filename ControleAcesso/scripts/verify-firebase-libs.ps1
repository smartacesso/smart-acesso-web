# Verifica JARs criticos do Firebase em WEB-INF/lib antes do deploy
$libDir = Join-Path $PSScriptRoot "..\WebContent\WEB-INF\lib"
$requiredJars = @(
    "firebase-admin-9.3.0.jar",
    "guava-32.1.3-jre.jar",
    "google-http-client-jackson2-1.44.1.jar",
    "google-http-client-gson-1.44.1.jar",
    "google-auth-library-credentials-1.28.0.jar",
    "google-auth-library-oauth2-http-1.28.0.jar",
    "grpc-core-1.62.2.jar",
    "jackson-core-2.17.0.jar"
)

$requiredClasses = @{
    "google-http-client-jackson2-1.44.1.jar" = "com/google/api/client/json/jackson2/JacksonFactory.class"
    "google-http-client-gson-1.44.1.jar"   = "com/google/api/client/json/gson/GsonFactory.class"
    "firebase-admin-9.3.0.jar"             = "com/google/firebase/FirebaseApp.class"
    "guava-32.1.3-jre.jar"                 = "com/google/common/base/Supplier.class"
}

Add-Type -AssemblyName System.IO.Compression.FileSystem

$missing = @()
foreach ($jar in $requiredJars) {
    $path = Join-Path $libDir $jar
    if (-not (Test-Path $path)) {
        $missing += "JAR ausente: $jar"
    }
}

foreach ($entry in $requiredClasses.GetEnumerator()) {
    $path = Join-Path $libDir $entry.Key
    if (-not (Test-Path $path)) { continue }
    $zip = [System.IO.Compression.ZipFile]::OpenRead($path)
    $found = $zip.Entries | Where-Object { $_.FullName -eq $entry.Value }
    $zip.Dispose()
    if (-not $found) {
        $missing += "Classe ausente em $($entry.Key): $($entry.Value)"
    }
}

$jarCount = (Get-ChildItem $libDir -Filter "*.jar").Count
Write-Host "WEB-INF/lib: $jarCount JARs"

if ($missing.Count -gt 0) {
    Write-Host ""
    Write-Host "FALHA - dependencias Firebase incompletas:" -ForegroundColor Red
    $missing | ForEach-Object { Write-Host "  - $_" -ForegroundColor Red }
    Write-Host ""
    Write-Host "Execute: scripts/download-firebase-libs.ps1"
    Write-Host "Depois: rebuild + redeploy do EAR no WildFly"
    exit 1
}

Write-Host "OK - dependencias Firebase presentes. Faca redeploy do EAR no WildFly." -ForegroundColor Green
exit 0
