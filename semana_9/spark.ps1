# spark.ps1 - "recetas" tipo Makefile para correr los ejercicios de Spark 3.3.2 (C:\spark) de la semana 9.
# Mismo estilo que ..\hadoop.ps1 y ..\PARCIAL\parcial.ps1.
# Uso: .\spark.ps1 <receta>
# Ejecutar en PowerShell nativa de Windows (no WSL), con C:\spark\bin en el Path (GUIA_EJECUCION.md paso 4).
#
# Cada receta corre su archivo de scala\ (mismo nombre que la receta) con spark-shell -i.
# Las recetas clasp01-vNN-B-bloque-... son el codigo del cuaderno de Colab ClaSp01_VNN,
# que esta copiado en el bloque B de spark_01_base.txt (V01-V03) o spark_02.txt (V04-V06).
#   .\spark.ps1 help
#   .\spark.ps1 clasp01-v01-1-bloque-txtShake-query           (spark_01_base.txt, 1.er bloque)
#   .\spark.ps1 clasp01-v02-2-bloque-masDos-miclase-OpeRand   (spark_01_base.txt, 2.o bloque)
#   .\spark.ps1 clasp01-v03-3-bloque-if-while-area            (spark_01_base.txt, 3.er bloque)
#   .\spark.ps1 clasp01-v06-1-bloque-contar                   (spark_02.txt, 1.er bloque)
#   .\spark.ps1 clasp01-v05-2-bloque-errorsexam               (spark_02.txt, 2.o bloque)
#   .\spark.ps1 clasp01-v04-3-bloque-logData                  (spark_02.txt, 3.er bloque)
#   .\spark.ps1 sales1spark-venpa-ventas-por-pais             (sales1spark.txt)

param(
    [Parameter(Position = 0)]
    [string]$Target = "help"
)

$ScalaDir = Join-Path $PSScriptRoot "scala"

# receta -> carpeta que crea con saveAsTextFile ($null si no guarda nada) y descripcion para help
$Recetas = [ordered]@{
    "clasp01-v01-1-bloque-txtShake-query"         = @{ Salida = "output_shake_";  Desc = "spark_01_base.txt bloque 1: wordcount del manual (split por comas)" }
    "clasp01-v02-2-bloque-masDos-miclase-OpeRand" = @{ Salida = $null;            Desc = "spark_01_base.txt bloque 2: variables, lambdas, def, class, object + tarea OpeRand" }
    "clasp01-v03-3-bloque-if-while-area"          = @{ Salida = $null;            Desc = "spark_01_base.txt bloque 3: if, while, foreach, for/yield, funciones" }
    "clasp01-v06-1-bloque-contar"                 = @{ Salida = "output_shake";   Desc = "spark_02.txt bloque 1: conteo de palabras (split por espacios)" }
    "clasp01-v05-2-bloque-errorsexam"             = @{ Salida = $null;            Desc = "spark_02.txt bloque 2: filter con startsWith / contains" }
    "clasp01-v04-3-bloque-logData"                = @{ Salida = $null;            Desc = "spark_02.txt bloque 3: Dataset (spark.read.textFile) con filter" }
    "sales1spark-venpa-ventas-por-pais"           = @{ Salida = "01output_venta"; Desc = "sales1spark.txt: cantidad de ventas por pais" }
}

function Show-Help {
    Write-Host "Recetas disponibles:" -ForegroundColor Cyan
    Write-Host "  help"
    Write-Host "      Muestra esta ayuda (default)"
    foreach ($nombre in $Recetas.Keys) {
        Write-Host "  $nombre"
        Write-Host "      $($Recetas[$nombre].Desc)"
    }
    Write-Host ""
    Write-Host "Ejemplo: .\spark.ps1 clasp01-v06-1-bloque-contar" -ForegroundColor DarkGray
}

function Invoke-Receta([string]$Nombre) {
    if (-not (Get-Command spark-shell -ErrorAction SilentlyContinue)) {
        Write-Host "No se encontro spark-shell - agrega C:\spark\bin al Path y abre una PowerShell nueva." -ForegroundColor Red
        return
    }
    $archivo = Join-Path $ScalaDir "$Nombre.scala"
    if (-not (Test-Path $archivo)) {
        Write-Host "No se encontro $archivo" -ForegroundColor Red
        return
    }
    $salida = $Recetas[$Nombre].Salida

    # Los .scala usan rutas relativas a semana_9 (shake.txt, carpetas de salida)
    Push-Location $PSScriptRoot
    # Borra la salida anterior: si existe, saveAsTextFile falla (FileAlreadyExistsException)
    if ($salida) { Remove-Item -Recurse -Force $salida -ErrorAction SilentlyContinue }

    Write-Host "Ejecutando scala\$Nombre.scala en spark-shell ..." -ForegroundColor Cyan
    # Cada .scala termina en System.exit(0). Si una linea da error de compilacion no llega ahi
    # y spark-shell queda abierto en scala> : salir con :quit
    spark-shell -i $archivo

    # spark-shell -i no devuelve exit code de error si una linea falla; se revisa _SUCCESS
    if ($salida) {
        if (Test-Path (Join-Path $salida "_SUCCESS")) {
            Write-Host "Resultado guardado en semana_9\$salida (archivos part-*)" -ForegroundColor Green
        } else {
            Write-Host "El job no termino bien (no hay _SUCCESS en semana_9\$salida)." -ForegroundColor Red
        }
    }
    Pop-Location
}

$receta = $Target.ToLower()
if ($receta -eq "help") {
    Show-Help
} elseif ($Recetas.Keys | Where-Object { $_.ToLower() -eq $receta }) {
    # Los nombres llevan mayusculas (txtShake, OpeRand); se acepta escribirlos en cualquier caso
    Invoke-Receta ($Recetas.Keys | Where-Object { $_.ToLower() -eq $receta })
} else {
    Write-Host "Receta desconocida: $Target" -ForegroundColor Red
    Show-Help
}
