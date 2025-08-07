# Script de diagnóstico simple para WebSocket
Write-Host "=== Diagnóstico WebSocket Simple ===" -ForegroundColor Green

# Verificar puertos en uso
Write-Host "`nVerificando puertos en uso..." -ForegroundColor Yellow
try {
    $puertos8081 = Get-NetTCPConnection -LocalPort 8081 -ErrorAction SilentlyContinue
    if ($puertos8081) {
        Write-Host "Puerto 8081 está en uso por:" -ForegroundColor Red
        $puertos8081 | ForEach-Object { Write-Host "   - PID: $($_.OwningProcess), Estado: $($_.State)" -ForegroundColor Red }
    } else {
        Write-Host "Puerto 8081 está libre" -ForegroundColor Green
    }
} catch {
    Write-Host "Error al verificar puertos: $($_.Exception.Message)" -ForegroundColor Red
}

# Verificar IP del celular
Write-Host "`nVerificando IP del celular..." -ForegroundColor Yellow
try {
    $ipAddresses = Get-NetIPAddress | Where-Object { $_.AddressFamily -eq "IPv4" -and $_.IPAddress -notlike "127.*" }
    Write-Host "Direcciones IP disponibles:" -ForegroundColor Cyan
    $ipAddresses | ForEach-Object {
        Write-Host "   - $($_.IPAddress) en $($_.InterfaceAlias)" -ForegroundColor Cyan
    }
} catch {
    Write-Host "Error al verificar IP: $($_.Exception.Message)" -ForegroundColor Red
}

# Verificar configuración del código
Write-Host "`nVerificando configuración del código..." -ForegroundColor Yellow

$proyeccionServicePath = "app/src/main/java/com/tuempresa/medicalapp/core/network/ProyeccionService.kt"
if (Test-Path $proyeccionServicePath) {
    $content = Get-Content $proyeccionServicePath -Raw
    $puerto8081Count = ([regex]::Matches($content, "8081")).Count
    $puerto8080Count = ([regex]::Matches($content, "8080")).Count
    
    Write-Host "ProyeccionService.kt:" -ForegroundColor Cyan
    Write-Host "   - Referencias a puerto 8081: $puerto8081Count" -ForegroundColor Cyan
    Write-Host "   - Referencias a puerto 8080: $puerto8080Count" -ForegroundColor Cyan
} else {
    Write-Host "No se encontró ProyeccionService.kt" -ForegroundColor Red
}

Write-Host "`n=== Fin del diagnóstico ===" -ForegroundColor Green
Write-Host "`nRecomendaciones:" -ForegroundColor Yellow
Write-Host "1. Asegúrate de que la TV y el celular estén en la misma red WiFi" -ForegroundColor White
Write-Host "2. Usa la IP correcta del celular (no localhost)" -ForegroundColor White
Write-Host "3. Prueba con el archivo test_websocket.html" -ForegroundColor White 