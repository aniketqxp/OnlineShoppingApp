# ============================================================
# VectorStore Clean Build & Run Script
# ============================================================

$REDIS_EXE = "$env:TEMP\redis\redis-server.exe"
$REDIS_CLI = "$env:TEMP\redis\redis-cli.exe"
$LIB_PATH  = "lib\*"

# ── 1. Ensure Redis is running ────────────────────────────────
Write-Host "`n[1/3] Checking Redis..." -ForegroundColor Cyan
$ping = & $REDIS_CLI ping 2>&1
if ($ping -ne "PONG") {
    Write-Host "      Redis not running. Starting..." -ForegroundColor Yellow
    Start-Process -FilePath $REDIS_EXE -WindowStyle Minimized
    Start-Sleep -Seconds 2
    $ping = & $REDIS_CLI ping 2>&1
    if ($ping -eq "PONG") {
        Write-Host "      Redis started OK." -ForegroundColor Green
    } else {
        Write-Host "      WARNING: Redis unavailable. App will use in-memory fallback." -ForegroundColor Red
    }
} else {
    Write-Host "      Redis already running (PONG)." -ForegroundColor Green
}

# ── 2. Clean & Compile ────────────────────────────────────────
Write-Host "`n[2/3] Cleaning and compiling..." -ForegroundColor Cyan
Get-Process -Name java -ErrorAction SilentlyContinue | Stop-Process -Force
if (Test-Path out) { Remove-Item -Recurse -Force out }
Get-ChildItem -Path src -Filter *.class -Recurse | Remove-Item -Force
New-Item -ItemType Directory -Force -Path out | Out-Null

javac -encoding UTF-8 -cp "src;$LIB_PATH" -d out (Get-ChildItem src\*.java | ForEach-Object { $_.FullName })

if ($LASTEXITCODE -ne 0) {
    Write-Host "`nBUILD FAILED. Check errors above." -ForegroundColor Red
    pause
    exit 1
}
Write-Host "      Build successful." -ForegroundColor Green

# ── 3. Launch ─────────────────────────────────────────────────
    Write-Host "`n[3/3] Launching VectorStore..." -ForegroundColor Cyan
java -cp "out;$LIB_PATH;src" VectorStore
