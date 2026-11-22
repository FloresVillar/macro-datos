# GuiaInstalacion Hadoop cluster 3.3.6 -261.pdf

GuiaInstalacion.md 2026-05-05

Instalación y Configuración de Hadoop en un Cluster

de 3 nodos

Paso 1: Requerimientos y Preparación de la VM

1. Software Necesario:

VirtualBox: Instálalo en tu PC real (Anfitrión). ISO de Linux: Se recomienda Ubuntu Server o Linux Mint (por ser ligeros).

2. Crear la VM "Base":

Crea una nueva VM llamada Hadoop-Master . Desmarcar la casilla Proceder con instalación desatendida Asigna 2GB de RAM y 25GB de disco . En Configuración de la VM recién creada, ve a la sección Red -> Adaptador2 -> Habilitar adaptador de Red . Selecciona Adaptador Solo Anfitrión y luego Aceptar .

3. Instalación de Ubuntu Server:

Procede con la instalación normal. Al momento de configurar el usuario, indica: Nombre de equipo: master Usuario: hadoop Marca la casilla Instalar OpenSSH Server . Espera que termine la instalación.

Paso 2: Configuración del Sistema

Una vez instalada la VM Base, vamos a instalar las herramientas necesarias y configurar la red para que los nodos puedna comunicarse entre sí.

1. Instalar y activar OpenSSH Server (Si no lo instalaste junto al Ubuntu)

# Instalación de OpenSSH Server sudo apt update sudo apt install openssh-server -y

# Activa el servicio sudo systemctl enable --now ssh

# Verifica que se activo correctamente. Debería ver un mensaje en verde que diga active (running) sudo systemctl status ssh

1 / 11

GuiaInstalacion.md 2026-05-05

2. Instalar Java 11

sudo apt update sudo apt install openjdk-11-jdk openssh-server -y

3. Configuración de IPs estáticas :

Vamos a fijar las direcciones IP del clúster para evitar problemas al clonar las VM o si se reinician. Primero hay que comprobar la interfaz de red que se va a modificar.

ip a

Aqui deberían aparecer 3 interfaces de red. La red NAT suele tener el nombre enp0s3 y su IP la forma 10.0.X.X , y la red Solo-Anfitrión suele tener el nombre enp0s8 y, o no tiene dirección IP o tiene una de la forma 192.168.X.X . A continuación vamos a fijar las direcciones IP:

sudo nano /etc/netplan/00-installer-config.yaml

Modifica el archivo de esta forma:

network: ethernets: enp0s3: dhcp4: true dhcp6: true enp0s8: accept-ra: no dhcp4: no dhco6: no addresses: - 192.168.56.10/24 version: 2

Finalmente, aplica los cambios:

sudo netplan apply

4. Modificar hostnames para el cluster .

2 / 11

GuiaInstalacion.md 2026-05-05

sudo nano /etc/hosts

En el editor de texto, agregar al final:

192.168.56.10 master 192.168.56.11 worker1 192.168.56.12 worker2

Para evitar problemas más adelante, comentar la línea que contiene el nombre master al principio del archivo. Debería verse algo así:

127.0.0.1 localhost # 127.0.1.1 master

5. Reiniciar para ver los cambios

sudo reboot

Después de reiniciar la VM, se debería ver la interfaz de red con la IP estática:

ip a

Paso 3: Clonar las VM

1. En VirtualBox

Haz clic derecho en la VM y selecciona Clonar . En el menú: Nombre : Hadoop-Worker1 Tipo de clon : Clonación completa Política de Dirección MAC : Generar nuevas direcciones MAC para todos los adaptadores de red. Haz clic en Terminar y arranca la VM recién clonada.

2. Modifica el hostname

sudo hostnamectl set-hostname worker1

3 / 11

GuiaInstalacion.md 2026-05-05

3. Modifica la IP estática

sudo nano /etc/netplan/00-installer-config.yaml

En el editor de texto, modifica la dirección IP de la interfaz enp0s8 a su IP correspondiente:

network: ethernets: enp0s3: dhcp4: true dhcp6: true enp0s8: accept-ra: no dhcp4: no dhcp6: no addresses: - 192.168.56.11/24 <---- AQUI CAMBIA LA IP version: 2

Luego aplicar los cambios:

sudo netplan apply

4. Reinicia la VM y comprobar cambios

sudo reboot

Después de reiniciar, se debería notar el cambio de hostname. Ahora deberia aparecer hadoop@worker1 en línea de comandos. Luego, ejecutando ip a se debería ver el cambio en dirección IP.

Para verificar la conexión, desde el master manda un ping al worker recien configurado:

ping -c 3 worker1

5. Repite... .

Repite estos pasos por cada nodo worker que desees. Solo procura que cada uno tenga una IP estática única y que figure correctamente en /etc/hosts .

4 / 11

GuiaInstalacion.md 2026-05-05

Paso 4: Configurar SSH (EN EL NODO MASTER)

1. Crear archivo de configuración

mkdir -p ~/.ssh chmod 700 ~/.ssh nano ~/.ssh/config

En el editor de texto, escribe:

Host master HostName 192.168.56.10 User hadoop StrictHostKeyChecking accept-new

Host worker1 HostName 192.168.56.11 User hadoop StrictHostKeyChecking accept-new

Host worker2 HostName 192.168.56.12 User hadoop StrictHostKeyChecking accept-new

Finalmente, define los permisos del nuevo archivo config:

chmod 600 ~/.ssh/config

2. Crea y distribuye llaves SSH

# Genera llaves sin passkey ssh-keygen -t rsa -P ""

# Distribuye las llaves a los nodos ssh-copy-id master ssh-copy-id worker1 ssh-copy-id worker2

3. Verifica la conexión

5 / 11

GuiaInstalacion.md 2026-05-05

ssh worker1 'hostname && whoami'

"

Paso 5: Instalación de Hadoop (En el nodo MASTER)

1. Descarga Hadoop 3.3.6

wget https://archive.apache.org/dist/hadoop/common/hadoop-3.3.6/hadoop- 3.3.6.tar.gz

2. Descomprime el archivo descargado

tar -xvzf hadoop-3.3.6.tar.gz

3. Renombra carpeta descomprimida y elimina el comprimido

mv hadoop-3.3.6 hadoop rm hadoop-3.3.6.tar.gz

Paso 6: Configuración de Hadoop (En MASTER)

1. Variables de entorno

sudo nano ~/.bashrc

En el editor de texto, agregar al final:

export HADOOP_HOME=$HOME/hadoop export HADOOP_INSTALL=$HADOOP_HOME export HADOOP_MAPRED_HOME=$HADOOP_HOME export HADOOP_COMMON_HOME=$HADOOP_HOME export HADOOP_HDFS_HOME=$HADOOP_HOME export YARN_HOME=$HADOOP_HOME export HADOOP_COMMON_LIB_NATIVE_DIR=$HADOOP_HOME/lib/native export PATH=$PATH:$HADOOP_HOME/sbin:$HADOOP_HOME/bin export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64

6 / 11

GuiaInstalacion.md 2026-05-05

Finalmente para cargar los cambios:

source ~/.bashrc

2. Configurar hadoop-env.sh

nano ~/hadoop/etc/hadoop/hadoop-env.sh

En el editor de texto, agregar:

export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64

3. Configurar core-site.xml

nano ~/hadoop/etc/hadoop/core-site.xml

En el editor de texto, agregar:

```xml
<configuration>
<property>
<name>fs.defaultFS</name>
<value>hdfs://master:9000</value>
</property>
</configuration>
```

4. Configurar hdfs-site.xml

nano ~/hadoop/etc/hadoop/hdfs-site.xml

En el editor de texto, agregar:

```xml
<configuration>
<property>
<name>dfs.replication</name>
<value>2</value>
</property>
<property>
<name>dfs.namenode.name.dir</name>
```

7 / 11

GuiaInstalacion.md 2026-05-05

```xml
<value>file:///home/hadoop/hadoop_data/hdfs/namenode</value>
</property>
<property>
<name>dfs.datanode.data.dir</name>
<value>file:///home/hadoop/hadoop_data/hdfs/datanode</value>
</property>
</configuration>
```

5. Configurar mapred-site.xml

nano ~/hadoop/etc/hadoop/mapred-site.xml

En el editor de texto, agregar:

```xml
<configuration>
<property>
<name>mapreduce.framework.name</name>
<value>yarn</value>
</property>
<property>
<name>mapreduce.application.classpath</name>
<value>$HADOOP_HOME/share/hadoop/mapreduce/*:$HADOOP_HOME/share/hadoop/mapred
```

uce/lib/*</value>

```xml
</property>
<property>
<name>yarn.app.mapreduce.am.env</name>
<value>HADOOP_MAPRED_HOME=${HADOOP_HOME}</value>
</property>
<property>
<name>mapreduce.map.env</name>
<value>HADOOP_MAPRED_HOME=${HADOOP_HOME}</value>
</property>
<property>
<name>mapreduce.reduce.env</name>
<value>HADOOP_MAPRED_HOME=${HADOOP_HOME}</value>
</property>
</configuration>
```

6. Configurar yarn-site.xml

nano ~/hadoop/etc/hadoop/yarn-site.xml

En el editor de texto, agregar:

8 / 11

GuiaInstalacion.md 2026-05-05

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

7. Configurar workers

nano ~/hadoop/etc/hadoop/workers

En el editor de texto, agregar:

worker1 worker2

Paso 7: Instalar Hadoop en Workers y sincronizar los cambios

1. Crear carpetas para Namenode y Datanode en cada nodo

mkdir -p ~/hadoop_data/hdfs/namenode mkdir -p ~/hadoop_data/hdfs/datanode

También puedes crear las carpetas en los workers desde el master via SSH

ssh worker1 'mkdir -p ~/hadoop_data/hdfs/datanode' ssh worker1 'mkdir -p ~/hadoop_data/hdfs/namenode'

ssh worker2 'mkdir -p ~/hadoop_data/hdfs/datanode' ssh worker2 'mkdir -p ~/hadoop_data/hdfs/namenode'

2. Enviar la carpeta hadoop a los workers

scp -r ~/hadoop worker1:~/ scp -r ~/hadoop worker2:~/

9 / 11

GuiaInstalacion.md 2026-05-05

3. Enviar .bashrc a los workers

scp ~/.bashrc worker1:~/ scp ~/.bashrc worker2:~/

Debes cargar los cambios en el mismo nodo usando source ~/.bashrc

Paso 8: Iniciar Hadoop (en Master)

1. Formatea el Namenode

hdfs namenode -format

2. Inicia los servicios de archivos distribuidos (HDFS) y el gestor de recursos (YARN)

start-dfs.sh start-yarn.sh

3. Verificar

jps

Deberia aparecer NameNode , SecondaryNameNode y ResourceManager en el master ; y DataNode y NodeManager en los workers .

Paso 9: Ejecución de ejemplo

1. Generar un archivo de prueba

# Crear un archivo de prueba de 10 000 líneas de texto... yes "Este es un archivo de prueba para el curso de Análisis de Macrodatos" | head -n 10000 > datos_prueba.txt

2. Cargar el archivo al HDFS

# Crear la carpeta de entrada en HDFS hdfs dfs -mkdir -p /input

# Subir el archivo generado hdfs dfs -put datos_prueba.txt /input/

10 / 11

GuiaInstalacion.md 2026-05-05

# Verificar que el archivo ya vive en el clúster hdfs dfs -ls /input/

3. Ejecutamos un WordCount de prueba

Hadoop incluye un paquete de ejemplos junto a su instalación.

hadoop jar $HADOOP_HOME/share/hadoop/mapreduce/hadoop-mapreduce-examples- 3.3.6.jar wordcount /input/datos_prueba.txt /output

4. Ver el resultado

hdfs dfs -cat /output/part-r-00000

11 / 11
