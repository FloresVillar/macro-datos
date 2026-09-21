# hadoop.ps1 - "recetas" tipo Makefile para administrar el cluster Hadoop 3.3.0 (C:\Hadoop3)
# Uso: .\hadoop.ps1 <target> [argumento]
# Ejecutar en PowerShell nativa de Windows (no WSL). Ver targets con: .\hadoop.ps1 help
# La mayoria de targets no llevan argumento (help, status, start, stop, clean-datanode, format).
# "salesjam" y "pc1" son la excepcion: necesitan el nombre del paquete NetBeans como
# segundo argumento, ej: .\hadoop.ps1 salesjam TransaccionesPorCiudad
#                        .\hadoop.ps1 pc1 RecursosPorRegionCategoria

param(
    [Parameter(Position = 0)]
    [string]$Target = "help",
    [Parameter(Position = 1)]
    [string]$Paquete,
    [Parameter(Position = 2)]
    [string]$Extra
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
    Write-Host "  salesjam <Pkg>  Corre <Pkg>.Driver contra MACRO-DATOS.jar y muestra el resultado"
    Write-Host "  pc1 <Pkg> [arg] Corre <Pkg>.Driver contra PC1.jar y muestra el resultado (arg = 3er parametro opcional, ej. palabra clave)"
    Write-Host ""
    Write-Host "Ejemplo: .\hadoop.ps1 status" -ForegroundColor DarkGray
    Write-Host "Ejemplo: .\hadoop.ps1 salesjam TransaccionesPorCiudad" -ForegroundColor DarkGray
    Write-Host "Ejemplo: .\hadoop.ps1 pc1 RecursosPorRegionCategoria" -ForegroundColor DarkGray
    Write-Host "Ejemplo (con arg opcional): .\hadoop.ps1 pc1 BusquedaPorPalabraClave Cusco" -ForegroundColor DarkGray
    Write-Host "  -> el 3er argumento es la palabra clave a buscar; si se omite, BusquedaPorPalabraClave usa 'Laguna' por defecto" -ForegroundColor DarkGray
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

function Invoke-SalesJam {
    if ([string]::IsNullOrWhiteSpace($Paquete)) {
        Write-Host "Uso: .\hadoop.ps1 salesjam <NombrePaquete>" -ForegroundColor Red
        Write-Host "Ejemplo: .\hadoop.ps1 salesjam TransaccionesPorCiudad" -ForegroundColor DarkGray
        return
    }
    # CAMBIAR DE ACUERDO A LA RUTA QUE SE USE
    $Jar        = "C:\Users\esauf\Documents\NetBeansProjects\MACRO-DATOS\dist\MACRO-DATOS.jar"
    $CsvLocal   = "C:\Users\esauf\Desktop\uni\macro-datos\Ventas File-20260908\Ventas\SalesJan2009.csv"
    $InputDir   = "/salesjam_input"
    $InputFile  = "$InputDir/SalesJan2009.csv"
    $OutputDir  = "/salesjam_output/$Paquete"

    if (-not (Test-Path $Jar)) {
        Write-Host "No se encontro $Jar - corre Build en NetBeans primero." -ForegroundColor Red
        return
    }

    # Verifica si el CSV ya esta en HDFS; si no, lo sube.
    hadoop fs -test -e $InputFile
    if ($LASTEXITCODE -ne 0) {
        Write-Host "CSV no encontrado en $InputDir - subiendolo..." -ForegroundColor Yellow
        if (-not (Test-Path $CsvLocal)) {
            Write-Host "No se encontro el CSV local en $CsvLocal" -ForegroundColor Red
            return
        }
        hadoop fs -mkdir -p $InputDir
        hadoop fs -put $CsvLocal $InputDir
    }

    Write-Host "Ejecutando $Paquete.Driver ..." -ForegroundColor Cyan
    $cp = (hadoop classpath)
    hadoop fs -rm -r -skipTrash $OutputDir 2>$null
    java -cp "$Jar;$cp" "$Paquete.Driver" $InputDir $OutputDir
    if ($LASTEXITCODE -ne 0) {
        Write-Host "El job termino con error (exit code $LASTEXITCODE)." -ForegroundColor Red
        return
    }

    Write-Host ""
    Write-Host "Resultado ($OutputDir):" -ForegroundColor Green
    hadoop fs -cat "$OutputDir/*"
}

function Invoke-Pc1 {
    if ([string]::IsNullOrWhiteSpace($Paquete)) {
        Write-Host "Uso: .\hadoop.ps1 pc1 <NombrePaquete>" -ForegroundColor Red
        Write-Host "Ejemplo: .\hadoop.ps1 pc1 RecursosPorRegionCategoria" -ForegroundColor DarkGray
        return
    }
    # CAMBIAR DE ACUERDO A LA RUTA QUE SE USE
    $Jar        = "C:\Users\esauf\Documents\NetBeansProjects\PC1\dist\PC1.jar"
    $CsvLocal   = "C:\Users\esauf\Desktop\uni\macro-datos\PC1\Inventario_recursos_turisticos.csv"
    $InputDir   = "/pc1_input"
    $InputFile  = "$InputDir/Inventario_recursos_turisticos.csv"
    $OutputDir  = "/pc1_output/$Paquete"

    if (-not (Test-Path $Jar)) {
        Write-Host "No se encontro $Jar - corre Build en NetBeans primero." -ForegroundColor Red
        return
    }

    # Verifica si el CSV ya esta en HDFS; si no, lo sube.
    hadoop fs -test -e $InputFile
    if ($LASTEXITCODE -ne 0) {
        Write-Host "CSV no encontrado en $InputDir - subiendolo..." -ForegroundColor Yellow
        if (-not (Test-Path $CsvLocal)) {
            Write-Host "No se encontro el CSV local en $CsvLocal" -ForegroundColor Red
            return
        }
        hadoop fs -mkdir -p $InputDir
        hadoop fs -put $CsvLocal $InputDir
    }

    Write-Host "Ejecutando $Paquete.Driver ..." -ForegroundColor Cyan
    $cp = (hadoop classpath)
    hadoop fs -rm -r -skipTrash $OutputDir 2>$null
    # $Extra es un tercer argumento opcional (ej. la palabra clave de
    # BusquedaPorPalabraClave) - si no se pasa, el Driver usa su valor por defecto.
    if ([string]::IsNullOrWhiteSpace($Extra)) {
        java -cp "$Jar;$cp" "$Paquete.Driver" $InputDir $OutputDir
    } else {
        java -cp "$Jar;$cp" "$Paquete.Driver" $InputDir $OutputDir $Extra
    }
    if ($LASTEXITCODE -ne 0) {
        Write-Host "El job termino con error (exit code $LASTEXITCODE)." -ForegroundColor Red
        return
    }

    # El CSV tiene tildes (region/categoria) - sin esto la consola muestra
    # los acentos rotos aunque el dato en HDFS este bien en UTF-8.
    [Console]::OutputEncoding = [System.Text.Encoding]::UTF8

    Write-Host ""
    Write-Host "Resultado ($OutputDir):" -ForegroundColor Green
    hadoop fs -cat "$OutputDir/*"
}

switch ($Target.ToLower()) {
    "help"           { Show-Help }
    "status"         { Invoke-Status }
    "start"          { Invoke-Start }
    "stop"           { Invoke-Stop }
    "clean-datanode" { Invoke-CleanDatanode }
    "format"         { Invoke-Format }
    "salesjam"       { Invoke-SalesJam }
    "pc1"            { Invoke-Pc1 }
    default {
        Write-Host "Target desconocido: $Target" -ForegroundColor Red
        Show-Help
    }
}
