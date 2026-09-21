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
## PC1
1. En netbeans crear el proyecto `PC1`
2. Project Properties → Libraries → Compile → Add JAR/Folder, agregar solo estos 4: `C:\Hadoop3\`
  - `share\hadoop\common\hadoop-common-3.3.0.jar`
  - `share\hadoop\mapreduce\hadoop-mapreduce-client-core-3.3.0.jar`
  - `share\hadoop\mapreduce\hadoop-mapreduce-client-common-3.3.0.jar`
  - `share\hadoop\mapreduce\hadoop-mapreduce-client-jobclient-3.3.0.jar`

3. Para cada ejercicio 
  - Clic derecho sobre "Source Packages" (dentro del proyecto) → New → Java Package (NOMBRE_DEL_PACKAGE ) 
  - Ahora clic derecho sobre el paquete nuevo NOMBRE_DEL_PACKAGE que apareció en el árbol → New → Java Class.(CREAR las Clases Driver,Mapper,Reduce)

### 1a Recursos Por Region Categoria

**Objetivo y enfoque:** queremos saber cuántos recursos turísticos hay registrados
por cada combinación de región y categoría (ej. cuántos "Sitios Naturales" tiene
Cusco, cuántas "Manifestaciones Culturales" tiene Lima, etc.) — es la misma idea
que "transacciones por país" de SalesJam, pero agrupando por 2 columnas juntas en
vez de 1. El Mapper separa la línea por `;` (este CSV usa punto y coma, no coma
como el de SalesJam), toma REGIÓN [0] y CATEGORÍA [5], las combina en una sola key
compuesta ("Region|Categoria") y emite 1. El Reducer agrupa por esa combinación y
suma los 1's, dando el conteo de cada una.

> Nota: para saltar la fila de cabecera se usa `key.get() == 0` (la posición en
> bytes de la primera línea siempre es 0) en vez de comparar el texto de la
> cabecera — más seguro, porque el CSV tiene columnas con tildes (ej. "REGIÓN")
> que podrían tener problemas de codificación al compararlas como texto exacto.

- Build del proyecto → genera dist\PC1.jar.
- crear la carpeta para los datos
```powershell
hadoop fs -mkdir -p /pc1_input  
```
-  Subir el CSV (UTF-8) a HDFS
```powershell
hadoop fs -put "C:\Users\esauf\Desktop\uni\macro-datos\PC1\Inventario_recursos_turisticos.csv" /pc1_input
```
- Ejecutar:
```powershell
$cp = (hadoop classpath)
hadoop fs -rm -r /pc1_output/RecursosPorRegionCategoria
java -cp "C:\Users\esauf\Documents\NetBeansProjects\PC1\dist\PC1.jar;$cp" RecursosPorRegionCategoria.Driver /pc1_input /pc1_output/RecursosPorRegionCategoria
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
hadoop fs -cat /pc1_output/RecursosPorRegionCategoria/*
```

**Que se hizo ?**

- Mapper: por cada línea (salvo la cabecera), separa por `;`, toma REGIÓN [0] y CATEGORÍA [5], las une en una key compuesta y emite ("Region|Categoria", 1).
```cmd
("Cusco|1. SITIOS NATURALES", 1)
("Cusco|2. MANIFESTACIONES CULTURALES", 1)
("Amazonas|1. SITIOS NATURALES", 1)
```
- Reducer: Hadoop agrupa por cada combinación región+categoría y suma los 1's, dando el conteo de recursos de ese grupo.

RESULTADO (125 filas = 25 regiones x 5 categorías; extracto de las primeras)
```powershell
Amazonas|1. SITIOS NATURALES	26
Amazonas|2. MANIFESTACIONES CULTURALES	47
Amazonas|5. ACONTECIMIENTOS PROGRAMADOS	4
Apurímac|1. SITIOS NATURALES	73
Apurímac|2. MANIFESTACIONES CULTURALES	88
Apurímac|3. FOLCLORE	3
Apurímac|4. REALIZACIONES TÉCNICAS, CIENTÍFICAS Y ARTÍSTICAS CONTEMPORÁNEAS	2
Apurímac|5. ACONTECIMIENTOS PROGRAMADOS	28
Cusco|1. SITIOS NATURALES	255
Cusco|2. MANIFESTACIONES CULTURALES	214
Cusco|3. FOLCLORE	81
Cusco|4. REALIZACIONES TÉCNICAS, CIENTÍFICAS Y ARTÍSTICAS CONTEMPORÁNEAS	24
Cusco|5. ACONTECIMIENTOS PROGRAMADOS	116
Lima|1. SITIOS NATURALES	218
Lima|2. MANIFESTACIONES CULTURALES	331
Lima|3. FOLCLORE	171
Lima|4. REALIZACIONES TÉCNICAS, CIENTÍFICAS Y ARTÍSTICAS CONTEMPORÁNEAS	50
Lima|5. ACONTECIMIENTOS PROGRAMADOS	138
...(107 filas más, una por cada región+categoría restante)
```
> Nota: la consola mostró los acentos rotos (`Apur├¡mac`) hasta agregar
> `[Console]::OutputEncoding = [System.Text.Encoding]::UTF8` — verificado con los
> bytes crudos que el dato en HDFS ya estaba bien codificado en UTF-8 desde el
> principio, era solo un problema de cómo lo mostraba la consola de PowerShell.

### 1b Recursos Por Provincia Tipo

**Objetivo y enfoque:** el dataset de turismo tiene 3 niveles de clasificación de
un recurso: una Categoría general (5 valores, ej. "1. SITIOS NATURALES"), un Tipo
de Categoría intermedio (35 valores, ej. "Arquitectura y Espacios Urbanos",
"g. Cuerpo de Agua") y un Sub Tipo específico (188 valores). Acá queremos contar
cuántos recursos turísticos hay en cada Provincia, separados por ese Tipo de
Categoría intermedio — por ejemplo, cuántos recursos de "Arquitectura y Espacios
Urbanos" tiene la provincia de Lima, cuántos de "Cuerpo de Agua" tiene Huarochirí, etc.

Cada línea del CSV es un recurso turístico individual (una fila = un recurso, no
una transacción). El Mapper separa la línea por `;` (el CSV de turismo usa punto
y coma como separador de columnas, no coma), toma el texto de la columna
Provincia [posición 1] y el texto de la columna Tipo de Categoría [posición 6], y
los junta en una sola cadena de texto con un separador propio ("Provincia|Tipo")
para usarla como key. Emite esa key junto con el valor 1. Como en Hadoop el
Reducer solo recibe agrupados los pares que compartan exactamente la misma key,
todos los recursos de, por ejemplo, "Lima|Arquitectura y Espacios Urbanos" le
llegan juntos al Reducer, que simplemente suma esos 1's y devuelve el total —
así se obtiene el conteo de recursos para cada combinación específica de
provincia y tipo de categoría.

Pasos: crear el paquete `RecursosPorProvinciaTipo` en NetBeans (Mapper/Reducer/Driver
desde `PC1/source_Packages/1b_RecursosPorProvinciaTipo/`), Build, y ejecutar:
```powershell
.\hadoop.ps1 pc1 RecursosPorProvinciaTipo
```

RESULTADO (verificado también con `awk` sobre el CSV; extracto top 10 por conteo, dataset completo tiene 1 fila por cada combinación existente)
```powershell
Lima|Arquitectura y Espacios Urbanos	93
La Convencion|j. Caídas de agua	52
Arequipa|Arquitectura y Espacios Urbanos	37
Huamanga|Arquitectura y Espacios Urbanos	34
Huarochiri|g. Cuerpo de Agua	33
Lima|Museos y otros	31
Cusco|Arquitectura y Espacios Urbanos	29
Mariscal Nieto|Arquitectura y Espacios Urbanos	27
Pasco|Arquitectura y Espacios Urbanos	26
Huancayo|Arquitectura y Espacios Urbanos	25
...(resto de combinaciones provincia+tipo existentes)
```

### 1c Recursos Por Distrito Subtipo

**Objetivo y enfoque:** cada recurso turístico del dataset está clasificado en un
Distrito (la subdivisión geográfica más pequeña que trae el CSV, hay 1005
distritos distintos) y en un Sub Tipo de Categoría (la clasificación temática más
específica, hay 188 subtipos distintos — por ejemplo "Lagunas", "Playas",
"Cataratas", "Platos Típicos"). Queremos contar cuántos recursos hay en cada
distrito, separados por ese subtipo — ej. cuántas "Lagunas" tiene el distrito de
Ocongate, cuántas "Playas" tiene Huarmey.

El Mapper toma cada línea del CSV (una línea = un recurso turístico) y la separa
por `;`. Extrae el texto de la columna Distrito [posición 2] y el texto de la
columna Sub Tipo de Categoría [posición 7], y forma con ellos una key compuesta de
texto (ej. "OCONGATE|Lagunas"), emitiendo esa key junto al valor 1. Hadoop se
encarga de agrupar automáticamente todos los pares que tengan exactamente la
misma key antes de pasárselos al Reducer — así, todos los recursos de
"OCONGATE|Lagunas" llegan juntos, y el Reducer solo tiene que sumar esos 1's para
obtener el conteo total de esa combinación puntual de distrito y subtipo.

Pasos: crear el paquete `RecursosPorDistritoSubtipo` (código en
`PC1/source_Packages/1c_RecursosPorDistritoSubtipo/`), Build, ejecutar:
```powershell
.\hadoop.ps1 pc1 RecursosPorDistritoSubtipo
```

RESULTADO (verificado también con `awk`; extracto top 10 por conteo, dataset completo tiene 1 fila por cada combinación existente)
```powershell
OCONGATE|Lagunas	18
HUARMEY|Playas	14
EL CARMEN DE LA FRONTERA|Lagunas	14
PICHARI|Cataratas	13
PACHANGARA|Fiestas religiosas-patronales	13
MIRAFLORES|Parques	13
PACHANGARA|Platos Típicos	12
PACHANGARA|Otros	12
IQUITOS|Platos Típicos	11
SAN ANTONIO|Bodegas de pisco, vinos y/u otros licores	10
...(resto de combinaciones distrito+subtipo existentes)
```

### 1d Manifestaciones Culturales Por Region

**Objetivo y enfoque:** el dataset clasifica cada recurso turístico en una de 5
categorías generales (Sitios Naturales, Manifestaciones Culturales, Folclore,
Realizaciones Técnicas/Científicas/Artísticas, Acontecimientos Programados).
Queremos aislar solo los recursos de la categoría "Manifestaciones Culturales" y
contar cuántos hay en cada región — a diferencia de las consultas anteriores, acá
no agrupamos todas las líneas, sino que primero **descartamos** las que no
correspondan a esa categoría específica.

El Mapper separa cada línea por `;` y lee el texto de la columna Categoría
[posición 5]. Compara ese texto contra el literal exacto "2. MANIFESTACIONES
CULTURALES" (así aparece escrito en el CSV, con el número y el punto incluidos);
si no coincide, la línea se descarta por completo y no se emite nada para ella.
Si sí coincide, toma el texto de la columna Región [posición 0] como key y emite
el valor 1. El Reducer recibe, agrupados por región, solo los 1's de las líneas
que pasaron el filtro, y los suma — dando así el conteo de recursos culturales
de cada región, sin contaminarse con las demás categorías.

Pasos: crear el paquete `ManifestacionesCulturalesPorRegion` (código en
`PC1/source_Packages/1d_ManifestacionesCulturalesPorRegion/`), Build, ejecutar:
```powershell
.\hadoop.ps1 pc1 ManifestacionesCulturalesPorRegion
```

RESULTADO (verificado también con `awk`, coincide exacto con la porción "2. MANIFESTACIONES CULTURALES" ya vista en 1a)
```powershell
Amazonas	47
Áncash	77
Apurímac	88
Arequipa	139
Ayacucho	121
Cajamarca	77
Callao	15
Cusco	214
Huancavelica	32
Huánuco	73
Ica	49
Junín	159
La Libertad	76
Lambayeque	75
Lima	331
Loreto	67
Madre De Dios	8
Moquegua	58
Pasco	91
Piura	77
Puno	170
San Martín	23
Tacna	32
Tumbes	13
Ucayali	22
```

### 1e Ranking Region Categoria

**Objetivo y enfoque:** queremos encontrar las 5 combinaciones de región+categoría
con más recursos turísticos registrados (ej. saber que "Lima con Manifestaciones
Culturales" es la combinación más numerosa del país). Esto es distinto a contar
por grupo (como en 1a): acá hace falta **comparar los conteos entre todos los
grupos** para saber cuáles son los 5 más grandes, y esa comparación no se puede
hacer dentro de un solo grupo aislado.

La limitación de Hadoop es que un Reducer solo ve, de una vez, los valores que
comparten exactamente la misma key — nunca puede comparar contra los valores de
otra key distinta. Por eso, si cada combinación región+categoría fuera su propia
key (como en 1a), cada Reducer vería solo un grupo y no podría saber si el suyo
es de los 5 más grandes o no. La solución: el Mapper separa cada línea por `;`,
toma Región [posición 0] y Categoría [posición 5], las junta en un texto
("Región|Categoría"), pero en vez de usar eso como key, lo manda como *value*, y
usa una key fija idéntica para todas las líneas ("ALL"). Como todas las líneas
comparten esa misma key, **todas** terminan en el mismo y único Reducer.

Dentro de ese Reducer (que recibe los ~6204 pares región|categoría de golpe), se
recorre la lista y se cuenta cuántas veces se repite cada combinación distinta,
guardando esos conteos en un `HashMap<String,Integer>` (una tabla en memoria que
asocia cada combinación de texto con su número de apariciones). Una vez contado
todo, se pasan esas entradas a una lista y se ordenan de mayor a menor número de
apariciones, y se devuelven solo las primeras 5 de esa lista ordenada, numeradas
como "1.", "2." ... "5.". El Driver además fija `setNumReduceTasks(1)`
explícitamente, para dejar constancia de que el diseño depende de que corra un
único Reducer (aunque, al ser una sola key, Hadoop ya lo haría así de todas formas).

Pasos: crear el paquete `RankingRegionCategoria` (código en
`PC1/source_Packages/1e_RankingRegionCategoria/`), Build, ejecutar:
```powershell
.\hadoop.ps1 pc1 RankingRegionCategoria
```

RESULTADO (real, corrido y confirmado por el usuario)
```powershell
1. Lima|2. MANIFESTACIONES CULTURALES	331
2. Cusco|1. SITIOS NATURALES	255
3. Lima|1. SITIOS NATURALES	218
4. Cusco|2. MANIFESTACIONES CULTURALES	214
5. Áncash|1. SITIOS NATURALES	207
```
> Coincide con los datos ya vistos en 1a (Lima|Manifestaciones=331, Cusco|Sitios
> Naturales=255, etc.) — confirma que el ranking está tomando los conteos correctos.

### 2 Estadisticas Latitud

**Objetivo y enfoque:** el CSV trae, para cada recurso turístico, sus coordenadas
geográficas en dos columnas: LATITUD [posición 9] y LONGITUD [posición 10] (ambas
en grados decimales). Alrededor de 1257 de las 6204 filas (20%) no tienen estas
coordenadas registradas — vienen vacías. Queremos calcular, sobre todas las filas
que sí tienen coordenada, la media (promedio) y la desviación estándar de la
Latitud — es decir, un solo par de números que resuma qué tan dispersos
geográficamente (de norte a sur) están los recursos turísticos del Perú en su
conjunto.

El Mapper separa cada línea por `;`, lee el texto de la columna Latitud
[posición 9] y lo recorta de espacios en blanco. Si ese texto queda vacío, la
línea se descarta (no tiene coordenada). Si no está vacío, intenta convertirlo a
número decimal (`Double.parseDouble`); si esa conversión falla (dato corrupto),
también se descarta, dentro de un `try/catch`. Si el valor es válido, se emite
bajo una key fija ("Latitudes") junto con el número — la key fija fuerza que
absolutamente todos los valores lleguen al mismo Reducer, porque hace falta
tenerlos todos juntos para calcular un promedio y una desviación que abarque a
todos, no solo a un subgrupo.

El Reducer recibe esa lista completa de latitudes válidas y, en una sola pasada
(sin necesitar guardarlas todas en una lista), va acumulando 3 valores: cuántas
son (`cantidad`), la suma de todas (`suma`), y la suma de cada una elevada al
cuadrado (`sumaCuadrados`). Con esos 3 números alcanza para calcular la media
(`suma / cantidad`) y la desviación estándar con la fórmula estadística
`raíz cuadrada( (sumaCuadrados / cantidad) - media² )`, sin tener que recorrer
la lista una segunda vez ni guardarla completa en memoria.

Pasos: crear el paquete `EstadisticasLatitud` (código en
`PC1/source_Packages/2_EstadisticasLatitud/`), Build, ejecutar:
```powershell
.\hadoop.ps1 pc1 EstadisticasLatitud
```

RESULTADO (verificado también con `awk`, coincide exacto)
```powershell
Latitudes	media=-75.12124782573784 desviacion_estandar=3.020210... n=4947
```
> De las 6204 filas totales, 4947 tenían Latitud válida (las ~1257 restantes
> venían vacías o con datos corruptos y se descartaron, como se explicó arriba).

### 3 Busqueda Por Palabra Clave

**Objetivo y enfoque:** queremos poder buscar un texto cualquiera (una palabra
clave) dentro de varios campos de texto del dataset a la vez — Región, Provincia,
Distrito, Nombre del Recurso, Categoría, Tipo de Categoría y Sub Tipo de
Categoría — y que el resultado sea el registro completo de cada recurso que
tenga esa palabra en alguno de esos campos. A diferencia de las consultas
anteriores, esto no agrupa ni cuenta nada: es un filtro de texto tipo `grep`,
pero corrido como un job de Hadoop.

Para que la palabra a buscar no quede fija en el código (y se pueda cambiar sin
recompilar), el Driver lee un tercer argumento de línea de comandos opcional
(`args[2]`): si se pasa, esa es la palabra clave; si no se pasa, usa "Laguna"
como valor por defecto. Ese valor se guarda dentro del objeto `JobConf` con
`configuracion_job.set("palabraClave", palabraClave)`. Cuando Hadoop reparte el
trabajo entre los Mappers, cada uno ejecuta primero un método especial llamado
`configure(JobConf job)` —se llama una sola vez, antes de que arranque a
procesar líneas— donde se lee ese mismo valor de vuelta con `job.get("palabraClave",
"Laguna")` y se guarda en una variable de la clase, para poder usarla después en
cada llamada a `map()`.

En cada `map()`, la línea se separa por `;`, se toman los textos de las columnas
Región [0], Provincia [1], Distrito [2], Nombre del Recurso [4], Categoría [5],
Tipo de Categoría [6] y Sub Tipo de Categoría [7], se concatenan todos en un solo
texto y se pasan a minúsculas. Si ese texto combinado contiene la palabra clave
(también pasada a minúsculas, para que la búsqueda no distinga mayúsculas de
minúsculas), se emite la línea completa original como key. El Reducer no hace
ningún cálculo real — solo sirve para completar el patrón Mapper/Reducer que
exige la API de Hadoop, y deja pasar el resultado tal cual.

Pasos: crear el paquete `BusquedaPorPalabraClave` (código en
`PC1/source_Packages/3_BusquedaPorPalabraClave/`), Build, ejecutar (con la
palabra clave "Cusco" como tercer argumento):
```powershell
.\hadoop.ps1 pc1 BusquedaPorPalabraClave Cusco
```

RESULTADO (extracto — se verificó independiente con `awk` que ~690 de las 6204
filas contienen "cusco" en alguno de los 7 campos de texto revisados; los
ejemplos de abajo son reales, tomados de la corrida del usuario)
```powershell
Cusco;Urubamba;URUBAMBA;3688;Templo De San Pedro Apóstol De Urubamba.;2. MANIFESTACIONES CULTURALES;Arquitectura y Espacios Urbanos;Iglesias;https://...;-72.1160821;-13.3055229;20260919	1
Cusco;Urubamba;URUBAMBA;3690;Villa De Urubamba ;2. MANIFESTACIONES CULTURALES;Pueblos;Tradicionales;https://...;-72.11600299999998;-13.305923;20260919	1
Cusco;Urubamba;YUCAY;1779;Zona Arqueológica Yucay;2. MANIFESTACIONES CULTURALES;Sitios Arqueológicos;Zonas arqueológicas;https://...;-72.0850484305;-13.31253125887;20260919	1
Cusco;Urubamba;YUCAY;6370;Fiesta De San Isidro;5. ACONTECIMIENTOS PROGRAMADOS;Fiestas;Fiestas religiosas-patronales;https://...;-72.0861;-13.3196;20260919	1
...(el resto de filas donde "Cusco" aparece en algún campo de texto)
```

### 4 Extremos Geograficos Por Region

**Objetivo y enfoque:** por cada región del país, queremos identificar el
recurso turístico ubicado más al norte y el ubicado más al sur — es decir, el
máximo y el mínimo de la columna Latitud [9], agrupados por Región [0], pero
mostrando además el nombre del recurso correspondiente a cada extremo (no solo
el número de latitud). A diferencia de sumar o contar, encontrar un máximo y un
mínimo obliga al Reducer a comparar cada valor nuevo contra el mejor que ya
había visto hasta ese momento dentro de su mismo grupo.

El Mapper separa la línea por `;`, y si la columna Latitud [9] viene vacía la
descarta (mismo problema del 20% de filas sin coordenadas ya visto en la
categoría 2). Si tiene valor, arma como key la Región [0], y como value un texto
compuesto "latitud|nombreDelRecurso" (uniendo la Latitud [9] y el Nombre del
Recurso [4] con un separador propio) — necesita mandar los dos datos juntos,
porque el Reducer va a necesitar el nombre para poder decir CUÁL recurso es el
extremo, no solo cuál es el valor numérico.

El Reducer, para cada región, recorre todos esos pares "latitud|nombre" que le
llegaron agrupados, separa cada uno por el `|` para recuperar el número y el
nombre por separado, y mantiene 2 variables que se van actualizando a medida que
avanza: la latitud más alta vista hasta el momento (arrancando desde
`Double.NEGATIVE_INFINITY`, un valor que cualquier número real va a superar en la
primera comparación) junto con su nombre, y la latitud más baja (arrancando
desde `Double.POSITIVE_INFINITY`) junto con el suyo. Al terminar de recorrer
todos los pares de la región, esas dos variables contienen el extremo norte y
el extremo sur, y se devuelven juntos en un solo texto de salida.

Pasos: crear el paquete `ExtremosGeograficosPorRegion` (código en
`PC1/source_Packages/4_ExtremosGeograficosPorRegion/`), Build, ejecutar:
```powershell
.\hadoop.ps1 pc1 ExtremosGeograficosPorRegion
```

RESULTADO (verificado también con `awk` para la región Cusco, coincide exacto)
```powershell
Cusco	Norte=Mirador Natural De Embarcadero Puerto Ene  (-73.9958)  Sur=Nevado Quillca (-71.009115)
```
> Nota sobre la codificación de las columnas: LATITUD [9] va de -81 a -68 y
> LONGITUD [10] va de -18 a -0.6 — valores típicos de longitud oeste y latitud
> sur de Perú respectivamente, pero **invertidos** respecto a lo que dicen sus
> propios nombres de columna en la cabecera del CSV original. Parece un error
> del dataset fuente (MINCETUR), no algo introducido por nuestro código — se usó
> la columna [9] tal cual la llama la cabecera ("LATITUD"), sin corregirla,
> ya que no es tarea nuestra alterar los datos originales.

## Categoría 5: consultas encadenadas (2 MapReduce cada una)

**Estado: ejecutado y verificado.** Las 4 corridas (5a1, 5a2, 5b1, 5b2)
terminaron con "Job ... completed successfully" y los resultados tienen sentido
geográfico (se revisan más abajo, en cada sub-sección).

**Objetivo y enfoque general:** el enunciado exige 2 consultas que encadenen al
menos 2 jobs de MapReduce cada una. La idea elegida: comparar cada recurso
turístico contra el **promedio de SU PROPIA región**, para saber si está "más al
norte/sur" (o "más al este/oeste") que el promedio regional. Esto necesita
forzosamente 2 pasadas separadas, porque el promedio de una región no se puede
conocer hasta haber visto TODOS los recursos de esa región — y para comparar un
recurso individual contra ese promedio, hace falta que el promedio ya esté
calculado de antemano. No se puede hacer en un solo Mapper/Reducer.

**Mecanismo de encadenado usado (DistributedCache):** el primer job calcula el
promedio de la región y lo deja escrito como un archivo chico en HDFS (una fila
por región, ~25 filas). El Driver del segundo job usa
`DistributedCache.addCacheFile(new URI("/pc1_output/<JobUno>/part-00000"), configuracion_job)`
para decirle a Hadoop "copiá este archivo chico a cada máquina que vaya a correr
un Mapper". Cada Mapper del segundo job, en su método `configure()` (que corre
una sola vez, antes de procesar cualquier línea), lee ese archivo ya copiado
localmente y lo carga en un `HashMap<String,Double>` en memoria — así, al
procesar cada recurso individual, puede consultar al instante el promedio de su
región sin tener que volver a calcularlo.

### 5a1 Promedio Latitud Por Region (job 1 de la cadena a)

Calcula el promedio de la columna Latitud [9] agrupado por Región [0] — mismo
patrón Mapper/Reducer que categorías anteriores (emitir región+valor, sumar y
dividir por cantidad en el Reducer), con un solo Reducer (`setNumReduceTasks(1)`)
para que el resultado quede en un único archivo, necesario para el paso siguiente.

```powershell
.\hadoop.ps1 pc1 PromedioLatitudPorRegion
```

**Que se hizo?** El job corrió bien ("Job ... completed successfully") y
produjo 25 filas, una por región, cada una con el promedio de Latitud [9] de
todos sus recursos.

RESULTADO (extracto — últimas filas visibles de la corrida real, el resto de
las 25 regiones también se calculó pero se cortó en la consola)
```powershell
Junín	-75.28549318274075
La Libertad	-78.73156423594106
Lambayeque	-79.7929173491869
Lima	-76.70621953721943
Loreto	-73.56948806312205
Madre De Dios	-69.81553553987237
Moquegua	-70.95218651611212
Pasco	-75.7875449643834
Piura	-80.30952993162616
Puno	-69.8251357579454
San Martín	-76.84181952783477
Tacna	-70.3348343400723
Tumbes	-80.56112173984202
Ucayali	-74.45727524214202
Áncash	-77.58917372611148
```

### 5a2 Clasificar Norte Sur Region (job 2 de la cadena a)

Lee el archivo de promedios que dejó 5a1 (vía DistributedCache) y, por cada
recurso individual, compara su Latitud [9] contra el promedio de su propia
región: si es mayor, se clasifica como "Norte del promedio"; si es menor,
"Sur del promedio". Emite (región+clasificación, 1) y el Reducer suma cuántos
recursos cayeron en cada combinación.

```powershell
.\hadoop.ps1 pc1 ClasificarNorteSurRegion
```
> Requiere haber corrido 5a1 primero (el Driver de este job apunta a la ruta fija
> `/pc1_output/PromedioLatitudPorRegion/part-00000`).

**Que se hizo?** Corrió bien, leyendo el promedio por región calculado en 5a1
y clasificando cada recurso individual contra el promedio de su propia región.

RESULTADO (completo)
```powershell
Lima|Norte del promedio	298
Lima|Sur del promedio	330
Loreto|Norte del promedio	97
Loreto|Sur del promedio	39
Madre De Dios|Norte del promedio	46
Madre De Dios|Sur del promedio	23
Moquegua|Norte del promedio	89
Moquegua|Sur del promedio	58
Pasco|Norte del promedio	98
Pasco|Sur del promedio	88
Piura|Norte del promedio	119
Piura|Sur del promedio	111
Puno|Norte del promedio	154
Puno|Sur del promedio	160
Tacna|Norte del promedio	39
Tacna|Sur del promedio	32
Tumbes|Norte del promedio	29
Tumbes|Sur del promedio	27
Ucayali|Norte del promedio	18
Ucayali|Sur del promedio	55
```
> (más las filas del resto de regiones no mostradas en este extracto de consola)

### 5b1 Promedio Longitud Por Region (job 1 de la cadena b)

Idéntico a 5a1 pero con la columna Longitud [10] en vez de Latitud [9].

```powershell
.\hadoop.ps1 pc1 PromedioLongitudPorRegion
```

**Que se hizo?** Mismo job que 5a1 pero con la columna Longitud [10]; corrió
bien y dio 25 promedios regionales.

RESULTADO (extracto — últimas filas visibles de la corrida real)
```powershell
Puno	-15.346570458143123
San Martín	-6.519708832043513
Tacna	-17.60353501123209
Tumbes	-3.7436317286120584
Ucayali	-9.013219821980321
Áncash	-9.345583562226063
```

### 5b2 Clasificar Este Oeste Region (job 2 de la cadena b)

Idéntico a 5a2 pero clasifica cada recurso como "Este del promedio" u "Oeste
del promedio" según su Longitud [10] comparada con el promedio de su región
(leído del resultado de 5b1).

```powershell
.\hadoop.ps1 pc1 ClasificarEsteOesteRegion
```
> Requiere haber corrido 5b1 primero (apunta a `/pc1_output/PromedioLongitudPorRegion/part-00000`).

**Que se hizo?** Corrió bien, clasificando cada recurso contra el promedio de
Longitud de su propia región.

RESULTADO (completo)
```powershell
Lima|Este del promedio	237
Lima|Oeste del promedio	391
Loreto|Este del promedio	95
Loreto|Oeste del promedio	41
Madre De Dios|Este del promedio	22
Madre De Dios|Oeste del promedio	47
Moquegua|Este del promedio	64
Moquegua|Oeste del promedio	83
Pasco|Este del promedio	76
Pasco|Oeste del promedio	110
Piura|Este del promedio	88
Piura|Oeste del promedio	142
Puno|Este del promedio	130
Puno|Oeste del promedio	184
Tacna|Este del promedio	39
Tacna|Oeste del promedio	32
Tumbes|Este del promedio	33
Tumbes|Oeste del promedio	23
Ucayali|Este del promedio	52
Ucayali|Oeste del promedio	21
```
> (más las filas del resto de regiones no mostradas en este extracto de consola)

## Categoría 6: clasificación (2 consultas)

**Estado: ejecutado y verificado.** Las 4 corridas (6a1, 6a2, 6b1, 6b2)
terminaron bien, y el 6a1 se verificó además de forma independiente con `awk`
sobre el CSV crudo (coincide exacto).

**Objetivo y enfoque general:** el dataset no tiene una columna numérica ideal
para un modelo de clasificación tradicional (no hay más variables continuas que
Latitud y Longitud). Se investigó y eligió el algoritmo de **"centroide más
cercano" (Nearest Centroid Classifier)**: un modelo de clasificación real,
simple de implementar de forma distribuida, que funciona así — durante el
"entrenamiento" (job 1), se calcula el centro geográfico promedio (centroide)
de cada clase; durante la "predicción" (job 2), a cada punto se le asigna la
clase cuyo centroide esté geográficamente más cerca (por distancia euclidiana
entre coordenadas). Se mide la **exactitud (accuracy)** comparando la clase
predicha contra la clase real que ya trae el CSV: accuracy = correctos / (correctos + incorrectos).

Se armaron 2 consultas con la misma técnica pero prediciendo etiquetas
distintas, para poder comparar qué tan bien funciona el modelo según qué se
quiera predecir:

### 6a1 Centroides Categoria (job 1 — "entrenamiento")

Calcula el centroide (promedio de Latitud [9] y Longitud [10]) de cada una de
las 5 Categorías [5] generales del dataset.
```powershell
.\hadoop.ps1 pc1 CentroidesCategoria
```

**Que se hizo?** Corrió bien y dio 5 centroides (uno por categoría),
verificados de forma independiente con `awk` sobre el CSV crudo — coinciden
exactos (mismos valores hasta el 4to decimal).

RESULTADO (completo, 5 filas — una por categoría)
```powershell
1. SITIOS NATURALES	-75.1253,-11.1432
2. MANIFESTACIONES CULTURALES	-74.9507,-11.5919
3. FOLCLORE	-76.5263,-9.1836
4. REALIZACIONES TÉCNICAS, CIENTÍFICAS Y ARTÍSTICAS CONTEMPORÁNEAS	-74.9449,-11.5125
5. ACONTECIMIENTOS PROGRAMADOS	-76.2143,-10.0156
```

### 6a2 Clasificar Por Centroide Categoria (job 2 — "predicción" + accuracy)

Para cada recurso, calcula la distancia a los 5 centroides de 6a1 y predice la
categoría del centroide más cercano; compara contra la Categoría [5] real y
cuenta aciertos/errores.
```powershell
.\hadoop.ps1 pc1 ClasificarPorCentroideCategoria
```
> Requiere haber corrido 6a1 primero.

**Que se hizo?** Corrió bien. Se confirmó la predicción hecha antes de
ejecutar: la exactitud salió **baja**, porque las categorías turísticas
(Sitios Naturales, Manifestaciones Culturales, etc.) no se agrupan
geográficamente — están repartidas por todo el país.

RESULTADO (completo)
```powershell
correcto	1378
incorrecto	3569
```
**Accuracy = 1378 / (1378 + 3569) = 1378 / 4947 ≈ 27.85%**

### 6b1 Centroides Region (job 1 — "entrenamiento", 25 clases)

Igual que 6a1 pero calculando el centroide de cada una de las 25 Regiones [0]
en vez de las 5 categorías.
```powershell
.\hadoop.ps1 pc1 CentroidesRegion
```

**Que se hizo?** Corrió bien, dando 25 centroides (uno por región).

RESULTADO (extracto — últimas filas visibles de la corrida real)
```powershell
Madre De Dios	-69.81553553987237,-12.35428942713688
Moquegua	-70.95218651611212,-16.977820672918675
Pasco	-75.7875449643834,-10.562516673818518
Piura	-80.30952993162616,-4.99377793335483
Puno	-69.8251357579454,-15.346570458143123
San Martín	-76.84181952783477,-6.519708832043513
Tacna	-70.3348343400723,-17.60353501123209
Tumbes	-80.56112173984202,-3.7436317286120584
Ucayali	-74.45727524214202,-9.013219821980321
Áncash	-77.58917372611148,-9.345583562226063
```

### 6b2 Clasificar Por Centroide Region (job 2 — "predicción" + accuracy)

Igual que 6a2 pero prediciendo la Región [0] real a partir de la cercanía a los
25 centroides de 6b1.
```powershell
.\hadoop.ps1 pc1 ClasificarPorCentroideRegion
```
> Requiere haber corrido 6b1 primero.

**Que se hizo?** Corrió bien. Se confirmó también la predicción hecha antes de
ejecutar: la exactitud salió considerablemente **más alta** que 6a2, porque las
regiones sí son zonas geográficas compactas por definición.

RESULTADO (completo)
```powershell
correcto	3004
incorrecto	1943
```
**Accuracy = 3004 / (3004 + 1943) = 3004 / 4947 ≈ 60.73%**

**Tabla comparativa de métricas — modelos de clasificación (categoría 6)**, tal
como pide el enunciado ("elaborar una tabla donde se compare las métricas...
para cada modelo"):

| Modelo | Etiqueta predicha | # clases | Correctos | Incorrectos | Accuracy |
|---|---|---|---|---|---|
| 6a (Nearest Centroid) | Categoría | 5 | 1378 | 3569 | 27.85% |
| 6b (Nearest Centroid) | Región | 25 | 3004 | 1943 | 60.73% |

Interpretación: el mismo algoritmo (centroide más cercano, basado solo en
coordenadas) predice mucho mejor la Región que la Categoría — confirma que la
ubicación geográfica de un recurso turístico dice más sobre en qué región está
que sobre qué tipo de recurso es.

## Categoría 7: regresión (2 consultas)

**Estado: ejecutado y verificado.** Las 3 corridas (7a, 7b1, 7b2) terminaron
bien, y tanto 7a como 7b2 se verificaron de forma independiente con `awk`
sobre el CSV crudo — coinciden exactos.

**Objetivo y enfoque general:** regresión lineal simple (Y = pendiente·X +
intercepto), calculada con la fórmula cerrada de mínimos cuadrados. En vez de
guardar todos los puntos (X,Y) en una lista y recorrerla dos veces, el Reducer
acumula 5 sumas en una sola pasada (`n`, `sumaX`, `sumaY`, `sumaXY`, `sumaX²`,
`sumaY²` — y también `sumaY²` para el R²), y con esas sumas alcanza para
calcular por álgebra tanto la pendiente/intercepto como el **R²** (qué tan bien
se ajusta la recta) y el **error cuadrático medio / MSE** (la métrica de "loss"
que pide el enunciado para la tabla comparativa), sin una segunda pasada.

### 7a Regresion Longitud Desde Latitud

Un solo job (no necesita encadenar): usa como X la Latitud [9] y como Y la
Longitud [10] de cada uno de los ~4947 recursos con coordenadas válidas, y
calcula la recta que mejor las relaciona.
```powershell
.\hadoop.ps1 pc1 RegresionLongitudDesdeLatitud
```

**Que se hizo?** Corrió bien sobre los 4947 recursos con coordenadas válidas;
verificado de forma independiente con `awk` sobre el CSV crudo — coincide
exacto en los 5 valores (pendiente, intercepto, R², MSE, n).

RESULTADO (completo)
```powershell
ALL	pendiente=-0.9193459071092119 intercepto=-80.3201302965638 R2=0.6125423464836328 loss_MSE=4.876643738966836 n=4947
```
Interpretación: R²≈0.61 indica una relación lineal moderada-fuerte entre
Latitud y Longitud a nivel de todo el país (tiene sentido: el territorio
peruano es alargado en diagonal, así que moverse en latitud arrastra bastante
la longitud también).

### 7b1 Conteo Por Provincia (job 1 de la cadena)

Por cada Provincia [1], cuenta cuántos recursos turísticos tiene en total, y
con un `HashSet` cuántos Distritos [2] **distintos** hay entre esos recursos.
Resultado chico: ~190 filas, una por provincia.
```powershell
.\hadoop.ps1 pc1 ConteoPorProvincia
```

**Que se hizo?** Corrió bien, dando ~190 filas (una por provincia) con
"cantidadRecursos,cantidadDistritosDistintos".

RESULTADO (extracto — últimas filas visibles de la corrida real)
```powershell
Utcubamba	4,2
Victor Fajardo	26,5
Vilcas Huaman	6,1
Viru	5,2
Yarowilca	2,2
Yauli	25,4
Yauyos	89,11
Yungay	17,5
Yunguyo	31,6
Zarumilla	8,2
```

### 7b2 Regresion Recursos Por Distritos (job 2 de la cadena)

Toma como **input** directamente la carpeta de salida de 7b1 (no el CSV
original — este es un encadenado por INPUT, distinto al de la categoría 5 que
usaba DistributedCache) y corre la misma regresión lineal que 7a, pero con X =
cantidad de distritos distintos de la provincia e Y = cantidad de recursos de
esa provincia — buscando responder "¿tener más distritos implica tener más
recursos turísticos registrados?".

```powershell
$cp = (hadoop classpath)
hadoop fs -rm -r /pc1_output/RegresionRecursosPorDistritos 2>$null
java -cp "C:\Users\esauf\Documents\NetBeansProjects\PC1\dist\PC1.jar;$cp" RegresionRecursosPorDistritos.Driver /pc1_output/ConteoPorProvincia /pc1_output/RegresionRecursosPorDistritos
hadoop fs -cat /pc1_output/RegresionRecursosPorDistritos/*
```
> Este último no usa la receta `.\hadoop.ps1 pc1 <Paquete>` porque esa receta
> siempre usa `/pc1_input` (el CSV) como input fijo — acá el input real es la
> salida de 7b1, así que se ejecuta el comando manual. Requiere haber corrido
> 7b1 primero.

**Que se hizo?** Corrió bien, leyendo directamente el archivo de conteos que
dejó 7b1 en vez del CSV original. Verificado de forma independiente con `awk`
(recalculando distritos distintos y conteos por provincia directo desde el CSV
crudo) — coincide exacto en los 4 valores.

RESULTADO (completo)
```powershell
ALL	pendiente=7.102514663469329 intercepto=-6.971923911986782 R2=0.665930669283448 loss_MSE=432.649803247709 n=190
```
Interpretación: pendiente ≈7.1 significa que, en promedio, cada distrito
adicional en una provincia se asocia con ~7 recursos turísticos más — R²≈0.67
indica una relación bastante fuerte entre cuántos distritos tiene una
provincia y cuántos recursos turísticos concentra.

**Tabla comparativa de métricas — modelos de regresión (categoría 7)**, tal
como pide el enunciado:

| Modelo | X (predictor) | Y (a predecir) | n | Pendiente | Intercepto | R² | Loss (MSE) |
|---|---|---|---|---|---|---|---|
| 7a | Latitud | Longitud | 4947 | -0.9193 | -80.3201 | 0.6125 | 4.8766 |
| 7b | Cantidad de distritos por provincia | Cantidad de recursos por provincia | 190 | 7.1025 | -6.9719 | 0.6659 | 432.6498 |

Interpretación: ambos modelos tienen un ajuste (R²) parecido (~0.61 y ~0.67),
pero operan a escalas totalmente distintas — 7a relaciona coordenadas
individuales de ~4947 recursos, mientras que 7b relaciona conteos agregados de
solo 190 provincias (por eso su MSE es mucho más grande: está prediciendo
cantidades de recursos, que varían en decenas, no coordenadas geográficas que
varían en unidades).

