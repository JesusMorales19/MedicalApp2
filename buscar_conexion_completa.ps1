# Script completo para buscar IP y puerto de conexion
Write-Host "=== Buscando Conexion Completa ===" -ForegroundColor Green

# Lista amplia de IPs comunes
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
    "192.168.1.105",
    "192.168.0.106",
    "192.168.1.106",
    "192.168.0.107",
    "192.168.1.107",
    "192.168.0.108",
    "192.168.1.108",
    "192.168.0.109",
    "192.168.1.109",
    "192.168.0.110",
    "192.168.1.110",
    "192.168.0.111",
    "192.168.1.111",
    "192.168.0.112",
    "192.168.1.112",
    "192.168.0.113",
    "192.168.1.113",
    "192.168.0.114",
    "192.168.1.114",
    "192.168.0.115",
    "192.168.1.115"
)

# Lista de puertos comunes para WebSocket
$puertosComunes = @(
    8083,
    8081,
    8082,
    8083,
    8084,
    8085,
    8086,
    8087,
    8088,
    8089,
    8090,
    9000,
    9001,
    9002,
    9003,
    9004,
    9005
)

Write-Host "Probando IPs y puertos..." -ForegroundColor Yellow

$conexionesEncontradas = @()

foreach ($ip in $ipsComunes) {
    Write-Host "`nProbando IP: $ip" -ForegroundColor Cyan
    
    try {
        $ping = Test-Connection -ComputerName $ip -Count 1 -Quiet -TimeoutSeconds 1
        if ($ping) {
            Write-Host "  Ping exitoso" -ForegroundColor Green
            
            foreach ($puerto in $puertosComunes) {
                try {
                    $tcpClient = New-Object System.Net.Sockets.TcpClient
                    $result = $tcpClient.BeginConnect($ip, $puerto, $null, $null)
                    $success = $result.AsyncWaitHandle.WaitOne(1000, $false)
                    
                    if ($success) {
                        Write-Host "  Puerto $puerto ABIERTO" -ForegroundColor Green
                        $conexion = "ws://$ip`:$puerto"
                        $conexionesEncontradas += $conexion
                        Write-Host "  CONEXION ENCONTRADA: $conexion" -ForegroundColor Yellow
                        $tcpClient.EndConnect($result)
                        $tcpClient.Close()
                    }
                } catch {
                    # Puerto cerrado, continuar
                }
            }
        } else {
            Write-Host "  Ping fallido" -ForegroundColor Red
        }
    } catch {
        Write-Host "  Error en ping" -ForegroundColor Red
    }
}

Write-Host "`n=== RESULTADOS ===" -ForegroundColor Cyan
if ($conexionesEncontradas.Count -gt 0) {
    Write-Host "Conexiones encontradas:" -ForegroundColor Green
    foreach ($conexion in $conexionesEncontradas) {
        Write-Host "  $conexion" -ForegroundColor Yellow
    }
    Write-Host "`nUsa cualquiera de estas conexiones en tu TV" -ForegroundColor Green
} else {
    Write-Host "No se encontraron conexiones" -ForegroundColor Red
    Write-Host "Verifica que:" -ForegroundColor Yellow
    Write-Host "1. El servidor este iniciado en el celular" -ForegroundColor White
    Write-Host "2. Ambos dispositivos esten en la misma red WiFi" -ForegroundColor White
    Write-Host "3. El firewall no este bloqueando" -ForegroundColor White
}

Write-Host "`n=== CONFIGURACION RECOMENDADA ===" -ForegroundColor Cyan
Write-Host "IP del celular: Verifica en Configuracion > WiFi" -ForegroundColor White
Write-Host "Puerto: 8080 (configurado en la app)" -ForegroundColor White
Write-Host "URL: ws://[IP_DEL_CELULAR]:8080" -ForegroundColor White 