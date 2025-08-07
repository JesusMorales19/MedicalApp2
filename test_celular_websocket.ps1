# Script para probar conexión WebSocket al celular
Write-Host "=== Test WebSocket al Celular ===" -ForegroundColor Green

$celularIP = "192.168.0.104"
$puerto = 8080

Write-Host "Probando conexion a: ws://$celularIP`:$puerto" -ForegroundColor Yellow

# Verificar si el puerto está abierto
try {
    $tcpClient = New-Object System.Net.Sockets.TcpClient
    $result = $tcpClient.BeginConnect($celularIP, $puerto, $null, $null)
    $success = $result.AsyncWaitHandle.WaitOne(3000, $false)
    
    if ($success) {
        Write-Host "✅ Puerto $puerto está abierto en $celularIP" -ForegroundColor Green
        $tcpClient.EndConnect($result)
        $tcpClient.Close()
    } else {
        Write-Host "❌ Puerto $puerto está cerrado en $celularIP" -ForegroundColor Red
        Write-Host "   - Verifica que el servidor esté iniciado en el celular" -ForegroundColor Yellow
        Write-Host "   - Verifica que el firewall no esté bloqueando" -ForegroundColor Yellow
    }
} catch {
    Write-Host "❌ Error al conectar: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`nInstrucciones para la TV:" -ForegroundColor Cyan
Write-Host "1. Conectate a: ws://$celularIP`:$puerto" -ForegroundColor White
Write-Host "2. Asegúrate de que el celular y la TV estén en la misma red WiFi" -ForegroundColor White
Write-Host "3. Verifica que el servidor esté iniciado en la app Android" -ForegroundColor White 