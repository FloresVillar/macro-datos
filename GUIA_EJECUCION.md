#
### (SIEMPRE al comenzar )Powershell modo administrador
**ir a la carpeta de trabajo**
- cd C:\Users\esauf\Desktop\uni\macro-datos

**ejecutar el target que dirige a "C:\Hadoop3\sbin"**

Es donde estan los .sh
- ejecutar:  .\hadoop.ps1 start  (revisar el codigo del .ps1 de ser necesario)

- hadoop fs -mkdir -p /input_dir

### **el siguiente .txt debe estar en C**
hadoop fs -put C:/input_file.txt /input_dir
hadoop fs -ls /input_dir/

>/input_dir no es una carpeta de Windows (no es relativo a C:\ ni a la carpeta donde estás parado). Es una ruta dentro del namespace de HDFS, un sistema de archivos completamente separado y virtual que vive "encima" de Windows.

Por eso no importa desde qué carpeta se corra el comando (macro-datos, sbin, o cualquier otra) — hadoop fs -mkdir /input_dir siempre apunta al mismo lugar: la raíz / de HDFS, definida en tu core-site.xml:
<value>hdfs://0.0.0.0:19000</value>

### **el siguiente .jar que muestra el segundo comando abajo debe estar en C:/**
```cmd
hadoop dfs -cat /input_dir/input_file.txt   
hadoop fs -rm -r /output_dir   # borrar el output si existiera antes , en HDFS 
hadoop jar C:/MapReduceClient.jar wordcount /input_dir /output_dir
hadoop dfs -cat /output_dir/*
```
>`MapReduceClient.jar` NO viene con Hadoop. Se descargó de un tutorial externo de GitHub (https://github.com/MuhammadBilalYar/Hadoop-On-Window), transcrito en `semana_1/02 TestHadoop.md` (a partir de `semana_1/02 TestHadoop.pdf`).

### **Resultado de hadoop jar C:/MapReduceClient.jar wordcount /input_dir /output_dir**

1. Conexión y envío del job:
Connecting to ResourceManager at /0.0.0.0:8032 Submitted application application_1789514111121_0001
El cliente hadoop jar se conecta al ResourceManager de YARN (el orquestador) y le entrega el trabajo. YARN le asigna un ID: application_1789514111121_0001.

2. Preparación
Total input files to process : 1
number of splits: 1
Hadoop mira /input_dir, encuentra 1 archivo (el input_file.txt), y decide en cuántos "splits" (trozos) dividirlo para procesarlo en paralelo — aquí solo 1, porque el archivo es chico (1888 bytes).

3. Ejecución (map → reduce)
map 0% reduce 0% map 100% reduce 0% map 100% reduce 100%
Job ... completed successfully
- Map: lee el archivo split por split y por cada número que encuentra emite un par (numero, 1).
- Reduce: agrupa todos los pares por número y suma los 1's → obtiene el conteo total de cada número.
- Como ves, primero termina el map (100%) y luego arranca y termina el reduce — es literalmente el patrón Map→Reduce.

4. Counters (estadísticas del job) — la parte larga al final:
- Map input records=30 → el archivo tenía 30 líneas/registros de entrada.
- Map output records=390 → el map emitió 390 pares (numero, 1) — o sea, 390 números individuales en total en el archivo.
- Reduce output records=21 → después de agrupar y sumar, quedaron 21 números distintos (esos son las 21 líneas que ves en /output_dir cuando haces cat).
- Bytes Read=1888 / Bytes Written=120 → leyó tu input completo y escribió un resultado mucho más chico (ya resumido/agregado).
- Launched map tasks=1, Launched reduce tasks=1 → cuántas tareas paralelas se lanzaron (aquí mínimo, por lo chico del archivo).

#### Resumen :¿Qué hace entonces cada pieza del comando?
**hadoop jar C:/MapReduceClient.jar wordcount /input_dir /output_dir**
- hadoop jar C:/MapReduceClient.jar → "toma este archivo, lo carga, y prepara para ejecutar una clase dentro del jar"
- wordcount → el jar en realidad es multi-programa (trae varios ejemplos: pi, grep, wordcount, etc.). Este argumento le dice cuál clase específica ejecutar — en este caso, org.apache.hadoop.examples.WordCount (lo confirma el stack trace).
- /input_dir /output_dir → son los argumentos que la clase WordCount.main() espera: de dónde leer y dónde escribir.

## Conceptos importantes del flujo
- YARN no sabe nada de "contar palabras" — YARN solo sabe orquestar tareas (repartir el trabajo en la máquina/cluster). 

- El jar es el que define la lógica de qué hacer con cada línea de datos. Si mañana se quisiera hacer algo distinto (como un ejercicio SalesCountry de más abajo en la guía), se cambiaría el jar por uno con un propio Mapper/Reducer, pero el comando hadoop jar ... seguiría funcionando igual — solo cambia qué lógica se ejecuta adentro.

- Con esto el clúster Hadoop 3.3.0 está completamente funcional de punta a punta: 
  - HDFS (almacenamiento) + 
  - YARN (orquestación de recursos) + 
  - MapReduce (procesamiento). 


**mas comandos en**

semana_1\02 TestHadoop.md

## Ejercicio SalesCountry (MapReduce con clases propias, compilado en NetBeans)
1. File → New Project → Categories: Java → Projects: Java Application
2. Project Name: MACRO-DATOS
   - dejar tildado "Create Main Class" (esto genera automáticamente
     macro.datos.MACRODATOS — de ahí sale el Main-Class que quedó
     grabado en el manifest, mencionado más abajo en la guía)
   - Finish → el proyecto se crea en
     C:\Users\esauf\Documents\NetBeansProjects\MACRO-DATOS\
3. Clic derecho en "Source Packages" → New → Java Package → nombre: SalesCountry
4. Clic derecho en el paquete SalesCountry → New → Java Class, repetir 3 veces:
  - SalesMapper
  - SalesCountryReducer
  - SalesCountryDriver

5. Copiar el contenido de cada .java desde `Ventas File-20260908\Ventas\ (o la ruta donde esten los codigos de las clases)`
   (SalesMapper.java, SalesCountryReducer.java, SalesCountryDriver.java)
   hacia el archivo vacío correspondiente que generó NetBeans — pegar el
   código completo, reemplazando el esqueleto vacío del wizard.

>NOTA:El proyecto NetBeans vive en `C:\Users\esauf\Documents\NetBeansProjects\MACRO-DATOS\` (no en Desktop\uni\macro-datos). Paquete `SalesCountry` (clases `SalesMapper`, `SalesCountryReducer`, `SalesCountryDriver`)

6. Project Properties → Libraries → Compile → Add JAR/Folder, agregar solo estos 4: `C:\Hadoop3\`
  - `share\hadoop\common\hadoop-common-3.3.0.jar`
  - `share\hadoop\mapreduce\hadoop-mapreduce-client-core-3.3.0.jar`
  - `share\hadoop\mapreduce\hadoop-mapreduce-client-common-3.3.0.jar`
  - `share\hadoop\mapreduce\hadoop-mapreduce-client-jobclient-3.3.0.jar`
7. (`el campo main class verifica que la clase Driver ya compila y tiene un main valido`) Project Properties → Run → Main Class: `SalesCountry.SalesCountryDriver`
8. clic derecho en proyecto → Build → genera `C:\Users\esauf\Documents\NetBeansProjects\MACRO-DATOS\dist\MACRO-DATOS.jar` (+ `dist\lib\` con las 4 deps)

8. **subir el csv a HDFS**
```powershell
hadoop fs -mkdir -p /sales_input
hadoop fs -put <RUTA_REAL_DEL_CSV> /sales_input    # <RUTA_REAL_DEL_CSV> "C:\Users\esauf\Desktop\uni\macro-datos\Ventas File-20260908\Ventas\SalesJan2009.csv"
hadoop fs -ls /sales_input
```
**salida del listado**

```text
Found 1 items
-rw-r--r--   1 esauf supergroup     123637 2026-09-08 19:09 /sales_input/SalesJan2009.csv
```

8. Ejecutando el JAR :  (NO usar `hadoop jar` — ver bug abajo)
```powershell
$cp = (hadoop classpath) #  Guarda en la variable $cp la lista de todos los .jar de Hadoop (los que tienen las clases Job, Configuration, FileInputFormat, etc.)
hadoop fs -rm -r /sales_output  # si existe
java -cp "<RUTA_AL_JAR>;$cp" <paquete>.<Clase> /sales_input /sales_output  
java -cp "C:\Users\esauf\Documents\NetBeansProjects\MACRO-DATOS\dist\MACRO-DATOS.jar;$cp" SalesCountry.SalesCountryDriver /sales_input /sales_output 
hadoop fs -cat /sales_output/*
```
>NOTA: (SalesCountry.SalesCountryDriver) es la única que necesita tener un método main(),donde arranca el programa. Esta clase le dice a hadoop "para este trabajo, usá esta clase como Mapper y esta otra como Reducer" ( job_conf.setMapperClass(SalesCountry.SalesMapper.class) y job_conf.setReducerClass(SalesCountry.SalesCountryReducer.class))

**resultado esperado**
999 registros de `SalesJan2009.csv` agrupados por país, 58 países distintos:
```
United States   462
United Kingdom  100
Canada  76
Australia       38
Switzerland     36
France  27
...
```

### Bug: `hadoop jar` no ejecuta nada en Windows con jars propios (sin error visible)
```powershell
hadoop jar MACRO-DATOS.jar SalesCountry.SalesCountryDriver /sales_input /sales_output
```
termina al instante, exit code 0, sin logs, no crea el output. `yarn application -list -appStates ALL` confirma que no llegó nada a YARN.

**Regla general: usar `java -cp "<jar>;$(hadoop classpath)" <paquete.Clase>` en vez de `hadoop jar` para cualquier jar propio compilado.**

### Revisando el codigo de los .java
Es recomendable revisarlo linea a linea (de por si es interesante)

### Concretando los ejercicios 
las columnas reales de SalesJan2009.csv: 
- Transaction_date(0) 
- Product(1) Price(2) 
- Payment_Type(3) 
- Name(4) City(5) 
- State(6) 
- Country(7) 
- Account_Created(8) 
- Last_Login(9) 
- Latitude(10) 
- Longitude(11).

#### 1 Numero de trasacciones por pais

**Objetivo y enfoque:** cada línea del CSV es una transacción, y su columna Country [7]
dice a qué país corresponde. Queremos saber cuántas transacciones tuvo cada país por
separado. Para eso, el Mapper usa el país como key (variable, distinta por línea) y
emite un 1 por transacción; el Reducer agrupa por país y suma esos 1's, dando el
conteo de transacciones de cada uno.

1. Clic derecho en "Source Packages" (proyecto MACRO-DATOS) → New → Java Package → Package Name: TransaccionesPorPais → Finish.
2. Clic derecho sobre el paquete TransaccionesPorPais → New → Java Class, repetir 3 veces:
   - Mapper
   - Reducer
   - Driver
3. Pegar en cada archivo el código de MACRO-DATOS/source_Packages/01_TransaccionesPorPais/ (acá en el repo): Mapper.java, Reducer.java, Driver.java — ya tienen el package TransaccionesPorPais; correcto.
4. Clic derecho en el proyecto MACRO-DATOS → Build → genera dist\MACRO-DATOS.jar actualizado (con SalesCountry + TransaccionesPorPais adentro).
5. Subir el CSV a HDFS (si no está ya en esa ruta):
```cmd
hadoop fs -mkdir -p /salesjam_input
hadoop fs -put "C:\Users\esauf\Desktop\uni\macro-datos\Ventas File-20260908\Ventas\SalesJan2009.csv" /salesjam_input
```
6. Ejecutar:
```powershell
$cp = (hadoop classpath)
hadoop fs -rm -r /salesjam_output/01_transacciones_pais
java -cp "C:\Users\esauf\Documents\NetBeansProjects\MACRO-DATOS\dist\MACRO-DATOS.jar;$cp" TransaccionesPorPais.Driver /salesjam_input /salesjam_output/01_transacciones_pais
hadoop fs -cat /salesjam_output/01_transacciones_pais/*
```
**Que se hizo ?**

- Mapper: por cada linea del CSV , toma la columa 8 [7] (Country) y emite (pais,1) 
```cmd
("United Kingdom", 1)
("United States", 1)
("United States", 1)
("Australia", 1)
("Israel", 1)
("France", 1)
("United States", 1)
("Netherlands", 1)
("United States", 1)
```
- Reducer: Hadoop agrupa automaticamente todos los pares con el mismo pais y se los para al Reducer que solo suma esos 1's para cada pais

RESULTADO
```powershell
Argentina       1
Australia       38
Austria 7
Bahrain 1
Belgium 8
Bermuda 1
Brazil  5
Bulgaria        1
CO      1
Canada  76
Cayman Isls     1
China   1
Costa Rica      1
Country 1
Czech Republic  3
Denmark 15
Dominican Republic      1
Finland 2
France  27
Germany 25
Greece  1
Guatemala       1
Hong Kong       1
Hungary 3
Iceland 1
India   2
Ireland 49
Israel  1
Italy   15
Japan   2
Jersey  1
Kuwait  1
Latvia  1
Luxembourg      1
Malaysia        1
Malta   2
Mauritius       1
Moldova 1
Monaco  2
Netherlands     22
New Zealand     6
Norway  16
Philippines     2
Poland  2
Romania 1
Russia  1
South Africa    5
South Korea     1
Spain   12
Sweden  13
Switzerland     36
Thailand        2
The Bahamas     2
Turkey  6
Ukraine 1
United Arab Emirates    6
United Kingdom  100
United States   462
```

#### 2 Numero de transaccion por Ciudad

**Objetivo y enfoque:** mismo problema que la consulta 1, pero agrupando por ciudad
en vez de país. La columna City [5] dice a qué ciudad corresponde cada transacción.
El Mapper usa la ciudad como key y emite un 1 por línea; el Reducer agrupa por
ciudad y suma esos 1's, dando el conteo de transacciones de cada una (por eso salen
tantas más filas de resultado que en la consulta 1 — hay muchas más ciudades que países).

1. En NetBeans, andá a la pestaña "Projects" (no "Files").
2. Clic derecho sobre "Source Packages" (dentro del proyecto MACRO-DATOS) → New → Java Package (TransaccionesPorCiudad ) 
5. Ahora clic derecho sobre el paquete nuevo TransaccionesPorCiudad que apareció en el árbol → New → Java Class.(crear las Clases Driver,Mapper,Reduce)
4. Reemplazar el codigo de esas 3 clase por `MACRO-DATOS/source_Packages/02_TransaccionesPorCiudad/<clase>.java` o donde se tenga el codigo.  
5. Clic derecho sobre el proyecto MACRO-DATOS (el nodo raíz, no un paquete) → Build.Esto regenera` C:\Users\esauf\Documents\NetBeansProjects\MACRO-DATOS\dist\MACRO-DATOS.jar con SalesCountry + TransaccionesPorPais + TransaccionesPorCiudad todos adentro`
6. PowerShell como administrador, andá a la carpeta de trabajo:
```powershell
cd C:\Users\esauf\Desktop\uni\macro-datos
./hadoop.ps1 start  
hadoop fs -mkdir -p /salesjam_input
hadoop fs -rm -r /salesjam_output/02_transacciones_ciudad
#Guardá el classpath de Hadoop en una variable:
$cp = (hadoop classpath) 
java -cp "C:\Users\esauf\Documents\NetBeansProjects\MACRO-DATOS\dist\MACRO-DATOS.jar;$cp" TransaccionesPorCiudad.Driver /salesjam_input /salesjam_output/02_transacciones_ciudad 
hadoop fs -cat /salesjam_output/02_transacciones_ciudad/*
```
**Que se hizo ?**

- Mapper: por cada linea del CSV , toma la columna 6 [5] (City) y emite (ciudad,1)
```cmd
("Basildon", 1)
("Parkville                   ", 1)
("Astoria                     ", 1)
("Echuca", 1)
("Cahaba Heights              ", 1)
("Mickleton                   ", 1)
("Peoria                      ", 1)
("Martin                      ", 1)
("Tel Aviv", 1)
```
- Reducer: Hadoop agrupa automaticamente todos los pares con la misma ciudad y se los pasa al Reducer que solo suma esos 1's para cada ciudad

RESULTADO (extracto, dataset completo tiene ~470 ciudades distintas)
```powershell
Aardal	1
Aberdeen	2
Amsterdam	3
Arlington                   	6
Atlanta	1
Austin                      	4
Bogota	1
Brussels	3
Buenos Aires	1
Calgary	11
Chicago                     	5
City	1
Dubai	3
Houston                     	7
London	19
Madrid	3
Melbourne	5
Munich	2
New York                    	9
Paris	6
Rome	1
sandhya	1
Seattle                     	6
Sydney	3
Tokyo	1
Toronto	6
Vancouver	8
WalcProduct3ee	1
Zurich	2
```
> Nota: `City 1` es la fila de cabecera contada como si fuera una ciudad (mismo caso que `Country 1` en la consulta 1). `sandhya` y `WalcProduct3ee` son registros con datos sucios del CSV original (columnas corridas), no un error del código.


**PARA LOS SIGUIENTES EJERCICIOS**

> **IMPORTANTE — a partir de la consulta 3, las secciones solo muestran el comando `.\hadoop.ps1 salesjam <Paquete>`, pero eso NO alcanza por sí solo.** La receta `salesjam` únicamente ejecuta el jar ya compilado y muestra el resultado — no crea nada en NetBeans ni compila. Antes de correr ese comando para cualquier ejercicio (3 en adelante), hay que tener hecho, igual que en la 1 y la 2:
> 1. El paquete + las 3 clases (`Mapper`, `Reducer`, `Driver`) creadas en el proyecto `MACRO-DATOS` de NetBeans — a mano (New Package → New Class x3 → pegar código) o ya copiadas por archivo en `C:\Users\esauf\Documents\NetBeansProjects\MACRO-DATOS\src\<Paquete>\` (en cuyo caso solo falta hacer Refresh en NetBeans para verlas).
> 2. **Build** del proyecto (clic derecho en `MACRO-DATOS` → Build) — regenera `dist\MACRO-DATOS.jar` con el paquete nuevo adentro. Sin este paso, `hadoop.ps1 salesjam` va a fallar porque la clase `<Paquete>.Driver` no existe todavía en el jar.
>
> Recién después de esos 2 pasos tiene sentido correr el comando de la receta.

#### 3 Numero Suma Precios Productos

**Objetivo y enfoque:** cada línea del CSV es una transacción de venta, y su columna
Price [2] es el monto de esa venta puntual. Queremos un solo número: la suma de esos
montos en las ~997 transacciones del archivo (osea, la facturación total), sin
separar por producto, ciudad ni nada más. Para lograrlo con Mapper/Reducer —que
agrupan por key— el truco es usar una key fija ("Total") en cada línea en vez de
una que varíe: así todas caen en el mismo grupo, y el Reducer termina sumando
absolutamente todo en un solo resultado.

```powershell
.\hadoop.ps1 salesJam SumaPreciosProductos
```

**Que se hizo ?**

- Mapper: por cada linea del CSV (salvo la fila de cabecera), toma la columna 3 [2] (Price) y emite ("Total", precio) — todos los precios van a la misma key fija, para que el Reducer los sume todos juntos.
```cmd
("Total", 1200)
("Total", 1200)
("Total", 1200)
("Total", 3600)
("Total", 1200)
("Total", 1200)
```
- Reducer: Hadoop agrupa todos los pares bajo la única key "Total" y se los pasa juntos al Reducer, que solo suma todos los precios.

RESULTADO (verificado también de forma independiente con `awk` sobre el CSV, coincide exacto)

El CSV original tiene terminadores de línea `CR` solo (no `CRLF`/`LF`), por lo que `awk`
no lo separa en líneas hasta convertirlo primero:
```bash
tr '\r' '\n' < "Ventas File-20260908/Ventas/SalesJan2009.csv" > /tmp/sales_lf.csv
awk -F',' 'NR>1 && $3 ~ /^[0-9]+$/ {sum+=$3} END {print sum}' /tmp/sales_lf.csv
```
```powershell
Total   1617500
```

#### 4 Suma Precios Product3

**Objetivo y enfoque:** queremos saber cuánto se facturó, en total, SOLO de las
ventas de "Product3" — no de los otros dos productos. El Mapper filtra: revisa la
columna Product [1], y si no es exactamente "Product3" no emite nada para esa línea
(se descarta). Para las que sí son Product3, emite el precio [2] bajo una key fija
("Product3"), y el Reducer suma solo esos.

```powershell
.\hadoop.ps1 salesjam SumaPreciosProduct3
```

**Que se hizo ?**

- Mapper: por cada linea del CSV, filtra — solo si la columna 1 (Product) es exactamente "Product3" toma la columna 2 (Price) y emite ("Product3", precio). Las líneas de otros productos (o la cabecera) no emiten nada.
```cmd
("Product3", 7500)
("Product3", 7500)
("Product3", 7500)
```
- Reducer: Hadoop agrupa todos los pares bajo la única key "Product3" (ya filtrados por el Mapper) y se los pasa juntos al Reducer, que solo suma esos precios.

RESULTADO (verificado también de forma independiente con `awk` filtrando solo filas Product3)
```bash
awk -F',' 'NR>1 { p=$2; gsub(/^[ \t]+|[ \t]+$/,"",p); if (p=="Product3" && $3 ~ /^[0-9]+$/) sum+=$3 } END {print sum}' /tmp/sales_lf.csv
```
```powershell
Product3        112500
```

#### 5 Suma Precios Por Tipo Producto

**Objetivo y enfoque:** a diferencia de la consulta 4 (que aisló solo Product3),
acá queremos la facturación de **cada** producto por separado, los tres a la vez.
En vez de una key fija o un filtro, el Mapper usa directamente el nombre del
producto [1] como key (que varía según la línea) y emite el precio [2] como value.
El Reducer agrupa automáticamente por cada producto distinto y suma los precios
de su grupo — el mismo mecanismo que en la consulta 1, pero sumando precios en
vez de contar transacciones.

```powershell
.\hadoop.ps1 salesjam SumaPreciosPorTipoProducto
```

**Que se hizo ?**

- Mapper: toma la columna 1 (Product) como key y la columna 2 (Price) como value, emite (producto, precio) — sin filtrar por ningún producto en particular, a diferencia de la consulta 4.
- Reducer: Hadoop agrupa por producto, y el Reducer suma los precios de cada grupo.

RESULTADO (verificado también con `awk`, coincide exacto)
```powershell
Product1	1015400
Product2	489600
Product3	112500
```

#### 6 Suma Precios Por Producto Y Tipo De Pago

**Objetivo y enfoque:** un paso más de detalle que la consulta 5 — no solo cuánto
facturó cada producto, sino cuánto facturó cada producto **desglosado por método
de pago** (ej. cuánto de Product1 se pagó con Visa vs. con Mastercard). Como
Mapper/Reducer solo agrupan por una key, el truco es combinar dos columnas en una
sola key compuesta: Product [1] + Payment_Type [3], unidas con un guion (ej.
"Product1-Visa"). El Reducer agrupa por esa combinación exacta y suma los precios [2].

```powershell
.\hadoop.ps1 salesjam SumaPreciosPorProductoYPago
```

**Que se hizo ?**

- Mapper: arma una key compuesta "Producto-TipoDePago" (columnas 1 y 3) y emite (esa key, precio).
- Reducer: Hadoop agrupa por esa combinación exacta, y el Reducer suma los precios de cada grupo.

RESULTADO (verificado también con `awk`, coincide exacto — 3 productos x 4 tipos de pago = 12 combinaciones)
```powershell
Product1-Amex	105800
Product1-Diners	97200
Product1-Mastercard	280550
Product1-Visa	531850
Product2-Amex	75600
Product2-Diners	21600
Product2-Mastercard	140400
Product2-Visa	252000
Product3-Amex	7500
Product3-Diners	15000
Product3-Mastercard	37500
Product3-Visa	52500
```

#### 7 Precio Producto Mas Costoso

**Objetivo y enfoque:** ya no queremos sumar nada — queremos encontrar el precio [2]
más alto registrado en todo el archivo (la venta más cara). Igual que en la consulta
3, se usa una key fija ("Max") para que todos los precios caigan en el mismo grupo,
pero el Reducer ya no suma: compara cada valor contra el máximo visto hasta el
momento (arrancando desde `Integer.MIN_VALUE`) y se queda con el más grande.

```powershell
.\hadoop.ps1 salesjam PrecioProductoMasCostoso
```

**Que se hizo ?**

- Mapper: emite todos los precios bajo la misma key fija "Max".
- Reducer: recorre todos los precios que llegan bajo esa key y se queda con el más alto (`Integer.MIN_VALUE` como punto de partida).

RESULTADO (verificado también con `awk`, coincide exacto)
```powershell
Max	7500
```

#### 8 Precio Mas Barato Product1

**Objetivo y enfoque:** combina las dos ideas anteriores — filtrar (como en la 4) y
buscar un extremo (como en la 7), pero esta vez el mínimo. El Mapper filtra por
Product [1] == "Product1" (descarta las demás líneas) y emite el precio [2] bajo
una key fija ("Product1"). El Reducer recorre esos precios y se queda con el más
bajo (arrancando desde `Integer.MAX_VALUE` en vez de MIN_VALUE, al revés que en la 7).

```powershell
.\hadoop.ps1 salesjam PrecioMasBaratoProduct1
```

**Que se hizo ?**

- Mapper: filtra — solo si la columna Product es "Product1", emite (Product1, precio).
- Reducer: recorre esos precios y se queda con el más bajo (`Integer.MAX_VALUE` como punto de partida).

RESULTADO (verificado también con `awk`, coincide exacto)
```powershell
Product1	250
```

#### 10 Tarjeta Mas Usada Por Ciudad Pais

**Objetivo y enfoque:** para cada combinación de ciudad+país, queremos saber cuál
es el método de pago (tarjeta) más frecuente. Esto ya no es solo sumar/contar un
número — hace falta comparar frecuencias dentro de cada grupo. El Mapper combina
City [5] + Country [7] en una key compuesta ("Ciudad|Pais"), y emite el
Payment_Type [3] de esa transacción como value (texto, no número). El Reducer,
para cada grupo, cuenta cuántas veces aparece cada tarjeta distinta usando un
`HashMap<String,Integer>`, y al final se queda con la que tuvo más apariciones.

```powershell
.\hadoop.ps1 salesjam TarjetaMasUsadaPorCiudadPais
```

**Que se hizo ?**

- Mapper: arma una key compuesta "Ciudad|Pais" (columnas 5 y 7) y emite como value el tipo de tarjeta (columna 3) de esa transacción.
- Reducer: por cada grupo Ciudad|Pais, cuenta cuántas veces aparece cada tarjeta en un `HashMap<String,Integer>`, y se queda con la de mayor conteo.

RESULTADO (extracto — dataset completo tiene una fila por cada combinación distinta de ciudad+país; verificado con un spot-check contra el CSV para "York|United Kingdom")
```powershell
York|United Kingdom	Visa (2)
Zug|Switzerland	Amex (1)
Zurich|Switzerland	Visa (1)
sandhya|CO	000" (1)
```
> Nota: `sandhya|CO` es la misma fila con datos sucios ya vista antes (columnas corridas por una coma embebida) — no un error del código.

#### 11 Desviacion Estandar Precios

**Objetivo y enfoque:** queremos medir qué tan dispersos están los precios de venta
respecto al promedio — no solo el promedio (media), sino cuánto varían (desviación
estándar). Igual que en la 3 y la 7, se usa una key fija ("Precios") para que todos
los valores lleguen al mismo Reducer. Ahí, en una sola pasada por todos los precios [2],
se acumulan tres cosas: cuántos son (n), la suma de todos, y la suma de cada uno
elevado al cuadrado. Con esos tres números alcanza para calcular la media
(suma/n) y la desviación estándar (`sqrt((sumaCuadrados/n) - media²)`) sin tener
que guardar la lista completa de precios en memoria.

```powershell
.\hadoop.ps1 salesjam DesviacionEstandarPrecios
```

**Que se hizo ?**

- Mapper: emite todos los precios bajo la misma key fija "Precios".
- Reducer: acumula cantidad, suma y suma de cuadrados en una sola pasada, y calcula media y desviación estándar con la fórmula `sqrt((sumaCuadrados/n) - media^2)`.

RESULTADO (verificado también con `awk`, coincide exacto)
```powershell
Precios	media=1622.3671013039118 desviacion_estandar=1098.5018582773716 n=997
```

#### 12 Buscar United Kingdom

**Objetivo y enfoque:** a diferencia de todas las anteriores, esto no es una
agregación (contar/sumar/máximo/mínimo) — es una búsqueda de texto simple, tipo
`grep`. Queremos ver el registro completo de cada transacción que mencione
"United Kingdom" en cualquier parte de la línea. El Mapper no separa columnas ni
calcula nada: solo revisa si la línea completa contiene ese texto, y si es así,
la emite tal cual (la línea entera funciona como key). El Reducer no suma nada
importante, solo deja pasar el resultado — el trabajo real ya lo hizo el filtro del Mapper.

```powershell
.\hadoop.ps1 salesjam BuscarUnitedKingdom
```

**Que se hizo ?**

- Mapper: no agrupa ni suma nada — si la línea contiene "United Kingdom", la emite completa como key.
- Reducer: solo pasa el conteo (normalmente 1, salvo líneas duplicadas exactas).

RESULTADO (verificado con `grep -c "United Kingdom"` sobre el CSV, da 100 líneas — coincide con el conteo de la consulta 1)
```powershell
1/1/09 12:42,Product1,1200,Visa,ashton,Exeter,England,United Kingdom,12/15/08 1:16,2/9/09 2:52,50.7,-3.5333333	1
1/1/09 16:00,Product1,1200,Visa,Toni,Bolton,England,United Kingdom,10/7/08 15:19,2/3/09 16:45,53.5833333,-2.4333333	1
...(98 líneas más)
```
