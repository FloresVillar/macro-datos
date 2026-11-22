# Semana 9 — Instalación de Spark 3.3.2 en Windows (C:)

Manual base: `instalacion spark3.3.2.pdf` (transcrito en `instalacion spark3.3.2.pdf.md`).

### 0. Conversión de los PDF a Markdown
Los PDF se convirtieron con el script de `limpieza_datos` (los `.txt` no se convierten). `limpieza_datos` es un enlace a `/home/esau/limpieza_datos` dentro de WSL `UbuntuTrabajo`, por eso se corre desde WSL:
```powershell
wsl -e bash -lc "cd '/mnt/c/Users/esauf/Desktop/uni/macro-datos/semana_9' && python3 ~/limpieza_datos/scripts/pdf_to_md.py '09 RDD_v02.pdf' 'instalacion spark3.3.2.pdf'"
```
- `wsl -e bash -lc "..."` → ejecuta el comando dentro de Linux (WSL).
- `pdf_to_md.py` → genera `<nombre>.pdf.md` junto al PDF y una carpeta `<nombre>_files/` con las imágenes.

### 1. Spark 3.3.2 en `C:\spark`
Ya estaba descargado y descomprimido (`spark-3.3.2-bin-hadoop3.tgz`). Se verificó con:
```powershell
Get-Content C:\spark\RELEASE
```
→ `Spark 3.3.2 (git revision 5103e00c4c) built for Hadoop 3.3.2`

Java ya estaba: `C:\java` → OpenJDK 1.8.0_504 (Temurin). `HADOOP_HOME = C:\Hadoop3` con `winutils.exe` y `hadoop.dll` (de semanas anteriores).

### 2. Scala 2.12.15
Se usa 2.12.15 porque es la misma versión que trae Spark 3.3.2 (`C:\spark\jars\scala-library-2.12.15.jar`).

>El link de Lightbend (`downloads.lightbend.com`) ya no funciona ("This content has moved"). El instalador ahora está en GitHub:
https://github.com/scala/scala/releases/download/v2.12.15/scala-2.12.15.msi

Instalar con opciones por defecto → queda en `C:\Program Files (x86)\scala`. Verificar en una terminal nueva:
```powershell
scala -version
```
→ `Scala code runner version 2.12.15 -- Copyright 2002-2021, LAMP/EPFL and Lightbend, Inc.`

### 3. Variables de usuario
`JAVA_HOME = C:\java` ya existía (como variable del sistema), no se repite.
```powershell
[Environment]::SetEnvironmentVariable('SCALA_HOME','C:\Program Files (x86)\scala','User')
[Environment]::SetEnvironmentVariable('SPARK_HOME','C:\spark','User')
```
- `SetEnvironmentVariable(nombre, valor, 'User')` → crea la variable de forma permanente para tu usuario (no solo para esa terminal).

Verificar en una terminal **nueva** (las variables se cargan al abrir la ventana):
```powershell
$env:SCALA_HOME; $env:SPARK_HOME
```
→ `C:\Program Files (x86)\scala` y `C:\spark`

### 4. Variables del sistema: Path
`C:\java\bin` ya estaba. Se agregó `C:\spark\bin` a mano (no por comando, para no dañar otras entradas del Path):

`Win + R` → `sysdm.cpl` → **Opciones avanzadas** → **Variables de entorno** → en **Variables del sistema** seleccionar `Path` → **Editar** → **Nuevo** → `C:\spark\bin` → Aceptar.

>Windows muestra `Path`, no `PATH`: es la misma variable (Windows no distingue mayúsculas en los nombres de variables).

### 5. Comandos de prueba
#### 5.1 SparkPi
```powershell
cd C:\spark\bin
run-example SparkPi 10
```
- `cd C:\spark\bin` → ir a la carpeta de los programas de Spark (con el Path ya configurado no es obligatorio).
- `run-example` → es `C:\spark\bin\run-example.cmd`, script de Spark para correr sus programas de ejemplo; por dentro llama a `spark-submit`.
- `SparkPi` → nombre del programa de ejemplo (clase `org.apache.spark.examples.SparkPi`, dentro de `C:\spark\examples\jars\spark-examples_2.12-3.3.2.jar`).
- `10` → en cuántas partes (tareas) se divide el trabajo.

Qué hace: estima π lanzando puntos al azar en un cuadrado y contando cuántos caen dentro del círculo inscrito (proporción × 4 ≈ π). Sirve como prueba de que Java + Spark + variables están bien. (montecarlo)

Resultado obtenido (entre muchos mensajes `INFO`, que son el log normal de Spark):
```
Pi is roughly 3.1424071424071425
```

#### Resultado de `run-example SparkPi 10` (qué dice el log)
1. Arranque del driver
`Running Spark version 3.3.2` · `Submitted application: Spark Pi` · `started service 'sparkDriver' on port 49708` · `MemoryStore started with capacity 366.3 MiB` · `started service 'SparkUI' on port 4040`
Se crea el **SparkContext** (el "cerebro" del programa, el driver). Reserva memoria para guardar datos y abre una página web de monitoreo en http://localhost:4040 (solo vive mientras el programa corre).

2. Carga del programa
`Added JAR .../spark-examples_2.12-3.3.2.jar` · `Starting executor ID driver on host Diosdeisrael.bbrouter`
Carga el `.jar` que contiene SparkPi. El **executor** (quien hace el trabajo) es el mismo proceso del driver: es **modo local**, todo en tu PC, sin clúster.

3. El job (map → reduce)
`Starting job: reduce at SparkPi.scala:38` · `Got job 0 ... with 10 output partitions` · `Submitting ResultStage 0 (MapPartitionsRDD[1] at map at SparkPi.scala:34)`
- `map` (línea 34) es una **transformación**: genera puntos al azar y marca 1 si cae dentro del círculo, 0 si no. Por sí sola no ejecuta nada (evaluación perezosa).
- `reduce` (línea 38) es una **acción**: suma todos los 1 → recién aquí Spark lanza el trabajo (el "job").
- `10 output partitions` → el `10` del comando: el RDD se parte en 10 particiones.
- Una sola etapa (`ResultStage 0`) porque no hay que redistribuir datos entre particiones (no hay *shuffle*).

4. Las tareas
`Starting task 0.0 ... task 9.0` · `Running task ...` · `Finished task ... (1/10) ... (10/10)`
Una **tarea por partición** = 10 tareas, corriendo en paralelo en los núcleos de tu CPU (~0.4–0.5 s cada una). Cada una devuelve su conteo parcial al driver (`1008 bytes result sent to driver`).

5. Resultado
`Job 0 finished ... took 0,744181 s` → `Pi is roughly 3.1424071424071425`
El driver junta los conteos: (puntos dentro del círculo / total) × 4 ≈ π. No sale exacto porque es una estimación con puntos al azar (cada corrida da un valor distinto).

6. Cierre
`Stopped Spark web UI` · `Successfully stopped SparkContext` · `Deleting directory ...\Temp\spark-...`
Apaga todo y borra los archivos temporales.

>Es lo mismo que explica `09 RDD_v02.pdf`: las transformaciones (`map`) son perezosas y solo se ejecutan cuando llega una acción (`reduce`, `count`, `collect`...).

Para ver solo la línea del resultado:
```powershell
run-example SparkPi 10 2>&1 | Select-String "Pi is roughly"
```
- `2>&1` → Spark escribe sus logs por la salida de errores; esto la junta con la salida normal para poder filtrarla.
- `| Select-String "..."` → deja solo las líneas que contienen ese texto.

>**Error: `run-example : El término 'run-example' no se reconoce...`**
La terminal se abrió antes de agregar `C:\spark\bin` al Path, y PowerShell no ejecuta programas de la carpeta actual solo por su nombre. Solución: abrir una PowerShell **nueva**, o ejecutar con `.\` delante ("en esta carpeta"):
```powershell
.\run-example SparkPi 10
```

#### 5.2 spark-shell con `shake.txt`
El manual usa `repos.txt`, que no tenemos. Se usa `shake.txt` (obras completas de Shakespeare, Project Gutenberg: 124 456 líneas, ~5 MB); el nombre de salida del manual (`output_shake_`) indica que es este archivo.

**a) Abrir la consola de Spark**
```powershell
cd C:\Users\esauf\Desktop\uni\macro-datos\semana_9
spark-shell
```
- `cd ...\semana_9` → ir a la carpeta de `shake.txt`, para que Spark lo encuentre solo por su nombre.
- `spark-shell` → consola interactiva de Scala con Spark ya cargado. Arranca un driver en modo local (igual que SparkPi), la web de monitoreo en http://localhost:4040 y deja listo el objeto **`sc`** (el SparkContext).

Está lista cuando aparece el logo de Spark, la línea `Spark context available as 'sc'` y el prompt `scala>`.

**b) Cargar el archivo**
```scala
val txtShake = sc.textFile("shake.txt")
```
- `val txtShake` → variable de Scala que no se puede reasignar (`val` = valor fijo).
- `sc.textFile("shake.txt")` → crea un **RDD** donde cada elemento es una línea del archivo.

Es una **transformación**: todavía no lee nada (perezoso). Solo responde algo como:
```
txtShake: org.apache.spark.rdd.RDD[String] = shake.txt MapPartitionsRDD[1] at textFile ...
```
`RDD[String]` = colección distribuida de textos.

**c) Ver el contenido**
El manual usa:
```scala
txtShake.collect
```
- `collect` → **acción**: trae todas las líneas al driver y las imprime. Con este archivo son 124 456 líneas en pantalla.

Mejor ver solo las primeras 10:
```scala
txtShake.take(10)
```
- `take(10)` → también es acción, pero trae solo los primeros 10 elementos. Debe mostrar `This is the 100th Etext file presented by Project Gutenberg, and` ...

Resultado obtenido:
```
scala> txtShake.collect
res0: Array[String] = Array(This is the 100th Etext file presented by Project Gutenberg, and, is presented in cooperation with World Library, Inc., from their, ... PROHIBITED COMMERCI...

scala> txtShake.take(10)
res1: Array[String] = Array(This is the 100th Etext file presented by Project Gutenberg, and, is presented in cooperation with World Library, Inc., from their, ..., "", Shakespeare, "", ...)
```
- `res0`, `res1` → nombres automáticos que la consola da a cada resultado (se pueden reusar: `res1.length`).
- `Array(...)` → `collect`/`take` devuelven un arreglo normal de Scala (ya no es RDD: los datos están en el driver).
- `""` → líneas vacías del archivo.
- `collect` termina en `...` → la consola recorta lo que imprime, pero el arreglo completo (124 456 líneas) sí se trajo a memoria.
- Si se repite `val txtShake = ...`, se crea un RDD nuevo (`MapPartitionsRDD[3]`, luego `[5]`): el número es solo el ID interno que Spark asigna a cada RDD.

**d) Conteo (el wordcount del manual)**
```scala
val query = txtShake.flatMap(line => line.split(",")).map(word => (word,1)).reduceByKey(_ + _)
```
- `flatMap(line => line.split(","))` → parte cada línea por las comas y "aplana": de un RDD de líneas pasa a un RDD de pedazos (una línea con 2 comas da 3 elementos).
- `map(word => (word,1))` → convierte cada pedazo en un par `(pedazo, 1)`.
- `reduceByKey(_ + _)` → agrupa los pares por la clave (el pedazo) y suma los 1 → `(pedazo, cantidad de veces)`. `_ + _` es la forma corta de `(a, b) => a + b`.

Las tres son **transformaciones**: no se ejecuta nada todavía, solo se arma el plan.

>El manual parte por `","`, no por espacios: lo que cuenta no son palabras sino los trozos de texto entre comas (ej. `This is the 100th Etext file presented by Project Gutenberg` es un solo "word"). Es exactamente el mismo patrón map → reduce del wordcount de Hadoop.

```scala
query.toDebugString
```
- `toDebugString` → muestra el **linaje** (lineage) del RDD: la cadena de transformaciones que lo produce, desde `shake.txt`. Es lo que Spark usaría para recalcular una partición perdida (tolerancia a fallos, ver `09 RDD_v02.pdf`).

**e) Guardar en memoria, ejecutar y ver el resultado**
```scala
query.cache()
```
- `cache()` → marca el RDD para que, la primera vez que se calcule, se quede guardado en memoria. Así la siguiente acción no vuelve a leer `shake.txt` ni a repetir flatMap/map/reduceByKey. Por sí solo no ejecuta nada (también es perezoso).

```scala
query.saveAsTextFile("output_shake_")
```
- `saveAsTextFile("output_shake_")` → **acción**: aquí recién se ejecuta todo el plan (leer → partir → contar) y se guarda el resultado en la carpeta `semana_9\output_shake_\`. Como hay `cache()`, el resultado además queda en memoria.
- Dentro de la carpeta: un archivo `part-0000N` por partición (cada línea es un par `(pedazo,cantidad)`) y un archivo vacío `_SUCCESS` que indica que terminó bien. Igual que la salida de Hadoop.
- Si la carpeta ya existe da error (`FileAlreadyExistsException`): borrarla o usar otro nombre.

```scala
query.collect
```
- `collect` → **acción**: trae todos los pares `(pedazo, cantidad)` al driver y los imprime (la consola recorta con `...`). Esta vez es rápido porque lo lee del cache, no recalcula.

#### Resultado de d) y e)
>**Error: `<console>:23: error: not found: value query`** → se corrió `query.toDebugString` antes de definir `query`. Primero va `val query = ...`.

```
scala> val query = txtShake.flatMap(line => line.split(",")).map(word => (word,1)).reduceByKey(_ + _)
query: org.apache.spark.rdd.RDD[(String, Int)] = ShuffledRDD[8] at reduceByKey at <console>:23
```
- `RDD[(String, Int)]` → ahora es un RDD de pares (texto, número).
- `ShuffledRDD` → `reduceByKey` necesita juntar todos los pares con la misma clave, que pueden estar en particiones distintas → los redistribuye (**shuffle**).

```
scala> query.toDebugString
res3: String =
(2) ShuffledRDD[8] at reduceByKey at <console>:23 []
 +-(2) MapPartitionsRDD[7] at map at <console>:23 []
    |  MapPartitionsRDD[6] at flatMap at <console>:23 []
    |  shake.txt MapPartitionsRDD[5] at textFile at <console>:23 []
    |  shake.txt HadoopRDD[4] at textFile at <console>:23 []
```
El linaje se lee **de abajo hacia arriba**:
- `HadoopRDD[4]` → lectura cruda de `shake.txt` (Spark usa el lector de archivos de Hadoop).
- `MapPartitionsRDD[5]` → `textFile` convierte eso en líneas de texto.
- `MapPartitionsRDD[6]` → `flatMap` (partir por comas).
- `MapPartitionsRDD[7]` → `map` (pares `(pedazo,1)`).
- `ShuffledRDD[8]` → `reduceByKey` (sumar).
- `(2)` → número de particiones: Spark partió el archivo en 2 (por defecto `textFile` usa mínimo 2).
- `+-` → marca un límite de **etapa** (stage) por el shuffle: el job tiene 2 etapas (leer/flatMap/map → reduceByKey).
- `[]` → nivel de almacenamiento vacío: todavía no está en cache.

```
scala> query.cache()
res4: query.type = ShuffledRDD[8] at reduceByKey at <console>:23

scala> query.saveAsTextFile("output_shake_")

scala> query.collect
res6: Array[(String, Int)] = Array(("    'Gainst all the world will rightfully maintain.",1), (" yet they lie",1), ("  PANDULPH. Lady",1), ...
```
- `cache()` devuelve el mismo RDD (`ShuffledRDD[8]`), ahora marcado para guardarse en memoria.
- `saveAsTextFile` no imprime nada = terminó sin error.
- `collect` → casi todo tiene cantidad `1`: como se parte por comas, cada "pedazo" es una frase completa y casi nunca se repite igual. Los espacios al inicio se conservan (`"    'Gainst..."`) porque no se recortan.

Carpeta generada `semana_9\output_shake_\`:
```
part-00000        2 860 656 bytes
part-00001        2 864 550 bytes
_SUCCESS          0 bytes
.part-0000N.crc   (checksums)
```
- 2 archivos `part-` = 2 particiones (el `(2)` del linaje). En total 154 959 pares `(pedazo,cantidad)`.
- `.crc` → sumas de verificación que escribe el lector/escritor de Hadoop para detectar archivos dañados.

### 6. Ejercicio `sales1spark.txt`: ventas por país
Bloque original (para pegar en `spark-shell`):
```scala
val bdsales = sc.textFile("SalesJan2009_04.csv")
bdsales.collect
val mp = bdsales.mapPartitionsWithIndex{ (idx,iter) => if(idx == 0) iter.drop(1) else iter }
mp.collect
mp.take(2)
val venpa = mp.map(s => s.split(",")(7)).map(word => (word,1)).reduceByKey(_ + _)
venpa.collect
venpa.take(2)
venpa.saveAsTextFile("01output_venta")
```
- `sc.textFile(...)` → carga el CSV como RDD de líneas.
- `mapPartitionsWithIndex{ (idx,iter) => ... }` → recorre cada partición con su número (`idx`); en la partición 0 quita la primera línea (`iter.drop(1)`), que es la cabecera con los nombres de columnas.
- `split(",")(7)` → toma la columna 8 (índice desde 0) = `Country`.
- `map` + `reduceByKey` → mismo conteo que con `shake.txt`, pero por país.
- `saveAsTextFile("01output_venta")` → guarda el resultado en esa carpeta.

>`SalesJan2009_04.csv` no está en el repo. Se usa `Ventas File-20260908\Ventas\SalesJan2009.csv` (999 líneas, mismas columnas: `Transaction_date,Product,Price,Payment_Type,Name,City,State,Country,...`).

#### Error en los datos: aparece `(CO,1)` como país
Fila que lo causa:
```
1/28/09 18:00,Product1,"13,000",Visa,sandhya,Centennial,CO,United States,...
```
El precio `"13,000"` tiene una coma dentro de comillas. `split(",")` no entiende comillas: corta ahí también, las columnas se corren un lugar y la columna 7 queda en `CO` (Colorado) en vez de `United States` → United States sale 462 en vez de 463.

Corrección: partir solo en las comas que están fuera de comillas:
```scala
val venpa = mp.map(s => s.split(""",(?=(?:[^"]*"[^"]*")*[^"]*$)""")(7)).map(word => (word,1)).reduceByKey(_ + _)
```
- `"""..."""` → texto literal de Scala (no hay que escapar las comillas de adentro).
- `,(?=...)` → expresión regular: corta en una coma solo si después de ella queda un número par de comillas hasta el final de la línea, o sea, si está fuera de comillas.

#### Con el script
Corre el bloque (ya corregido) sin pegarlo a mano (ver sección 8):
```powershell
cd C:\Users\esauf\Desktop\uni\macro-datos\semana_9
.\spark.ps1 sales1spark-venpa-ventas-por-pais
```
- Ejecuta `scala\sales1spark-venpa-ventas-por-pais.scala`: muestra líneas leídas (999), las 2 primeras filas sin cabecera y los pares `(país, cantidad)`, y guarda `semana_9\01output_venta\`.
- La ruta del CSV está en la primera línea de código del `.scala` (relativa a `semana_9`).

Resultado (extracto):
```
(United States,463)
(United Kingdom,100)
(Canada,76)
(Ireland,49)
(Australia,38)
(Switzerland,36)
...
```
>Si hay otra `spark-shell` abierta aparece `WARN Utils: Service 'SparkUI' could not bind on port 4040. Attempting port 4041.`: no es error, solo usa el siguiente puerto para la web de monitoreo.

### 7. Cuadernos de Colab (`spark_01_base.txt` y `spark_02.txt`)
Cada enlace de los `.txt` abre un cuaderno de Colab del profesor (archivos `ClaSp01_V01.ipynb` a `ClaSp01_V06.ipynb`), y el bloque ``` que le sigue es el código de ese cuaderno, comentado línea por línea con `//`. El código es **Scala** (aunque el bloque diga `python` y esté en Colab): se ejecuta en `spark-shell`, no en Colab.

| Cuaderno | Dónde está el código | Contenido |
|---|---|---|
| ClaSp01_V01 | `spark_01_base.txt`, 1.er bloque | Manual de instalación + SparkPi + wordcount por comas (pasos 1–5) |
| ClaSp01_V02 | `spark_01_base.txt`, 2.º bloque | Variables, lambdas, `def`, `class`, `object` + tarea `OpeRand` → `class` |
| ClaSp01_V03 | `spark_01_base.txt`, 3.er bloque | `if`, `while`, `foreach`, `for ... yield`, parámetros por defecto |
| ClaSp01_V06 | `spark_02.txt`, 1.er bloque | Conteo de palabras partiendo por espacios (7.1) |
| ClaSp01_V05 | `spark_02.txt`, 2.º bloque | `filter` con `startsWith` / `contains`, `persist`, `split(" ")(n)` |
| ClaSp01_V04 | `spark_02.txt`, 3.er bloque | Dataset (`spark.read.textFile`) con `filter`, `first`, `count` |

Completado en `spark_01_base.txt`: `repos.txt` → `shake.txt` (1.er bloque); `val x = 6 + 4`, la clase `miclase`, el `.` de `new miclase("hola","amiga").imprimo(...)`, la tarea `class OpeRandC` y `:quit` (2.º bloque). Las plantillas `class nombre(...)` / `objeto nombre{...}` quedaron como comentario (no se ejecutan).

#### 7.1 Cuaderno V06: conteo de palabras de `shake.txt`
En `spark-shell`, abierto desde `semana_9`:
```scala
val txtShake = sc.textFile("shake.txt")
val contar = txtShake.flatMap(line => line.split(" ")).map(word => (word,1)).reduceByKey(_ + _)
contar.count
contar.toDebugString
contar.cache()
contar.saveAsTextFile("output_shake")
contar.take(10)
```
- `sc.textFile("shake.txt")` → RDD de líneas, igual que en el paso 5.2.
- `line.split(" ")` → corta por espacios: cada elemento es una **palabra**, no una frase entre comas. Es el único cambio frente al manual.
- `map(word => (word,1))` + `reduceByKey(_ + _)` → mismo conteo, ahora por palabra.
- `contar.count` → acción: cuántas palabras **distintas** hay.
- `contar.toDebugString` → linaje (igual al de `query` del paso 5.2).
- `contar.cache()` → guarda el resultado en memoria para las acciones siguientes.
- `contar.saveAsTextFile("output_shake")` → escribe en `semana_9\output_shake\` (distinta de `output_shake_` del paso 5.2).
- `contar.take(10)` → muestra 10 pares `(palabra, cantidad)`. El cuaderno usa `contar.collect`, que imprime todos.

Con el script:
```powershell
cd C:\Users\esauf\Desktop\uni\macro-datos\semana_9
.\spark.ps1 clasp01-v06-1-bloque-contar
```
- Ejecuta `scala\clasp01-v06-1-bloque-contar.scala`: los mismos comandos en el mismo orden del cuaderno y al final indica la carpeta de salida (ver sección 8).

Resultado:
```
contar.count = 67506
contar.toDebugString:
(2) ShuffledRDD[4] at reduceByKey ...
 +-(2) MapPartitionsRDD[3] at map ...
    |  MapPartitionsRDD[2] at flatMap ...
    |  shake.txt MapPartitionsRDD[1] at textFile ...
    |  shake.txt HadoopRDD[0] at textFile ...
contar.collect (primeros 10 pares):
(hack'd.,1)
(durst,,1)
(Ah!,3)
(bone,7)
...
```
Carpeta `semana_9\output_shake\`: `part-00000` y `part-00001` (~426 KB cada uno), `_SUCCESS` y los `.crc`.

>Al partir solo por espacios la puntuación queda pegada: `hack'd.` y `durst,` cuentan como palabras distintas de `hack'd` y `durst`. Así lo hace el cuaderno.

### 8. Recetas `spark.ps1` (un archivo `.scala` por ejercicio)
Cada receta corre su archivo de `semana_9\scala\` (mismo nombre que la receta) con `spark-shell -i`. Mismo estilo que `..\hadoop.ps1` y `..\PARCIAL\parcial.ps1`.
```powershell
cd C:\Users\esauf\Desktop\uni\macro-datos\semana_9
.\spark.ps1 help
.\spark.ps1 clasp01-v02-2-bloque-masDos-miclase-OpeRand
```
- `.\spark.ps1 help` → lista las recetas con su descripción (default).
- `.\spark.ps1 <receta>` → corre `scala\<receta>.scala`. El nombre se puede escribir en mayúsculas o minúsculas.

Nombre = `clasp01-v<NN>-<B>-bloque-<variables principales>`: `vNN` es el cuaderno `ClaSp01_VNN` y `B` la posición del bloque dentro de su `.txt`.

| Receta | Código de | Resultado (prueba) |
|---|---|---|
| `clasp01-v01-1-bloque-txtShake-query` | `spark_01_base.txt`, 1.er bloque | guarda `output_shake_` |
| `clasp01-v02-2-bloque-masDos-miclase-OpeRand` | `spark_01_base.txt`, 2.º bloque | `x = 10` ... `object OpeRand: 2 4 8 10`, `class OpeRandC: op1.porDos = 2, op1.masDos = 4, op2.porDos = 2` |
| `clasp01-v03-3-bloque-if-while-area` | `spark_01_base.txt`, 3.er bloque | `a = 8 -> s = 1`, `a = 9 -> s = 0`, `area(4) = 115.2` |
| `clasp01-v06-1-bloque-contar` | `spark_02.txt`, 1.er bloque | `contar.count = 67506`, guarda `output_shake` |
| `clasp01-v05-2-bloque-errorsexam` | `spark_02.txt`, 2.º bloque | `lineas.count = 124456`, `errorsexam04.count = 1846` (el `split(" ")(2)` no da error con `shake.txt`) |
| `clasp01-v04-3-bloque-logData` | `spark_02.txt`, 3.er bloque | `logData.count = 124456`, `contThe.count = 36457` |
| `sales1spark-venpa-ventas-por-pais` | `sales1spark.txt` | `(United States,463)`, guarda `01output_venta` |

Qué hace cada receta (`Invoke-Receta` en `spark.ps1`):
1. Comprueba que `spark-shell` esté en el Path y que exista el `.scala`.
2. Se para en `semana_9` (los `.scala` usan rutas relativas: `shake.txt`, carpetas de salida).
3. Si la receta guarda resultado, borra la carpeta anterior (si existe, `saveAsTextFile` falla).
4. Ejecuta `spark-shell -i scala\<receta>.scala`.
5. Si guarda resultado, revisa `_SUCCESS` (`spark-shell -i` no devuelve código de error si una línea falla).

Diferencias de los `.scala` frente al código de los `.txt` (el de los `.txt` es para pegar línea por línea en la consola):
- `spark-shell -i` compila el archivo **completo de una vez** y no deja repetir un `val`/`var` (`error: x is already defined`). Las redeclaraciones se cambiaron: `x` → `x2`, `ab1`/`ab2` → `ab3`/`ab4` (V02); `var a = 9` → `a = 9`, `var arg = "SCALA"` → `arg = "SCALA"`, `s` → `s2`, `i` → `i2` (V03). Cada cambio tiene un comentario `// en el cuaderno: ...`.
- Al correr un archivo no se imprime el valor de cada línea: se usa `println(...)`.
- `collect` se cambió por `take(n)` para no imprimir las 124 456 líneas.
- V05: `split(" ")(2)` y `(4)` van dentro de `Try(...)`: si una línea tuviera menos palabras, el error se muestra y el archivo sigue hasta el final.
- Cada `.scala` termina en `System.exit(0)` (en el cuaderno, `:quit`). Si una línea da error de compilación no llega ahí y `spark-shell` queda abierto en `scala>`: salir con `:quit`.

>Al cerrar puede aparecer `ERROR ShutdownHookManager: Exception while deleting Spark temp dir: ...\Temp\spark-...`: en Windows Spark a veces no puede borrar sus temporales. No afecta el resultado.
