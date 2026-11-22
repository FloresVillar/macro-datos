
<div align="center">

#  ANÁLISIS EN MACRODATOS

### EXAMEN PARCIAL

**2026-2**

<img width="1200" height="1509" alt="UNI-logo" src="https://github.com/user-attachments/assets/0c31891b-7e30-43d9-9756-57a0af3c0464" />


---

#  Integrantes

- Esaú Flores Villar
- José Manuel Montalvo Espinoza

---
</div>

# ÍNDICE GENERAL

## 1. INTRODUCCIÓN Y CONTEXTO DEL PROYECTO
* **1.1.** Objetivos del Proyecto
* **1.2.** Descripción de la Fuente de Datos (Dataset SUTRAN)
  * **1.2.1.** Ficha Técnica y Metadatos de la PNDA
  * **1.2.2.** Diccionario de Datos y Tipos de Variables
  * **1.2.3.** Perfil del CSV y Calidad de Datos (Limpieza, Encoding UTF-8 y Nulos)
* **1.3.** Referencias Bibliográficas e Investigaciones Previas

---

## 2. ARQUITECTURA E IMPLEMENTACIÓN DEL CLÚSTER HADOOP
* **2.1.** Topología y Recursos de Hardware/Software
* **2.2.** Configuración del Entorno de Virtualización (VirtualBox & Hyper-V)
* **2.3.** Guía Paso a Paso de Instalación y Configuración Multi-nodo
  * **2.3.1.** Creación e Instalación de la Máquina Virtual Base (`master`)
  * **2.3.2.** Configuración de Red Fija (Netplan) y Resolución de Nombres (`/etc/hosts`)
  * **2.3.3.** Clonación de Nodos Trabajadores (`worker1` y `worker2`)
  * **2.3.4.** Configuración SSH con Autenticación mediante Claves Públicas
  * **2.3.5.** Instalación de Java 11 y Configuración de Variables de Entorno (`.bashrc`)
  * **2.3.6.** Configuración de XMLs de Hadoop (`core-site.xml`, `hdfs-site.xml`, `mapred-site.xml`, `yarn-site.xml`, `workers`)
  * **2.3.7.** Formateo e Inicio de Servicios Distribuidos (HDFS & YARN)
* **2.4.** Verificación de Nodos Conectados (Panel Web YARN `:8088` / HDFS `:9870` y comandos `jps`, `dfsadmin`)
* **2.5.** Prueba Inicial de Funcionamiento en Paralelo (*WordCount*)

---

## 3. DESARROLLO DE CONSULTAS Y ALGORITMOS EN HADOOP MAPREDUCE
* **3.1.** Consultas de Complejidad con Decimales (MapReduce Triple)
  * **3.1.1. Consulta 1 (`SeveridadPorVia`):** Índice de Severidad de Víctimas por Carretera
    * *a) Formulación de la Pregunta y Lógica de Negocio*
    * *b) Diseño del Pipeline de 3 Jobs (Encadenamiento de Mappers y Reducers)*
    * *c) Inputs, Outputs y Resultados Obtenidos*
  * **3.1.2. Consulta 2 (`PuntosNegrosPorTramo`):** Detección de Tramos Críticos por Z-Score
    * *a) Formulación de la Pregunta y Análisis Estadístico por Tramo (10 km)*
    * *b) Arquitectura MapReduce Triple y Manejo de Decimales*
    * *c) Inputs, Outputs y Ranking de Puntos Negros*
* **3.2.** Algoritmo de Agrupamiento Geográfico en MapReduce (Sin Librerías)
  * **3.2.1. Consulta 3 (`AgrupamientoDBSCAN`):** Clustering Espacial Basado en Densidad
    * *a) Justificación del Algoritmo y Cálculo de Distancia Haversine*
    * *b) Arquitectura de 19 Jobs MapReduce (Vecindad, Propagación Etiquetada, Resumen, Ranking y Métricas)*
    * *c) Selección de Parámetros ($\epsilon = 2\text{ km}$, $\text{minPts} = 10$)*
    * *d) Resultados y Centroides de Zonas Críticas Obtenidos*
* **3.3.** Modelos de Machine Learning Nativos en MapReduce con Gradiente Descendente
  * **3.3.1.** Arquitectura Común de Red Neuronal Multicapa (MLP) en MapReduce
  * **3.3.2. Consulta 4 (`PerceptronClasificacion`):** Predicción de Accidentes con Fallecidos
    * *a) Preparación de Variables, Normalización Z-Score y Split Train/Test*
    * *b) Implementación de Gradiente Descendente Completo por Épocas (53 Jobs)*
    * *c) Función de Pérdida con Entropía Cruzada Ponderada y Evaluación*
  * **3.3.3. Consulta 5 (`PerceptronRegresion`):** Estima de Cantidad de Heridos
    * *a) Transformación Logarítmica y Modelo de Salida Lineal*
    * *b) Propagación de Gradientes Distribuidos y Curva de Pérdida*

---

## 4. EVALUACIÓN DE RENDIMIENTO Y COMPARACIÓN EXPERIMENTAL
* **4.1.** Métricas del Algoritmo de Agrupamiento (*Clúster vs. Nodo Único*)
  * **4.1.1.** *Silhouette Score*
  * **4.1.2.** *Davies-Bouldin Index*
* **4.2.** Métricas de Evaluación de Modelos de Machine Learning
  * **4.2.1.** Tabla Comparativa del Perceptrón de Clasificación (*Accuracy, Precision, Recall, F1-Score, Matriz de Confusión*)
  * **4.2.2.** Tabla Comparativa del Perceptrón de Regresión (*MSE, MAE, $R^2$*)
* **4.3.** Análisis de Rendimiento de la Infraestructura (*Clúster Distribuido vs. Nodo Único*)
  * **4.3.1.** Tabla Comparativa de Tiempos de Ejecución
  * **4.3.2.** Cálculo del *Speedup* y Discusión Técnica sobre la Carga de Trabajo y Overhead de Red
  * **4.3.3.** Monitoreo de Recursos del Sistema (*CPU Promedio, Uso de Memoria RAM, htop y vmstat*)

---

## 5. ANALÍTICA VISUAL Y DASHBOARDS EN POWER BI
* **5.1.** Exportación y Transformación de Resultados HDFS a TSV/UTF-8
* **5.2.** Integración de Coordenadas Geográficas y Categorización en Power Query
* **5.3.** Visualización de Resultados:
  * **5.3.1.** Mapa Geográfico de Accidentes por Grupo DBSCAN
  * **5.3.2.** Mapa de Burbujas de Zonas Críticas y Puntos Negros
  * **5.3.3.** Gráficos de Severidad por Departamento
  * **5.3.4.** Curvas de Pérdida por Época de los Modelos MLP

---

## 6. CONCLUSIONES Y RECOMENDACIONES
* **6.1.** Conclusiones sobre la Infraestructura y el Paralelismo en Hadoop
* **6.2.** Hallazgos Clave en la Red Vial SUTRAN
* **6.3.** Recomendaciones para Escalamiento de Macrodatos

---

## 7. ANEXOS Y ENTREGABLES
* **Anexo A:** Scripts de Automatización (`parcial.sh` y `parcial.ps1`)
* **Anexo B:** Estructura de Clases Java y Paquetes Compilados (`PARCIAL.jar`)
* **Anexo C:** Licencia y Origen del Dataset PNDA - SUTRAN



---




### PREPARACIÓN DEL ENTORNO

# Guía: clúster Hadoop 3.3.6 en VirtualBox (1 master + 2 workers)

> **Estado: ejecutada y comprobada (2026-10-17/18).** Clúster funcionando: 2 DataNodes vivos y WordCount correcto. Cada paso tiene debajo su resultado real y la captura.

**Fuente:** `semana_7/GuiaInstalacion Hadoop cluster 3.3.6 -261.pdf` (guía del curso, fechada 2026-05-05; versión md en el mismo directorio). Lo que no está en esa guía va marcado como **(agregado)**. Los "Resultado esperado" son del autor, salvo donde la guía los da (prompt `hadoop@worker1`, salida de `jps`).

La otra guía de `semana_7/` (`Instalacion_y_Configuracion_de_Hadoop_3_en_cluster_de_nodos (1).pdf`) es una alternativa más antigua (Java 8, red puente 192.168.18.x, un usuario distinto por nodo, errores de tipeo en los XML); queda solo como referencia.

**Requisito del enunciado (Parte 2):** "Implementar un Clúster de Hadoop e instalar Hadoop multimodo 1 maestro y 2 o más esclavos, en máquinas virtuales de Ubuntu o Windows usando virtual box u otro virtualizador a su criterio. Es estrictamente necesario que la implementación sea mediante máquinas virtuales." El informe debe llevar capturas de cada paso de configuración y de la pantalla final con los nodos conectados.

## Topología

| VM | Hostname | IP (red solo anfitrión) | Usuario | Servicios esperados |
| --- | --- | --- | --- | --- |
| Hadoop-Master | `master` | 192.168.56.10 | `hadoop` | NameNode, SecondaryNameNode, ResourceManager |
| Hadoop-Worker1 | `worker1` | 192.168.56.11 | `hadoop` | DataNode, NodeManager |
| Hadoop-Worker2 | `worker2` | 192.168.56.12 | `hadoop` | DataNode, NodeManager |

## Antes de empezar

- **ISO disponible:** `PARCIAL/ubuntu-26.04.1-live-server-amd64.iso`. La guía usa Java 11 (`openjdk-11-jdk`), y Hadoop 3.3.6 solo corre con Java 8 u 11. Ubuntu 26.04 (resolute) sí incluye `openjdk-11-jdk` en sus repositorios: versión `11.0.32.1+1-1ubuntu1~26.04` para amd64, según packages.ubuntu.com (consultado el 2026-10-17). El `apt-cache policy` del paso 2.1 lo confirma dentro de la VM. **(agregado)**
- **RAM del equipo anfitrión** (ASUS ExpertBook P1503CVA, capturas [`imagenes/CUANTA_RAM_TENGO.png`](../imagenes/CUANTA_RAM_TENGO.png) y [`imagenes/CUANTA_MEMORIA_RAM_USAN_PROCESOS.png`](../imagenes/CUANTA_MEMORIA_RAM_USAN_PROCESOS.png)): 16 GB instalados, 5,08 GB libres al momento de la captura (uso 68 %). Las 3 VM suman 6 GB. Con el Paso 0, WSL ya queda apagado. Cerrar Chrome, VS Code, Edge y Teams libera unos 2,9 GB; cerrar WSL y VS Code corta la sesión de trabajo con Claude Code, así que conviene hacerlo solo para las mediciones. Si aún falta, bajar los workers a 1,5 GB. **(agregado)**
- **CPU del anfitrión:** Intel Core 7 240H, 10 núcleos / 16 hilos; alcanza para 2 vCPU por VM. **(agregado)**
- **WSL2 y VirtualBox:** con el hipervisor de Windows activo (WSL2, Hyper-V, VBS), VirtualBox no puede usar VT-x y corre sobre Hyper-V (ícono de tortuga verde en la VM; en `VBox.log`: `Attempting fall back to NEM: VT-x is not available`). Es muy lento: la instalación de Ubuntu no avanzaba. `wsl --shutdown` **no alcanza**, porque el hipervisor se carga al arrancar Windows. Hay que desactivarlo y reiniciar; ver el [Paso 0](#paso-0-desactivar-hyper-v-si-se-usa-wsl2-y-reactivarlo-al-terminar), que también explica cómo reactivar WSL2 al terminar. **(agregado)**
- **Capturas:** tomar una por cada paso de configuración mientras se avanza.

## Paso 0: desactivar Hyper-V si se usa WSL2 (y reactivarlo al terminar)

**(agregado)** Este paso solo hace falta si en Windows está activo el hipervisor, que normalmente lo activan **WSL2**, Docker Desktop, Hyper-V o la seguridad basada en virtualización (VBS). Para saberlo, en PowerShell:

```powershell
(Get-CimInstance Win32_ComputerSystem).HypervisorPresent
```

Si dice `False`, saltar al Paso 1. Si dice `True`, VirtualBox no puede usar VT-x y corre en modo lento (tortuga verde en la barra de la VM; en `VBox.log`: `Attempting fall back to NEM: VT-x is not available`). En ese modo la instalación de Ubuntu casi no avanza. `wsl --shutdown` no alcanza, porque el hipervisor se carga al arrancar Windows.

**Importante:** mientras el hipervisor esté apagado, **WSL2 y Docker Desktop no funcionan**. Al terminar el proyecto hay que reactivarlo con el punto 0.2.

**Estado original del equipo** (revisado el 2026-10-17; es el estado al que vuelve el punto 0.2):

| Elemento | Estado original |
|---|---|
| Hipervisor de Windows (`HypervisorPresent`) | Activo |
| `hypervisorlaunchtype` (bcdedit) | `auto` (por defecto) |
| VBS: `EnableVirtualizationBasedSecurity` | `1` (corriendo) |
| Integridad de memoria (HVCI) | Ya desactivada (`0`) |
| Biometría segura de Windows Hello (`Scenarios\SecureBiometrics`, `Enabled`) | `1` (activada) |
| WSL2 | En uso (`vmmemWSL` corriendo) |
| Plataforma de máquina virtual | Activada (WSL2 la necesita) |

### 0.1 Desactivar (antes de usar VirtualBox)

**Atajo** (PowerShell como administrador; la receta corre los mismos comandos del paso 1, el paso 2 de Configuración sigue siendo manual):

```powershell
cd C:\Users\esauf\Desktop\uni\macro-datos\PARCIAL
.\parcial.ps1 hyperv-off
# Inicio -> Reiniciar; luego, otra vez en PARCIAL:
cd C:\Users\esauf\Desktop\uni\macro-datos\PARCIAL
.\parcial.ps1 hyperv-check      # debe decir False / 0
```

**Sin la receta (paso a paso manual).** El bloque del paso 1 es exactamente lo que ejecuta `hyperv-off`; está aquí solo como referencia o por si `parcial.ps1` no está a mano. Con el atajo de arriba **no hace falta** correrlo. En conjunto, impide que Windows cargue su hipervisor y VBS al arrancar (incluido lo que los volvía a encender: Windows Hello y Credential Guard), para que VirtualBox use el procesador directamente.

1. En **PowerShell como administrador** (clic derecho en Inicio → "Terminal (Administrador)"; lo mismo que `hyperv-off`):

```powershell
wsl --shutdown
bcdedit /set hypervisorlaunchtype off                     # el arranque no carga el hipervisor
bcdedit /set "{current}" vsmlaunchtype Off                # ni VSM (VBS)
reg add "HKLM\SYSTEM\CurrentControlSet\Control\DeviceGuard" /v EnableVirtualizationBasedSecurity /t REG_DWORD /d 0 /f
reg add "HKLM\SYSTEM\CurrentControlSet\Control\DeviceGuard" /v RequirePlatformSecurityFeatures /t REG_DWORD /d 0 /f
reg add "HKLM\SYSTEM\CurrentControlSet\Control\DeviceGuard\Scenarios\SecureBiometrics" /v Enabled /t REG_DWORD /d 0 /f   # Windows Hello: vuelve a encender VBS
reg add "HKLM\SYSTEM\CurrentControlSet\Control\DeviceGuard\Scenarios\WindowsHello" /v Enabled /t REG_DWORD /d 0 /f
reg add "HKLM\SYSTEM\CurrentControlSet\Control\Lsa" /v LsaCfgFlags /t REG_DWORD /d 0 /f                                     # Credential Guard
dism /online /disable-feature /featurename:VirtualMachinePlatform /norestart   # si dice que no existe, ignorarlo
dism /online /disable-feature /featurename:HypervisorPlatform /norestart
dism /online /disable-feature /featurename:Microsoft-Hyper-V-All /norestart
bcdedit /enum "{current}" | findstr /i "hypervisor vsm"   # debe decir Off en las dos
```

2. Si aparece, desactivar también **Configuración → Cuentas → Opciones de inicio de sesión → Inicio de sesión mejorado** (en este equipo no aparece; por eso se apaga desde el registro con `WindowsHello` y `SecureBiometrics`). El 0.2 lo vuelve a activar.
3. **Reiniciar** con **Inicio → Reiniciar** (no Apagar: con el inicio rápido no recarga la configuración de arranque).
4. Comprobar (PowerShell normal):

```powershell
(Get-CimInstance Win32_ComputerSystem).HypervisorPresent   # False
(Get-CimInstance -Namespace root\Microsoft\Windows\DeviceGuard -ClassName Win32_DeviceGuard).VirtualizationBasedSecurityStatus   # 0
```

   Al encender una VM debe aparecer el ícono de chip "V" en lugar de la tortuga verde. Si se usa huella o cara para entrar a Windows, puede pedir registrarlas de nuevo; el PIN sigue funcionando.

**Resultado (2026-10-17):** `HypervisorPresent` = `False`, `VirtualizationBasedSecurityStatus` = `0`. Hicieron falta varios reinicios hasta encontrar que VBS seguía corriendo por Windows Hello (`SecureBiometrics`, `WindowsHello`) aunque el registro lo tenía apagado; el bloque de arriba ya incluye todo. Si en otro equipo sigue en `True` / `2`, VBS puede estar bloqueado en UEFI: usar `DG_Readiness_Tool` de Microsoft o desactivar la virtualización en la BIOS.

### 0.2 Reactivar WSL2 (al terminar el proyecto)

**Atajo** (PowerShell como administrador; la receta corre los mismos comandos del paso 2, el Inicio de sesión mejorado del paso 3 sigue siendo manual):

```powershell
cd C:\Users\esauf\Desktop\uni\macro-datos\PARCIAL
.\parcial.ps1 hyperv-on
# reactivar el Inicio de sesión mejorado (paso 3) si se desactivó; Inicio -> Reiniciar; luego:
cd C:\Users\esauf\Desktop\uni\macro-datos\PARCIAL
.\parcial.ps1 hyperv-check      # debe decir True / 2
wsl -l -v                       # distros con VERSION 2
```

WSL2 y VirtualBox no funcionan a la vez: hay que alternar. Para usar WSL2: `hyperv-on` y reiniciar. Para volver a las VM: `hyperv-off` y reiniciar. Hacerlo de forma definitiva al terminar el proyecto (después de la exposición).

**Sin la receta (paso a paso manual).** El bloque de comandos del paso 2 es exactamente lo que ejecuta `hyperv-on`; está aquí solo como referencia o por si `parcial.ps1` no está a mano. Con el atajo de arriba **no hace falta** correrlo. En conjunto, deshace el 0.1: vuelve a encender el hipervisor de Windows y VBS, devuelve Windows Hello a como estaba y reactiva las características de virtualización que WSL2 necesita.

1. Cerrar VirtualBox y apagar todas las VM.
2. En **PowerShell como administrador** (lo mismo que `hyperv-on`):

```powershell
bcdedit /set hypervisorlaunchtype auto
reg add "HKLM\SYSTEM\CurrentControlSet\Control\DeviceGuard" /v EnableVirtualizationBasedSecurity /t REG_DWORD /d 1 /f
reg add "HKLM\SYSTEM\CurrentControlSet\Control\DeviceGuard\Scenarios\SecureBiometrics" /v Enabled /t REG_DWORD /d 1 /f
reg add "HKLM\SYSTEM\CurrentControlSet\Control\DeviceGuard\Scenarios\WindowsHello" /v Enabled /t REG_DWORD /d 1 /f
bcdedit /deletevalue "{current}" vsmlaunchtype
Remove-ItemProperty HKLM:\SYSTEM\CurrentControlSet\Control\DeviceGuard -Name RequirePlatformSecurityFeatures -ErrorAction SilentlyContinue
Remove-ItemProperty HKLM:\SYSTEM\CurrentControlSet\Control\Lsa -Name LsaCfgFlags -ErrorAction SilentlyContinue
dism /online /enable-feature /featurename:VirtualMachinePlatform /all /norestart
dism /online /enable-feature /featurename:HypervisorPlatform /all /norestart
dism /online /enable-feature /featurename:Microsoft-Hyper-V-All /all /norestart
```

   - `VirtualMachinePlatform` es **obligatoria** para WSL2.
   - `HypervisorPlatform` y `Microsoft-Hyper-V-All` solo hacen falta si estaban activadas antes (no se pudo verificar sin permisos de administrador). Activarlas no afecta a WSL. Si `dism` dice que no existen, ignorarlo.
   - `LsaCfgFlags` (Credential Guard) y `RequirePlatformSecurityFeatures` no se revisaron antes de ponerlos en `0`, así que no se sabe su valor original. Los `Remove-ItemProperty` los borran para que Windows use su valor por defecto, que es lo más probable que hubiera. WSL2 no los necesita. **(agregado)**

3. Si en el 0.1 se desactivó **Configuración → Cuentas → Opciones de inicio de sesión → Inicio de sesión mejorado**, volver a activarlo. **(agregado)**
4. **Reiniciar el PC** con **Inicio → Reiniciar**.
5. Comprobar en PowerShell normal:

```powershell
(Get-CimInstance Win32_ComputerSystem).HypervisorPresent   # debe decir True
(Get-CimInstance -Namespace root\Microsoft\Windows\DeviceGuard -ClassName Win32_DeviceGuard).VirtualizationBasedSecurityStatus   # original: 2 (VBS corriendo)
wsl --status
wsl -l -v                                                  # las distros deben aparecer con VERSION 2
wsl                                                        # debe abrir la distro sin errores
```

Si `wsl` falla con un error de virtualización, ejecutar `wsl --update` y reiniciar otra vez. Si dice que falta la característica de WSL (en el diagnóstico del 0.1, `Microsoft-Windows-Subsystem-Linux` no aparecía habilitada), ejecutar como administrador `wsl --install --no-distribution` o `dism /online /enable-feature /featurename:Microsoft-Windows-Subsystem-Linux /all /norestart`, y reiniciar. **(agregado)**

Si se usaba huella o cara para entrar a Windows, puede pedir registrarlas de nuevo; el PIN sigue funcionando.

## Paso 1: crear la VM base en VirtualBox

**Crear la VM** (ventana principal de VirtualBox → **New**):

1. **VM Name:** `Hadoop-Master` (sin espacio al final). Si quedó mal escrito, se corrige con la VM apagada en **Settings → General → Nombre**. **(agregado)**
2. **ISO Image:** `C:\Users\esauf\Desktop\uni\macro-datos\PARCIAL\ubuntu-26.04.1-live-server-amd64.iso`. OS y versión se rellenan solos; dejarlos aunque digan 25.04.
3. Desmarcar **Proceed with Unattended Installation**.
4. Abrir **Specify virtual hardware**: **Base Memory** `2048 MB`, **Number of CPUs** `2`, **Use EFI** desmarcado.
5. Abrir **Specify virtual hard disk**: **Create a New Virtual Hard Disk**, **Disk Size** `25.00 GB`, `VDI`, **Reservar completamente** desmarcado.
6. **Terminar**.

**Configurar la red y la pantalla** (VM seleccionada → **Settings**):

7. **Red → Adaptador 1:** dejar en **NAT**.
8. **Adaptador 2:** **Conectar a:** `Adaptador sólo anfitrión`, **Nombre:** `VirtualBox Host-Only Ethernet Adapter`. Lo demás como viene.
9. **Pantalla → Pantalla:** **Controlador gráfico** `VMSVGA`, **Habilitar aceleración 3D** desmarcado, **Memoria de video** `128 MB` (la VM se crea con 16 MB). Luego **Aceptar**. **(agregado)**

**Instalar Ubuntu** (todo pasa en la ventana de la VM, **Hadoop-Master [Corriendo]**; clic dentro para usar el teclado, **Ctrl derecho** para salir):

10. **Start**.
11. **GNU GRUB:** Enter en **Try or Install Ubuntu Server**.
12. Esperar el instalador. Lo normal es que el menú de idioma aparezca en pocos minutos. **(agregado)** Si la pantalla se queda fija en `Loading essential drivers` (últimas líneas: `enp0s3: renamed from eth0` / `enp0s8: renamed from eth1`), está colgado. En la ejecución del 2026-10-17 estuvo más de 5 min sin cambios, con la VM usando un núcleo de CPU completo. Revisar en este orden:
    1. **¿Corre sobre Hyper-V?** Fue la causa el 2026-10-17. Hay tres señales: el ícono de tortuga verde en la barra de la VM, `(Get-CimInstance Win32_ComputerSystem).HypervisorPresent` en `True` (PowerShell), o la línea `HM: HMR3Init: Attempting fall back to NEM: VT-x is not available` en `C:\Users\esauf\VirtualBox VMs\<nombre de la VM>\Logs\VBox.log`. Si aparece cualquiera: apagar la VM, hacer el [Paso 0](#paso-0-desactivar-hyper-v-si-se-usa-wsl2-y-reactivarlo-al-terminar) (punto 0.1), reiniciar Windows y volver al paso 10. Mientras no se haga, ningún otro ajuste sirve.
    2. **RAM:** con 2048 MB el instalador live queda justo, porque carga el sistema en memoria. Apagar la VM y subir **Settings → Sistema → Memoria base** a `4096 MB` solo para la instalación; después se puede volver a `2048 MB`.
    3. **Pantalla:** comprobar que quedó como en el paso 9 (VMSVGA, sin 3D, 128 MB). Con poca memoria de video la consola puede congelarse aunque el instalador siga corriendo.
    4. Si nada de lo anterior aplica, reiniciar la VM (**Máquina → Reiniciar**). En un intento anterior del 2026-10-17 bastó con eso.
13. **Idioma:** `English` → Enter.
14. **Choose the type of installation:** `(X) Ubuntu Server`, third-party drivers sin marcar → **[ Done ]**.
15. **Network configuration:** no tocar → **[ Done ]**. Debe salir `enp0s3` con `DHCPv4 10.0.2.x` y `enp0s8` con `192.168.56.x` (o `disabled`); la IP fija se pone en el 2.2. Si `enp0s3` no tiene IP, revisar que el Adaptador 1 esté en NAT con **Cable conectado**. **(agregado)**
16. **Proxy configuration:** vacío → **[ Done ]**.
17. **Ubuntu archive mirror configuration:** esperar la prueba → **[ Done ]**.
18. **Guided storage configuration:** dejar `(X) Use an entire disk` (disco de 25 GB), **desmarcar** `Set up this disk as an LVM group` con la barra espaciadora (con LVM, `/` queda en 11,5 GB y el resto sin usar) → **[ Done ]**.
19. **Storage configuration** (resumen): `/` debe salir con ~23 GB → **[ Done ]** → en el aviso de confirmación, **[ Continue ]**.
20. **Profile configuration:** name `hadoop`, server's name `master`, username `hadoop`, contraseña (la misma en las 3 VM) → **[ Done ]**. 📷
21. **Upgrade to Ubuntu Pro:** **Skip for now** → **[ Continue ]**.
22. **SSH configuration:** marcar **Install OpenSSH server** con la barra espaciadora → **[ Done ]**. `Allow password authentication over SSH` queda marcado solo; dejarlo, porque `ssh-copy-id` lo necesita. No importar llaves. 📷 **(agregado)**
23. **Featured server snaps:** ninguno → **[ Done ]**.
24. Esperar y pulsar **[ Reboot Now ]**; si pide quitar el medio de instalación, Enter. 📷 La etapa `downloading and installing security updates` tarda varios minutos y parece quieta; es normal. Si tras reiniciar vuelve a salir el GRUB del instalador: **Dispositivos → Unidades ópticas → Eliminar disco** y **Máquina → Reiniciar**. **(agregado)**
    **Si la instalación se detiene** (en la ejecución del 2026-10-17: mensajes `watchdog: BUG: soft lockup` y más de 20 min en `copying metadata from /cdrom`), VirtualBox está corriendo sobre Hyper-V (ícono de tortuga verde). Solución:
    1. Apagar la VM: cerrar su ventana con la **X** y elegir apagar la máquina (**Máquina → Shut Down** no funciona con la VM colgada).
    2. Hacer el [Paso 0](#paso-0-desactivar-hyper-v-si-se-usa-wsl2-y-reactivarlo-al-terminar) (punto 0.1) y reiniciar Windows.
    3. Volver al paso 10. La tortuga ya no debe aparecer.
25. Iniciar sesión con `hadoop` y la contraseña. 📷 La contraseña no se ve al escribirla. Si da `Login incorrect`, revisar el usuario y que el teclado (inglés) no cambie los símbolos. **(agregado)**

📷 = captura para el informe.

**Resultado esperado:** la VM arranca y permite iniciar sesión como `hadoop@master`.

**Resultado (2026-10-17):** login correcto como `hadoop@master` (captura [`PARCIAL/imagenes/1_login!!!!!.png`](imagenes/1_login!!!!!.png)). `/` de 24,44 GB, uso de memoria 9 %, `enp0s3` con `10.0.2.15`. `enp0s8` no aparece en el resumen de login; se configura en el 2.2. **(agregado)**

## Paso 2: configurar el sistema (en master)

**(agregado)** Para copiar y pegar comandos, conectarse desde PowerShell de Windows en vez de usar la consola de la VM. Con la VM encendida, una sola vez:

```powershell
& "C:\Program Files\Oracle\VirtualBox\VBoxManage.exe" controlvm "Hadoop-Maste" natpf1 "ssh,tcp,127.0.0.1,2222,,22"   # reenvía el puerto 2222 de Windows al SSH de la VM
ssh -p 2222 hadoop@127.0.0.1   # la primera vez: yes; luego la contraseña de hadoop
```

Pegar con clic derecho; `exit` para salir. Después del 2.2 (IP fija) ya no hace falta el reenvío: `ssh hadoop@192.168.56.10` (o `.11`, `.12` para los workers). El portapapeles compartido de VirtualBox no sirve aquí: requiere escritorio gráfico.

### 2.1 Java 11 y SSH

**(agregado)** Si las teclas no coinciden (por ejemplo, `-` escribe `/`), el teclado quedó en inglés. Corregir con `sudo dpkg-reconfigure keyboard-configuration` → en la lista de marcas (Keyboard model) dejar `Generic 105-key PC` → `Spanish (Latin American)` (o `Spanish` si es de España) → valores por defecto, y luego `sudo setupcon`.

```bash
sudo apt update                                     # actualiza la lista de paquetes
apt-cache policy openjdk-11-jdk                     # si dice "Candidate: (none)", detenerse aquí
sudo apt install -y openjdk-11-jdk openssh-server   # instala Java 11 y el servidor SSH
sudo systemctl enable --now ssh                     # arranca SSH y lo deja activo al iniciar
sudo systemctl status ssh                           # debe decir active (running); salir con q
java -version                                       # debe mostrar 11.x
```

**Resultado esperado:** `ssh` aparece como `active (running)` y `java -version` muestra 11.x. Si `apt-cache policy` da `Candidate: (none)`, detenerse y decidir si se cambia a Ubuntu 24.04.

**Resultado (2026-10-17):** `Candidate: 11.0.32.1+1-1ubuntu1~26.04` (captura [`PARCIAL/imagenes/2_jdk.png`](imagenes/2_jdk.png)); Java 11 disponible. **(agregado)**

### 2.2 IP estática en la red solo anfitrión

```bash
ip a   # lista las interfaces de red y sus IP
```

Deben aparecer 3 interfaces: la NAT suele llamarse `enp0s3` (IP `10.0.x.x`) y la solo anfitrión `enp0s8` (sin IP o con `192.168.x.x`).

La guía edita `/etc/netplan/00-installer-config.yaml`. En versiones recientes de Ubuntu el archivo puede tener otro nombre (por ejemplo `50-cloud-init.yaml`); revisar con `ls /etc/netplan/` y editar el que exista. **(agregado)**

```bash
ls /etc/netplan/                        # muestra el nombre del archivo
sudo nano /etc/netplan/<archivo>.yaml   # reemplazar todo su contenido por el YAML de abajo
```

Guardar y salir de nano (aquí y en todos los pasos): **Ctrl+X → `Y` → Enter**, con el **Ctrl izquierdo** (el derecho es la tecla anfitrión de VirtualBox). Revisar con `cat <archivo>`. En `enp0s8` los tres valores van en `no`; si quedan en `true`, la interfaz toma además una IP por DHCP.

```yaml
network:
  version: 2
  ethernets:
    enp0s3:
      dhcp4: true
      dhcp6: true
    enp0s8:
      accept-ra: no
      dhcp4: no
      dhcp6: no
      addresses:
        - 192.168.56.10/24
```

> La guía escribe `dhco6` en el bloque de `enp0s8` del master; es un error de tipeo y debe ser `dhcp6`.

**(agregado)** En el master el archivo fue `00-installer-config.yaml`. Se reemplaza todo su contenido (las líneas `match`/`set-name` no hacen falta). Por SSH es más fácil pegar `sudo tee /etc/netplan/00-installer-config.yaml > /dev/null <<'EOF'`, el YAML y `EOF`, sin usar nano.

```bash
sudo netplan apply   # aplica la configuración; no debe mostrar errores
ip a show enp0s8     # debe decir inet 192.168.56.10/24
```

**Resultado esperado:** `netplan apply` no muestra errores.

**Resultado (2026-10-17):** `enp0s8` con `inet 192.168.56.10/24`, `valid_lft forever` (fija) y sin la `.103` del DHCP (captura [`imagenes/2_2_IP_estatica_corroborando.png`](../imagenes/2_2_IP_estatica_corroborando.png)). **(agregado)**

### 2.3 Nombres de los nodos

1. Abrir el archivo:

```bash
sudo nano /etc/hosts   # tabla de nombres → IP del nodo
```

2. En la segunda línea, poner `#` delante de `127.0.1.1 master`, para que el inicio quede:

```
127.0.0.1 localhost
# 127.0.1.1 master
```

3. Bajar al final del archivo y agregar:

```
192.168.56.10 master
192.168.56.11 worker1
192.168.56.12 worker2
```

4. Guardar y salir: **Ctrl+X → `Y` → Enter**. **(agregado)**
5. Comprobar con `cat /etc/hosts`. 📷 **(agregado)**

> Si `127.0.1.1 master` queda activa, el NameNode puede escuchar solo en local y los workers no se conectan. **(agregado: el motivo; la guía solo dice "para evitar problemas más adelante")**

### 2.4 Reiniciar

```bash
sudo reboot   # reinicia la VM; volver a entrar con hadoop
ip a          # enp0s8 debe seguir en 192.168.56.10
```

**Resultado esperado:** `enp0s8` muestra `192.168.56.10`.

**Resultado (2026-10-17):** tras reiniciar, `enp0s8` sigue en `192.168.56.10/24` (`forever`) y `enp0s3` en `10.0.2.15` (capturas [`PARCIAL/imagenes/2_4_reboot.png`](imagenes/2_4_reboot.png) y [`2_4_reboot_resultado.png`](imagenes/2_4_reboot_resultado.png)). **(agregado)**

## Paso 3: clonar los workers

1. Apagar Hadoop-Master con `sudo poweroff` **(agregado)**. Si se subió la RAM a 4096 MB para instalar (paso 12), volver a `2048 MB` en **Settings → Sistema → Memoria base** antes de clonar, para que los clones la hereden. **(agregado)** Clic derecho → **Clonar**: nombre **Hadoop-Worker1**, tipo **Clonación completa**, política de MAC **Generar nuevas direcciones MAC para todos los adaptadores de red**. Arrancar el clon.
2. En worker1, cambiar el hostname:
   ```bash
   sudo hostnamectl set-hostname worker1   # el prompt cambia tras reiniciar
   ```
3. Cambiar la IP de `enp0s8` a `192.168.56.11/24` en el mismo archivo de netplan y aplicar:
   ```bash
   sudo nano /etc/netplan/<archivo>.yaml   # cambiar solo .10 por .11
   sudo netplan apply                      # aplica la nueva IP
   ```
4. Reiniciar con `sudo reboot`. El prompt debe mostrar `hadoop@worker1` e `ip a` la IP nueva.
5. Con **las dos VM encendidas** (worker1 en estado "Guardado" no responde), desde master comprobar la conexión: **(agregado)**
   ```bash
   ping -c 3 worker1   # 3 respuestas, 0% packet loss
   ```
6. Repetir 1–5 para **Hadoop-Worker2** con hostname `worker2` e IP `192.168.56.12`. Según la guía, cada worker debe tener una IP estática única que figure correctamente en `/etc/hosts`; las entradas ya vienen clonadas desde master, pero conviene comprobarlo con `cat /etc/hosts` en cada nodo.

**Worker2, paso a paso** **(agregado)**:

1. Apagar Hadoop-Master con `sudo poweroff`. Clic derecho → **Clonar**: nombre **Hadoop-Worker2**, tipo **Clonación completa**, política de MAC **Generar nuevas direcciones MAC para todos los adaptadores de red**.
2. Arrancar Hadoop-Worker2 e iniciar sesión con `hadoop`.
3. Cambiar el hostname:
   ```bash
   sudo hostnamectl set-hostname worker2   # el prompt cambia tras reiniciar
   ```
4. Editar netplan y poner `192.168.56.12/24` en `addresses` de `enp0s8` (lo demás igual); guardar con **Ctrl+X → `Y` → Enter**:
   ```bash
   sudo nano /etc/netplan/00-installer-config.yaml   # cambiar solo la IP por .12
   sudo netplan apply                                # aplica la nueva IP
   ```
5. Reiniciar con `sudo reboot`. Comprobar: prompt `hadoop@worker2`, `ip a show enp0s8` con `192.168.56.12/24` y `cat /etc/hosts` con las 3 entradas. 📷
6. Encender las 3 VM y, desde master:
   ```bash
   ping -c 3 worker1   # 0% packet loss
   ping -c 3 worker2   # 0% packet loss
   ```
   📷

**Resultado esperado:** `ping -c 3 worker1` y `ping -c 3 worker2` responden desde master.

**Resultado (2026-10-17):** 0 % de pérdida de master a worker1 y worker2, y de cada worker a master (captura [`PARCIAL/imagenes/3_verificando_conexion_ip_master_worker1_worker2.png`](imagenes/3_verificando_conexion_ip_master_worker1_worker2.png)).

## Paso 4: SSH sin contraseña (en master)

```bash
mkdir -p ~/.ssh        # carpeta de llaves SSH
chmod 700 ~/.ssh       # solo el usuario puede entrar
nano ~/.ssh/config     # pegar el bloque de abajo
```

```
Host master
  HostName 192.168.56.10
  User hadoop
  StrictHostKeyChecking accept-new

Host worker1
  HostName 192.168.56.11
  User hadoop
  StrictHostKeyChecking accept-new

Host worker2
  HostName 192.168.56.12
  User hadoop
  StrictHostKeyChecking accept-new
```

```bash
chmod 600 ~/.ssh/config            # ssh rechaza el archivo si otros pueden leerlo
ssh-keygen -t rsa -P ""            # crea la llave; "Enter file in which to save the key": Enter
ssh-copy-id master                 # pide la contraseña de hadoop una vez
ssh-copy-id worker1                # ídem
ssh-copy-id worker2                # ídem
ssh worker1 'hostname && whoami'   # ya no debe pedir contraseña
```

Con las 3 VM encendidas.

**Resultado esperado:** el último comando imprime `worker1` y `hadoop` sin pedir contraseña.

**Resultado (2026-10-17):** `Number of key(s) added: 1` en los 3 nodos y `ssh worker1|worker2|master 'hostname && whoami'` → nombre del nodo y `hadoop`, sin contraseña (captura [`PARCIAL/imagenes/4_configurar_ssh_copy_id.png`](imagenes/4_configurar_ssh_copy_id.png)). `Permission denied, please try again` = contraseña mal escrita; se vuelve a escribir.

## Paso 5: descargar Hadoop 3.3.6 (en master)

```bash
cd ~                         # ir a /home/hadoop (cada comando en su propia línea)
wget https://archive.apache.org/dist/hadoop/common/hadoop-3.3.6/hadoop-3.3.6.tar.gz   # 696 MB; termina con "saved"
tar -xvzf hadoop-3.3.6.tar.gz   # descomprime; lista muchos archivos
mv hadoop-3.3.6 hadoop       # renombra la carpeta a ~/hadoop
rm hadoop-3.3.6.tar.gz       # borra el comprimido
ls ~/hadoop                  # debe mostrar bin, etc, sbin, share...
```

> En el PDF la URL aparece cortada como `hadoop- 3.3.6.tar.gz`; va sin espacio.

**Resultado esperado:** existe `~/hadoop/`.

## Paso 6: configurar Hadoop (en master)

### 6.1 Variables de entorno

Agregar al final de `~/.bashrc` (la guía usa `sudo nano`; no hace falta `sudo` para un archivo del propio usuario **(agregado)**):

Para pegar las líneas, conectarse a master por SSH desde PowerShell de Windows:

```powershell
ssh hadoop@192.168.56.10   # contraseña de hadoop; pegar con clic derecho; salir con exit
```

```bash
nano ~/.bashrc   # agregar las lineas que siguen
export HADOOP_HOME=$HOME/hadoop
export HADOOP_INSTALL=$HADOOP_HOME
export HADOOP_MAPRED_HOME=$HADOOP_HOME
export HADOOP_COMMON_HOME=$HADOOP_HOME
export HADOOP_HDFS_HOME=$HADOOP_HOME
export YARN_HOME=$HADOOP_HOME
export HADOOP_COMMON_LIB_NATIVE_DIR=$HADOOP_HOME/lib/native
export PATH=$PATH:$HADOOP_HOME/sbin:$HADOOP_HOME/bin
export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
```

```bash
source ~/.bashrc   # carga las variables en la sesión actual
echo $HADOOP_HOME  # debe mostrar /home/hadoop/hadoop
```

### 6.2 `~/hadoop/etc/hadoop/hadoop-env.sh`

Hadoop arranca los servicios de los workers por SSH, y esas sesiones no leen `~/.bashrc`; por eso la ruta de Java va también en este archivo.

1. Abrir el archivo:

```bash
nano ~/hadoop/etc/hadoop/hadoop-env.sh   # Ctrl+W y escribir export JAVA_HOME= para buscar la línea
```

2. Debajo de la línea `# export JAVA_HOME=` (dejarla como está), agregar:

```
export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
```

3. Guardar y salir: **Ctrl+X → `Y` → Enter**.
4. Comprobar:

```bash
grep '^export JAVA_HOME' ~/hadoop/etc/hadoop/hadoop-env.sh   # debe mostrar la línea agregada; ^ = AltGr + { y luego Espacio
grep -n JAVA_HOME ~/hadoop/etc/hadoop/hadoop-env.sh          # alternativa sin ^: la tuya es la que no empieza con #
```

### 6.3 `~/hadoop/etc/hadoop/core-site.xml`

Dice dónde está HDFS: en `master`, puerto 9000.

```bash
nano ~/hadoop/etc/hadoop/core-site.xml   # Ctrl+Fin; borrar <configuration></configuration> con Ctrl+K; pegar el bloque; Ctrl+X → Y → Enter
```

```xml
<configuration>
  <property>
    <name>fs.defaultFS</name>
    <value>hdfs://master:9000</value>
  </property>
</configuration>
```

### 6.4 `~/hadoop/etc/hadoop/hdfs-site.xml`

Cada bloque se guarda en 2 nodos y se indican las carpetas de datos del NameNode y los DataNodes.

```bash
nano ~/hadoop/etc/hadoop/hdfs-site.xml   # igual que 6.3
```

```xml
<configuration>
  <property>
    <name>dfs.replication</name>
    <value>2</value>
  </property>
  <property>
    <name>dfs.namenode.name.dir</name>
    <value>file:///home/hadoop/hadoop_data/hdfs/namenode</value>
  </property>
  <property>
    <name>dfs.datanode.data.dir</name>
    <value>file:///home/hadoop/hadoop_data/hdfs/datanode</value>
  </property>
</configuration>
```

### 6.5 `~/hadoop/etc/hadoop/mapred-site.xml`

Los trabajos MapReduce corren sobre YARN y saben dónde están las librerías de Hadoop.

```bash
nano ~/hadoop/etc/hadoop/mapred-site.xml   # igual que 6.3
```

> En el PDF el valor de `mapreduce.application.classpath` sale partido en dos líneas (`.../share/hadoop/mapred` + `uce/lib/*`); es un solo valor.

```xml
<configuration>
  <property>
    <name>mapreduce.framework.name</name>
    <value>yarn</value>
  </property>
  <property>
    <name>mapreduce.application.classpath</name>
    <value>$HADOOP_HOME/share/hadoop/mapreduce/*:$HADOOP_HOME/share/hadoop/mapreduce/lib/*</value>
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

### 6.6 `~/hadoop/etc/hadoop/yarn-site.xml`

El ResourceManager (YARN) corre en `master` y los NodeManagers pueden hacer el shuffle de MapReduce.

```bash
nano ~/hadoop/etc/hadoop/yarn-site.xml   # igual que 6.3
```

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

### 6.7 `~/hadoop/etc/hadoop/workers`

Lista de nodos donde `start-dfs.sh` y `start-yarn.sh` arrancan DataNode y NodeManager.

```bash
nano ~/hadoop/etc/hadoop/workers   # borrar localhost (Ctrl+K) y escribir las 2 líneas de abajo
```

```
worker1
worker2
```

```bash
cat ~/hadoop/etc/hadoop/workers    # debe mostrar solo worker1 y worker2
```

## Paso 7: copiar Hadoop a los workers (desde master)

1. Crear las carpetas de datos en master y en cada worker:
   ```bash
   mkdir -p ~/hadoop_data/hdfs/namenode                # en master
   mkdir -p ~/hadoop_data/hdfs/datanode                # en master
   ssh worker1 'mkdir -p ~/hadoop_data/hdfs/datanode'  # en worker1, desde master
   ssh worker1 'mkdir -p ~/hadoop_data/hdfs/namenode'
   ssh worker2 'mkdir -p ~/hadoop_data/hdfs/datanode'  # en worker2, desde master
   ssh worker2 'mkdir -p ~/hadoop_data/hdfs/namenode'
   ssh worker1 'ls ~/hadoop_data/hdfs'; ssh worker2 'ls ~/hadoop_data/hdfs'   # cada uno: datanode namenode
   ```
2. Enviar la carpeta de Hadoop:
   ```bash
   scp -r ~/hadoop worker1:~/   # copia ~/hadoop a worker1; tarda unos minutos
   scp -r ~/hadoop worker2:~/   # ídem a worker2
   ```
3. Enviar `.bashrc` y cargarlo en cada worker con `source ~/.bashrc`:
   ```bash
   scp ~/.bashrc worker1:~/   # copia las variables de Hadoop
   scp ~/.bashrc worker2:~/
   ```

**Resultado esperado:** comprobar desde master:

```bash
find ~/hadoop -type f | wc -l                     # cantidad de archivos en master
ssh worker1 'find ~/hadoop -type f | wc -l'       # igual o unos pocos más (scp copia los enlaces como archivos); 2026-10-18: 20029 master, 20033 workers
ssh worker2 'find ~/hadoop -type f | wc -l'       # ídem
ssh worker1 'ls ~/hadoop_data/hdfs; grep HADOOP_HOME= ~/.bashrc'   # datanode namenode y export HADOOP_HOME=$HOME/hadoop
ssh worker2 'ls ~/hadoop_data/hdfs; grep HADOOP_HOME= ~/.bashrc'   # ídem
```

## Paso 8: iniciar Hadoop (en master)

```bash
hdfs namenode -format     # formatea HDFS; solo la primera vez
start-dfs.sh              # arranca NameNode y DataNodes
start-yarn.sh             # arranca ResourceManager y NodeManagers
jps                       # procesos Java en master
ssh worker1 jps           # procesos Java en worker1
ssh worker2 jps           # procesos Java en worker2
hdfs dfsadmin -report     # debe decir Live datanodes (2)
```

**Resultado esperado:**
- `jps` en master: `NameNode`, `SecondaryNameNode`, `ResourceManager`.
- `jps` en cada worker: `DataNode`, `NodeManager`.
- `hdfs dfsadmin -report`: `Live datanodes (2)`. **(agregado)**
- En Chrome o Edge de Windows (con las VM encendidas; Windows llega a la red `192.168.56.x`), escribir en la barra de direcciones `http://192.168.56.10:9870` → pestaña **Datanodes**: worker1 y worker2 en `In service`; y `http://192.168.56.10:8088` → menú izquierdo **Nodes**: 2 nodos `RUNNING`. Son las capturas de "nodos conectados" que pide el informe. 📷 **(agregado: los puertos salen de la guía alternativa, sección 14)**

**Resultado (2026-10-18):** `hdfs dfsadmin -report` → `Live datanodes (2)`: worker1 (192.168.56.11) y worker2 (192.168.56.12), `Decommission Status : Normal`, ~14 GB libres de DFS cada uno. Navegador: `:9870` Datanodes con los 2 nodos en operación (versión 3.3.6) y `:8088` Nodes con `Active Nodes 2`, ambos `RUNNING` (capturas [`8_iniciar_hadoop.png`](imagenes/8_iniciar_hadoop.png), [`8_dfsadmin_report.png`](imagenes/8_dfsadmin_report.png), [`8_dfsadmin_report_verificando_en_navegador.png`](imagenes/8_dfsadmin_report_verificando_en_navegador.png) y [`8_dfsadmin_report_verificando_en_navegador_hadoop.png`](imagenes/8_dfsadmin_report_verificando_en_navegador_hadoop.png)). El `SHUTDOWN_MSG` al final de `hdfs namenode -format` es normal.

## Paso 9: prueba con WordCount (en master)

```bash
yes "Este es un archivo de prueba para el curso de Análisis de Macrodatos" | head -n 10000 > datos_prueba.txt   # archivo de 10000 líneas iguales
hdfs dfs -mkdir -p /input                # carpeta en HDFS; el WARN NativeCodeLoader es normal
hdfs dfs -put datos_prueba.txt /input/   # sube el archivo a HDFS
hdfs dfs -ls /input/                     # debe listar datos_prueba.txt
hadoop jar $HADOOP_HOME/share/hadoop/mapreduce/hadoop-mapreduce-examples-3.3.6.jar wordcount /input/datos_prueba.txt /output   # lanza el job; línea larga: pegarla por SSH (escrita a medias da "JAR does not exist")
hdfs dfs -cat /output/part-r-00000       # muestra el conteo de palabras
```

> En el PDF el nombre del jar aparece cortado como `hadoop-mapreduce-examples- 3.3.6.jar`; va sin espacio.

**Resultado esperado:** el job termina y `part-r-00000` muestra cada palabra con el conteo 10000, salvo `de` con 30000 (aparece 3 veces en la frase).

**Resultado (2026-10-18):** con la frase `...curso de ANALISI DE MACRODATOS ` el job leyó 690000 bytes y `part-r-00000` dio `de` 20000 y las demás palabras (incluidas `DE`, `ANALISI`, `MACRODATOS`) 10000 cada una; WordCount distingue mayúsculas (captura [`PARCIAL/imagenes/9_MAPREDUCE.png`](imagenes/9_MAPREDUCE.png)).

# DESARROLLO DEL EXAMEN PARCIAL

### Tanto en el informe como en la presentación

- **Referenciar la base de datos usada:** Superintendencia de Transporte Terrestre de Personas, Carga y Mercancías – SUTRAN. (2021). *Reportes preliminares de Accidentes – Centro de Gestión y Monitoreo* [Conjunto de datos]. Plataforma Nacional de Datos Abiertos. https://www.datosabiertos.gob.pe/dataset/reportes-preliminares-de-accidentes-centro-de-gesti%C3%B3n-y-monitoreo (licencia Open Data Commons Attribution). Archivo: `dataSSE_17122021_c.csv`, 3 832 accidentes de 2020-01-01 a 2021-11-30, 14 campos con `LATITUD` y `LONGITUD`.
- **Fuente y fecha de publicación:** SUTRAN, Subgerencia de Supervisión Electrónica; publicado en la PNDA el 2021-12-17, modificado el 2022-01-28, fecha de corte 2021-11-30, frecuencia declarada trimestral. Categoría PNDA: Transporte y movilidad.
- **Investigaciones anteriores y artículos que usaron el dataset** (el enunciado pide los dos; es lo mismo): tesis o artículos que analizaron **estos reportes de accidentes de SUTRAN** (en su sección de datos o metodología dicen "SUTRAN", "Centro de Gestión y Monitoreo" o citan la PNDA). Por cada uno anotar autor, año, título, enlace y qué hizo con los datos. Búsqueda del 2026-10-18: no se encontró ningún trabajo que use este archivo; antecedentes cercanos:
  1. Bazán Gutiérrez, E. D. (2022). *Gestión en seguridad vial y la cultura en prevención de accidentes de tránsito en una Institución de la SUTRAN, 2021* [Tesis de maestría, Universidad César Vallejo, Trujillo]. Resumen: estudio correlacional con encuesta a 61 trabajadores de SUTRAN; gestión de seguridad vial deficiente en 71 % y cultura de prevención en 69 %, con correlación alta (Rho de Spearman 0.869). No usa los reportes de accidentes. https://repositorio.ucv.edu.pe/bitstream/handle/20.500.12692/102915/Bazan_GED-SD.pdf?sequence=1
  2. Villena Carpio, R. A. (2024). *Mejora del diseño de carreteras pavimentadas en la red vial nacional con bermas-cunetas para el tránsito vehicular y drenaje longitudinal* [Tesis de maestría, Universidad Nacional Federico Villarreal]. Resumen: propone bermas-cunetas en el diseño de carreteras de la red vial nacional para mejorar la seguridad vial y el drenaje; estudio descriptivo con revisión documental, observación de campo y estadísticas de incidentes de SUTRAN (el registro no indica los años). https://hdl.handle.net/20.500.13084/9762
 
  Otros lugares para buscar más:  [Google Scholar](https://scholar.google.com/scholar?q=%22SUTRAN%22+%22Centro+de+Gesti%C3%B3n+y+Monitoreo%22+accidentes), [ALICIA (CONCYTEC)](https://alicia.concytec.gob.pe/vufind/Search/Results?lookfor=SUTRAN+accidentes&type=AllFields) y los repositorios de tesis [UNI](https://repositorio.uni.edu.pe/), [PUCP](https://tesis.pucp.edu.pe/) y [UNMSM](https://cybertesis.unmsm.edu.pe/) con `"SUTRAN" "Centro de Gestión y Monitoreo" accidentes` y `datos abiertos SUTRAN accidentes 2020 2021`. Si ninguno usa este archivo, el informe lo dice y cita estudios de accidentes en carreteras del Perú con otros datos de SUTRAN.

### 2.1 Implementar consultas de complejidad en Hadoop

Estilo de código: API clásica (`JobConf`, `JobClient.runJob`), un paquete por consulta con `Driver`, `Mapper*` y `Reducer*`, como `MACRO-DATOS/source_Packages/13_PaisProductoMayorFacturacion` (2 jobs encadenados con `args[1] + "_temp"`). "MapReduce triple" = 3 jobs encadenados igual (la salida de uno es la entrada del siguiente). Criterio adoptado el 2026-10-18: es el mismo patrón de encadenamiento de la consulta 13 y de las cadenas de la PC1 (`5a1`→`5a2`, `6a1`→`6a2`); no hay código del curso con un "triple" para contrastar.

#### 2.1.1 Preparar el CSV (Windows, una sola vez)

Carpeta: `PARCIAL/dataset_accidentes_cgm/`. Ya hecho: `dataSSE_17122021_c_utf8.csv` se generó desde el original con (PowerShell; no usar `>`, en PowerShell 5.1 escribe UTF-16):
```powershell
cd C:\Users\esauf\Desktop\uni\macro-datos\PARCIAL\dataset_accidentes_cgm   # carpeta del CSV
$texto = [IO.File]::ReadAllText("$PWD\dataSSE_17122021_c.csv", [Text.Encoding]::GetEncoding('ISO-8859-1'))   # lee el original en ISO-8859-1
$texto = $texto -replace "`r", ""                                          # CRLF → LF (sin \r al final de DISTRITO)
[IO.File]::WriteAllText("$PWD\dataSSE_17122021_c_utf8.csv", $texto, (New-Object Text.UTF8Encoding $false))   # guarda en UTF-8 sin BOM
Get-Content dataSSE_17122021_c_utf8.csv -Encoding UTF8 -TotalCount 3       # cabecera y 2 filas con tildes bien 📷
(Get-Content dataSSE_17122021_c_utf8.csv -Raw) -match "`r"                 # False = ya no hay CRLF 📷
Select-String -Path dataSSE_17122021_c_utf8.csv -Pattern 'Ñ' -Encoding UTF8 | Select-Object -First 2   # CAÑETE bien escrito (206 filas con Ñ) 📷
Get-FileHash dataSSE_17122021_c_utf8.csv                                   # SHA-256 empieza con 1EA9794452821CC7
```
Equivalente en Git Bash: `iconv -f ISO-8859-1 -t UTF-8 dataSSE_17122021_c.csv | tr -d '\r' > dataSSE_17122021_c_utf8.csv` (mismo resultado).
Comprobado el 2026-10-18 (captura [`PARCIAL/imagenes/Seccion_2_1_paso_a_preparacion.png`](imagenes/Seccion_2_1_paso_a_preparacion.png)): repetir el comando da un archivo idéntico (mismo SHA-256); 3 833 líneas (cabecera + 3 832 accidentes). La cabecera la salta el mapper (`key.get() == 0`, como en la PC1).
- Filas sin `PROGRESIVA` (80) o `CODIGO_VIA` (31): se descartan solo en las consultas que usan ese campo.

#### 2.1.2 Crear el proyecto NetBeans `PARCIAL` (una sola vez)

Igual que la PC1 (`GUIA_EJECUCION.md`, sección "Ejercicio SalesCountry"):

1. File → New Project → Categories: Java → Projects: Java Application.
2. Project Name: `PARCIAL`; dejar tildado "Create Main Class"; Finish → se crea en `C:\Users\esauf\Documents\NetBeansProjects\PARCIAL\`.
3. Clic derecho en el proyecto → Properties → Libraries → Compile → Add JAR/Folder, solo estos 4 de `C:\Hadoop3\`:
   - `share\hadoop\common\hadoop-common-3.3.0.jar`
   - `share\hadoop\mapreduce\hadoop-mapreduce-client-core-3.3.0.jar`
   - `share\hadoop\mapreduce\hadoop-mapreduce-client-common-3.3.0.jar`
   - `share\hadoop\mapreduce\hadoop-mapreduce-client-jobclient-3.3.0.jar`
4. Properties → Sources → Source/Binary Format: `JDK 8` (`javac.target=1.8`; así el JAR corre en el Java 11 del clúster).

#### 2.1.3 Consulta 1: `SeveridadPorVia` (MapReduce triple, decimales)

Pregunta: ¿qué vía tiene el mayor índice de severidad en cada departamento, y cómo quedan los departamentos ordenados por ese índice?

0. Crear los `.java` en NetBeans:
   - a) Proyecto `PARCIAL` → clic derecho en Source Packages → New → Java Package → Package Name: `SeveridadPorVia` (exacto: es el `package` de los `.java`) → Finish.
   - b) Clic derecho en el paquete `SeveridadPorVia` → New → Java Class, 7 veces: `Driver`, `Mapper`, `Reducer`, `MapperMaximo`, `ReducerMaximo`, `MapperRanking`, `ReducerRanking`.
   - c) En cada clase, borrar el esqueleto y pegar el contenido del archivo del mismo nombre de `PARCIAL/source_Packages/1_SeveridadPorVia/`.
   - Ya hecho el 2026-10-18 copiando los archivos a `src\SeveridadPorVia\`; si el paquete ya aparece en NetBeans (tras Refresh), saltar este paso.
1. Qué hace cada job:
   - Job 1 (`Mapper` / `Reducer`): clave `Departamento|Vía` → accidentes, fallecidos, heridos e índice `(3·fallecidos + heridos) / accidentes` (4 decimales). Descarta las 31 filas sin `CODIGO_VIA`.
   - Job 2 (`MapperMaximo` / `ReducerMaximo`): clave `Departamento` → la vía con mayor índice, solo vías con ≥ 5 accidentes; en empate, la de más accidentes.
   - Job 3 (`MapperRanking` / `ReducerRanking`): un solo reducer → ranking de departamentos de mayor a menor índice.
   - `Driver`: encadena los 3 jobs (`<salida>_temp1` → `<salida>_temp2` → `<salida>`) y borra los temporales de corridas anteriores.
2. Código: `PARCIAL/source_Packages/1_SeveridadPorVia/` (7 clases, paquete `SeveridadPorVia`), copiado a `C:\Users\esauf\Documents\NetBeansProjects\PARCIAL\src\SeveridadPorVia\`. Verificación previa (la hizo Claude el 2026-10-18 antes de entregar el código; no es un paso a ejecutar): compilación de prueba con `javac` contra las 4 librerías de 2.1.2, en una carpeta temporal, sin errores.
3. Compilar en NetBeans: clic derecho en el proyecto `PARCIAL` → Refresh → clic derecho → Build. Comprobar en PowerShell:
   ```powershell
   jar tf C:\Users\esauf\Documents\NetBeansProjects\PARCIAL\dist\PARCIAL.jar | Select-String SeveridadPorVia   # deben salir las 7 clases (+ ReducerRanking$1) 📷
   ```
   Hecho el 2026-10-18: las 8 `.class` dentro de `PARCIAL.jar` (captura [`PARCIAL/imagenes/Desarrollo_parcial_2_1_2_1_3_obtener_PARCIAL_JAR.png`](imagenes/Desarrollo_parcial_2_1_2_1_3_obtener_PARCIAL_JAR.png)).
4. Copiar a master (PowerShell de Windows; pide la contraseña de hadoop):
   ```powershell
   scp C:\Users\esauf\Desktop\uni\macro-datos\PARCIAL\dataset_accidentes_cgm\dataSSE_17122021_c_utf8.csv hadoop@192.168.56.10:~/   # CSV, solo la primera vez
   scp C:\Users\esauf\Documents\NetBeansProjects\PARCIAL\dist\PARCIAL.jar hadoop@192.168.56.10:~/   # JAR, después de cada Build
   ```
5. Ejecutar en el clúster (en master):
   ```bash
   hdfs dfs -mkdir -p /parcial_input                                         # crea la carpeta en HDFS (el NameNode de master solo registra el nombre); solo la primera vez
   hdfs dfs -put -f ~/dataSSE_17122021_c_utf8.csv /parcial_input/            # el CSV se guarda en los DataNodes de worker1 y worker2 (replicación 2); solo la primera vez
   hdfs dfs -rm -r -f /parcial_output/SeveridadPorVia                        # borra la salida anterior (si existe, el job falla)
   time java -cp "$HOME/PARCIAL.jar:$(hadoop classpath)" SeveridadPorVia.Driver /parcial_input /parcial_output/SeveridadPorVia   # master envía los 3 jobs a YARN y espera; las tareas corren en los workers; "real" = tiempo total 📷
   hdfs dfs -cat /parcial_output/SeveridadPorVia/part-00000                  # lee el ranking que el job 3 dejó en HDFS 📷
   ```
   Equivalente con receta de `parcial.sh` (`run` = estos 5 comandos: sube el CSV si falta, borra la salida, ejecuta, mide el tiempo y muestra el resultado):
   ```powershell
   scp C:\Users\esauf\Desktop\uni\macro-datos\PARCIAL\parcial.sh hadoop@192.168.56.10:~/   # en Windows: copiar el script a master (repetir si cambia)
   ```
   ```bash
   chmod +x ~/parcial.sh                    # en master, solo la primera vez
   ./parcial.sh status                      # jps en los 3 nodos y "Live datanodes (2)"
   ./parcial.sh run SeveridadPorVia         # "Tiempo total: ... s" + ranking; también lo guarda en ~/resultados/SeveridadPorVia_cluster.txt 📷
   ```
   Cómo funciona en conjunto:
   - a) Datos: `-put` divide el CSV en bloques de 128 MB y guarda cada bloque en 2 DataNodes (worker1 y worker2). El CSV pesa 434 KB, así que es 1 solo bloque con una copia en cada worker. Master (NameNode) solo guarda qué bloque está en qué nodo.
   - b) Envío: `java -cp` corre el `Driver` en master como cliente. `JobClient.runJob` sube `PARCIAL.jar` a HDFS y entrega el job 1 al ResourceManager de master; espera a que termine y recién entonces envía el job 2, y luego el 3.
   - c) Ejecución: el ResourceManager pide contenedores a los NodeManagers de worker1 y worker2 (master no ejecuta tareas: no está en `workers`). En un contenedor arranca el ApplicationMaster del job, que lanza los map y el reduce en contenedores de los workers.
   - d) Map y reduce: la API clásica (`mapred`) pide 2 maps por defecto, así que cada job divide su entrada en 2 partes (`number of splits:2`) y lanza 2 map en paralelo, cada uno leyendo su parte desde un DataNode local (`Data-local map tasks=2`). Luego hay 1 reduce (valor por defecto), que junta las salidas de los 2 map (`Shuffled Maps =2`) y escribe en HDFS (`_temp1`, `_temp2` y la salida final), que es la entrada del job siguiente.
   - e) Lo que se observó el 2026-10-18 (capturas [`..._inciso_5_..._MASTER_TIME.png`](imagenes/Desarrollo_parcial_2_1_2_1_3_inciso_5_ejecutar_en_el_cluster_MASTER_TIME.png) y [`_TIME_2.png`](imagenes/Desarrollo_parcial_2_1_2_1_3_inciso_5_ejecutar_en_el_cluster_MASTER_TIME_2.png), y API de YARN `http://192.168.56.10:8088/ws/v1/cluster/apps`):
     - Los 3 jobs terminaron `SUCCEEDED`, cada uno con `Launched map tasks=2` y `Launched reduce tasks=1`.
     - ApplicationMaster: worker2 (job 1, 94.1 s), worker1 (job 2, 80.9 s), worker2 (job 3, 75.8 s); YARN reparte entre los dos workers.
     - Tiempo total (`time`): `real 4m28.022s` (268 s). De eso, el cálculo real es poco (job 3: `CPU time spent 9780 ms`); casi todo es arrancar contenedores y escribir/leer HDFS entre jobs, porque el CSV es pequeño.
   Resultado (2026-10-18): ranking de 24 departamentos, igual al esperado del paso 7 (captura [`PARCIAL/imagenes/Desarrollo_parcial_2_1_2_1_3_inciso_5_ejecutar_en_el_cluster_MASTER_RANKING.png`](imagenes/Desarrollo_parcial_2_1_2_1_3_inciso_5_ejecutar_en_el_cluster_MASTER_RANKING.png)); `java -cp` funciona en el clúster.
   Se usa `java -cp` y no `hadoop jar`: NetBeans graba `Main-Class: parcial.PARCIAL` en el manifiesto (comprobado en `PC1.jar`: `pc1.PC1`) y `hadoop jar` ejecuta esa clase en vez del `Driver`; es el "bug" de `GUIA_EJECUCION.md` y pasa también en Linux.
6. Ejecutar en el nodo único (PowerShell **como administrador**, como en `GUIA_EJECUCION.md`):
   ```powershell
   cd C:\Users\esauf\Desktop\uni\macro-datos                                 # carpeta de hadoop.ps1
   ([Security.Principal.WindowsPrincipal][Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)   # debe decir True (no usar 'Administrators': en Windows en español da False siempre); si no, el NodeManager queda UNHEALTHY ("nm-local-dir ... not writable") y el job se queda en ACCEPTED (pasó el 2026-10-18)
   .\hadoop.ps1 start                                                        # arranca HDFS y YARN de C:\Hadoop3 (abre ventanas; no cerrarlas)
   .\hadoop.ps1 status                                                       # puertos 9870, 8088, 8042 y 19000 en OK antes de seguir
   # --- desde aquí hasta el final del bloque = .\parcial.ps1 run SeveridadPorVia (ver abajo) ---
   hadoop fs -mkdir -p /parcial_input                                        # solo la primera vez
   hadoop fs -put -f C:\Users\esauf\Desktop\uni\macro-datos\PARCIAL\dataset_accidentes_cgm\dataSSE_17122021_c_utf8.csv /parcial_input/   # solo la primera vez
   hadoop fs -rm -r -f /parcial_output/SeveridadPorVia                       # borra la salida anterior
   $cp = (hadoop classpath)                                                  # librerías de Hadoop
   Measure-Command { java -cp "C:\Users\esauf\Documents\NetBeansProjects\PARCIAL\dist\PARCIAL.jar;$cp" SeveridadPorVia.Driver /parcial_input /parcial_output/SeveridadPorVia | Out-Host }   # TotalSeconds = tiempo 📷
   hadoop fs -cat /parcial_output/SeveridadPorVia/part-00000                 # ranking 📷
   ```
   Equivalente con recetas de `parcial.ps1` (start y status = las 4 primeras líneas del bloque; run = los comandos marcados; run hace lo mismo: comprueba administrador, sube el CSV si falta, borra la salida, mide el tiempo y muestra el resultado):
   ```powershell
   cd C:\Users\esauf\Desktop\uni\macro-datos\PARCIAL                         # carpeta de parcial.ps1
   .\parcial.ps1 start                                                       # = comprobar administrador + ..\hadoop.ps1 start (no arranca si no es administrador)
   .\parcial.ps1 status                                                      # = ..\hadoop.ps1 status: puertos 9870, 8088, 8042 y 19000 en OK
   .\parcial.ps1 run SeveridadPorVia                                         # "Tiempo total: ... s" + ranking; también lo guarda en PARCIAL\resultados\SeveridadPorVia_nodo_unico.txt 📷
   ```
   Resultado (2026-10-18, captura [`PARCIAL/imagenes/Desarrollo_parcial_2_1_2_1_3_inciso_6_EN_WINDOWS_1_NODO_3_TIEMPOS.png`](imagenes/Desarrollo_parcial_2_1_2_1_3_inciso_6_EN_WINDOWS_1_NODO_3_TIEMPOS.png)):
   - a) Mismo ranking que el clúster: 24 líneas, puesto 1 `PUNO PE-34H 6.7895`, último `PASCO PE-3N 1.0323` (leído por WebHDFS del HDFS local).
   - b) Tiempo: `TotalSeconds 76.80` (1 min 16.8 s). Por job (YARN local): 20.9 s, 24.3 s y 22.5 s, todos `SUCCEEDED`.
   - c) Primer intento fallido: la PowerShell no era de administrador; el NodeManager quedó `UNHEALTHY` (`nm-local-dir ... not writable`) y el job 1 se quedó 16 min en `ACCEPTED`. Se mató con `yarn application -kill`, se reinició Hadoop como administrador y funcionó.

7. Interpretación para el informe y comparación (después de ejecutar los pasos 5 y 6):

   Resultado esperado, para verificar las salidas de los pasos 5 y 6 (calculado con `awk` sobre el CSV): 24 departamentos; puesto 1 `PUNO via=PE-34H accidentes=19 ... indice=6.7895`, 2 `ANCASH AN-107` (8; 5.6250), 3 `PIURA PE-04B` (9; 5.4444). Los dos lados deben dar lo mismo.

   Interpretación del resultado (confirmada con la salida del clúster):
   - a) Cómo leer el índice: víctimas ponderadas por accidente; 6.79 en PUNO PE-34H = cada accidente de esa vía deja en promedio el equivalente a casi 7 heridos (un fallecido cuenta como 3).
   - b) Vías más severas: PUNO PE-34H (6.79; 17 fallecidos en 19 accidentes), ANCASH AN-107 (5.63), PIURA PE-04B (5.44), HUANCAVELICA PE-28A (5.38), TACNA PE-1SD (5.36). Son vías con pocos accidentes pero muy graves.
   - c) Contraste: AREQUIPA PE-34A tiene la mayor cantidad (133 accidentes) pero índice 2.49 (puesto 16); muchos accidentes no implican accidentes más graves. LAMBAYEQUE PE-1N (74 accidentes) combina volumen y severidad (4.30, puesto 9).
   - d) Menos severo: PASCO PE-3N (1.03, puesto 24).
   - e) 24 de 25 departamentos: LORETO queda fuera porque tiene solo 4 accidentes y ninguna vía llega al mínimo de 5.
   - f) `VIA URBANA` (128 accidentes en el CSV) no es una carretera sino el valor de SUTRAN para calles urbanas; aparece como la vía más severa de CALLAO (6), LA LIBERTAD (8) y CUSCO (10). TODO: decidir si se deja (y se explica) o se excluye en el mapper.

   Comparación clúster vs. nodo único (Consulta 1):

   | | Clúster (3 VM) | Nodo único (Windows) |
   |---|---|---|
   | Tiempo total | 268.0 s (`real 4m28.022s`) | 76.8 s |
   | Job 1 / 2 / 3 (YARN) | 94.1 / 80.9 / 75.8 s | 20.9 / 24.3 / 22.5 s |
   | Resultado | 24 departamentos | idéntico |

   - Speedup = T(nodo único) / T(clúster) = 76.8 / 268.0 = **0.29**: el clúster fue unas 3.5 veces más lento.
   - Por qué el clúster fue más lento (para el informe):
     - a) No es la complejidad del cálculo sino el costo fijo de coordinar cada job: pedir contenedores a YARN, arrancar el ApplicationMaster, lanzar 2 map y 1 reduce, y escribir/leer HDFS entre jobs. Ese costo se paga por job, sin importar cuántos datos procese.
     - b) Prueba: el job 3 procesó solo 24 registros (`Map input records=24`, una línea por departamento) y aun así tardó 75.8 s en el clúster, casi lo mismo que el job 1 con los 3 832 accidentes (94.1 s). El job 3 usó apenas 9.8 s de CPU (`CPU time spent 9780 ms`).
     - c) Lo que sí pesa de ser MapReduce triple: el costo fijo se paga 3 veces (≈ 80 s × 3 ≈ 250 s en el clúster; ≈ 22 s × 3 ≈ 68 s en Windows).
     - d) El costo fijo es mayor en el clúster porque pasa por la red entre VM con 2 GB de RAM y 2 vCPU cada una; en Windows todo es local con 16 GB y 16 hilos.
     - e) Cuándo ganaría el clúster: cuando el cálculo pese más que ese costo fijo, es decir, con datos mucho más grandes (muchos bloques repartidos entre worker1 y worker2 trabajando en paralelo). Con 434 KB el cálculo dura segundos y repartirlo no compensa.
#### 2.1.4 Segunda consulta con decimales que implique al menos un Mapreduce triple

Consulta 2: `PuntosNegrosPorTramo`. Pregunta: ¿qué tramos de 10 km de carretera concentran anormalmente más accidentes que el resto de su propia vía (puntos negros)?

0. Crear los `.java` en NetBeans:
   - a) Proyecto `PARCIAL` → clic derecho en Source Packages → New → Java Package → Package Name: `PuntosNegrosPorTramo` (exacto: es el `package` de los `.java`) → Finish.
   - b) Clic derecho en el paquete `PuntosNegrosPorTramo` → New → Java Class, 7 veces: `Driver`, `Mapper`, `Reducer`, `MapperEstadisticas`, `ReducerEstadisticas`, `MapperPuntosNegros`, `ReducerPuntosNegros`.
   - c) En cada clase, borrar el esqueleto y pegar el contenido del archivo del mismo nombre de `C:\Users\esauf\Desktop\uni\macro-datos\PARCIAL\source_Packages\2_PuntosNegrosPorTramo\`.
   - Ya hecho el 2026-10-18 copiando esos 7 archivos a `C:\Users\esauf\Documents\NetBeansProjects\PARCIAL\src\PuntosNegrosPorTramo\`; si el paquete ya aparece en NetBeans (tras Refresh), saltar este paso.
1. Qué hace cada job:
   - Job 1 (`Mapper` / `Reducer`): clave `Vía|tramo`, con tramo = ⌊PROGRESIVA / 10⌋ (km 604.5 → tramo 60 = km 600-610) → accidentes, fallecidos, heridos, latitud y longitud promedio (5 decimales) y departamento más frecuente del tramo. Descarta filas sin `PROGRESIVA` (80) o sin `CODIGO_VIA` (31) y las de `VIA URBANA` (el km no aplica a calles).
   - Job 2 (`MapperEstadisticas` / `ReducerEstadisticas`): clave `Vía` → media y desviación estándar (poblacional) de accidentes por tramo, y `z = (accidentes del tramo − media) / desviación` (4 decimales). Solo vías con ≥ 3 tramos con accidentes y desviación > 0.
   - Job 3 (`MapperPuntosNegros` / `ReducerPuntosNegros`): el mapper deja pasar solo tramos con z > 2; un solo reducer los ordena de mayor a menor z.
   - `Driver`: encadena los 3 jobs (`<salida>_temp1` → `<salida>_temp2` → `<salida>`) y borra los temporales de corridas anteriores.
2. Código: `PARCIAL/source_Packages/2_PuntosNegrosPorTramo/` (7 clases), copiado a `C:\Users\esauf\Documents\NetBeansProjects\PARCIAL\src\PuntosNegrosPorTramo\`. Verificación previa (la hizo Claude el 2026-10-18 antes de entregar el código; no es un paso a ejecutar): compilación de prueba con `javac` contra las 4 librerías de 2.1.2, en una carpeta temporal, sin errores; y ejecución de prueba en modo local de Hadoop (un solo proceso Java, sin HDFS ni YARN): los 3 jobs terminan y la salida es idéntica a la del paso 7.
3. Compilar en NetBeans: clic derecho en el proyecto `PARCIAL` → Refresh → clic derecho → Build. Comprobar en PowerShell:
   ```powershell
   jar tf C:\Users\esauf\Documents\NetBeansProjects\PARCIAL\dist\PARCIAL.jar | Select-String PuntosNegrosPorTramo   # 7 clases + ReducerPuntosNegros$1 📷
   ```
   Hecho el 2026-10-18: las 8 `.class` dentro de `PARCIAL.jar`, junto al paquete `SeveridadPorVia` (captura [`PARCIAL/imagenes/Desarrollo_parcial_2_1_2_1_4_3_Segunda_Consulta_con_decimales.png`](imagenes/Desarrollo_parcial_2_1_2_1_4_3_Segunda_Consulta_con_decimales.png)).
4. Copiar a master (PowerShell de Windows; el CSV ya está en master y en HDFS desde 2.1.3):
   ```powershell
   scp C:\Users\esauf\Documents\NetBeansProjects\PARCIAL\dist\PARCIAL.jar hadoop@192.168.56.10:~/   # JAR nuevo (trae las 2 consultas)
   ```
5. Ejecutar en el clúster (en master):
   ```bash
   hdfs dfs -rm -r -f /parcial_output/PuntosNegrosPorTramo                   # borra la salida anterior (si existe, el job falla)
   time java -cp "$HOME/PARCIAL.jar:$(hadoop classpath)" PuntosNegrosPorTramo.Driver /parcial_input /parcial_output/PuntosNegrosPorTramo   # master envía los 3 jobs a YARN; las tareas corren en los workers; "real" = tiempo total 📷
   hdfs dfs -cat /parcial_output/PuntosNegrosPorTramo/part-00000             # ranking de puntos negros 📷
   ```
   Equivalente con receta de `parcial.sh` (`run` = estos 3 comandos, más subir el CSV si falta; imprime "Tiempo total"):
   ```powershell
   scp C:\Users\esauf\Desktop\uni\macro-datos\PARCIAL\parcial.sh hadoop@192.168.56.10:~/   # en Windows: copiar el script a master (repetir si cambia)
  ssh hadoop@192.168.56.10  
   ```
   ```bash
   chmod +x ~/parcial.sh                    # en master, solo la primera vez
   ./parcial.sh status                      # jps en los 3 nodos y "Live datanodes (2)"
   ./parcial.sh run PuntosNegrosPorTramo    # "Tiempo total: ... s" + ranking; también lo guarda en ~/resultados/PuntosNegrosPorTramo_cluster.txt 📷
   ```
   Resultado con la receta (2026-10-18, 2.ª corrida en el clúster; captura [`PARCIAL/imagenes/Desarrollo_parcial_2_1_2_1_4_5_Segunda_Consulta_con_decimales_en_MASTER_1_CON_BASH.png`](imagenes/Desarrollo_parcial_2_1_2_1_4_5_Segunda_Consulta_con_decimales_en_MASTER_1_CON_BASH.png)):
   - a) `./parcial.sh status`: master con NameNode, SecondaryNameNode y ResourceManager; worker1 y worker2 con DataNode y NodeManager; `Live datanodes (2)`.
   - b) `./parcial.sh run PuntosNegrosPorTramo`: los 3 jobs `SUCCEEDED` (API de YARN), ApplicationMaster en worker1, worker2 y worker1, con 46.2 s, 48.6 s y 43.4 s (suma 138.2 s; 1.ª corrida: 134.1 s). Salida de 45 líneas idéntica a la 1.ª corrida (WebHDFS). Primera prueba real de `parcial.sh`: funcionó.
   - c) Su "Tiempo total" no quedó registrado (corrida anterior al guardado en `.txt`); para la comparación se usa la 1.ª corrida (`real` 147.6 s).
   Cómo funciona en conjunto:
   - a) Datos: el CSV ya está en HDFS (`/parcial_input`, subido en 2.1.3), en 1 bloque con una copia en el DataNode de worker1 y otra en el de worker2. Master (NameNode) solo guarda dónde está.
   - b) Envío: `java -cp` corre el `Driver` en master como cliente; `JobClient.runJob` entrega el job 1 al ResourceManager de master, espera a que termine y recién entonces envía el job 2, y luego el 3.
   - c) Ejecución: el ResourceManager pide contenedores a los NodeManagers de worker1 y worker2 (master no ejecuta tareas: no está en `workers`); en uno arranca el ApplicationMaster del job, que lanza los map y el reduce en contenedores de los workers.
   - d) Map y reduce: la API `mapred` pide 2 maps por defecto, así que cada job lanza 2 map en paralelo, cada uno sobre su parte del archivo leída desde un DataNode local; luego 1 reduce junta las salidas y escribe en HDFS (`_temp1`, `_temp2` y la salida final), que es la entrada del job siguiente.

   Resultado (2026-10-18, capturas [`PARCIAL/imagenes/Desarrollo_parcial_2_1_2_1_4_5_Segunda_Consulta_con_decimales_en_MASTER_1.png`](imagenes/Desarrollo_parcial_2_1_2_1_4_5_Segunda_Consulta_con_decimales_en_MASTER_1.png), [`_2.png`](imagenes/Desarrollo_parcial_2_1_2_1_4_5_Segunda_Consulta_con_decimales_en_MASTER_2.png) y [`_3_RANKING.png`](imagenes/Desarrollo_parcial_2_1_2_1_4_5_Segunda_Consulta_con_decimales_en_MASTER_3_RANKING.png)):
   - a) 45 puntos negros, iguales al esperado del paso 7 (job 3: `Reduce output records=45`). La salida completa del clúster (leída por WebHDFS) es idéntica línea por línea a la de la prueba en modo local; la interpretación de abajo se hizo sobre esas 45 líneas, no solo sobre la captura.
   - b) Los 3 jobs `SUCCEEDED` (API de YARN): ApplicationMaster en worker2 (job 1, 41.4 s), worker1 (job 2, 45.1 s) y worker2 (job 3, 47.6 s).
   - c) Tiempo total: `real 2m27.631s` (147.6 s). Cada job tardó la mitad que en la Consulta 1 (80-94 s) con el mismo tamaño de datos: el tiempo del clúster varía entre corridas (contenedores ya en caché, otros procesos en el PC), así que para la tabla final conviene repetir cada consulta 2-3 veces y usar el promedio.
   - d) El job 3 recibió 909 tramos (`Map input records=909`: todos los tramos de vías con ≥ 3 tramos) y dejó pasar 45 (5 %).

6. Ejecutar en el nodo único (PowerShell **como administrador**):
   ```powershell
   cd C:\Users\esauf\Desktop\uni\macro-datos                                 # carpeta de hadoop.ps1
   ([Security.Principal.WindowsPrincipal][Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)   # debe decir True; si no, el NodeManager queda UNHEALTHY y el job se queda en ACCEPTED
   .\hadoop.ps1 start                                                        # arranca HDFS y YARN de C:\Hadoop3 (si ya está corriendo, no hace nada)
   .\hadoop.ps1 status                                                       # puertos 9870, 8088, 8042 y 19000 en OK antes de seguir
   # --- desde aquí hasta el final del bloque = .\parcial.ps1 run PuntosNegrosPorTramo (ver abajo) ---
   hadoop fs -rm -r -f /parcial_output/PuntosNegrosPorTramo                  # borra la salida anterior (el CSV ya está en /parcial_input desde 2.1.3)
   $cp = (hadoop classpath)                                                  # librerías de Hadoop
   Measure-Command { java -cp "C:\Users\esauf\Documents\NetBeansProjects\PARCIAL\dist\PARCIAL.jar;$cp" PuntosNegrosPorTramo.Driver /parcial_input /parcial_output/PuntosNegrosPorTramo | Out-Host }   # TotalSeconds = tiempo 📷
   hadoop fs -cat /parcial_output/PuntosNegrosPorTramo/part-00000            # ranking 📷
   ```
   Equivalente con recetas de `parcial.ps1` (start y status = las 4 primeras líneas del bloque; run = los comandos marcados; run hace lo mismo: comprueba administrador, sube el CSV si falta, borra la salida, mide el tiempo y muestra el resultado):
   ```powershell
   cd C:\Users\esauf\Desktop\uni\macro-datos\PARCIAL                         # carpeta de parcial.ps1
   .\parcial.ps1 start                                                       # = comprobar administrador + ..\hadoop.ps1 start (no arranca si no es administrador)
   .\parcial.ps1 status                                                      # = ..\hadoop.ps1 status: puertos 9870, 8088, 8042 y 19000 en OK
   .\parcial.ps1 run PuntosNegrosPorTramo                                    # "Tiempo total: ... s" + ranking; también lo guarda en PARCIAL\resultados\PuntosNegrosPorTramo_nodo_unico.txt 📷
   ```
   Resultado (2026-10-18, con `.\parcial.ps1 run PuntosNegrosPorTramo`; capturas [`PARCIAL/imagenes/Desarrollo_parcial_2_1_2_1_4_5_Segunda_Consulta_con_decimales_en_NODO_WINDOWS_ejecucion.png`](imagenes/Desarrollo_parcial_2_1_2_1_4_5_Segunda_Consulta_con_decimales_en_NODO_WINDOWS_ejecucion.png) y [`_resultados.png`](imagenes/Desarrollo_parcial_2_1_2_1_4_5_Segunda_Consulta_con_decimales_en_NODO_WINDOWS_resultados.png)):
   - a) 45 puntos negros, idénticos línea por línea a la salida del clúster (comparadas por WebHDFS). Primera prueba real de la receta `run`: funcionó.
   - b) Los 3 jobs `SUCCEEDED` (YARN local): 20.2 s, 22.6 s y 23.2 s (suma 66.0 s). El "Tiempo total" de esa corrida no se ve en las capturas.
   - c) 2.ª corrida (2026-10-18 14:05, con la receta que guarda el `.txt`): **Tiempo total 70.37 s**; jobs 18.6 s, 22.2 s y 21.7 s, todos `SUCCEEDED`; 45 líneas idénticas al clúster. Archivo `PARCIAL/resultados/PuntosNegrosPorTramo_nodo_unico.txt`; captura [`PARCIAL/imagenes/Desarrollo_parcial_2_1_2_1_4_5_Segunda_Consulta_con_decimales_en_WINDOWS_TIEMPO_TOTAL.png`](imagenes/Desarrollo_parcial_2_1_2_1_4_5_Segunda_Consulta_con_decimales_en_WINDOWS_TIEMPO_TOTAL.png).

7. Interpretación para el informe y comparación (después de ejecutar los pasos 5 y 6):

   Resultado esperado, para verificar las salidas de los pasos 5 y 6 (calculado con `awk` sobre el CSV y confirmado en modo local): 45 puntos negros. Primeros:
   - 1 `PE-1S km=600-610 departamento=AREQUIPA accidentes=30 ... media_via=5.3171 desv_via=4.7307 z=5.2177 lat=-15.81491 lon=-74.33192`
   - 2 `PE-3N km=130-140 PASCO` (25 accidentes, z=4.8443)
   - 3 `PE-3S km=0-10 JUNIN` (13, z=3.8157)
   - Por vía: PE-1S 7 tramos, PE-5N, PE-3S y PE-1N 5 cada una. Por departamento: JUNIN 8, LIMA 7, AREQUIPA 5.

   Interpretación del resultado (confirmada con la salida del clúster):
   - a) Cómo leer z: cuántas desviaciones estándar está un tramo por encima del promedio de accidentes por tramo de su propia vía. El tramo 1, `PE-1S km 600-610` (AREQUIPA), tuvo 30 accidentes cuando un tramo típico de la Panamericana Sur tiene 5.3 (z = 5.22).
   - b) Puntos negros más marcados: PE-1S km 600-610 AREQUIPA (z 5.22), PE-3N km 130-140 PASCO (4.84), PE-3S km 0-10 JUNIN (3.82), PE-3S km 50-60 JUNIN (3.44), PE-5N km 650-660 SAN MARTIN (3.36).
   - c) Frecuencia no es severidad: PE-3N km 130-140 (PASCO) es el 2.º tramo con más accidentes respecto a su vía (25) pero tuvo 0 fallecidos. Complementa la Consulta 1, que mide severidad por vía.
   - d) Concentración: la PE-1S (Panamericana Sur) tiene 7 de los 45 puntos negros; por departamento, JUNIN 8, LIMA 7, AREQUIPA 5.
   - e) Limitaciones: se excluyen `VIA URBANA` (el km no aplica a calles) y las filas sin `PROGRESIVA` o `CODIGO_VIA`; la media de la vía se calcula solo con tramos que tuvieron al menos 1 accidente (los tramos sin accidentes no aparecen en los datos), por lo que es un umbral exigente.
   - f) Las columnas `lat` y `lon` (promedio del tramo) sirven para el mapa de puntos negros en Power BI.

   Comparación clúster vs. nodo único (Consulta 2):

   | | Clúster (3 VM) | Nodo único (Windows) |
   |---|---|---|
   | Tiempo total | 147.6 s (`real 2m27.631s`) | 70.37 s (2.ª corrida) |
   | Job 1 / 2 / 3 (YARN) | 41.4 / 45.1 / 47.6 s (suma 134.1 s) | 20.2 / 22.6 / 23.2 s (suma 66.0 s) |
   | Resultado | 45 puntos negros | idéntico |

   - Speedup = T(nodo único) / T(clúster) = 70.37 / 147.6 = **0.48**: el clúster fue unas 2.1 veces más lento. Con la suma de los jobs en YARN da casi lo mismo (66.0 / 134.1 = 0.49).
   - Por qué: mismo motivo que la Consulta 1. El costo fijo de cada job (contenedores, ApplicationMaster, HDFS entre jobs) domina con un CSV de 434 KB, y es mayor en el clúster (red entre VM, 2 GB y 2 vCPU por VM) que en Windows (local, 16 GB y 16 hilos). La diferencia fue menor que en la Consulta 1 (0.29) porque esta vez los jobs del clúster tardaron la mitad (41-48 s frente a 80-94 s); ver la variación entre corridas en el paso 5 (c).

### 2.2 Consulta de Agrupamiento ( basado íntegramente en Hadoop Mapreduce sobre las coordenadas geográficas)

Consulta 3: `AgrupamientoDBSCAN`. Pregunta: ¿en qué zonas del país se concentran los accidentes (zonas críticas), según sus coordenadas?

Algoritmo: DBSCAN (agrupamiento por densidad; el enunciado excluye K-means). Un punto es núcleo si tiene al menos `minPts` accidentes (contándose) a ≤ `eps` km (distancia haversine); un grupo es un conjunto de núcleos conectados más los puntos cercanos a ellos; el resto es ruido. Usa todos los accidentes con coordenadas (3 832), incluida `VIA URBANA`: aquí se agrupan coordenadas, no km de vía.

0. Crear los `.java` en NetBeans:
   - a) Proyecto `PARCIAL` → clic derecho en Source Packages → New → Java Package → Package Name: `AgrupamientoDBSCAN` (exacto: es el `package` de los `.java`) → Finish.
   - b) Clic derecho en el paquete `AgrupamientoDBSCAN` → New → Java Class, 14 veces: `Driver`, `Distancia`, `Puntos`, `MapperVecindad`, `ReducerVecindad`, `MapperPropagacion`, `ReducerPropagacion`, `MapperEtiquetado`, `MapperResumen`, `ReducerResumen`, `MapperRanking`, `ReducerRanking`, `MapperMetricas`, `ReducerMetricas`.
   - c) En cada clase, borrar el esqueleto y pegar el contenido del archivo del mismo nombre de `C:\Users\esauf\Desktop\uni\macro-datos\PARCIAL\source_Packages\3_AgrupamientoDBSCAN\`.
   - Alternativa a a)-c), copiando los archivos en vez de crearlos uno por uno (PowerShell); después, en NetBeans, clic derecho en el proyecto `PARCIAL` → Refresh:
     ```powershell
     New-Item -ItemType Directory -Force C:\Users\esauf\Documents\NetBeansProjects\PARCIAL\src\AgrupamientoDBSCAN   # carpeta del paquete (si no existe)
     Copy-Item C:\Users\esauf\Desktop\uni\macro-datos\PARCIAL\source_Packages\3_AgrupamientoDBSCAN\Driver.java C:\Users\esauf\Documents\NetBeansProjects\PARCIAL\src\AgrupamientoDBSCAN\   # un archivo
     Copy-Item C:\Users\esauf\Desktop\uni\macro-datos\PARCIAL\source_Packages\3_AgrupamientoDBSCAN\*.java C:\Users\esauf\Documents\NetBeansProjects\PARCIAL\src\AgrupamientoDBSCAN\   # los 14 de una vez
     (Get-ChildItem C:\Users\esauf\Documents\NetBeansProjects\PARCIAL\src\AgrupamientoDBSCAN\*.java).Count   # debe dar 14
     ```
   - Ya hecho el 2026-10-18 con esa copia (los 14 archivos están en `C:\Users\esauf\Documents\NetBeansProjects\PARCIAL\src\AgrupamientoDBSCAN\`); si el paquete ya aparece en NetBeans (tras Refresh), saltar este paso.
1. Qué hace cada job (19 jobs encadenados en el `Driver`, todo en MapReduce):
   - Job 1, Vecindad (`MapperVecindad` / `ReducerVecindad`): el mapper reparte cada accidente en una rejilla de celdas de lado ≥ `eps` (su celda con marca `H` y copia a las 8 vecinas con marca `G`); el reducer de cada celda calcula, para sus puntos `H`, qué puntos están a ≤ `eps` km y lo marca como núcleo si son ≥ `minPts`. → `<salida>_vecindad`
   - Jobs 2 a 15, Propagación (`MapperPropagacion` / `ReducerPropagacion`, 1 job por vuelta): cada núcleo manda su etiqueta a sus vecinos y cada punto se queda con la menor. El `Driver` repite hasta que el contador `CAMBIOS` da 0: con los parámetros elegidos, 14 vueltas. → `<salida>_iter1` … `_iter14`
   - Job 16, Etiquetado (`MapperEtiquetado`, solo map): grupo final de cada accidente; sin grupo = ruido (`-1`). → `<salida>_puntos` (sirve para el mapa de Power BI)
   - Job 17, Resumen (`MapperResumen` / `ReducerResumen`): por grupo, accidentes, centroide (lat/lon promedio), fallecidos, heridos y departamento más frecuente. → `<salida>_resumen`
   - Job 18, Ranking (`MapperRanking` / `ReducerRanking`, 1 reducer): grupos ordenados por tamaño (grupo 1 = el más grande) y el ruido al final. → `<salida>`
   - Job 19, Métricas (`MapperMetricas` / `ReducerMetricas`, 1 reducer): Silhouette y Davies-Bouldin. Mapper y reducer cargan en `configure()` todos los puntos etiquetados (`Puntos.java`), como el `configure()` de la PC1 (5a2, 6a2). → `<salida>_metricas`
   - Auxiliares: `Distancia` (haversine) y `Puntos` (carga de puntos). Parámetros: `eps = 2` km y `minPts = 10` por defecto; se pueden cambiar con argumentos opcionales: `... AgrupamientoDBSCAN.Driver <entrada> <salida> [eps_km] [minPts]`.
2. Verificación previa del código (la hizo Claude el 2026-10-18 en Windows, antes de entregar el código; **no es un paso a ejecutar** ni un requisito: queda como evidencia y sustento para el informe). Código verificado: `PARCIAL/source_Packages/3_AgrupamientoDBSCAN/` (14 clases).
   - a) Compilación de prueba fuera de NetBeans, contra las mismas 4 librerías de 2.1.2, dejando los `.class` en una carpeta temporal (no toca el proyecto): sin errores. Comando usado (Git Bash):
     ```bash
     H=C:/Hadoop3/share/hadoop
     javac -encoding UTF-8 -source 1.8 -target 1.8 -cp "$H/common/hadoop-common-3.3.0.jar;$H/mapreduce/hadoop-mapreduce-client-core-3.3.0.jar;$H/mapreduce/hadoop-mapreduce-client-common-3.3.0.jar;$H/mapreduce/hadoop-mapreduce-client-jobclient-3.3.0.jar" -d <carpeta_temporal> PARCIAL/source_Packages/3_AgrupamientoDBSCAN/*.java   # misma compilación que hace el Build de NetBeans
     ```
   - b) Ejecución de prueba en modo local de Hadoop (todo en un solo proceso Java, leyendo el CSV del disco de Windows, sin HDFS ni YARN; configurado con `fs.defaultFS=file:///` y `mapreduce.framework.name=local` en una carpeta de configuración temporal): los 19 jobs terminan. Sirve para comprobar la lógica, no para medir tiempos.
   - c) Contraste con un DBSCAN clásico sin Hadoop (búsqueda en anchura sobre los 3 832 puntos): mismos 30 grupos, mismos 699 puntos agrupados y 3 133 de ruido, Silhouette 0.6443 y Davies-Bouldin 0.2100 idénticos. Con `minPts = 5` hubo diferencias en el 4.º decimal: puntos de borde a ≤ `eps` de dos grupos, que cada implementación asigna a uno distinto (ambigüedad propia de DBSCAN).
   - d) Elección de parámetros (corridas en modo local):

     | `eps` / `minPts` | Grupos | Ruido | Silhouette ↑ | Davies-Bouldin ↓ | Vueltas | Jobs |
     |---|---|---|---|---|---|---|
     | 1 km / 5 | 108 | 2 874 (75 %) | 0.743 | 0.206 | 10 | 15 |
     | 2 km / 5 | 136 | 2 148 (56 %) | 0.673 | 0.273 | 24 | 29 |
     | 3 km / 5 | 139 | 1 615 (42 %) | 0.540 | 0.309 | 30 | 35 |
     | **2 km / 10 (elegido)** | **30** | 3 133 (82 %) | 0.644 | 0.210 | 14 | 19 |

     Cada fila es una ejecución de prueba como la de b), cambiando `eps` y `minPts` con los argumentos opcionales del `Driver`.

     Se eligió 2 km / 10: buenas métricas, 30 zonas de ≥ 10 accidentes fáciles de interpretar y de mapear, y la mitad de jobs que 2 km / 5 (cada job cuesta 40-90 s en el clúster).
3. Compilar en NetBeans: clic derecho en el proyecto `PARCIAL` → Refresh → clic derecho → Build. Comprobar en PowerShell:
   ```powershell
   jar tf C:\Users\esauf\Documents\NetBeansProjects\PARCIAL\dist\PARCIAL.jar | Select-String AgrupamientoDBSCAN   # 14 clases + ReducerRanking$1 + ReducerPropagacion$Contador 📷
   ```
4. Copiar a master (PowerShell de Windows; el CSV ya está en master y en HDFS desde 2.1.3):
   ```powershell
   scp C:\Users\esauf\Documents\NetBeansProjects\PARCIAL\dist\PARCIAL.jar hadoop@192.168.56.10:~/   # JAR nuevo (trae las 3 consultas)
   scp C:\Users\esauf\Desktop\uni\macro-datos\PARCIAL\parcial.sh hadoop@192.168.56.10:~/           # parcial.sh actualizado: ahora también muestra las métricas
   ```
5. Ejecutar en el clúster (en master):
   ```bash
   hdfs dfs -rm -r -f /parcial_output/AgrupamientoDBSCAN                     # borra la salida anterior (el Driver borra los _vecindad, _iter, etc.)
   time java -cp "$HOME/PARCIAL.jar:$(hadoop classpath)" AgrupamientoDBSCAN.Driver /parcial_input /parcial_output/AgrupamientoDBSCAN   # 19 jobs; muestra "Vuelta n: ... etiquetas cambiaron"; "real" = tiempo total 📷
   hdfs dfs -cat /parcial_output/AgrupamientoDBSCAN/part-00000               # ranking de grupos 📷
   hdfs dfs -cat /parcial_output/AgrupamientoDBSCAN_metricas/part-00000      # Silhouette y Davies-Bouldin 📷
   ```
   Equivalente con receta de `parcial.sh` (`run` = estos 4 comandos; imprime "Tiempo total" y también las métricas):
   ```bash
   ssh hadoop@192.168.56.10
   ./parcial.sh status                      # jps en los 3 nodos y "Live datanodes (2)"
   ./parcial.sh run AgrupamientoDBSCAN      # "Tiempo total: ... s" + ranking + métricas; también lo guarda en ~/resultados/AgrupamientoDBSCAN_cluster.txt 📷
   ```
   Traer los resultados del clúster a Windows (PowerShell; trae todos los `.txt` que dejaron las recetas):
   ```powershell
   New-Item -ItemType Directory -Force C:\Users\esauf\Desktop\uni\macro-datos\PARCIAL\resultados   # carpeta local (si no existe)
   scp "hadoop@192.168.56.10:~/resultados/*.txt" C:\Users\esauf\Desktop\uni\macro-datos\PARCIAL\resultados\   # <Paquete>_cluster.txt
   ```
   Cómo funciona en conjunto:
   - a) Datos: el CSV ya está en HDFS (`/parcial_input`), en 1 bloque con una copia en el DataNode de worker1 y otra en el de worker2. Master (NameNode) solo guarda dónde está.
   - b) Envío: `java -cp` corre el `Driver` en master como cliente; `JobClient.runJob` entrega cada job al ResourceManager de master y espera a que termine antes de enviar el siguiente. En las vueltas de propagación, además, el `Driver` lee el contador `CAMBIOS` del job terminado para decidir si lanza otra vuelta.
   - c) Ejecución: el ResourceManager pide contenedores a los NodeManagers de worker1 y worker2 (master no ejecuta tareas: no está en `workers`); en uno arranca el ApplicationMaster de cada job, que lanza los map y el reduce en contenedores de los workers.
   - d) Map y reduce: cada job lanza 2 map en paralelo (la API `mapred` pide 2 por defecto) sobre su entrada en HDFS y luego 1 reduce, que escribe en HDFS la entrada del job siguiente. El job de etiquetado no tiene reduce: cada map escribe directo su salida.
   Resultado (2026-10-18, con `./parcial.sh run AgrupamientoDBSCAN`; capturas [`PARCIAL/imagenes/Desarrollo_parcial_2_2_consulta_de_agrupamiento_3_build_4_copiar_a_master.png`](imagenes/Desarrollo_parcial_2_2_consulta_de_agrupamiento_3_build_4_copiar_a_master.png), [`..._5_comando_ejecucion.png`](imagenes/Desarrollo_parcial_2_2_consulta_de_agrupamiento_3_build_4_copiar_a_master_5_comando_ejecucion.png), [`..._5_comando_resultados_a.png`](imagenes/Desarrollo_parcial_2_2_consulta_de_agrupamiento_3_build_4_copiar_a_master_5_comando_resultados_a.png) y [`_b.png`](imagenes/Desarrollo_parcial_2_2_consulta_de_agrupamiento_3_build_4_copiar_a_master_5_comando_resultados_b.png)):
   - a) Build: las 16 `.class` (14 clases + `ReducerPropagacion$Contador` + `ReducerRanking$1`) dentro de `PARCIAL.jar`; `scp` del JAR y de `parcial.sh` al 100 %.
   - b) Los 19 jobs `SUCCEEDED` (API de YARN): 1 vecindad, 14 vueltas de propagación, etiquetado, resumen, ranking y métricas. ApplicationMaster repartido entre worker2 (10 jobs) y worker1 (9).
   - c) Tiempo total: **852.61 s** (14 min 13 s). Suma de los 19 jobs en YARN: 793.0 s; cada job entre 33.3 s y 47.2 s.
   - d) Ranking (31 líneas) y métricas idénticos a los del modo local y del DBSCAN clásico (comparados por WebHDFS): 30 grupos, 699 agrupados, 3 133 de ruido, Silhouette 0.6443, Davies-Bouldin 0.2100.
   - e) Esa corrida usó el `parcial.sh` anterior (3 545 bytes en la captura): no guardó el `.txt`.
   - f) 2.ª corrida (2026-10-18 18:23, con el `parcial.sh` actual): **816.33 s**; resultado en `PARCIAL/resultados/AgrupamientoDBSCAN_cluster.txt` (traído de master con `scp`). Promedio de las 2 corridas: 834.47 s.

6. Ejecutar en el nodo único (PowerShell **como administrador**):
   ```powershell
   cd C:\Users\esauf\Desktop\uni\macro-datos\PARCIAL                         # carpeta donde se ejecuta (la de parcial.ps1)
   ([Security.Principal.WindowsPrincipal][Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)   # debe decir True; si no, el NodeManager queda UNHEALTHY y el job se queda en ACCEPTED
   # --- desde aquí hasta el final del bloque = .\parcial.ps1 run AgrupamientoDBSCAN (ver abajo); requiere Hadoop arrancado con .\parcial.ps1 start ---
   hadoop fs -rm -r -f /parcial_output/AgrupamientoDBSCAN                    # borra la salida anterior (el CSV ya está en /parcial_input desde 2.1.3)
   $cp = (hadoop classpath)                                                  # librerías de Hadoop
   Measure-Command { java -cp "C:\Users\esauf\Documents\NetBeansProjects\PARCIAL\dist\PARCIAL.jar;$cp" AgrupamientoDBSCAN.Driver /parcial_input /parcial_output/AgrupamientoDBSCAN | Out-Host }   # TotalSeconds = tiempo 📷
   hadoop fs -cat /parcial_output/AgrupamientoDBSCAN/part-00000              # ranking 📷
   hadoop fs -cat /parcial_output/AgrupamientoDBSCAN_metricas/part-00000     # métricas 📷
   ```
   Equivalente con recetas de `parcial.ps1` (start = comprobar administrador y arrancar Hadoop; status = puertos; run = el bloque de arriba, y además: comprueba administrador, sube el CSV si falta, borra la salida, mide el tiempo y muestra el resultado y las métricas):
   ```powershell
   cd C:\Users\esauf\Desktop\uni\macro-datos\PARCIAL                         # carpeta de parcial.ps1
   .\parcial.ps1 start                                                       # = comprobar administrador + ..\hadoop.ps1 start (no arranca si no es administrador)
   .\parcial.ps1 status                                                      # = ..\hadoop.ps1 status: puertos 9870, 8088, 8042 y 19000 en OK
   .\parcial.ps1 run AgrupamientoDBSCAN                                      # "Tiempo total: ... s" + ranking + métricas; también lo guarda en PARCIAL\resultados\AgrupamientoDBSCAN_nodo_unico.txt 📷
   ```
   Resultado (2026-10-18, con `.\parcial.ps1 run AgrupamientoDBSCAN`; captura [`PARCIAL/imagenes/Desarrollo_parcial_2_2_consulta_de_agrupamiento_6_EN_WINDOWS_EJECUCION_RESULTADO.png`](imagenes/Desarrollo_parcial_2_2_consulta_de_agrupamiento_6_EN_WINDOWS_EJECUCION_RESULTADO.png); archivo `PARCIAL/resultados/AgrupamientoDBSCAN_nodo_unico.txt`):
   - a) Los 19 jobs `SUCCEEDED` (YARN local), con las mismas 14 vueltas de propagación.
   - b) Tiempo total: **487.89 s** (8 min 8 s). Suma de los 19 jobs en YARN: 445.4 s; cada job entre 19.4 s y 27.2 s.
   - c) Ranking (31 líneas) y métricas idénticos a los del clúster.
   - d) Primera prueba real del guardado en `.txt` de `parcial.ps1`: funcionó.
7. Interpretación para el informe y comparación (después de ejecutar los pasos 5 y 6):

   Resultado esperado, para verificar las salidas de los pasos 5 y 6 (modo local y DBSCAN clásico, 2026-10-18): 30 grupos + 1 línea de ruido; métricas `grupos 30`, `puntos_agrupados 699`, `puntos_ruido 3133`, `silhouette 0.6443`, `davies_bouldin 0.2100`. Primeros grupos:
   - `grupo 1 accidentes=223 centroide_lat=-11.69051 centroide_lon=-76.25282 departamento=LIMA fallecidos=25 heridos=138`
   - `grupo 2 accidentes=56 ... LIMA` · `grupo 3 accidentes=34 centroide_lat=-15.81589 centroide_lon=-74.33202 AREQUIPA`
   - `ruido accidentes=3133 fallecidos=977 heridos=4795`
   - Las vueltas de propagación deben ser 14 (`Vuelta 14: 0 etiquetas cambiaron`). La `etiqueta_dbscan` de cada grupo (el `NRO_REPORTE` menor del grupo) es interna y no hace falta en el informe.

   Interpretación del resultado (confirmada con la salida del clúster):
   - a) Qué es un grupo: una zona donde cada accidente tiene al menos otros 9 a ≤ 2 km, encadenados; 30 zonas reúnen 699 accidentes (18 %). El 82 % restante (3 133) son accidentes dispersos a lo largo de las carreteras, sin una concentración tan densa: es esperable en una red vial larga.
   - b) La zona más grande, grupo 1 (223 accidentes, centroide −11.691, −76.253, LIMA), está entera sobre la **PE-22 (Carretera Central)**; también los grupos 2 (56), 4 (24) y 5 (23, JUNIN) son de la PE-22: la Carretera Central concentra 4 de las 5 zonas más grandes.
   - c) El grupo 3 (34 accidentes, −15.816, −74.332, AREQUIPA, PE-1S) es el mismo lugar que el punto negro n.º 1 de la Consulta 2 (PE-1S km 600-610): dos métodos distintos (densidad de coordenadas y z-score por tramo) señalan la misma zona.
   - d) Severidad: el grupo 4 (PE-22, LIMA) tiene 24 accidentes y 34 fallecidos, más de un fallecido por accidente; el grupo 1, con 223 accidentes, tiene 25.
   - e) Por departamento: LIMA 12 zonas, AREQUIPA 7, ANCASH 4, JUNIN 2, ICA 2, PASCO, LA LIBERTAD y CALLAO 1 cada uno.
   - f) Métricas: Silhouette 0.644 (de −1 a 1; > 0.5 = grupos bien separados entre sí) y Davies-Bouldin 0.210 (cuanto más cerca de 0, grupos más compactos y separados). Se calculan sin el ruido.
   - g) Para Power BI: el ranking (`<salida>/part-00000`) trae el centroide de cada zona; `<salida>_puntos` trae cada accidente con su grupo y sus coordenadas, para el mapa coloreado por grupo.

   Comparación clúster vs. nodo único (Consulta 3):

   | Tiempo | Clúster (3 VM) | Nodo único (Windows) |
   |---|---|---|
   | Tiempo total | 852.61 s (1.ª corrida) y 816.33 s (2.ª); promedio 834.47 s (13 min 54 s) | 487.89 s (8 min 8 s) |
   | Jobs | 19 (14 vueltas de propagación) | 19 (14 vueltas de propagación) |
   | Suma de los jobs (YARN) | 793.0 s | 445.4 s |
   | Tiempo por job (mín. – máx.) | 33.3 – 47.2 s | 19.4 – 27.2 s |
   | Speedup = T(nodo único) / T(clúster) | **0.58** (con el promedio; 0.57 y 0.60 por corrida) | |

   | Métricas del agrupamiento (enunciado: "en cluster y 1 solo nodo") | Clúster | Nodo único |
   |---|---|---|
   | Grupos | 30 | 30 |
   | Puntos agrupados / ruido | 699 / 3 133 | 699 / 3 133 |
   | Silhouette ↑ | 0.6443 | 0.6443 |
   | Davies-Bouldin ↓ | 0.2100 | 0.2100 |

   Análisis de la comparación:
   - a) Métricas: idénticas en los dos (y el ranking de 31 líneas también). DBSCAN es determinista (mismos datos, `eps` y `minPts`): repartir el trabajo entre workers no cambia el resultado, solo el tiempo.
   - b) Tiempo: el clúster fue 1.7 veces más lento (speedup 0.58 con el promedio de 2 corridas; las corridas variaron solo 4 %: 852.61 y 816.33 s). Es la consulta con más jobs (19 frente a 3 de las Consultas 1 y 2), así que el costo fijo por job (contenedores, ApplicationMaster, escribir y leer HDFS entre jobs) se paga 19 veces: 33-47 s por job en el clúster frente a 19-27 s en Windows.
   - c) El cálculo más pesado es el job de métricas (cada uno de los 699 puntos agrupados contra todos los demás) y el de vecindad; aun así, el job de métricas usó solo 5.9 s de CPU en el clúster (`CPU time spent (ms)=5910`, captura [`_resultados_a.png`](imagenes/Desarrollo_parcial_2_2_consulta_de_agrupamiento_3_build_4_copiar_a_master_5_comando_resultados_a.png), el job con `Reduce output records=5`), frente a los 33-47 s que tarda cada job: casi todo es coordinación.
   - d) Speedup de las 3 consultas: 0.29 (Consulta 1), 0.48 (Consulta 2) y 0.58 (Consulta 3). En ninguna gana el clúster: con un CSV de 434 KB el costo de coordinar supera la ganancia de repartir el cálculo.

### 2.3 2 modelos de multilayer perceptrón, 1 de clasificación y 1 de regresión, usando el concepto clásico de gradiente descendiente en Hadoop Mapreduce

Plan común a las dos consultas (paquete `PerceptronComun`, en `PARCIAL/source_Packages/45_PerceptronComun/`):

- Red: 18 entradas → 8 neuronas ocultas (tanh) → 1 salida; 161 pesos iniciados al azar en [−0.5, 0.5] con semilla fija (42), así el entrenamiento es reproducible.
- Entradas (`Variables.java`), normalizadas con z-score: hora (seno, coseno), mes (seno, coseno), día de la semana (seno, coseno), latitud, longitud, progresiva (si falta, la media), modalidad en one-hot (8 clases) y 1 si es `VIA URBANA`. No se usan heridos para predecir fallecidos ni al revés: son consecuencias del mismo accidente.
- Train / test: 80 % / 20 % por hash de `NRO_REPORTE` (siempre el mismo reparto).
- Gradiente descendente clásico por lotes completos: en cada época se suma el gradiente de **todos** los ejemplos de train y se actualizan los pesos una vez, `w ← w − tasa · gradiente / ejemplos`. Sin Adam ni momentum.
- Jobs que encadena `Entrenamiento.java`:

  | Job | Qué hace | Salida |
  |---|---|---|
  | 1, Estadísticas (`MapperEstadisticas` / `ReducerEstadisticas`) | Media y desviación de cada variable, solo con train | `<salida>_estadisticas` |
  | 2, Vectores (`MapperVectores`, solo map) | Vector normalizado de cada accidente y marca `train` / `test` | `<salida>_vectores` |
  | 3 a 52, Épocas (`MapperEpoca` / `ReducerEpoca`, 1 job por época) | Cada map recibe los pesos por la configuración del job, suma el gradiente de su bloque (forward + backpropagation) y emite **una** línea con esa suma al terminar (`close()`); el reduce suma las de los 2 map; el `Driver` actualiza los pesos y lanza la época siguiente | `<salida>_epoca<n>` (se borra al leerla) |
  | 53, Evaluación (`MapperEvaluacion` / `ReducerEvaluacion`) | Predice el test con los pesos finales y calcula las métricas | `<salida>` |
  | — (el `Driver`) | Curva de pérdida por época y pesos finales, para el gráfico del informe | `<salida>_curva` |

- Parámetros por defecto: 50 épocas; tasa 0.5 (clasificación) y 0.3 (regresión). Se pueden cambiar sin recompilar: `... <Paquete>.Driver <entrada> <salida> [epocas] [tasa]`. No se probaron antes de entregar el código: si la pérdida de la curva no baja, subir las épocas o ajustar la tasa.
- Costo: 53 jobs por modelo; con 33-47 s por job en el clúster ≈ 30-40 min por modelo, y con 19-27 s en Windows ≈ 17-24 min.

#### 2.3.1 Consulta 4: `PerceptronClasificacion`

Pregunta: ¿el accidente tendrá fallecidos? Etiqueta 1 si `CANT_FALLECIDOS > 0` (660 de 3 832, 17 %). Salida sigmoide (probabilidad); pérdida entropía cruzada con peso 4.8 para los accidentes con fallecidos (hay unos 4.8 sin fallecidos por cada uno con fallecidos), para que la red no aprenda a decir siempre "no".

0. Crear los `.java` en NetBeans (este paso también crea el paquete común que usa la Consulta 5):
   - a) Proyecto `PARCIAL` → clic derecho en Source Packages → New → Java Package → Package Name: `PerceptronComun` → Finish. Repetir con `PerceptronClasificacion`.
   - b) En `PerceptronComun`, New → Java Class, 10 veces: `Variables`, `Red`, `Entrenamiento`, `MapperEstadisticas`, `ReducerEstadisticas`, `MapperVectores`, `MapperEpoca`, `ReducerEpoca`, `MapperEvaluacion`, `ReducerEvaluacion`. En `PerceptronClasificacion`, 1 clase: `Driver`.
   - c) En cada clase, borrar el esqueleto y pegar el archivo del mismo nombre de `C:\Users\esauf\Desktop\uni\macro-datos\PARCIAL\source_Packages\45_PerceptronComun\` y `C:\Users\esauf\Desktop\uni\macro-datos\PARCIAL\source_Packages\4_PerceptronClasificacion\`.
   - Alternativa a a)-c), copiando los archivos (PowerShell); después, en NetBeans, clic derecho en el proyecto `PARCIAL` → Refresh:
     ```powershell
     New-Item -ItemType Directory -Force C:\Users\esauf\Documents\NetBeansProjects\PARCIAL\src\PerceptronComun, C:\Users\esauf\Documents\NetBeansProjects\PARCIAL\src\PerceptronClasificacion   # carpetas de los paquetes
     Copy-Item C:\Users\esauf\Desktop\uni\macro-datos\PARCIAL\source_Packages\45_PerceptronComun\*.java C:\Users\esauf\Documents\NetBeansProjects\PARCIAL\src\PerceptronComun\   # las 10 clases comunes
     Copy-Item C:\Users\esauf\Desktop\uni\macro-datos\PARCIAL\source_Packages\4_PerceptronClasificacion\Driver.java C:\Users\esauf\Documents\NetBeansProjects\PARCIAL\src\PerceptronClasificacion\   # el Driver
     ```
   - Ya hecho el 2026-10-18 con esa copia; si los paquetes ya aparecen en NetBeans (tras Refresh), saltar este paso.
1. Código: `PerceptronComun` (10 clases, ver el plan común arriba) + `PerceptronClasificacion/Driver.java` (50 épocas, tasa 0.5, peso 4.8). No se compiló ni se ejecutó antes de entregarlo: el paso 2 es la primera compilación.
2. Compilar en NetBeans: clic derecho en el proyecto `PARCIAL` → Refresh → clic derecho → Build. Comprobar en PowerShell:
   ```powershell
   jar tf C:\Users\esauf\Documents\NetBeansProjects\PARCIAL\dist\PARCIAL.jar | Select-String Perceptron   # 10 clases de PerceptronComun + PerceptronClasificacion/Driver + PerceptronRegresion/Driver 📷
   ```
   Si el Build marca errores, copiar el mensaje (archivo y línea) para corregirlo.
3. Copiar a master (PowerShell de Windows; el CSV ya está en master y en HDFS desde 2.1.3):
   ```powershell
   scp C:\Users\esauf\Documents\NetBeansProjects\PARCIAL\dist\PARCIAL.jar hadoop@192.168.56.10:~/   # JAR nuevo (trae las 5 consultas)
   ```
4. Ejecutar en el clúster (en master):
   ```bash
   ssh hadoop@192.168.56.10  
   ./parcial.sh status                          # jps en los 3 nodos y "Live datanodes (2)"
   ./parcial.sh run PerceptronClasificacion     # 53 jobs: muestra "Epoca n: perdida de entrenamiento ..."; "Tiempo total" + métricas; lo guarda en ~/resultados/PerceptronClasificacion_cluster.txt 📷
   hdfs dfs -cat /parcial_output/PerceptronClasificacion_curva/part-00000   # curva de pérdida por época (para el gráfico) 📷
   ```
   Equivalente a mano (en master):
   ```bash
   hdfs dfs -rm -r -f /parcial_output/PerceptronClasificacion               # borra la salida anterior (el Driver borra los _estadisticas, _vectores, etc.)
   time java -cp "$HOME/PARCIAL.jar:$(hadoop classpath)" PerceptronClasificacion.Driver /parcial_input /parcial_output/PerceptronClasificacion   # "real" = tiempo total 📷
   hdfs dfs -cat /parcial_output/PerceptronClasificacion/part-00000         # métricas en test 📷
   ```
   Cómo funciona en conjunto:
   - a) Datos: el CSV ya está en HDFS (`/parcial_input`), en 1 bloque con una copia en worker1 y otra en worker2.
   - b) Envío: `java -cp` corre el `Driver` en master como cliente; cada job se entrega al ResourceManager de master y el `Driver` espera a que termine. Entre épocas, el `Driver` lee de HDFS la suma de gradientes, actualiza los pesos en memoria y los manda en la configuración del job siguiente.
   - c) Ejecución: las tareas corren en contenedores de worker1 y worker2 (master no está en `workers`).
   - d) Paralelismo de cada época: 2 map en paralelo (la API `mapred` pide 2 por defecto), cada uno calcula el gradiente de su mitad de los ejemplos; el reduce los suma. Es el gradiente de todos los ejemplos, calculado en partes.
   - Monitor de recursos (htop) durante la ejecución (2026-10-18 20:50, época 11, master con "Epoca 11: perdida de entrenamiento 1.083382 (3062 ejemplos)"): [`PARCIAL/imagenes/Desarrollo_2_3_1_PERCEPTRON_CLASIFICACION_master_workers_cpu.png`](imagenes/Desarrollo_2_3_1_PERCEPTRON_CLASIFICACION_master_workers_cpu.png). En worker2 aparece un 2.º proceso `java` recién arrancado (PID 15688, 0:00.18 de CPU): el contenedor del job nuevo. Memoria usada: worker1 587 MB y worker2 617 MB de 1.60 GB. La CPU está en 1-3 % porque la captura cayó entre tareas: cada map calcula solo 1-2 s (`CPU time spent (ms)=1090`) y el resto de los ~35 s por época es arrancar y cerrar contenedores. Para ver el pico, capturar `htop` 2-3 s después de que master muestre `map 0% reduce 0%`; el promedio sale de `vmstat`.
   - Resultado (2026-10-18 21:03, con `./parcial.sh run PerceptronClasificacion`; captura [`PARCIAL/imagenes/Desarrollo_2_3_1_PERCEPTRON_CLASIFICACION_master_workers_cpu_RESULTADO.png`](imagenes/Desarrollo_2_3_1_PERCEPTRON_CLASIFICACION_master_workers_cpu_RESULTADO.png); archivo `~/resultados/PerceptronClasificacion_cluster.txt` en master):
     - a) Tiempo total: **1055.25 s** (17 min 35 s) para los 53 jobs. Al terminar, en `htop` los workers vuelven a reposo (CPU 0-1 %, memoria 592 y 623 MB de 1.60 GB).
     - b) Métricas en test (770 accidentes): `perdida_test 0.6417`, `accuracy 0.5948`, `precision 0.2362`, `recall 0.6183`, `f1 0.3418`, `accuracy_base_siempre_no 0.8299`, matriz `VP=81 FP=262 VN=377 FN=50`.
     - c) Curva de pérdida de entrenamiento (`hdfs dfs -cat /parcial_output/PerceptronClasificacion_curva/part-00000`; capturas [`PARCIAL/imagenes/Desarrollo_2_3_1_PERCEPTRON_CLASIFICACION_master_workers_cpu_part-00000.png`](imagenes/Desarrollo_2_3_1_PERCEPTRON_CLASIFICACION_master_workers_cpu_part-00000.png) y [`_part-00000_2.png`](imagenes/Desarrollo_2_3_1_PERCEPTRON_CLASIFICACION_master_workers_cpu_part-00000_2.png)): baja en todas las épocas, de **1.207365** (época 1) a **1.058217** (época 50), −12 %. Baja rápido al inicio (1.207 → 1.085 en 10 épocas) y muy lento al final (≈ −0.0004 por época entre la 40 y la 50): todavía no se estabilizó del todo; más épocas o una tasa mayor podrían mejorar algo, a costa de ~20 s de clúster por época extra. La pérdida es la entropía cruzada con peso 4.8, por eso es mayor que la `perdida_test` sin peso (0.6417). Al final del archivo, la línea `pesos_finales` trae los 161 pesos entrenados.
5. Ejecutar en el nodo único (PowerShell como administrador):
   ```powershell
   cd C:\Users\esauf\Desktop\uni\macro-datos\PARCIAL                         # carpeta de parcial.ps1
   .\parcial.ps1 start                                                       # comprueba administrador y arranca Hadoop (si ya está corriendo, no hace nada)
   .\parcial.ps1 status                                                      # puertos 9870, 8088, 8042 y 19000 en OK
   .\parcial.ps1 run PerceptronClasificacion                                 # "Tiempo total" + métricas; lo guarda en PARCIAL\resultados\PerceptronClasificacion_nodo_unico.txt 📷
   hadoop fs -cat /parcial_output/PerceptronClasificacion_curva/part-00000   # curva de pérdida por época 📷
   ```
   - Resultado: capturas [`PARCIAL/imagenes/Desarrollo_2_3_1_PERCEPTRON_CLASIFICACION_unico_Nodo_windows_RESULTADOS_FINALES.png`](imagenes/Desarrollo_2_3_1_PERCEPTRON_CLASIFICACION_unico_Nodo_windows_RESULTADOS_FINALES.png) y [`PARCIAL/imagenes/Desarrollo_2_3_1_PERCEPTRON_CLASIFICACION_unico_Nodo_windows_TIEMPOS.png`](imagenes/Desarrollo_2_3_1_PERCEPTRON_CLASIFICACION_unico_Nodo_windows_TIEMPOS.png).
6. Interpretación para el informe y comparación (después de ejecutar los pasos 4 y 5):
   - Qué mirar en el resultado: la curva debe bajar; `accuracy` hay que compararla con `accuracy_base_siempre_no` (≈ 0.83: decir siempre "no" ya acierta el 83 %); con clases desbalanceadas importan más `recall` (cuántos accidentes con fallecidos detecta) y `f1`.
   - Interpretación (sobre el resultado del clúster):
     - a) De los 131 accidentes con fallecidos del test (VP + FN = 81 + 50), el modelo detecta 81: **recall 0.62**. La línea base "siempre no" no detecta ninguno (recall 0, F1 0).
     - b) A cambio da muchas falsas alarmas: de 343 accidentes que marca con fallecidos, 81 los tienen (**precisión 0.24**). Por eso su accuracy (0.59) queda por debajo de la base (0.83): el peso 4.8 hace que la red prefiera avisar de más antes que perderse un accidente con fallecidos.
     - c) F1 = 0.34: con las variables disponibles (hora, fecha, lugar, modalidad) hay señal, pero limitada; el informe debe presentarlo así, comparándolo con la base y no solo por la accuracy.
   - Tabla que pide el enunciado (línea 51):

     | Métrica (test, 770 accidentes) | Clúster | Nodo único |
     |---|---|---|
     | Pérdida (entropía cruzada) | 0.6417 | 0.6417 |
     | Accuracy / base "siempre no" | 0.5948 / 0.8299 | 0.5948 / 0.8299 |
     | Precisión / Recall / F1 | 0.2362 / 0.6183 / 0.3418 | 0.2362 / 0.6183 / 0.3418 |
     | Matriz (VP / FP / VN / FN) | 81 / 262 / 377 / 50 | 81 / 262 / 377 / 50 |
     | Tiempo total / speedup | 1055.25 s / 1.12 | 1181.66 s / 1.00 |

   - Los dos lados deben dar métricas iguales o casi (misma semilla y mismos datos; la suma de gradientes en otro orden puede cambiar los últimos decimales).

#### 2.3.2 Consulta 5: `PerceptronRegresion`

Pregunta: ¿cuántos heridos tendrá el accidente? Se predice `log(1 + CANT_HERIDOS)` (muchos ceros y valores de hasta 71). Salida lineal; pérdida error cuadrático medio. Misma red, mismas entradas y mismos jobs que la Consulta 4 (paquete `PerceptronComun`).

0. Crear el `.java` en NetBeans (requiere el paquete `PerceptronComun` de 2.3.1 paso 0):
   - a) Proyecto `PARCIAL` → clic derecho en Source Packages → New → Java Package → Package Name: `PerceptronRegresion` → Finish.
   - b) En `PerceptronRegresion`, New → Java Class: `Driver`; borrar el esqueleto y pegar `C:\Users\esauf\Desktop\uni\macro-datos\PARCIAL\source_Packages\5_PerceptronRegresion\Driver.java`.
   - Alternativa copiando el archivo (PowerShell); después, Refresh en NetBeans:
     ```powershell
     New-Item -ItemType Directory -Force C:\Users\esauf\Documents\NetBeansProjects\PARCIAL\src\PerceptronRegresion   # carpeta del paquete
     Copy-Item C:\Users\esauf\Desktop\uni\macro-datos\PARCIAL\source_Packages\5_PerceptronRegresion\Driver.java C:\Users\esauf\Documents\NetBeansProjects\PARCIAL\src\PerceptronRegresion\
     ```
   - Ya hecho el 2026-10-18 con esa copia; si el paquete ya aparece en NetBeans (tras Refresh), saltar este paso.
1. Código: `PerceptronRegresion/Driver.java` (50 épocas, tasa 0.3) + `PerceptronComun`. No se compiló ni se ejecutó antes de entregarlo.
2. Compilar: el mismo Build de 2.3.1 paso 2 ya incluye `PerceptronRegresion/Driver.class` (verlo en la salida de `jar tf`).
3. Copiar a master: el mismo `PARCIAL.jar` de 2.3.1 paso 3 (si ya se copió después del Build, no hace falta repetir):
   ```powershell
   scp C:\Users\esauf\Documents\NetBeansProjects\PARCIAL\dist\PARCIAL.jar hadoop@192.168.56.10:~/
   ```
4. Ejecutar en el clúster (en master):
   ```bash
   ssh hadoop@192.168.56.10 
   ./parcial.sh run PerceptronRegresion         # 53 jobs; "Tiempo total" + métricas; lo guarda en ~/resultados/PerceptronRegresion_cluster.txt 📷
   hdfs dfs -cat /parcial_output/PerceptronRegresion_curva/part-00000       # curva de pérdida por época 📷
   ```
   Equivalente a mano (en master):
   ```bash
   hdfs dfs -rm -r -f /parcial_output/PerceptronRegresion                   # borra la salida anterior
   time java -cp "$HOME/PARCIAL.jar:$(hadoop classpath)" PerceptronRegresion.Driver /parcial_input /parcial_output/PerceptronRegresion   # "real" = tiempo total 📷
   hdfs dfs -cat /parcial_output/PerceptronRegresion/part-00000             # métricas en test 📷
   ```
   Cómo funciona en conjunto: igual que la Consulta 4, con otra salida y otra pérdida.
   - a) Datos: el CSV ya está en HDFS (`/parcial_input`), en 1 bloque con una copia en worker1 y otra en worker2.
   - b) Envío: `java -cp` corre el `Driver` en master como cliente; entre épocas lee de HDFS la suma de gradientes, actualiza los pesos y los manda en la configuración del job siguiente.
   - c) Ejecución: las tareas corren en contenedores de worker1 y worker2 (master no está en `workers`).
   - d) Paralelismo de cada época: 2 map en paralelo calculan el gradiente de su mitad de los ejemplos; el reduce los suma.
   - Resultado (clúster, `./parcial.sh run PerceptronRegresion`; archivo `~/resultados/PerceptronRegresion_cluster.txt` en master): capturas [`PARCIAL/imagenes/Desarrollo_2_3_2_PERCEPTRON_REGRESION_master_workers_inicio.png`](imagenes/Desarrollo_2_3_2_PERCEPTRON_REGRESION_master_workers_inicio.png), [`PARCIAL/imagenes/Desarrollo_2_3_2_PERCEPTRON_REGRESION_master_workers_monitoreo.png`](imagenes/Desarrollo_2_3_2_PERCEPTRON_REGRESION_master_workers_monitoreo.png), [`PARCIAL/imagenes/Desarrollo_2_3_2_PERCEPTRON_REGRESION_master_workers_monitoreo_TIEMPOS.png`](imagenes/Desarrollo_2_3_2_PERCEPTRON_REGRESION_master_workers_monitoreo_TIEMPOS.png), [`PARCIAL/imagenes/Desarrollo_2_3_2_PERCEPTRON_REGRESION_master_workers_monitoreo_RESULTADOS_1.png`](imagenes/Desarrollo_2_3_2_PERCEPTRON_REGRESION_master_workers_monitoreo_RESULTADOS_1.png) y [`PARCIAL/imagenes/Desarrollo_2_3_2_PERCEPTRON_REGRESION_master_workers_monitoreo_RESULTADOS_2.png`](imagenes/Desarrollo_2_3_2_PERCEPTRON_REGRESION_master_workers_monitoreo_RESULTADOS_2.png).
5. Ejecutar en el nodo único (PowerShell como administrador):
   ```powershell
   cd C:\Users\esauf\Desktop\uni\macro-datos\PARCIAL                         # carpeta de parcial.ps1
   .\parcial.ps1 start                                                       # comprueba administrador y arranca Hadoop (si ya está corriendo, no hace nada)
   .\parcial.ps1 run PerceptronRegresion                                     # "Tiempo total" + métricas; lo guarda en PARCIAL\resultados\PerceptronRegresion_nodo_unico.txt 📷
   hadoop fs -cat /parcial_output/PerceptronRegresion_curva/part-00000       # curva de pérdida por época 📷
   ```
   - Resultado: capturas [`PARCIAL/imagenes/Desarrollo_2_3_2_PERCEPTRON_REGRESION_one_nodo_windows_RESULTADOS.png`](imagenes/Desarrollo_2_3_2_PERCEPTRON_REGRESION_one_nodo_windows_RESULTADOS.png) y [`PARCIAL/imagenes/Desarrollo_2_3_2_PERCEPTRON_REGRESION_one_nodo_windows_TIEMPOS.png`](imagenes/Desarrollo_2_3_2_PERCEPTRON_REGRESION_one_nodo_windows_TIEMPOS.png).
6. Interpretación para el informe y comparación (después de ejecutar los pasos 4 y 5):
   - Qué mirar en el resultado: la curva debe bajar; `mse` hay que compararlo con `mse_base_media` (predecir siempre la media): `r2 > 0` significa que la red explica algo más que la media; `mae_heridos` es el error medio en cantidad de heridos.
   - Tabla que pide el enunciado (línea 51), TODO:

     | Métrica (test) | Clúster | Nodo único |
     |---|---|---|
     | MSE / MSE base (escala log) | 0.4533 / 0.4674 | 0.4533 / 0.4674 |
     | MAE / R² | 0.5311 / 0.0303 | 0.5311 / 0.0303 |
     | MAE en heridos | 1.3632 | 1.3632 |
     | Tiempo total / speedup | 1105.57 s / 1.22 | 1344.12 s / 1.00 |

### 2.4 Comparación global clúster vs. nodo único (para el informe y la presentación)

Requisitos del enunciado (`PARCIAL/CC531-parcial-20262.pdf.md`): tabla de "tiempos de ejecución, speedup, uso promedio de CPU y memoria en paralelo y en un solo nodo" (línea 47), imagen del monitor de recursos (htop) de cada nodo durante la ejecución en paralelo (línea 49) y tablas de métricas de los modelos (línea 51) y del agrupamiento (línea 53).

#### 2.4.1 Tabla de tiempos y speedup

| Consulta | Jobs | Clúster (3 VM) | Nodo único (Windows) | Speedup = T(nodo) / T(clúster) | Resultado igual en los dos |
|---|---|---|---|---|---|
| 1. `SeveridadPorVia` (2.1.3) | 3 | 268.0 s | 76.8 s | 0.29 | sí (24 departamentos) |
| 2. `PuntosNegrosPorTramo` (2.1.4) | 3 | 147.6 s | 70.37 s | 0.48 | sí (45 puntos negros) |
| 3. `AgrupamientoDBSCAN` (2.2) | 19 | 834.47 s (promedio de 2) | 487.89 s | 0.58 | sí (30 grupos, mismas métricas) |
| 4. `PerceptronClasificacion` (2.3.1) | 53 | 1055.25 s | 1181.66 s | 1.12 | sí (mismas métricas) |
| 5. `PerceptronRegresion` (2.3.2) | 53 | 1105.57 s | 1344.12 s | 1.22 | sí (mismas métricas) |

Lectura para el informe:
- a) En las 3 primeras consultas gana el nodo único (speedup < 1); solo los perceptrones (53 jobs) son más rápidos en el clúster (1.12 y 1.22). Con un CSV de 434 KB el cálculo dura segundos y casi todo el tiempo es el costo fijo de cada job (contenedores, ApplicationMaster, HDFS entre jobs), mayor en el clúster (red entre VM, 2 GB y 2 vCPU por VM) que en Windows (local, 16 GB y 16 hilos).
- b) El speedup sube con la cantidad de jobs (0.29 → 0.48 → 0.58 → 1.12 → 1.22): en Windows el costo por job también existe (19-27 s), así que cuanto más jobs, más se parecen los dos lados en proporción.
- c) Los resultados son idénticos en los dos: repartir el trabajo cambia el tiempo, no el resultado.

#### 2.4.2 Uso de CPU y memoria (TODO: medir)

Se mide con `PuntosNegrosPorTramo`, la consulta más rápida (147.6 s en clúster, 70.37 s en Windows): con muestras cada 5 s da ~30 en clúster y ~14 en Windows.

1. Clúster: en worker1 y worker2 (consola de VirtualBox), antes de lanzar la consulta:
   ```bash
   vmstat 5 > ~/vmstat_$(hostname).txt &      # registra CPU y memoria cada 5 s en segundo plano
   htop                                        # barras de CPU por núcleo y memoria 📷 (con la consulta corriendo)
   ```
   En master, dos terminales SSH desde Windows (`ssh hadoop@192.168.56.10`):
   ```bash
   # terminal 1
   vmstat 5 > ~/vmstat_$(hostname).txt &
   ./parcial.sh run PuntosNegrosPorTramo
   # terminal 2
   htop                                        # 📷
   ```
   Al terminar la consulta, en cada VM (salir de htop con `q`; en master, en la terminal 1):
   ```bash
   kill %1                                     # detiene vmstat
   pkill vmstat                                # (si se cerró la terminal, kill %1 no sirve)
   awk 'NR>2 && NR<=32 {cpu+=100-$15; mem+=$4; n++} END {printf "CPU %.1f %%, memoria libre %.0f MB (%d muestras)\n", cpu/n, mem/n/1024, n}' ~/vmstat_$(hostname).txt   # solo las 30 muestras de la consulta (147.6 s / 5 s); columna 15 = % CPU ociosa, columna 4 = memoria libre (KB)
   ```
2. Nodo único (dos PowerShell; solo la de la consulta como administrador):
   ```powershell
   # ventana 1: monitor (Get-Counter falla en Windows en español; se usa CIM). Ctrl+C al terminar la consulta
   $f = "C:\Users\esauf\Desktop\uni\macro-datos\PARCIAL\resultados\recursos_windows.csv"
   "cpu,mem_libre_mb" | Set-Content $f
   while ($true) {
     $cpu   = (Get-CimInstance Win32_Processor).LoadPercentage                      # % de CPU
     $libre = [int]((Get-CimInstance Win32_OperatingSystem).FreePhysicalMemory / 1024)  # MB libres
     "$cpu,$libre" | Add-Content $f -PassThru                                       # guarda y muestra
     Start-Sleep 5
   }

   # ventana 1, al terminar: promedio
   $datos = Import-Csv $f
   $datos.cpu          | Measure-Object -Average     # CPU promedio (%)
   $datos.mem_libre_mb | Measure-Object -Average     # memoria libre promedio (MB)

   # ventana 2: consulta
   cd C:\Users\esauf\Desktop\uni\macro-datos\PARCIAL
   .\parcial.ps1 start
   .\parcial.ps1 run PuntosNegrosPorTramo
   ```
   Captura del Administrador de tareas (pestaña Rendimiento) con la consulta corriendo 📷.
   - Resultado: [`PARCIAL/imagenes/Desarrollo_2_4_1_TABLA_DE_TIEMPOS.png`](imagenes/Desarrollo_2_4_1_TABLA_DE_TIEMPOS.png), [`PARCIAL/imagenes/Desarrollo_2_4_1_TABLA_DE_TIEMPOS_2.png`](imagenes/Desarrollo_2_4_1_TABLA_DE_TIEMPOS_2.png), [`PARCIAL/imagenes/Desarrollo_2_4_1_TABLA_DE_TIEMPOS_3.png`](imagenes/Desarrollo_2_4_1_TABLA_DE_TIEMPOS_3.png); CPU y memoria: [`PARCIAL/imagenes/Desarrollo_2_4_1_TABLA_DE_TIEMPOS_4.png`](imagenes/Desarrollo_2_4_1_TABLA_DE_TIEMPOS_4.png) (master), [`PARCIAL/imagenes/Desarrollo_2_4_1_TABLA_DE_TIEMPOS_5.png`](imagenes/Desarrollo_2_4_1_TABLA_DE_TIEMPOS_5.png) (workers) y [`PARCIAL/imagenes/Desarrollo_2_4_1_TABLA_DE_TIEMPOS_UN_NODO_WINDOW.png`](imagenes/Desarrollo_2_4_1_TABLA_DE_TIEMPOS_UN_NODO_WINDOW.png) (Windows).
3. Tabla para el informe (PuntosNegrosPorTramo, 30 muestras de 5 s en cada VM, 17 en Windows):

   | Nodo | CPU promedio | Memoria libre promedio | Memoria usada = total − libre |
   |---|---|---|---|
   | master | 31.0 % | 160 MB | 1478 MB de 1638 MB (90 %) |
   | worker1 | 7.9 % | 598 MB | 1040 MB de 1638 MB (63 %) |
   | worker2 | 8.4 % | 587 MB | 1051 MB de 1638 MB (64 %) |
   | Nodo único (Windows) | 4.41 % | 1506 MB | 14455 MB de 15961 MB (91 %) |
   - Total de cada VM: 1.60 GiB = 1638 MB (htop de los workers; master es la VM original de la que se clonaron). Total de Windows: 15961 MB (`TotalVisibleMemorySize`).

#### 2.4.3 Archivos para Power BI (enunciado, línea 65: al menos un gráfico con coordenadas y el agrupamiento)

| Gráfico | Archivo (HDFS del clúster) | Columnas útiles |
|---|---|---|
| Mapa de accidentes coloreado por grupo DBSCAN | `/parcial_output/AgrupamientoDBSCAN_puntos/part-*` | lat, lon, grupo (−1 = ruido), fallecidos, heridos, departamento |
| Mapa de zonas críticas (centroides) | `/parcial_output/AgrupamientoDBSCAN/part-00000` | centroide_lat, centroide_lon, accidentes, fallecidos |
| Mapa de puntos negros | `/parcial_output/PuntosNegrosPorTramo/part-00000` | lat, lon, z, vía, km |
| Ranking de severidad por departamento | `/parcial_output/SeveridadPorVia/part-00000` | departamento, vía, índice |
| Curva de pérdida de los perceptrones | `/parcial_output/Perceptron*_curva/part-00000` | época, pérdida |

1. Exportar desde el HDFS de Windows (resultados idénticos al clúster, ver 2.4.1; no hace falta `scp`). PowerShell como administrador:
   ```powershell
   cd C:\Users\esauf\Desktop\uni\macro-datos\PARCIAL
   .\parcial.ps1 start
   mkdir powerbi
   hadoop fs -cat /parcial_output/AgrupamientoDBSCAN_puntos/part-* > powerbi\dbscan_puntos.txt
   hadoop fs -cat /parcial_output/AgrupamientoDBSCAN/part-00000    > powerbi\dbscan_grupos.txt
   hadoop fs -cat /parcial_output/PuntosNegrosPorTramo/part-00000  > powerbi\puntos_negros.txt
   hadoop fs -cat /parcial_output/SeveridadPorVia/part-00000       > powerbi\severidad.txt
   ```
   Curvas de pérdida: ya están en `resultados\Perceptron*_curva_*.txt` (época, pérdida).
2. Convertir a `.tsv` con encabezado (las salidas traen `campo=valor` separados por espacios; se cambian por tabulaciones y se quita `campo=`):
   ```powershell
   cd powerbi
   $f = "id`tlat`tlon`tgrupo`tfallecidos`theridos`tdepartamento"
   @($f) + ((Get-Content dbscan_puntos.txt) -replace '\|', "`t") | Set-Content -Encoding utf8 dbscan_puntos.tsv

   $f = "grupo`taccidentes`tcentroide_lat`tcentroide_lon`tdepartamento`tfallecidos`theridos`tetiqueta"
   @($f) + ((Get-Content dbscan_grupos.txt) -replace '\s{2,}', "`t" -replace '\t[a-z_]+=', "`t") | Set-Content -Encoding utf8 dbscan_grupos.tsv

   $f = "puesto`tvia`tkm`tdepartamento`taccidentes`tfallecidos`theridos`tmedia_via`tdesv_via`tz`tlat`tlon"
   @($f) + ((Get-Content puntos_negros.txt) -replace '\s{2,}', "`t" -replace '\t[a-z_]+=', "`t") | Set-Content -Encoding utf8 puntos_negros.tsv

   $f = "puesto`tdepartamento`tvia`taccidentes`tfallecidos`theridos`tindice"
   @($f) + ((Get-Content severidad.txt) -replace '\s{2,}', "`t" -replace '\t[a-z_]+=', "`t") | Set-Content -Encoding utf8 severidad.tsv
   ```
   - Resultado (2026-10-18): `dbscan_puntos.tsv` 3832 filas, `dbscan_grupos.tsv` 31, `puntos_negros.tsv` 45, `severidad.tsv` 24 (SeveridadPorVia se volvió a ejecutar en Windows porque su salida ya no estaba en HDFS).
3. Power BI Desktop → Informe en blanco:
   - Archivo → Opciones → Seguridad → activar "Usar objetos visuales de mapa y mapa coroplético" (sin esto el mapa no aparece).
   - Por cada tabla: Inicio → Obtener datos → Texto/CSV → elegir el `.tsv` → **Transformar datos** (no Load) → en Power Query: Inicio → **Editor avanzado** → borrar todo y pegar el código de abajo (cambiando archivo y tipos según la tabla) → Listo → **Close & Apply**.
     ```
     let
         Origen = Csv.Document(File.Contents("C:\Users\esauf\Desktop\uni\macro-datos\PARCIAL\powerbi\dbscan_grupos.tsv"), [Delimiter="#(tab)", Encoding=65001, QuoteStyle=QuoteStyle.None]),
         Encabezados = Table.PromoteHeaders(Origen, [PromoteAllScalars=true]),
         Tipos = Table.TransformColumnTypes(Encabezados, {{"accidentes", Int64.Type}, {"centroide_lat", type number}, {"centroide_lon", type number}, {"fallecidos", Int64.Type}, {"heridos", Int64.Type}}, "en-US")
     in
         Tipos
     ```
     - `Delimiter="#(tab)"`: separa por tabulación (Power BI la detecta mal: "Ancho fijo 0,6" o ";" y la lista no la ofrece).
     - `PromoteHeaders`: la primera fila son los nombres de columna.
     - `"en-US"`: lee el punto como decimal. Sin esto, Power BI en español lee `-11.69051` como `-1169051` y el mapa no sirve. No usar "Cambiar tipo" columna por columna: cada uno agrega un paso sobre toda la tabla y deja columnas redondeadas (p. ej. `centroide_lon` = `-76`).

     | Archivo (`.tsv` en `PARCIAL\powerbi\`) | Lista de tipos (tercer paso) |
     |---|---|
     | `dbscan_grupos` | `{{"accidentes", Int64.Type}, {"centroide_lat", type number}, {"centroide_lon", type number}, {"fallecidos", Int64.Type}, {"heridos", Int64.Type}}` |
     | `dbscan_puntos` | `{{"lat", type number}, {"lon", type number}, {"fallecidos", Int64.Type}, {"heridos", Int64.Type}}` (`grupo` queda texto: colores por grupo) |
     | `puntos_negros` | `{{"puesto", Int64.Type}, {"accidentes", Int64.Type}, {"fallecidos", Int64.Type}, {"heridos", Int64.Type}, {"media_via", type number}, {"desv_via", type number}, {"z", type number}, {"lat", type number}, {"lon", type number}}` |
     | `severidad` | `{{"puesto", Int64.Type}, {"accidentes", Int64.Type}, {"fallecidos", Int64.Type}, {"heridos", Int64.Type}, {"indice", type number}}` |

     Curva de pérdida: `C:\Users\esauf\Desktop\uni\macro-datos\PARCIAL\resultados\PerceptronClasificacion_curva_unico_nodo.txt` (sin encabezado: quitar `Encabezados` y usar `Origen`; columnas época, pérdida).
   - Comprobar: en la Vista de tabla (2.º icono de la barra izquierda), al seleccionar `centroide_lon` la barra de abajo debe decir 31 valores distintos (no 10).
   - Si quedó una consulta con ⚠ de un intento anterior, bloquea la carga ("Literal no válido"): Transformar datos → panel Consultas → clic derecho en la del ⚠ → Eliminar.
   - Latitud/Longitud: Vista de tabla → elegir la tabla en el panel Datos → clic en el encabezado de la columna → pestaña **Herramientas de columnas** (arriba, no se abre sola) → Categoría de datos: `lat`/`centroide_lat` = Latitud, `lon`/`centroide_lon` = Longitud (en `dbscan_puntos`, `dbscan_grupos` y `puntos_negros`).
   - Capturas: [`PARCIAL/imagenes/Desarrollo_2_4_3_POWER_BI_1.png`](imagenes/Desarrollo_2_4_3_POWER_BI_1.png), [`_2.png`](imagenes/Desarrollo_2_4_3_POWER_BI_2.png) (delimitador mal detectado), [`_3_encabezado.png`](imagenes/Desarrollo_2_4_3_POWER_BI_3_encabezado.png), [`_ERROR.png`](imagenes/Desarrollo_2_4_3_POWER_BI_ERROR.png), [`_BDSCAN.png`](imagenes/Desarrollo_2_4_3_POWER_BI_BDSCAN.png), [`_DATOS_CENTROIDES_COLUMNAS.png`](imagenes/Desarrollo_2_4_3_POWER_BI_DATOS_CENTROIDES_COLUMNAS.png), [`_LATITUD_.png`](imagenes/Desarrollo_2_4_3_POWER_BI_LATITUD_.png) (`-1169051`), [`_LATITUD_CONFIGURACION.png`](imagenes/Desarrollo_2_4_3_POWER_BI_LATITUD_CONFIGURACION.png), [`_TIPOS.png`](imagenes/Desarrollo_2_4_3_POWER_BI_TIPOS.png), [`_CAMBIAR_TIPO.png`](imagenes/Desarrollo_2_4_3_POWER_BI_CAMBIAR_TIPO.png) (`centroide_lon` = `-76`), [`_LATITUD_LONGITUD_HERRAMIENTAS.png`](imagenes/Desarrollo_2_4_3_POWER_BI_LATITUD_LONGITUD_HERRAMIENTAS.png) (todas con prefijo `PARCIAL/imagenes/Desarrollo_2_4_3_POWER_BI`).
4. Gráficos (panel Visualizaciones; arrastrar columnas a cada casilla):

   | # | Gráfico | Tabla | Visual y casillas |
   |---|---|---|---|
   | 1 | Accidentes por grupo DBSCAN (obligatorio) | dbscan_puntos | Mapa: Latitud = lat, Longitud = lon, Leyenda = grupo; `-1` (ruido) en gris |
   | 2 | Zonas críticas (hecho; basta para el requisito "al menos un gráfico con coordenadas y agrupamiento") | dbscan_grupos | Mapa: Latitud = centroide_lat, Longitud = centroide_lon, Tamaño de burbuja = accidentes, Leyenda = grupo |
   | 3 | Puntos negros | puntos_negros | Mapa: lat / lon, Tamaño = z, Información sobre herramientas = via, km |
   | 4 | Severidad por departamento | severidad | Barras: Eje Y = departamento, Eje X = indice |
   | 5 | Curva de pérdida | curvas | Líneas: Eje X = época, Eje Y = pérdida |
   | 6 | Tiempos clúster vs. nodo único | tabla 2.4.1 (Inicio → Especificar datos) | Barras agrupadas: Eje = consulta, Valores = tiempo clúster y tiempo nodo único |

5. Entregables (obligatorios): Archivo → Guardar como → `PARCIAL\powerbi\parcial.pbix`; cada gráfico → "..." del visual → Exportar → imagen. [`archivo pbix`](powerbi/parcial.pbix)

### 2.5 Apagar y volver a arrancar (p. ej. para la exposición)

Apagar (en orden, para no dañar HDFS; no usar la X ni "Apagar" de VirtualBox):
```bash
stop-yarn.sh                  # en master
stop-dfs.sh                   # en master
sudo poweroff                 # en worker1, worker2 y al final master
```

Volver a arrancar (`parcial.sh` no arranca Hadoop; el JAR y el CSV siguen en el disco y en HDFS, no hay que copiarlos):
1. VirtualBox: encender master, worker1 y worker2.
2. En master:
   ```bash
   start-dfs.sh
   start-yarn.sh
   ./parcial.sh status                   # todo en OK
   hdfs dfsadmin -report                 # "Live datanodes (2)"
   ./parcial.sh run PuntosNegrosPorTramo # la más rápida (~2.5 min); los perceptrones (~18 min) no entran en 10 min de exposición
   ```
3. Nodo único (PowerShell como administrador):
   ```powershell
   cd C:\Users\esauf\Desktop\uni\macro-datos\PARCIAL
   .\parcial.ps1 start
   .\parcial.ps1 run PuntosNegrosPorTramo   # ~75 s
   ```
