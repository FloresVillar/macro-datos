# Graph Report - macro-datos  (2026-10-17)

## Corpus Check
- 104 files · ~26,220 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 4 file(s) not represented in the graph (top: (none) 2, .csv 2)

## Summary
- 542 nodes · 1353 edges · 68 communities (10 shown, 58 thin omitted)
- Extraction: 99% EXTRACTED · 1% INFERRED · 0% AMBIGUOUS · INFERRED: 9 edges (avg confidence: 0.76)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `cb45bb2e`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- Reducer
- Inventario_recursos_turisticos.csv
- Mapper
- Mapper
- Mapper
- Reducer
- org.apache.hadoop.io.Text
- Mapper
- Mapper
- org.apache.hadoop.io.DoubleWritable
- io
- Mapper
- distributedcache
- SalesCountryDriver (main, dos jobs encadenados)
- MapReduceBase
- hadoop.ps1
- path
- 02_TransaccionesPorCiudad/Driver.java
- 5a2_ClasificarNorteSurRegion/Mapper.java
- Reducer
- SalesMapper
- 07_PrecioProductoMasCostoso/Driver.java
- 1e_RankingRegionCategoria/Driver.java
- 2_EstadisticasLatitud/Driver.java
- 3_BusquedaPorPalabraClave/Driver.java
- SalesCountryDriver.java
- Mapper
- Mapper
- Mapper
- Mapper
- Mapper
- Mapper
- Mapper
- Mapper
- Mapper
- Mapper
- Mapper
- Mapper
- Mapper
- Mapper
- Mapper
- 5b1_PromedioLongitudPorRegion/Driver.java
- 7a_RegresionLongitudDesdeLatitud/Driver.java
- Reducer
- Reducer
- Reducer
- Reducer
- Reducer
- 03_SumaPreciosProductos/Driver.java
- 04_SumaPreciosProduct3/Driver.java
- 05_SumaPreciosPorTipoProducto/Driver.java
- 08_PrecioMasBaratoProduct1/Driver.java
- 11_DesviacionEstandarPrecios/Driver.java
- 12_BuscarUnitedKingdom/Driver.java
- 1a_RecursosPorRegionCategoria/Driver.java
- Reducer
- 1d_ManifestacionesCulturalesPorRegion/Driver.java
- org.apache.hadoop.io.LongWritable
- 4_ExtremosGeograficosPorRegion/Driver.java
- 5a1_PromedioLatitudPorRegion/Driver.java
- 6a1_CentroidesCategoria/Driver.java
- Mapper
- Reducer
- 6b1_CentroidesRegion/Driver.java
- 7b1_ConteoPorProvincia/Driver.java
- Mapper
- Graphify Project Instructions

## God Nodes (most connected - your core abstractions)
1. `Inventario_recursos_turisticos.csv` - 23 edges
2. `Proyecto NetBeans PC1` - 21 edges
3. `Guía de Ejecución — Hadoop + MapReduce` - 19 edges
4. `SalesJan2009.csv` - 15 edges
5. `Proyecto NetBeans MACRO-DATOS` - 14 edges
6. `Mapper` - 9 edges
7. `Mapper` - 9 edges
8. `Mapper` - 9 edges
9. `Mapper` - 9 edges
10. `Mapper` - 9 edges

## Surprising Connections (you probably didn't know these)
- `Proyecto NetBeans PC1` --references--> `Paquete BusquedaPorPalabraClave`  [EXTRACTED]
  README.md → GUIA_EJECUCION.md
- `Proyecto NetBeans PC1` --references--> `Paquete ExtremosGeograficosPorRegion`  [EXTRACTED]
  README.md → GUIA_EJECUCION.md
- `Macro Datos — Repo README` --references--> `hadoop.ps1 — administración del clúster y recetas`  [EXTRACTED]
  README.md → GUIA_EJECUCION.md
- `Proyecto NetBeans PC1` --references--> `Paquete ManifestacionesCulturalesPorRegion`  [EXTRACTED]
  README.md → GUIA_EJECUCION.md
- `Pregunta de examen — País con mayor facturación` --conceptually_related_to--> `Paquete PaisProductoMayorFacturacion`  [INFERRED]
  pregunta.md → GUIA_EJECUCION.md

## Import Cycles
- None detected.

## Hyperedges (group relationships)
- **Flujo WordCount sobre Hadoop** — mapreduceclient_jar, wordcount, hdfs, yarn, guia_ejecucion [EXTRACTED 0.90]
- **Consultas PC1 sobre inventario turístico** — pc1_project, inventario_recursos_csv, recursos_por_region_categoria, estadisticas_latitud, centroides_categoria, regresion_longitud_desde_latitud [INFERRED 0.80]

## Communities (68 total, 58 thin omitted)

### Community 1 - "Inventario_recursos_turisticos.csv"
Cohesion: 0.07
Nodes (60): Bug: hadoop jar no ejecuta jars propios en Windows, NodeManager requiere PowerShell como Administrador, Paquete BuscarUnitedKingdom, Paquete BusquedaPorPalabraClave, Paquete CentroidesCategoria, Paquete CentroidesRegion, Paquete ClasificarEsteOesteRegion, Paquete ClasificarNorteSurRegion (+52 more)

### Community 6 - "org.apache.hadoop.io.Text"
Cohesion: 0.07
Nodes (8): Reducer, Reducer, Reducer, Reducer, Reducer, Reducer, Reducer, SalesCountryReducer

### Community 10 - "org.apache.hadoop.io.DoubleWritable"
Cohesion: 0.10
Nodes (6): Mapper, Reducer, Mapper, Reducer, Mapper, Reducer

### Community 11 - "io"
Cohesion: 0.18
Nodes (3): Driver, Driver, Driver

### Community 13 - "distributedcache"
Cohesion: 0.18
Nodes (4): Driver, Driver, Driver, Driver

### Community 14 - "SalesCountryDriver (main, dos jobs encadenados)"
Cohesion: 0.31
Nodes (11): org.apache.hadoop.mapred.JobClient, org.apache.hadoop.mapred.JobConf, SalesCountryDriver (main, dos jobs encadenados), org.apache.hadoop.mapred.MapReduceBase, org.apache.hadoop.mapred.Reducer, SalesCountryReducer (suma de frecuencias), SalesReducer2 (argmax por precio), org.apache.hadoop.mapred.Mapper (+3 more)

### Community 15 - "MapReduceBase"
Cohesion: 0.04
Nodes (12): Reducer, Reducer, Reducer, Reducer, ReducerMaximo, Reducer, Reducer, Reducer (+4 more)

### Community 16 - "hadoop.ps1"
Cohesion: 0.31
Nodes (5): Invoke-CleanDatanode(), Invoke-Format(), Invoke-Start(), Invoke-Status(), Test-JavaRunning()

### Community 17 - "path"
Cohesion: 0.15
Nodes (4): Driver, Driver, Driver, Driver

### Community 19 - "5a2_ClasificarNorteSurRegion/Mapper.java"
Cohesion: 0.08
Nodes (4): Mapper, Mapper, Mapper, Mapper

## Knowledge Gaps
- **9 isolated node(s):** `MACRO-DATOS.jar`, `MapReduceClient.jar`, `PC1.jar`, `Instalación Hadoop 3.3.0 en Windows (semana_1)`, `SalesJan2009_04.csv` (+4 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 189 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **58 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Mapper` connect `5a2_ClasificarNorteSurRegion/Mapper.java` to `org.apache.hadoop.io.LongWritable`, `org.apache.hadoop.io.Text`, `MapReduceBase`?**
  _High betweenness centrality (0.006) - this node is a cross-community bridge._
- **What connects `MACRO-DATOS.jar`, `MapReduceClient.jar`, `PC1.jar` to the rest of the system?**
  _9 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Inventario_recursos_turisticos.csv` be split into smaller, more focused modules?**
  _Cohesion score 0.0655367231638418 - nodes in this community are weakly interconnected._
- **Why does `Mapper` connect `5a2_ClasificarNorteSurRegion/Mapper.java` to `org.apache.hadoop.io.LongWritable`, `org.apache.hadoop.io.Text`, `MapReduceBase`?**
  _High betweenness centrality (0.006) - this node is a cross-community bridge._
- **Should `org.apache.hadoop.io.Text` be split into smaller, more focused modules?**
  _Cohesion score 0.06575963718820861 - nodes in this community are weakly interconnected._
- **Should `org.apache.hadoop.io.DoubleWritable` be split into smaller, more focused modules?**
  _Cohesion score 0.1 - nodes in this community are weakly interconnected._
- **Should `MapReduceBase` be split into smaller, more focused modules?**
  _Cohesion score 0.041666666666666664 - nodes in this community are weakly interconnected._