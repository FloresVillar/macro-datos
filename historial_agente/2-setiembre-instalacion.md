# Historial - Instalación de Hadoop 3.3.0 en Windows

Resumen de la sesión con Claude Code. Continuar desde aquí en una terminal nativa de PowerShell (no WSL).

## Contexto
- Guía base: `Instalación de Hadoop 3.3.0.pdf` (en esta misma carpeta)
- Carpeta winutils del profe: `winutils/winutils/` (contiene versiones desde hadoop-2.6.5 hasta hadoop-3.4.0)
- **Versión correcta de winutils a usar: `hadoop-3.3.0-YARN-8246`** (es la que está marcada con recuadro rojo en el PDF, página 3 — NO usar la carpeta genérica `hadoop-3.3.0`, ese fue un error corregido durante la sesión)

## Pasos completados

1. **JDK 8 instalado** vía Eclipse Temurin (OpenJDK), NO Oracle JDK (Oracle pedía crear cuenta y daba problemas).
   - Instalado originalmente en `C:\Program Files\Eclipse Adoptium\jdk-8.0.504.1-hotspot`
   - **Copiado manualmente a `C:\Java`** (la guía exige que Java quede en `C:\Java` sin espacios en la ruta, porque los scripts `.cmd` de Hadoop y winutils fallan con espacios como en "Program Files")
   - `JAVA_HOME` = `C:\Java` (variable de sistema, ya configurada y verificada)
   - Verificado con `java -version` y `javac -version` → OpenJDK 1.8.0_504 ✅

2. **Hadoop 3.3.0 descargado** desde https://archive.apache.org/dist/hadoop/common/hadoop-3.3.0/hadoop-3.3.0.tar.gz
   - Extraído con 7-Zip (doble extracción: `.tar.gz` → `.tar` → carpeta final). Algunos archivos `.symlink` (ej. `libnativetask.so`) se omitieron durante la extracción — es normal, son librerías nativas de Linux que no se usan en Windows.
   - Movido y renombrado a `C:\Hadoop3`
   - `HADOOP_HOME` = `C:\Hadoop3` (variable de sistema, ya configurada y verificada)
   - Path del sistema incluye: `%JAVA_HOME%\bin` y `%HADOOP_HOME%\bin`
   - Verificado con `hadoop version` → Hadoop 3.3.0 ✅

3. **Winutils copiado** — contenido de `winutils/winutils/hadoop-3.3.0-YARN-8246/bin/*` copiado (sobrescribiendo) a `C:\Hadoop3\bin`. Confirmado visualmente (tamaños de archivo y presencia de `.iobj`/`.ipdb` característicos de esa variante) ✅

## Actualización (misma sesión, continuada desde WSL vía interop de PowerShell)

Se completó toda la instalación sin necesidad de terminal nativa: Claude Code (corriendo en WSL) invocó `powershell.exe` directamente desde bash, aprovechando la interoperabilidad de WSL2 con binarios de Windows.

- ✅ Carpetas `data/namenode` y `data/datanode` — ya existían.
- ✅ Los 4 XML (core-site, hdfs-site, mapred-site, yarn-site) — ya existían con el contenido correcto.
- ✅ `hdfs namenode -format` — formateo exitoso.
- ✅ `start-all.cmd` — arrancó NameNode, DataNode y ResourceManager. **El NodeManager de YARN no arrancó con este script** (el comando quedó colgado tras "starting yarn daemons", problema de creación de ventanas de consola vía interop).
- ⚠️ Al lanzar `yarn nodemanager` manualmente, falló con: `Permissions incorrectly set for dir .../nm-local-dir/filecache, should be rwxr-xr-x, actual value = rwxrwxr-x`. Este es un bug conocido de Hadoop-en-Windows: cuando el proceso NO corre como Administrador, el campo "Group" de los directorios nuevos queda igual al "Owner" (el mismo usuario), así que `winutils` siempre calcula permisos de grupo = permisos de owner. `icacls` para ajustar el DACL NO lo soluciona (el problema es el campo Group, no el DACL).
- ✅ **Solución:** ejecutar el NodeManager desde una PowerShell **elevada (como Administrador)**, vía `Start-Process powershell -Verb RunAs` (disparado desde WSL, el usuario aceptó el UAC manualmente). Al correr elevado, Windows asigna el grupo "Administrators" (no el propio usuario) a los directorios nuevos, lo que separa correctamente owner/group y satisface el check de permisos. Se borró `C:\tmp\hadoop-esauf` (que había quedado con permisos incorrectos) antes de reintentar.
- ✅ Verificado: puertos 9870 (NameNode), 8088 (ResourceManager), 8042 (NodeManager) y 19000 (HDFS RPC) todos responden. 4 procesos `java.exe` corriendo.

**Nota para el futuro:** si hay que reiniciar el clúster (`start-all.cmd` o `start-dfs.cmd`/`start-yarn.cmd`), el NodeManager de YARN probablemente necesite lanzarse siempre desde una terminal elevada (Administrador) — de lo contrario fallará con el mismo error de permisos. NameNode, DataNode y ResourceManager no tuvieron ese problema.

## Cómo retomar en una próxima sesión (tras cerrar/reiniciar el PC)

Lo persistente: variables de entorno, los 4 XML, y los datos ya formateados de HDFS (`C:\Hadoop3\data\`). NO persistente: los procesos (namenode/datanode/resourcemanager/nodemanager se detienen al cerrar sesión/apagar).

Para retomar:
1. Abrir PowerShell **como Administrador**.
2. `cd C:\Hadoop3\sbin`
3. `start-all.cmd`
4. Verificar en navegador: http://localhost:9870/ y http://localhost:8088/

**NO volver a correr `hdfs namenode -format`** — ya está formateado; reformatear generaría un nuevo clusterID que no coincidiría con el DataNode ya registrado.

## Pendiente (siguientes pasos, ya completados — ver arriba)

1. **Crear carpetas de datos:**
   ```powershell
   New-Item -ItemType Directory -Force -Path "C:\Hadoop3\data\namenode"
   New-Item -ItemType Directory -Force -Path "C:\Hadoop3\data\datanode"
   ```

2. **Editar 4 archivos XML** en `C:\Hadoop3\etc\hadoop\`:

   **core-site.xml**
   ```xml
   <configuration>
     <property>
       <name>fs.default.name</name>
       <value>hdfs://0.0.0.0:19000</value>
     </property>
   </configuration>
   ```

   **hdfs-site.xml**
   ```xml
   <configuration>
     <property>
       <name>dfs.replication</name>
       <value>1</value>
     </property>
     <property>
       <name>dfs.namenode.name.dir</name>
       <value>/Hadoop3/data/namenode</value>
     </property>
     <property>
       <name>dfs.datanode.data.dir</name>
       <value>/Hadoop3/data/datanode</value>
     </property>
   </configuration>
   ```

   **mapred-site.xml**
   ```xml
   <configuration>
     <property>
       <name>mapreduce.framework.name</name>
       <value>yarn</value>
     </property>
     <property>
       <name>mapreduce.application.classpath</name>
       <value>%HADOOP_HOME%/share/hadoop/mapreduce/*,%HADOOP_HOME%/share/hadoop/mapreduce/lib/*,%HADOOP_HOME%/share/hadoop/common/*,%HADOOP_HOME%/share/hadoop/common/lib/*,%HADOOP_HOME%/share/hadoop/yarn/*,%HADOOP_HOME%/share/hadoop/yarn/lib/*,%HADOOP_HOME%/share/hadoop/hdfs/*,%HADOOP_HOME%/share/hadoop/hdfs/lib/*</value>
     </property>
   </configuration>
   ```

   **yarn-site.xml**
   ```xml
   <configuration>
     <property>
       <name>yarn.nodemanager.aux-services</name>
       <value>mapreduce_shuffle</value>
     </property>
     <property>
       <name>yarn.nodemanager.env-whitelist</name>
       <value>JAVA_HOME,HADOOP_COMMON_HOME,HADOOP_HDFS_HOME,HADOOP_CONF_DIR,CLASSPATH_PREPEND_DISTCACHE,HADOOP_YARN_HOME,HADOOP_MAPRED_HOME</value>
     </property>
   </configuration>
   ```

3. **Formatear HDFS:**
   ```powershell
   hdfs namenode -format
   ```
   Debe mostrar mensaje de éxito tipo "has been successfully formatted".

4. **Iniciar servicios** desde `C:\Hadoop3\sbin`:
   ```powershell
   cd C:\Hadoop3\sbin
   start-all.cmd
   ```
   Verificar que se abran 4 ventanas: Hadoop Namenode, Hadoop Datanode, YARN Resource Manager, YARN Node Manager.

5. **Verificar en navegador:** http://localhost:8088/

## Notas / lecciones de la sesión

- Esta sesión de Claude Code corría dentro de **WSL** (Platform: linux, rutas `/mnt/c/...`), separado del entorno real de Windows donde se configuraron las variables de entorno. Por eso el agente no podía ejecutar directamente los comandos de PowerShell — solo daba instrucciones para que el usuario las corriera.
- El usuario va a reabrir Claude Code desde una **terminal nativa de PowerShell** para que el agente pueda interactuar directamente con el entorno de Windows.
- Ojo: verificar siempre las capturas/imágenes del PDF contra las sugerencias del agente — hubo un error de recomendación de versión de winutils (se sugirió `hadoop-3.3.0` genérico cuando el PDF marcaba explícitamente `hadoop-3.3.0-YARN-8246`), corregido a tiempo gracias a que el usuario cuestionó la sugerencia.
