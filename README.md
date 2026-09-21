# Macro Datos — CC531 Análisis en Macrodatos

**Esto es el Laboratorio 01 (proyecto NetBeans `PC1`): 19 consultas MapReduce sobre Hadoop 3.3.0, corriendo contra el dataset de Turismo del PNDA/MINCETUR.** Todo lo que hay que ejecutar y entregar es `PC1`.

El repo también trae `MACRO-DATOS` (SalesCountry + 11 ejercicios "SalesJam") como material de **práctica previa**, no forma parte de la entrega — se menciona al final por si sirve de referencia.

Esta es la guía rápida para levantar el entorno en una máquina nueva. **El detalle completo de cada paso, decisión de diseño, bug encontrado y resultado ya verificado está en `GUIA_EJECUCION.md` — este README solo resume y apunta para allá.**

## 1. Prerrequisitos

Java 8, Hadoop 3.3.0, NetBeans IDE, PowerShell nativa de Windows (no WSL, no cmd).

## 2. Instalar Hadoop

Seguir `semana_1/Instalacion-Hadoop-3.md` paso a paso (descarga, variables de entorno, `winutils.exe` — ya está en `winutils/winutils/hadoop-3.3.0-YARN-8246/bin/`, configs XML, formatear namenode).

> El NodeManager de YARN necesita PowerShell **como Administrador** en Windows, si no, no arranca — detalle del bug en `GUIA_EJECUCION.md`.

## 3. Administrar el cluster

```powershell
cd C:\ruta\a\este\repo
.\hadoop.ps1 help     # ver todos los comandos
.\hadoop.ps1 start
.\hadoop.ps1 status
.\hadoop.ps1 stop
```
`http://localhost:9870` (NameNode) y `http://localhost:8088` (ResourceManager) para verificar en el navegador.

## 4. Armar el proyecto NetBeans `PC1`

Resumen (pasos completos y por qué en `GUIA_EJECUCION.md`, sección `## PC1`):

1. New Project → Java Application → nombre `PC1`.
2. Project Properties → Libraries → Compile → agregar los 4 jars de `C:\Hadoop3\...` (`hadoop-common`, `hadoop-mapreduce-client-core` **[no `client-app`]**, `client-common`, `client-jobclient`).
3. Por cada carpeta en `PC1/source_Packages/` de este repo: crear un paquete con ese mismo nombre **sin el prefijo numérico** (ej. `1a_RecursosPorRegionCategoria` → paquete `RecursosPorRegionCategoria`), y adentro 3 clases (`Mapper`, `Reducer`, `Driver`) con el código correspondiente pegado.
4. Build del proyecto → genera `dist\PC1.jar`.

**El proyecto NetBeans real vive fuera del repo**, en `C:\Users\<TU_USUARIO>\Documents\NetBeansProjects\PC1\`. La carpeta `PC1/source_Packages/` de este repo es solo la copia de referencia del código, versionada con git.

## 5. Ejecutar las consultas de PC1

```powershell
.\hadoop.ps1 pc1 <NombrePaquete> [argumentoOpcional]
```
Ejemplos: `.\hadoop.ps1 pc1 RecursosPorRegionCategoria`, `.\hadoop.ps1 pc1 BusquedaPorPalabraClave Cusco`.

Hay 6 consultas **encadenadas** (2 MapReduce cada una) — hay que correr el job 1 antes que el job 2. Orden exacto de cada par, y el único caso que no usa la receta estándar (`RegresionRecursosPorDistritos`), documentados en `GUIA_EJECUCION.md`.

## 6. Estructura del repo

```
GUIA_EJECUCION.md            <- documentación completa de PC1, paso a paso, con resultados reales
hadoop.ps1                    <- administración del cluster + receta pc1
PC1/                            <- código fuente de las 19 consultas del Laboratorio 01 (esto es lo que importa)

semana_1/, semana_2/, semana_3/   <- material del curso (instalación, teoría, enunciado del lab)
winutils/, mapreduce2/             <- material de instalación / fuente original del tutorial
```

### Material de práctica (no es la entrega)

`MACRO-DATOS/` (SalesCountry + 11 ejercicios "SalesJam") y `Ventas File-20260908/` son ejercicios previos con los que se practicó el patrón Mapper/Reducer/Driver antes de armar `PC1`. Se ejecutan igual con `.\hadoop.ps1 salesjam <NombrePaquete>`, pero no son parte del laboratorio a entregar.
