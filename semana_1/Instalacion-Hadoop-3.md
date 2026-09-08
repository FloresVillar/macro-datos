# Instalación de Hadoop 3.3.0 en Windows

## Prerequisitos

1. Java 8 runtime environment (JRE)
2. Apache Hadoop 3.3.0

## Instrucciones

1. Según el Manual "Instalación de JDK" con el instalador:
   https://javadl.oracle.com/webapps/download/AutoDL?BundleId=247948_0ae14417abb444ebb02b9815e2103550

   Al final su instalación de Java debe estar en la ruta `C:/Java`

2. Descargar Hadoop 3.3.0 desde la dirección:
   https://archive.apache.org/dist/hadoop/common/hadoop-3.3.0/hadoop-3.3.0.tar.gz

3. Extraer el contenido de hadoop-3.3.0.tar.gz y colocarlo en el directorio `C:`
   _(captura: carpeta `Hadoop3` creada en `C:\`)_

4. Configurar las variables de entorno de Windows:
   - Variable: `JAVA_HOME` — valor: `C:\Java`
   - Variable: `HADOOP_HOME` — valor: `C:\Hadoop3` (o el nombre de tu carpeta)
   - En el `Path` agregar la ruta del `bin` de hadoop y de Java (`C:\Hadoop3\bin`, `C:\Java\bin`)

5. Descargar el archivo Winutils del aula virtual y copiar la carpeta `bin` de la versión correspondiente al hadoop descargado.

   > Versión correcta para esta instalación: `hadoop-3.3.0-YARN-8246` (no la carpeta genérica `hadoop-3.3.0`)

6. Configurar los archivos base de hadoop ubicados en `%HADOOP_HOME%\etc\hadoop`

   **core-site.xml**
   ```xml
   <configuration>
     <property>
       <name>fs.default.name</name>
       <value>hdfs://0.0.0.0:19000</value>
     </property>
   </configuration>
   ```

   En el directorio de `%HADOOP_HOME%` crear un directorio `data` y luego los directorios `datanode` y `namenode`, y editar el archivo `hdfs-site.xml`:

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

7. Aplicar el formateo de los directorios del HDFS, ejecutar:
   ```powershell
   hdfs namenode -format
   ```
   Si todo salió correctamente debería mostrar un mensaje de éxito tipo `"has been successfully formatted"`.

8. Ir a la carpeta `sbin` de la instalación de hadoop y ejecutar:
   ```powershell
   start-all.cmd
   ```
   Revisar que se hayan activado los servicios de:
   - Hadoop Namenode
   - Hadoop datanode
   - YARN Resource Manager
   - YARN Node Manager

9. Abrir en navegador: http://localhost:8088/

## Referencias

- https://github.com/ruslanmv/How-to-install-Hadoop-on-Windows/tree/master?tab=readme-ov-file
- Manual de instalación de Hadoop 2.8.0 en el aula virtual.
- En Linux: https://es.unixlinux.online/qu/1002014012.html
