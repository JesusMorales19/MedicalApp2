# Script para probar conectividad al celular
Write-Host "=== Test de Conectividad al Celular ===" -ForegroundColor Green

$celularIP = "192.168.0.104"  # Cambia esta IP por la que muestra tu celular
$puerto = 8080

Write-Host "`n1. Probando ping al celular..." -ForegroundColor Yellow
try {
    $ping = Test-Connection -ComputerName $celularIP -Count 1 -Quiet
    if ($ping) {
        Write-Host "✅ Ping exitoso a $celularIP" -ForegroundColor Green
    } else {
        Write-Host "❌ Ping fallido a $celularIP" -ForegroundColor Red
    }
} catch {
    Write-Host "❌ Error en ping: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n2. Probando puerto TCP..." -ForegroundColor Yellow
try {
    $tcpClient = New-Object System.Net.Sockets.TcpClient
    $result = $tcpClient.BeginConnect($celularIP, $puerto, $null, $null)
    $success = $result.AsyncWaitHandle.WaitOne(5000, $false)
    
    if ($success) {
        Write-Host "✅ Puerto $puerto está abierto en $celularIP" -ForegroundColor Green
        $tcpClient.EndConnect($result)
        $tcpClient.Close()
    } else {
        Write-Host "❌ Puerto $puerto está cerrado en $celularIP" -ForegroundColor Red
        Write-Host "   - El servidor no está iniciado o el firewall lo bloquea" -ForegroundColor Yellow
    }
} catch {
    Write-Host "❌ Error al conectar: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n3. Probando WebSocket..." -ForegroundColor Yellow
Write-Host "   - Abre test_websocket.html en tu navegador" -ForegroundColor White
Write-Host "   - IP: $celularIP" -ForegroundColor White
Write-Host "   - Puerto: $puerto" -ForegroundColor White
Write-Host "   - URL: ws://$celularIP`:$puerto" -ForegroundColor White

Write-Host "`n=== Recomendaciones ===" -ForegroundColor Cyan
Write-Host "1. Verifica que el servidor esté iniciado en el celular" -ForegroundColor White
Write-Host "2. Desactiva temporalmente el firewall del celular" -ForegroundColor White
Write-Host "3. Asegúrate de que ambos dispositivos estén en la misma red WiFi" -ForegroundColor White
Write-Host "4. Prueba desde diferentes dispositivos (computadora, otro celular)" -ForegroundColor White 