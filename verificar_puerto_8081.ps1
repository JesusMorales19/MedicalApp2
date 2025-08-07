# Script para verificar puerto 8081
Write-Host "=== Verificando Puerto 8081 ===" -ForegroundColor Green

$puerto = 8081

# Verificar si el puerto está en uso localmente
Write-Host "`n1. Verificando puerto 8081 localmente..." -ForegroundColor Yellow
try {
    $puertoLocal = Get-NetTCPConnection -LocalPort $puerto -ErrorAction SilentlyContinue
    if ($puertoLocal) {
        Write-Host "  Puerto $puerto está en uso localmente" -ForegroundColor Red
        Write-Host "  Proceso: $($puertoLocal.OwningProcess)" -ForegroundColor Red
    } else {
        Write-Host "  Puerto $puerto está libre localmente" -ForegroundColor Green
    }
} catch {
    Write-Host "  Puerto $puerto está libre localmente" -ForegroundColor Green
}

# Lista de IPs para probar
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

Write-Host "`n2. Probando puerto 8081 en diferentes IPs..." -ForegroundColor Yellow

$conexionesEncontradas = @()

foreach ($ip in $ipsComunes) {
    Write-Host "`nProbando IP: $ip" -ForegroundColor Cyan
    
    try {
        $ping = Test-Connection -ComputerName $ip -Count 1 -Quiet -TimeoutSeconds 2
        if ($ping) {
            Write-Host "  Ping exitoso" -ForegroundColor Green
            
            try {
                $tcpClient = New-Object System.Net.Sockets.TcpClient
                $result = $tcpClient.BeginConnect($ip, $puerto, $null, $null)
                $success = $result.AsyncWaitHandle.WaitOne(3000, $false)
                
                if ($success) {
                    Write-Host "  Puerto $puerto ABIERTO" -ForegroundColor Green
                    $conexion = "ws://$ip`:$puerto"
                    $conexionesEncontradas += $conexion
                    Write-Host "  CONEXION ENCONTRADA: $conexion" -ForegroundColor Yellow
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

Write-Host "`n=== RESULTADOS ===" -ForegroundColor Cyan
if ($conexionesEncontradas.Count -gt 0) {
    Write-Host "Conexiones encontradas en puerto 8081:" -ForegroundColor Green
    foreach ($conexion in $conexionesEncontradas) {
        Write-Host "  $conexion" -ForegroundColor Yellow
    }
    Write-Host "`nUsa cualquiera de estas conexiones en tu TV" -ForegroundColor Green
} else {
    Write-Host "No se encontraron conexiones en puerto 8081" -ForegroundColor Red
    Write-Host "Verifica que:" -ForegroundColor Yellow
    Write-Host "1. El servidor este iniciado en el celular" -ForegroundColor White
    Write-Host "2. Ambos dispositivos esten en la misma red WiFi" -ForegroundColor White
    Write-Host "3. El firewall no este bloqueando" -ForegroundColor White
}

Write-Host "`n=== CONFIGURACION FINAL ===" -ForegroundColor Cyan
Write-Host "Puerto configurado: 8081" -ForegroundColor White
Write-Host "URL para TV: ws://[IP_DEL_CELULAR]:8081" -ForegroundColor White 