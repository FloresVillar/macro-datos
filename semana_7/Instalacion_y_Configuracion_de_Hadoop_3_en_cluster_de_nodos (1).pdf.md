# Instalacion_y_Configuracion_de_Hadoop_3_en_cluster_de_nodos (1).pdf

## Guía definitiva de instalación y configuración de

## Hadoop 3.3.6 en clúster distribuido Ubuntu

## Server

### 1. Estructura del clúster

Rol Hostname IP Usuario Servicios

Master master 192.168.18.101 master NameNode, SecondaryNameNode, ResourceManager

Slave1 slave1 192.168.18.100 slave1 DataNode, NodeManager Slave2 slave2 192.168.18.102 slave2 DataNode, NodeManager

El siguiente procedimiento se aplica tanto para un solo esclavo (slave1 como para varios (slave2, slave3, etc.).

Basta con repetir los pasos de configuración en cada nuevo nodo esclavo.

### 2. Requisitos previos

En todos los nodos (master y esclavos):

sudo apt update sudo apt install -y openjdk-8-jdk ssh rsync

Verificar instalación de Java:

java -version

Debe mostrar una versión 1.8 o superior.

### 3. Configuración de red

Editar el archivo en todos los nodos : /etc/hosts

sudo nano /etc/hosts

Agregar las líneas:

127.0.0.1 localhost 192.168.18.101 master 192.168.18.100 slave1 192.168.18.102 slave2

Probar conectividad:

ping -c1 master ping -c1 slave1 ping -c1 slave2

### 4. Creación de usuarios

Cada servidor debe tener su propio usuario, con el mismo nombre de host para claridad.

En master → usuario master

En slave1  usuario slave1

Guía definitiva de instalación y configuración de Hadoop 3.3.6 en clúster distribuido Ubuntu Server 1

En slave2  usuario slave2

### 5. Instalación de Hadoop

En cada nodo:

cd ~ wget https://downloads.apache.org/hadoop/common/hadoop-3.3.6/hadoop-3.3.6.tar.gz tar -xzf hadoop-3.3.6.tar.gz mv hadoop-3.3.6 hadoop

### 6. Configuración de variables de entorno

Editar en cada nodo y agregar al final: ~/.bashrc

export HADOOP_HOME/home/$(whoami)/hadoop export HADOOP_INSTALL$HADOOP_HOME export HADOOP_MAPRED_HOME$HADOOP_HOME export HADOOP_COMMON_HOME$HADOOP_HOME export HADOOP_HDFS_HOME$HADOOP_HOME export YARN_HOME$HADOOP_HOME export HADOOP_COMMON_LIB_NATIVE_DIR$HADOOP_HOME/lib/native export PATH$PATH$HADOOP_HOME/sbin:$HADOOP_HOME/bin

Aplicar y verificar:

source ~/.bashrc hadoop version

### 7. Configuración del Master

Editar los siguientes archivos en : /home/master/hadoop/etc/hadoop/

## core-site.xml

```xml
<configuration>
<property>
<name>fs.defaultFS/name>
<value>hdfs://master:9000/value>
</property>
</configuration>
```

## hdfs-site.xml

```xml
<configuration>
<property>
<name>dfs.replication</name>
<value>2/value>
</property>
<property>
<name>dfs.namenode.name.dir</name>
<value>file:/hadoopdata/namenode</value>
</property>
<property>
```

Guía definitiva de instalación y configuración de Hadoop 3.3.6 en clúster distribuido Ubuntu Server 2

```xml
<name>dfs.datanode.data.dir</name>
<value>file:/hadoopdata/datanode</value>
</property>
</configuration>
```

## mapred-site.xml

```xml
<configuration>
<property>
<name>mapreduce.framework.name</name>
<value>yarn</value>
</property>
<property>
<name>mapreduce.application.classpath</name>
<value>
```

/home/master/hadoop/etc/hadoop, /home/master/hadoop/share/hadoop/common/*, /home/master/hadoop/share/hadoop/common/lib/*, /home/master/hadoop/share/hadoop/hdfs/*, /home/master/hadoop/share/hadoop/hdfs/lib/*, /home/master/hadoop/share/hadoop/mapreduce/*, /home/master/hadoop/share/hadoop/yarn/*, /home/master/hadoop/share/hadoop/yarn/lib/*

```xml
</value>
</property>
</configuration>
```

## yarn-site.xml

```xml
<configuration>
<property>
<name>yarn.nodemanager.aux-services</name>
<value>mapreduce_shuffle</value>
</property>
<property>
<name>yarn.resourcemanager.hostname</name>
<value>master</value>
</property>
</configuration>
```

## hadoop-env.sh

export JAVA_HOME/usr/lib/jvm/java-8-openjdk-amd64/jre

### 8. Archivo de nodos (workers)

Editar : /home/master/hadoop/etc/hadoop/workers

slave1 slave2

### 9. Preparación de directorios

Guía definitiva de instalación y configuración de Hadoop 3.3.6 en clúster distribuido Ubuntu Server 3

En el master:

sudo mkdir -p /hadoopdata/namenode /hadoopdata/datanode sudo chown R master:master /hadoopdata

En cada nodo esclavo:

sudo mkdir -p /hadoopdata/namenode /hadoopdata/datanode sudo chown R $(whoami):$(whoami) /hadoopdata

### 10. Configuración SSH sin contraseña

En el master:

mkdir -p ~/.ssh && chmod 700 /.ssh cat > ~/.ssh/config <<'EOF' Host slave1 HostName 192.168.18.100 User slave1 StrictHostKeyChecking accept-new

Host slave2 HostName 192.168.18.102 User slave2 StrictHostKeyChecking accept-new EOF chmod 600 /.ssh/config

Generar y distribuir claves:

[ -f ~/.ssh/id_rsa  || ssh-keygen -t rsa P "" ssh-copy-id slave1 ssh-copy-id slave2

Prueba de conexión:

ssh slave1 'hostname && whoami' ssh slave2 'hostname && whoami'

### 11. Formateo del NameNode

En el master:

hdfs namenode -format

### 12. Sincronización de configuración

Enviar configuración a esclavos:

cd ~/hadoop/etc/hadoop for f in core-site.xml hdfs-site.xml yarn-site.xml mapred-site.xml hadoop-env.sh; do scp "$f" slave1/home/slave1/hadoop/etc/hadoop/; scp "$f" slave2/home/slave2/hadoop/etc/hadoop/; done

Guía definitiva de instalación y configuración de Hadoop 3.3.6 en clúster distribuido Ubuntu Server 4

### 13. Inicio de servicios

En el master:

start-dfs.sh start-yarn.sh

Verificar:

jps ssh slave1 jps ssh slave2 jps

Servicios esperados:

### Master

NameNode SecondaryNameNode ResourceManager

### Esclavos

DataNode NodeManager

### 14. Interfaz Web

Servicio URL Descripción

NameNode http://192.168.18.1019870 Estado general del HDFS ResourceManager http://192.168.18.1018088 Gestión de trabajos YARN

DataNode (slave1 http://192.168.18.1009864 Estado del DataNode

DataNode (slave2 http://192.168.18.1029864 Estado del DataNode

NodeManager http://192.168.18.1008042 Tareas locales YARN

### 15. Verificación de HDFS

hdfs dfs -mkdir -p /user/master/input echo "hadoop mapreduce yarn test" > texto1.txt echo "bigdata cluster system processing" > texto2.txt hdfs dfs -put texto1.txt texto2.txt /user/master/input/ hdfs dfs -ls /user/master/input

### 16. Ejemplo MapReduce: WordCount

 Ejecutar el trabajo:

hadoop jar $HADOOP_HOME/share/hadoop/mapreduce/hadoop-mapreduce-examples-3.3.6.jar wordcount /user/mast er/input /user/master/output

 Verificar resultados:

hdfs dfs -cat /user/master/output/part-r-00000

Salida esperada:

Guía definitiva de instalación y configuración de Hadoop 3.3.6 en clúster distribuido Ubuntu Server 5

bigdata 1 cluster 2 distributed 1 hadoop 3 hdfs 1 mapreduce 3 processing 1 system 1 test 1 yarn 2

### 17. Detener servicios

En el master:

stop-yarn.sh stop-dfs.sh

## Conclusión

El clúster Hadoop 3.3.6 queda completamente operativo con:

Comunicación entre nodos verificada vía . /etc/hosts

SSH sin contraseña configurado.

HDFS y YARN funcionando de forma distribuida.

Ejecución exitosa de trabajos MapReduce.

Interfaces web de monitoreo accesibles.

Facilidad para escalar con nuevos esclavos repitiendo el procedimiento.

Guía definitiva de instalación y configuración de Hadoop 3.3.6 en clúster distribuido Ubuntu Server 6
