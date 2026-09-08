# hadoop.ps1 - "recetas" tipo Makefile para administrar el cluster Hadoop 3.3.0 (C:\Hadoop3)
# Uso: .\hadoop.ps1 <target>
# Ejecutar en PowerShell nativa de Windows (no WSL). Ver targets con: .\hadoop.ps1 help

param(
    [Parameter(Position = 0)]
    [string]$Target = "help"
)

$HadoopHome  = "C:\Hadoop3"
$DatanodeDir = "$HadoopHome\data\datanode"
$SbinDir     = "$HadoopHome\sbin"
$Ports       = @{ 9870 = "NameNode UI"; 8088 = "ResourceManager UI"; 8042 = "NodeManager UI"; 19000 = "HDFS RPC" }

function Show-Help {
    Write-Host "Targets disponibles:" -ForegroundColor Cyan
    Write-Host "  help            Muestra esta ayuda (default)"
    Write-Host "  status          Revisa procesos java.exe y puertos del cluster"
    Write-Host "  start           Arranca el cluster (start-all.cmd)"
    Write-Host "  stop            Detiene el cluster (stop-all.cmd)"
    Write-Host "  clean-datanode  Borra el contenido de data\datanode (sin formatear)"
    Write-Host "  format          clean-datanode + hdfs namenode -format (pide confirmacion)"
    Write-Host ""
    Write-Host "Ejemplo: .\hadoop.ps1 status" -ForegroundColor DarkGray
}

function Test-JavaRunning {
    return [bool](Get-Process java -ErrorAction SilentlyContinue)
}

function Invoke-Status {
    if (Test-JavaRunning) {
        Write-Host "Procesos java.exe corriendo:" -ForegroundColor Green
        Get-Process java | Select-Object Id, StartTime | Format-Table -AutoSize
    } else {
        Write-Host "No hay procesos java.exe corriendo." -ForegroundColor Yellow
    }
    Write-Host ""
    Write-Host "Puertos:" -ForegroundColor Cyan
    foreach ($port in $Ports.Keys) {
        $open = Test-NetConnection -ComputerName localhost -Port $port -WarningAction SilentlyContinue
        $state = if ($open.TcpTestSucceeded) { "OK" } else { "cerrado" }
        $color = if ($open.TcpTestSucceeded) { "Green" } else { "DarkGray" }
        Write-Host ("  {0,-6} {1,-20} {2}" -f $port, $Ports[$port], $state) -ForegroundColor $color
    }
}

function Invoke-Start {
    if (Test-JavaRunning) {
        Write-Host "Ya hay procesos java.exe corriendo. Nada que hacer." -ForegroundColor Yellow
        return
    }
    Push-Location $SbinDir
    # start-all.cmd sigue funcionando pero imprime "This script is Deprecated"
    .\start-dfs.cmd
    .\start-yarn.cmd
    Pop-Location
    Write-Host "Recuerda: el NodeManager de YARN necesita PowerShell como Administrador." -ForegroundColor Yellow
}

function Invoke-Stop {
    Push-Location $SbinDir
    # mismo motivo que en Invoke-Start: evita el aviso de stop-all.cmd deprecado
    .\stop-yarn.cmd
    .\stop-dfs.cmd
    Pop-Location
}

function Invoke-CleanDatanode {
    if (Test-JavaRunning) {
        Write-Host "Hay procesos java.exe corriendo. Corre '.\hadoop.ps1 stop' antes de limpiar." -ForegroundColor Red
        return
    }
    if (Test-Path $DatanodeDir) {
        Remove-Item -Recurse -Force "$DatanodeDir\*" -ErrorAction SilentlyContinue
        Write-Host "Contenido de $DatanodeDir borrado." -ForegroundColor Green
    } else {
        Write-Host "$DatanodeDir no existe todavia." -ForegroundColor Yellow
    }
}

function Invoke-Format {
    if (Test-JavaRunning) {
        Write-Host "Hay procesos java.exe corriendo. Corre '.\hadoop.ps1 stop' antes de formatear." -ForegroundColor Red
        return
    }
    Write-Host "Esto va a:"
    Write-Host "  1. Borrar el contenido de $DatanodeDir"
    Write-Host "  2. Ejecutar hdfs namenode -format (genera un clusterID nuevo)"
    $confirm = Read-Host "¿Continuar? (escribe SI para confirmar)"
    if ($confirm -ne "SI") {
        Write-Host "Cancelado." -ForegroundColor Yellow
        return
    }
    Invoke-CleanDatanode
    hdfs namenode -format
    Write-Host ""
    Write-Host "Reformateo completado. Siguiente paso: .\hadoop.ps1 start" -ForegroundColor Cyan
}

switch ($Target.ToLower()) {
    "help"           { Show-Help }
    "status"         { Invoke-Status }
    "start"          { Invoke-Start }
    "stop"           { Invoke-Stop }
    "clean-datanode" { Invoke-CleanDatanode }
    "format"         { Invoke-Format }
    default {
        Write-Host "Target desconocido: $Target" -ForegroundColor Red
        Show-Help
    }
}
