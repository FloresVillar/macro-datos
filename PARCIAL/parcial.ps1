# parcial.ps1 - "recetas" tipo Makefile para correr las consultas del parcial en el
# nodo unico (Hadoop 3.3.0 de Windows, C:\Hadoop3). Mismo estilo que ..\hadoop.ps1 (receta pc1).
# Uso: .\parcial.ps1 <target> [Paquete]
# Ejecutar en PowerShell COMO ADMINISTRADOR, con Hadoop ya arrancado (..\hadoop.ps1 start).
#   .\parcial.ps1 help
#   .\parcial.ps1 start            (comprueba administrador y arranca Hadoop con ..\hadoop.ps1 start)
#   .\parcial.ps1 status           (puertos y procesos, con ..\hadoop.ps1 status)
#   .\parcial.ps1 run SeveridadPorVia
#   .\parcial.ps1 run PuntosNegrosPorTramo
#   .\parcial.ps1 hyperv-off       (apaga hipervisor/VBS para VirtualBox; luego Inicio -> Reiniciar)
#   .\parcial.ps1 hyperv-on        (deshace hyperv-off para volver a WSL2; luego Inicio -> Reiniciar)
#   .\parcial.ps1 hyperv-check     (despues de reiniciar: muestra HypervisorPresent y VBS)

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
    Write-Host "  hyperv-off   Apaga hipervisor, VBS y Windows Hello para que VirtualBox no vaya en modo tortuga (reiniciar despues)"
    Write-Host "  hyperv-on    Deshace hyperv-off (vuelve WSL2 / Docker; reiniciar despues)"
    Write-Host "  hyperv-check Despues de reiniciar: HypervisorPresent y VBS (apagado = False / 0)"
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

# Guia 0.1. Con el hipervisor de Windows cargado, VirtualBox corre en modo "tortuga" y la VM
# se cuelga en "Loading essential drivers". Ademas de hypervisorlaunchtype hubo que apagar
# VBS, que Windows Hello volvia a encender aunque el registro lo tuviera en 0 (2026-10-17).
# Para revertir (volver a usar WSL2): guia 0.2.
function Invoke-HypervOff {
    if (-not (Test-Admin)) {
        Write-Host "Esta PowerShell no es de administrador - abre 'Terminal (Administrador)'." -ForegroundColor Red
        return
    }
    $dg = "HKLM\SYSTEM\CurrentControlSet\Control\DeviceGuard"
    wsl --shutdown
    bcdedit /set hypervisorlaunchtype off
    bcdedit /set "{current}" vsmlaunchtype Off
    reg add $dg /v EnableVirtualizationBasedSecurity /t REG_DWORD /d 0 /f
    reg add $dg /v RequirePlatformSecurityFeatures /t REG_DWORD /d 0 /f
    reg add "$dg\Scenarios\SecureBiometrics" /v Enabled /t REG_DWORD /d 0 /f
    reg add "$dg\Scenarios\WindowsHello" /v Enabled /t REG_DWORD /d 0 /f
    reg add "HKLM\SYSTEM\CurrentControlSet\Control\Lsa" /v LsaCfgFlags /t REG_DWORD /d 0 /f   # Credential Guard
    # Si una caracteristica no existe en este Windows, dism lo dice y se sigue
    dism /online /disable-feature /featurename:VirtualMachinePlatform /norestart
    dism /online /disable-feature /featurename:HypervisorPlatform /norestart
    dism /online /disable-feature /featurename:Microsoft-Hyper-V-All /norestart

    Write-Host ""
    Write-Host "Debe decir Off en las dos:" -ForegroundColor Cyan
    bcdedit /enum "{current}" | findstr /i "hypervisor vsm"
    Write-Host ""
    Write-Host "Reinicia con Inicio -> Reiniciar (no Apagar) y luego: .\parcial.ps1 hyperv-check" -ForegroundColor Yellow
}

# Guia 0.2: deshace hyperv-off para volver a usar WSL2 / Docker (estado original: guia 0).
# RequirePlatformSecurityFeatures y LsaCfgFlags se borran porque no se conocia su valor original.
function Invoke-HypervOn {
    if (-not (Test-Admin)) {
        Write-Host "Esta PowerShell no es de administrador - abre 'Terminal (Administrador)'." -ForegroundColor Red
        return
    }
    $dg = "HKLM\SYSTEM\CurrentControlSet\Control\DeviceGuard"
    bcdedit /set hypervisorlaunchtype auto
    bcdedit /deletevalue "{current}" vsmlaunchtype
    reg add $dg /v EnableVirtualizationBasedSecurity /t REG_DWORD /d 1 /f
    reg add "$dg\Scenarios\SecureBiometrics" /v Enabled /t REG_DWORD /d 1 /f
    reg add "$dg\Scenarios\WindowsHello" /v Enabled /t REG_DWORD /d 1 /f
    Remove-ItemProperty "HKLM:\SYSTEM\CurrentControlSet\Control\DeviceGuard" -Name RequirePlatformSecurityFeatures -ErrorAction SilentlyContinue
    Remove-ItemProperty "HKLM:\SYSTEM\CurrentControlSet\Control\Lsa" -Name LsaCfgFlags -ErrorAction SilentlyContinue
    dism /online /enable-feature /featurename:VirtualMachinePlatform /all /norestart   # obligatoria para WSL2
    dism /online /enable-feature /featurename:HypervisorPlatform /all /norestart
    dism /online /enable-feature /featurename:Microsoft-Hyper-V-All /all /norestart

    Write-Host ""
    Write-Host "Si se desactivo, volver a activar: Configuracion -> Cuentas -> Opciones de inicio de sesion -> Inicio de sesion mejorado" -ForegroundColor Yellow
    Write-Host "Reinicia con Inicio -> Reiniciar y luego: .\parcial.ps1 hyperv-check   (y wsl -l -v)" -ForegroundColor Yellow
}

function Invoke-HypervCheck {
    $hv  = (Get-CimInstance Win32_ComputerSystem).HypervisorPresent
    $vbs = (Get-CimInstance -Namespace root\Microsoft\Windows\DeviceGuard -ClassName Win32_DeviceGuard).VirtualizationBasedSecurityStatus
    Write-Host "HypervisorPresent:                 $hv"
    Write-Host "VirtualizationBasedSecurityStatus: $vbs"
    if (-not $hv -and $vbs -eq 0) {
        Write-Host "Hipervisor apagado (estado de hyperv-off) - VirtualBox puede usar VT-x directamente." -ForegroundColor Green
    } elseif ($hv) {
        Write-Host "Hipervisor cargado (estado de hyperv-on / original) - WSL2 funciona; VirtualBox va en modo tortuga." -ForegroundColor Yellow
        Write-Host "Si se esperaba apagado tras hyperv-off y reiniciar, VBS puede estar bloqueado en UEFI: DG_Readiness_Tool o BIOS." -ForegroundColor DarkGray
    } else {
        Write-Host "Estado mixto: hipervisor apagado pero VBS = $vbs. Reiniciar con Inicio -> Reiniciar y volver a comprobar." -ForegroundColor Red
    }
}

switch ($Target.ToLower()) {
    "help"   { Show-Help }
    "start"  { Invoke-Start }
    "status" { & $HadoopPs1 status }
    "run"    { Invoke-Run }
    "hyperv-off"   { Invoke-HypervOff }
    "hyperv-on"    { Invoke-HypervOn }
    "hyperv-check" { Invoke-HypervCheck }
    default {
        Write-Host "Target desconocido: $Target" -ForegroundColor Red
        Show-Help
    }
}
