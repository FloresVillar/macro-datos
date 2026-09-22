# Macro Datos — CC531 Análisis en Macrodatos

Repo del curso: instalación de Hadoop 3.3.0 en Windows, ejercicios de MapReduce en NetBeans, y el Laboratorio 01 (PC1) sobre el dataset de Turismo del PNDA.

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

## 4. Estructura del repo

```
GUIA_EJECUCION.md          <- documentación completa, paso a paso, con resultados reales
hadoop.ps1                  <- administración del cluster + recetas de ejecución
winutils/                    <- material de instalación de Hadoop

PC1/
  Inventario_recursos_turisticos.csv   <- dataset del Laboratorio 01 (PNDA)
  source_Packages/                      <- espejo del proyecto NetBeans "PC1" (7 categorías de consultas)
  PC1.jar                               <- build exportado desde NetBeans, listo para `hadoop.ps1 pc1`
```

**El proyecto NetBeans real vive fuera del repo**, en `C:\Users\<TU_USUARIO>\Documents\NetBeansProjects\PC1\`. La carpeta `PC1/source_Packages/` de arriba es solo una copia de referencia versionada con git.

## 5. Armar el proyecto NetBeans

Resumen (pasos completos y por qué en `GUIA_EJECUCION.md`, sección "## PC1"):

1. New Project → Java Application → nombre `PC1`.
2. Project Properties → Libraries → Compile → agregar los 4 jars de `C:\Hadoop3\...` (`hadoop-common`, `hadoop-mapreduce-client-core` **[no `client-app`]**, `client-common`, `client-jobclient`).
3. Por cada carpeta en `PC1/source_Packages/`: crear un paquete con ese mismo nombre **sin el prefijo numérico** (ej. `1a_RecursosPorRegionCategoria` → paquete `RecursosPorRegionCategoria`), y adentro 3 clases (`Mapper`, `Reducer`, `Driver`) con el código del repo pegado.
4. Build del proyecto → genera `dist\PC1.jar`.

## 6. Ejecutar los ejercicios

```powershell
.\hadoop.ps1 pc1 <NombrePaquete> [argumentoOpcional]
```

Las consultas encadenadas (2 MapReduce) necesitan correr el job 1 antes que el job 2 — orden exacto de cada par, y el único caso que no usa la receta estándar, en `GUIA_EJECUCION.md`.
