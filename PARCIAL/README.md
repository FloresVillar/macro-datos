# PARCIAL – CC531 Análisis en Macrodatos 2026-2

- Enunciado: [`CC531-parcial-20262.pdf`](CC531-parcial-20262.pdf) (versión md: [`CC531-parcial-20262.pdf.md`](CC531-parcial-20262.pdf.md))
- Grupo 9 – Flores Villar Esaú – registrado en el cuadro de temas del curso (Google Sheets "Grupo de evaluación Macrodatos 26-2", hoja *Parcial*).

## Dataset: Reportes preliminares de Accidentes – Centro de Gestión y Monitoreo

| Campo | Valor |
| --- | --- |
| URL | https://www.datosabiertos.gob.pe/dataset/reportes-preliminares-de-accidentes-centro-de-gesti%C3%B3n-y-monitoreo |
| Categoría PNDA | Transporte y movilidad |
| Publicador | Superintendencia de Transporte Terrestre de Personas, Carga y Mercancías – SUTRAN (Subgerencia de Supervisión Electrónica) |
| Fecha de lanzamiento | 2021-12-17 |
| Fecha modificada | 2022-01-28 |
| Frecuencia declarada | Trimestral (Quarterly) |
| Cobertura | Perú |
| Licencia | Open Data Commons Attribution License |
| Contacto | sgse@sutran.gob.pe |

Descripción (PNDA): la Subgerencia de Supervisión Electrónica, según el ROF de SUTRAN, es la unidad orgánica responsable del monitoreo permanente de los eventos o incidencias que se presentan en las vías de competencia de la SUTRAN.

### Archivos en `dataset_accidentes_cgm/`

| Archivo | Origen |
| --- | --- |
| `dataSSE_17122021_c.csv` | Descarga original de la PNDA (ISO-8859-1, separador `;`, fin de línea CRLF) |
| `dataSSE_17122021_c_utf8.csv` | Copia convertida a UTF-8 con fin de línea LF (`iconv` + `tr -d '\r'`), mismo contenido |
| `Formato_2_Diccionario_de_datos SSE.doc` | Diccionario de datos original de la PNDA |
| `Formato_2_Diccionario_de_datos SSE.docx` / `.docx.md` | Conversión con LibreOffice y luego `docx_to_md.py` |
| `leydigital.pdf` / `.pdf.md` | Norma adjunta por la PNDA (D. Leg. 1412, Ley de Gobierno Digital); genérica, no específica del dataset |

### Diccionario de datos

| Campo | Descripción | Tipo |
| --- | --- | --- |
| FECHA_CORTE | Fecha de corte (AAAAMMDD) | Numérico |
| NRO_REPORTE | Número de reporte / código único | Alfanumérico |
| FECHA_ACC | Fecha del accidente (AAAAMMDD) | Numérico |
| HORA_ACC | Hora del accidente (24 h) | Hora |
| MODALIDAD_ACC | Modalidad del accidente ("ESPECIAL" = atípico: caída de piedras, desprendimiento de llanta, etc.) | Texto |
| CANT_FALLECIDOS | Número de fallecidos | Numérico |
| CANT_HERIDOS | Número de heridos | Numérico |
| LATITUD | Latitud del accidente | Decimal |
| LONGITUD | Longitud del accidente | Decimal |
| PROGRESIVA | Kilómetro de la vía | Decimal |
| CODIGO_VIA | Código MTC de la vía (red nacional, departamental o vecinal) | Texto |
| DEPARTAMENTO / PROVINCIA / DISTRITO | Ubicación administrativa | Texto |

### Perfil del CSV

- 3 832 filas × 14 columnas; `NRO_REPORTE` sin duplicados.
- Periodo: `FECHA_ACC` de 2020-01-01 a 2021-11-30; `FECHA_CORTE` única = 20211130.
- Coordenadas: sin nulos ni ceros, todas dentro de Perú (lat −18.27 a −3.52, lon −81.28 a −69.05); 3 686 puntos únicos.
- Nulos: `PROGRESIVA` 80, `CODIGO_VIA` 31; el resto completo.
- `PROGRESIVA`: 0 a 1 814 km, mediana 146.25.
- `CANT_FALLECIDOS`: 0 a 33, media 0.29 (3 172 filas en 0). `CANT_HERIDOS`: 0 a 71, media 1.44 (1 962 filas en 0).
- `MODALIDAD_ACC` (8 clases): CHOQUE 1 967, DESPISTE 1 562, ATROPELLO 94, VOLCADURA 80, INCENDIO 65, ESPECIAL 44, ACCIDENTE 11, EMBISTE 9.
- `DEPARTAMENTO`: 25 valores (LIMA 872, AREQUIPA 468, JUNIN 317, ANCASH 285, ...). `CODIGO_VIA`: 179 valores (PE-1N 728, PE-1S 654, PE-22 379, ...).

### Cumplimiento de la rúbrica (dataset)

| Requisito del enunciado | Estado |
| --- | --- |
| Categoría Transporte y movilidad o Vivienda, urbanismo y territorio | ✅ Transporte y movilidad |
| Campos de latitud y longitud | ✅ |
| No usado por otro grupo | ✅ según el cuadro de temas al 2026-10-17 |
| Fuente y fecha de publicación | ✅ SUTRAN, 2021-12-17 (no es de los más recientes) |
| Investigaciones / artículos previos con el dataset | TODO: buscar |

### Observaciones para las consultas

- Clasificación: `MODALIDAD_ACC` está desbalanceada (CHOQUE + DESPISTE ≈ 92 %); considerar agrupar clases o usar otra etiqueta (p. ej. hubo/no hubo fallecidos).
- Regresión: `CANT_HERIDOS` / `CANT_FALLECIDOS` son conteos con muchos ceros.
- Agrupamiento: usar `LATITUD`/`LONGITUD` (no aplica K-means según el enunciado).
- Volumen pequeño (≈ 430 KB): tenerlo en cuenta al interpretar el speedup clúster vs. un nodo.
