# parcial.ps1 - "recetas" tipo Makefile para correr las consultas del parcial en el
# nodo unico (Hadoop 3.3.0 de Windows, C:\Hadoop3). Mismo estilo que ..\hadoop.ps1 (receta pc1).
# Uso: .\parcial.ps1 <target> [Paquete]
# Ejecutar en PowerShell COMO ADMINISTRADOR, con Hadoop ya arrancado (..\hadoop.ps1 start).
#   .\parcial.ps1 help
#   .\parcial.ps1 start            (comprueba administrador y arranca Hadoop con ..\hadoop.ps1 start)
#   .\parcial.ps1 status           (puertos y procesos, con ..\hadoop.ps1 status)
#   .\parcial.ps1 run SeveridadPorVia
#   .\parcial.ps1 run PuntosNegrosPorTramo

param(
    [Parameter(Position = 0)]
    [string]$Target = "help",
    [Parameter(Position = 1)]
    [string]$Paquete
)

# CAMBIAR DE ACUERDO A LA RUTA QUE SE USE
$Jar       = "C:\Users\esauf\Documents\NetBeansProjects\PARCIAL\dist\PARCIAL.jar"
$CsvLocal  = "C:\Users\esauf\Desktop\uni\macro-datos\PARCIAL\dataset_accidentes_cgm\dataSSE_17122021_c_utf8.csv"
$InputDir  = "/parcial_input"
$InputFile = "$InputDir/dataSSE_17122021_c_utf8.csv"
# Las recetas start/status reutilizan las de la PC1 en vez de duplicarlas
$HadoopPs1 = Join-Path $PSScriptRoot "..\hadoop.ps1"

function Show-Help {
    Write-Host "Targets disponibles:" -ForegroundColor Cyan
    Write-Host "  help         Muestra esta ayuda (default)"
    Write-Host "  start        Comprueba que la PowerShell sea de administrador y arranca Hadoop (..\hadoop.ps1 start)"
    Write-Host "  status       Procesos java.exe y puertos 9870/8088/8042/19000 (..\hadoop.ps1 status)"
    Write-Host "  run <Pkg>    Corre <Pkg>.Driver contra PARCIAL.jar, mide el tiempo y muestra el resultado"
    Write-Host ""
    Write-Host "Ejemplo: .\parcial.ps1 run SeveridadPorVia" -ForegroundColor DarkGray
    Write-Host "Ejemplo: .\parcial.ps1 run PuntosNegrosPorTramo" -ForegroundColor DarkGray
}

# Sin administrador el NodeManager queda UNHEALTHY ("nm-local-dir ... not writable")
# y el job se queda en ACCEPTED para siempre (paso el 2026-10-18).
# Se compara contra el rol integrado y no contra el texto 'Administrators',
# que en Windows en espanol da False siempre.
function Test-Admin {
    $principal = [Security.Principal.WindowsPrincipal][Security.Principal.WindowsIdentity]::GetCurrent()
    return $principal.IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)
}

function Invoke-Start {
    if (-not (Test-Admin)) {
        Write-Host "Esta PowerShell no es de administrador - abre 'Terminal (Administrador)'." -ForegroundColor Red
        return
    }
    & $HadoopPs1 start
}

function Invoke-Run {
    if ([string]::IsNullOrWhiteSpace($Paquete)) {
        Write-Host "Uso: .\parcial.ps1 run <NombrePaquete>" -ForegroundColor Red
        Write-Host "Ejemplo: .\parcial.ps1 run PuntosNegrosPorTramo" -ForegroundColor DarkGray
        return
    }
    if (-not (Test-Admin)) {
        Write-Host "Esta PowerShell no es de administrador - abre 'Terminal (Administrador)'." -ForegroundColor Red
        return
    }
    if (-not (Test-Path $Jar)) {
        Write-Host "No se encontro $Jar - corre Build en NetBeans primero." -ForegroundColor Red
        return
    }
    $OutputDir = "/parcial_output/$Paquete"

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

    # Borra la salida anterior: si existe, el job falla
    hadoop fs -rm -r -f -skipTrash $OutputDir 2>$null

    Write-Host "Ejecutando $Paquete.Driver ..." -ForegroundColor Cyan
    $cp = (hadoop classpath)
    # Measure-Command mide el tiempo total de los jobs encadenados (para la tabla clúster vs. nodo unico);
    # Out-Host para que los logs del job se sigan viendo en pantalla
    $tiempo = Measure-Command { java -cp "$Jar;$cp" "$Paquete.Driver" $InputDir $OutputDir | Out-Host }
    if ($LASTEXITCODE -ne 0) {
        Write-Host "El job termino con error (exit code $LASTEXITCODE)." -ForegroundColor Red
        return
    }

    # El CSV tiene tildes y Ñ (departamentos) - sin esto la consola muestra
    # los acentos rotos aunque el dato en HDFS este bien en UTF-8.
    [Console]::OutputEncoding = [System.Text.Encoding]::UTF8

    # Punto como separador decimal, igual que en las salidas de los jobs
    $segundos = [string]::Format([Globalization.CultureInfo]::InvariantCulture, "{0:F2}", $tiempo.TotalSeconds)

    # Se arma el texto una sola vez: se muestra en pantalla y se guarda en el .txt
    $lineas = New-Object System.Collections.Generic.List[string]
    $lineas.Add("Consulta: $Paquete")
    $lineas.Add("Ejecucion: nodo unico (Windows, Hadoop 3.3.0)")
    $lineas.Add("Fecha: " + (Get-Date -Format "yyyy-MM-dd HH:mm:ss"))
    $lineas.Add("Tiempo total: $segundos s")
    $lineas.Add("")
    $lineas.Add("Resultado ($OutputDir):")
    foreach ($l in (hadoop fs -cat "$OutputDir/part-00000")) { $lineas.Add($l) }

    # El agrupamiento deja sus métricas (Silhouette, Davies-Bouldin) en <salida>_metricas
    hadoop fs -test -e "${OutputDir}_metricas/part-00000"
    if ($LASTEXITCODE -eq 0) {
        $lineas.Add("")
        $lineas.Add("Metricas (${OutputDir}_metricas):")
        foreach ($l in (hadoop fs -cat "${OutputDir}_metricas/part-00000")) { $lineas.Add($l) }
    }

    Write-Host ""
    foreach ($l in $lineas) { Write-Host $l }

    # Guarda el resultado en PARCIAL\resultados\<Paquete>_nodo_unico.txt (UTF-8 sin BOM,
    # para que tildes y Ñ se vean bien en VS Code, el Bloc de notas y Power BI)
    $ResultadosDir = Join-Path $PSScriptRoot "resultados"
    New-Item -ItemType Directory -Force $ResultadosDir | Out-Null
    $archivo = Join-Path $ResultadosDir "${Paquete}_nodo_unico.txt"
    [IO.File]::WriteAllLines($archivo, $lineas, (New-Object Text.UTF8Encoding $false))
    Write-Host ""
    Write-Host "Guardado en $archivo" -ForegroundColor Green
}

switch ($Target.ToLower()) {
    "help"   { Show-Help }
    "start"  { Invoke-Start }
    "status" { & $HadoopPs1 status }
    "run"    { Invoke-Run }
    default {
        Write-Host "Target desconocido: $Target" -ForegroundColor Red
        Show-Help
    }
}
