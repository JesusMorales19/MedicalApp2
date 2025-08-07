# Script de diagnóstico para WebSocket y puertos
Write-Host "=== Diagnóstico WebSocket y Puertos ===" -ForegroundColor Green

# Verificar puertos en uso
Write-Host "`n🔍 Verificando puertos en uso..." -ForegroundColor Yellow
try {
    $puertos8080 = Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue
    $puertos8081 = Get-NetTCPConnection -LocalPort 8081 -ErrorAction SilentlyContinue
    
    if ($puertos8080) {
        Write-Host "⚠️ Puerto 8080 está en uso por:" -ForegroundColor Red
        $puertos8080 | ForEach-Object { Write-Host "   - PID: $($_.OwningProcess), Estado: $($_.State)" -ForegroundColor Red }
    } else {
        Write-Host "✅ Puerto 8080 está libre" -ForegroundColor Green
    }
    
    if ($puertos8081) {
        Write-Host "⚠️ Puerto 8081 está en uso por:" -ForegroundColor Red
        $puertos8081 | ForEach-Object { Write-Host "   - PID: $($_.OwningProcess), Estado: $($_.State)" -ForegroundColor Red }
    } else {
        Write-Host "✅ Puerto 8081 está libre" -ForegroundColor Green
    }
} catch {
    Write-Host "❌ Error al verificar puertos: $($_.Exception.Message)" -ForegroundColor Red
}

# Verificar configuración de red
Write-Host "`n🌐 Verificando configuración de red..." -ForegroundColor Yellow
try {
    $interfaces = Get-NetAdapter | Where-Object { $_.Status -eq "Up" }
    Write-Host "Interfaces de red activas:" -ForegroundColor Cyan
    $interfaces | ForEach-Object {
        Write-Host "   - $($_.Name): $($_.InterfaceDescription)" -ForegroundColor Cyan
    }
    
    $ipAddresses = Get-NetIPAddress | Where-Object { $_.AddressFamily -eq "IPv4" -and $_.IPAddress -notlike "127.*" }
    Write-Host "`nDirecciones IP disponibles:" -ForegroundColor Cyan
    $ipAddresses | ForEach-Object {
        Write-Host "   - $($_.IPAddress) en $($_.InterfaceAlias)" -ForegroundColor Cyan
    }
} catch {
    Write-Host "❌ Error al verificar red: $($_.Exception.Message)" -ForegroundColor Red
}

# Verificar firewall
Write-Host "`n🔥 Verificando firewall..." -ForegroundColor Yellow
try {
    $firewallRules = Get-NetFirewallRule | Where-Object { $_.DisplayName -like "*808*" -or $_.DisplayName -like "*WebSocket*" }
    if ($firewallRules) {
        Write-Host "Reglas de firewall relacionadas:" -ForegroundColor Cyan
        $firewallRules | ForEach-Object {
            Write-Host "   - $($_.DisplayName): $($_.Enabled)" -ForegroundColor Cyan
        }
    } else {
        Write-Host "ℹ️ No se encontraron reglas de firewall específicas para puertos 8080/8081" -ForegroundColor Yellow
    }
} catch {
    Write-Host "❌ Error al verificar firewall: $($_.Exception.Message)" -ForegroundColor Red
}

# Verificar archivos de configuración
Write-Host "`n📁 Verificando archivos de configuración..." -ForegroundColor Yellow

# Verificar ProyeccionService.kt
$proyeccionServicePath = "app/src/main/java/com/tuempresa/medicalapp/core/network/ProyeccionService.kt"
if (Test-Path $proyeccionServicePath) {
    $content = Get-Content $proyeccionServicePath -Raw
    $puerto8081Count = ([regex]::Matches($content, "8081")).Count
    $puerto8080Count = ([regex]::Matches($content, "8080")).Count
    
    Write-Host "ProyeccionService.kt:" -ForegroundColor Cyan
    Write-Host "   - Referencias a puerto 8081: $puerto8081Count" -ForegroundColor Cyan
    Write-Host "   - Referencias a puerto 8080: $puerto8080Count" -ForegroundColor Cyan
    
    if ($puerto8080Count -gt 0) {
        Write-Host "   ⚠️ Aún hay referencias al puerto 8080" -ForegroundColor Red
    }
} else {
    Write-Host "❌ No se encontró ProyeccionService.kt" -ForegroundColor Red
}

# Verificar ProyeccionScreen.kt
$proyeccionScreenPath = "app/src/main/java/com/tuempresa/medicalapp/presentation/screens/doctor/ProyeccionScreen.kt"
if (Test-Path $proyeccionScreenPath) {
    $content = Get-Content $proyeccionScreenPath -Raw
    $puerto8081Count = ([regex]::Matches($content, "8081")).Count
    $puerto8080Count = ([regex]::Matches($content, "8080")).Count
    
    Write-Host "ProyeccionScreen.kt:" -ForegroundColor Cyan
    Write-Host "   - Referencias a puerto 8081: $puerto8081Count" -ForegroundColor Cyan
    Write-Host "   - Referencias a puerto 8080: $puerto8080Count" -ForegroundColor Cyan
    
    if ($puerto8080Count -gt 0) {
        Write-Host "   ⚠️ Aún hay referencias al puerto 8080" -ForegroundColor Red
    }
} else {
    Write-Host "❌ No se encontró ProyeccionScreen.kt" -ForegroundColor Red
}

# Verificar AndroidManifest.xml
$manifestPath = "app/src/main/AndroidManifest.xml"
if (Test-Path $manifestPath) {
    $content = Get-Content $manifestPath -Raw
    $internetPermission = $content -match "android\.permission\.INTERNET"
    $networkPermission = $content -match "android\.permission\.ACCESS_NETWORK_STATE"
    $wifiPermission = $content -match "android\.permission\.ACCESS_WIFI_STATE"
    
    Write-Host "AndroidManifest.xml:" -ForegroundColor Cyan
    Write-Host "   - Permiso INTERNET: $internetPermission" -ForegroundColor Cyan
    Write-Host "   - Permiso ACCESS_NETWORK_STATE: $networkPermission" -ForegroundColor Cyan
    Write-Host "   - Permiso ACCESS_WIFI_STATE: $wifiPermission" -ForegroundColor Cyan
} else {
    Write-Host "❌ No se encontró AndroidManifest.xml" -ForegroundColor Red
}

Write-Host "`n=== Fin del diagnóstico ===" -ForegroundColor Green
Write-Host "`n💡 Recomendaciones:" -ForegroundColor Yellow
Write-Host "1. Asegúrate de que la TV y el celular estén en la misma red WiFi" -ForegroundColor White
Write-Host "2. Verifica que el firewall no esté bloqueando el puerto 8081" -ForegroundColor White
Write-Host "3. Usa la IP correcta del celular (no localhost)" -ForegroundColor White
Write-Host "4. Prueba con el archivo test_websocket.html para verificar la conexión" -ForegroundColor White 