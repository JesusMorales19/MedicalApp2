# Script para verificar IP y puerto del celular
Write-Host "=== Verificación de IP y Puerto del Celular ===" -ForegroundColor Green

# Lista de IPs comunes para probar
$ipsComunes = @(
    "192.168.0.104",
    "192.168.1.104", 
    "192.168.0.100",
    "192.168.1.100",
    "192.168.0.101",
    "192.168.1.101",
    "192.168.0.102",
    "192.168.1.102",
    "192.168.0.103",
    "192.168.1.103",
    "192.168.0.105",
    "192.168.1.105"
)

$puerto = 8080

Write-Host "`n🔍 Probando conectividad a diferentes IPs..." -ForegroundColor Yellow

foreach ($ip in $ipsComunes) {
    Write-Host "`nProbando IP: $ip" -ForegroundColor Cyan
    
    # Probar ping
    try {
        $ping = Test-Connection -ComputerName $ip -Count 1 -Quiet -TimeoutSeconds 2
        if ($ping) {
            Write-Host "  ✅ Ping exitoso" -ForegroundColor Green
            
            # Probar puerto
            try {
                $tcpClient = New-Object System.Net.Sockets.TcpClient
                $result = $tcpClient.BeginConnect($ip, $puerto, $null, $null)
                $success = $result.AsyncWaitHandle.WaitOne(2000, $false)
                
                if ($success) {
                    Write-Host "  ✅ Puerto $puerto ABIERTO" -ForegroundColor Green
                    Write-Host "  🎯 ¡IP ENCONTRADA! Usa: ws://$ip`:$puerto" -ForegroundColor Yellow
                    $tcpClient.EndConnect($result)
                    $tcpClient.Close()
                } else {
                    Write-Host "  ❌ Puerto $puerto cerrado" -ForegroundColor Red
                }
            } catch {
                Write-Host "  ❌ Error al conectar puerto: $($_.Exception.Message)" -ForegroundColor Red
            }
        } else {
            Write-Host "  ❌ Ping fallido" -ForegroundColor Red
        }
    } catch {
        Write-Host "  ❌ Error en ping: $($_.Exception.Message)" -ForegroundColor Red
    }
}

Write-Host "`n=== Instrucciones ===" -ForegroundColor Cyan
Write-Host "1. Si encontraste una IP con puerto abierto, úsala en la TV" -ForegroundColor White
Write-Host "2. Si no encontraste ninguna, verifica que el servidor esté iniciado" -ForegroundColor White
Write-Host "3. Asegurate de que ambos dispositivos esten en la misma red WiFi" -ForegroundColor White 