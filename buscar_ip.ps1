# Script simple para buscar IP del celular
Write-Host "=== Buscando IP del Celular ===" -ForegroundColor Green

$ipsComunes = @(
    "192.168.0.104",
    "192.168.1.104", 
    "192.168.0.100",
    "192.168.1.100",
    "192.168.0.101",
    "192.168.1.101"
)

$puerto = 8080

Write-Host "Probando IPs comunes..." -ForegroundColor Yellow

foreach ($ip in $ipsComunes) {
    Write-Host "Probando IP: $ip" -ForegroundColor Cyan
    
    try {
        $ping = Test-Connection -ComputerName $ip -Count 1 -Quiet -TimeoutSeconds 2
        if ($ping) {
            Write-Host "  Ping exitoso" -ForegroundColor Green
            
            try {
                $tcpClient = New-Object System.Net.Sockets.TcpClient
                $result = $tcpClient.BeginConnect($ip, $puerto, $null, $null)
                $success = $result.AsyncWaitHandle.WaitOne(2000, $false)
                
                if ($success) {
                    Write-Host "  Puerto $puerto ABIERTO" -ForegroundColor Green
                    Write-Host "  IP ENCONTRADA! Usa: ws://$ip`:$puerto" -ForegroundColor Yellow
                    $tcpClient.EndConnect($result)
                    $tcpClient.Close()
                } else {
                    Write-Host "  Puerto $puerto cerrado" -ForegroundColor Red
                }
            } catch {
                Write-Host "  Error al conectar puerto" -ForegroundColor Red
            }
        } else {
            Write-Host "  Ping fallido" -ForegroundColor Red
        }
    } catch {
        Write-Host "  Error en ping" -ForegroundColor Red
    }
}

Write-Host "`nInstrucciones:" -ForegroundColor Cyan
Write-Host "1. Si encontraste una IP con puerto abierto, usala en la TV" -ForegroundColor White
Write-Host "2. Si no encontraste ninguna, verifica que el servidor este iniciado" -ForegroundColor White
Write-Host "3. Asegurate de que ambos dispositivos esten en la misma red WiFi" -ForegroundColor White 