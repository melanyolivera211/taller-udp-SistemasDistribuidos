# Taller UDP: Conversión de Kilómetros a Millas con Arquitectura Hexagonal

Proyecto académico para la asignatura de **Sistemas Distribuidos** (8vo Semestre).
Esta solución implementa el **Ejercicio 4 (Conversión de Kilómetros a Millas)** migrado del modelo TCP tradicional a un sistema distribuido con **Protocolo UDP/IP (User Datagram Protocol)** bajo una **Arquitectura Hexagonal (Puertos y Adaptadores)** estricta, desacoplada y orientada a objetos.

---

## 🎯 Objetivo y Contexto

- **Fórmula de Conversión**: $1\text{ km} = 0.621371\text{ millas}$
- **Paradigma de Red**: Migración de conexión continua punto a punto (TCP `Socket`) a comunicación sin conexión mediante datagramas (`DatagramSocket`, `DatagramPacket`).
- **Arquitectura de Software**: Eliminación total del acoplamiento entre la lógica de red, la interfaz gráfica Swing y las reglas de negocio mediante **Hexagonal Architecture** y el **Patrón Observador**.

---

## 🏛️ Comparativa: Solución Original (TCP) vs Solución Hexagonal (UDP)

| Criterio | Solución Original (TCP) | Nueva Solución Hexagonal (UDP) |
| :--- | :--- | :--- |
| **Protocolo de Transporte** | TCP orientado a conexión | UDP sin conexión (datagramas independientes) |
| **Manejo de Concurrencia** | Hilo por cliente (`SubProcesoCliente`) y `ServerSocket.accept()` | Un bucle de escucha UDP concurrente con despacho desacoplado |
| **Tolerancia a Caídas / Timeout**| Detección pasiva por desconexión / `EOFException` | `socket.setSoTimeout(3000)` para detectar falta de respuesta del servidor sin congelar la interfaz |
| **Formato del Mensaje** | Flujo binario (`DataInputStream` / `DataOutputStream`) | Mensajería de texto estructurada UTF-8 (`CONVERTIR;15.5` -> `OK_CONVERSION;9.6313;...`) |
| **Acoplamiento UI** | La red manipulaba directamente los componentes de Swing | UI y Red se comunican únicamente a través de Puertos de Entrada y Observadores |
| **Separación de Capas** | Código mezclado en clases controladoras | Dominio puro, Aplicación (casos de uso) y Adaptadores aislados |

---

## 🏗️ Arquitectura Hexagonal (Puertos y Adaptadores)

El sistema organiza tanto el servidor como el cliente en capas concéntricas con responsabilidades estrictamente separadas:

### 1. Capa de Dominio (Núcleo)
- **Modelos y Value Objects**: `Kilometros`, `Millas`, `DestinoServidor`, `Destinatario`, `PuertoRed`, `ResultadoConversion`. Garantizan sus invariantes de negocio en el momento de la instanciación (por ejemplo, las distancias nunca pueden ser negativas ni NaN).
- **Puertos de Salida (Interfaces)**: Definen los contratos que el núcleo necesita hacia el exterior (`PuertoSalidaRed`, `ClienteUdpPort`, `PuertoNotificacionEvento`, `PuertoNotificacionCliente`). No dependen de librerías de red ni de componentes gráficos.

### 2. Capa de Aplicación (Casos de Uso)
- **Puertos de Entrada**: Interfaces que exponen las operaciones del sistema (`ConvertirKmInputPort`, `GestionarConexionInputPort`, `GestionarServidorInputPort`, `ProcesarPeticionUdpInputPort`).
- **Servicios de Aplicación**: `ClienteKmService`, `ProcesarPeticionUdpService`, `GestionarServidorService`. Orquestan el flujo de datos, validan la precondición de conexión y coordinan la notificación de eventos.
- **Mappers y DTOs**: Transforman los comandos externos en entidades de dominio sin filtrar detalles de infraestructura hacia el núcleo.

### 3. Capa de Adaptadores (Infraestructura)
- **Adaptadores de Red (UDP)**: Implementan los puertos de salida interactuando directamente con la API de sockets UDP de Java (`DatagramSocket` y `DatagramPacket`). El adaptador del cliente gestiona un timeout de 3 segundos para evitar bloqueos si el servidor no responde.
- **Adaptadores de Notificación (Patrón Observador)**: Despachan eventos de forma segura entre hilos (`CopyOnWriteArrayList`), permitiendo que la interfaz gráfica actualice logs y estados sin que el servicio conozca la existencia de Swing.

### 4. Entrypoints (Puntos de Entrada)
- **Interfaces Gráficas (Swing)**: `ServidorFrame` y `ClienteFrame`. Consumen únicamente los puertos de entrada de la aplicación y se registran como observadores de eventos.
- **Receptor UDP**: `ReceptorPeticionesUdp`. Escucha paquetes entrantes en un hilo en segundo plano y los despacha al caso de uso correspondiente.
- **Lanzador Central (`Main.java`)**: Orquesta el arranque permitiendo al usuario seleccionar si desea ejecutar el Servidor, el Cliente o ambos de forma simultánea.

---

## 📡 Protocolo de Mensajería UDP

Los paquetes viajan codificados en **UTF-8** utilizando delimitadores punto y coma (`;`):

1. **Verificación de conexión lógica**:
   - `Cliente -> Servidor`: `CONECTAR`
   - `Servidor -> Cliente`: `CONECTADO_OK;Servidor UDP listo para recibir conversiones de Km a Millas.`
2. **Conversión de distancia**:
   - `Cliente -> Servidor`: `CONVERTIR;<kilometros>` *(ejemplo: `CONVERTIR;15.5`)*
   - `Servidor -> Cliente`: `OK_CONVERSION;<millas>;<mensaje>` *(ejemplo: `OK_CONVERSION;9.6313;15.50 km equivalen a 9.6313 millas`)*
3. **Desconexión voluntaria**:
   - `Cliente -> Servidor`: `DESCONECTAR`
   - `Servidor -> Cliente`: `DESCONECTADO_OK;Sesión finalizada.`
4. **Errores controlados**:
   - `Servidor -> Cliente`: `ERROR;<mensaje explicativo>`

---

## 📁 Estructura del Proyecto

El proyecto está unificado bajo **un único archivo `pom.xml`** en la raíz:

```text
Taller UDP/
├── pom.xml                                      # Único POM Maven central del proyecto
├── src/
│   ├── main/java/kilometros/
│   │   ├── Main.java                            # Lanzador central interactivo
│   │   ├── servidor/                            # Módulo Servidor
│   │   │   ├── Main.java                        # Composition Root del servidor
│   │   │   ├── dominio/
│   │   │   │   ├── enums/                       # EstadoServidor, TipoRespuesta
│   │   │   │   ├── excepciones/                 # DominioException, KilometrosInvalidosException, etc.
│   │   │   │   ├── modelos/                     # CalculoKmAMillas, ResultadoConversion, RespuestaCliente
│   │   │   │   ├── puertos/salida/              # PuertoSalidaRed, ControladorServidorRedPort, etc.
│   │   │   │   └── vo/                          # Kilometros, Millas, Destinatario, PuertoRed
│   │   │   ├── aplicacion/
│   │   │   │   ├── dto/                         # ProcesarPeticionUdpCommand, ConvertirKmCommand, etc.
│   │   │   │   ├── mapper/                      # CalculoMapper, PeticionMapper
│   │   │   │   ├── puertos/entrada/             # ProcesarPeticionUdpInputPort, GestionarServidorInputPort
│   │   │   │   └── servicios/                   # ProcesarPeticionUdpService, GestionarServidorService
│   │   │   ├── adaptadores/red/                 # CanalUdp, AdaptadorSalidaUdp, UdpNetworkMapper
│   │   │   └── entrypoint/
│   │   │       ├── gui/                         # ServidorFrame (Swing)
│   │   │       └── udp/                         # ReceptorPeticionesUdp
│   │   │
│   │   └── cliente/                             # Módulo Cliente
│   │       ├── Main.java                        # Composition Root del cliente
│   │       ├── dominio/
│   │       │   ├── enums/                       # EstadoConexion
│   │       │   ├── excepciones/                 # DominioException, KilometrosInvalidosException, etc.
│   │       │   ├── modelos/                     # DatosConversion, ResultadoConversion, EventoCliente
│   │       │   ├── puertos/salida/              # ClienteUdpPort, PuertoNotificacionCliente
│   │       │   └── vo/                          # Kilometros, DestinoServidor
│   │       ├── aplicacion/
│   │       │   ├── dto/                         # ConectarCommand, ConvertirKmCommand
│   │       │   ├── excepciones/                 # ClienteRedException
│   │       │   ├── mapper/                      # ClienteMapper
│   │       │   ├── puertos/entrada/             # ConvertirKmInputPort, GestionarConexionInputPort
│   │       │   └── servicios/                   # ClienteKmService
│   │       ├── adaptadores/
│   │       │   ├── notificacion/                # AdaptadorNotificacionCliente, ObservadorCliente
│   │       │   └── red/                         # CanalUdp, AdaptadorClienteUdp, ProtocoloUdpMapper
│   │       └── entrypoint/gui/                  # ClienteFrame (Swing)
│   │
│   └── test/java/kilometros/
│       ├── IntegracionEndToEndTest.java         # Prueba E2E de comunicación real cliente-servidor
│       ├── servidor/                            # Pruebas unitarias e integración del servidor (9 tests)
│       └── cliente/                             # Pruebas unitarias e integración del cliente (22 tests)
└── README.md
```

---

## 🚀 Guía de Compilación y Ejecución

### Requisitos Previos
- **Java Development Kit (JDK)**: 17 o superior.
- **Apache Maven**: 3.8 o superior.

### 1. Ejecución mediante el Lanzador Central (Recomendado)
Desde la raíz del proyecto:
```bash
mvn exec:java
```
Esto abrirá una ventana que permite elegir con un solo clic:
- **1. Iniciar Servidor**
- **2. Iniciar Cliente**
- **3. Iniciar Ambos**: Abre las dos interfaces gráficas simultáneamente para probar de inmediato.

También es posible arrancar directamente por línea de comandos pasando el argumento deseado:
```bash
mvn exec:java -Dexec.args="servidor"
mvn exec:java -Dexec.args="cliente"
mvn exec:java -Dexec.args="ambos"
```

### 2. Ejecución mediante Perfiles de Maven
- **Solo Servidor**:
  ```bash
  mvn exec:java -Pservidor
  ```
- **Solo Cliente**:
  ```bash
  mvn exec:java -Pcliente
  ```

---

## 🧪 Ejecución de Pruebas Automatizadas

Para compilar y ejecutar toda la suite de pruebas unitarias y de integración:
```bash
mvn clean test
```
La suite ejecuta **32 pruebas automatizadas**:
- **9 pruebas del Servidor**: Validación de cálculo de millas, Value Objects, puertos de entrada y salida, y recepción UDP.
- **22 pruebas del Cliente**: Invariantes de datos, mapeo de protocolo UDP, control de timeout por desconexión y manejo de sesión.
- **1 prueba E2E completa**: Comunicación real por datagramas entre el cliente y el servidor en loopback.

---

## 🎓 Guía para la Sustentación ante el Docente

1. **¿Por qué usar Arquitectura Hexagonal en este taller?**
   - Permite que el núcleo del negocio (el cálculo de kilómetros a millas y sus reglas) permanezca completamente aislado e independiente de la tecnología de red (UDP) y del framework gráfico (Swing). Si mañana se migra a REST, gRPC o WebSockets, el dominio no cambia ni una sola línea de código.
2. **¿Cómo se desacopló la UI de la Red?**
   - En la versión original TCP, el socket modificaba directamente los componentes visuales de la ventana. En la versión Hexagonal se usa el **Patrón Observador**: los servicios notifican eventos a través de puertos de salida (`PuertoNotificacionEvento` y `PuertoNotificacionCliente`), y las vistas Swing solo actúan como observadores pasivos.
3. **¿Cómo maneja UDP la falta de conexión?**
   - UDP no tiene establecimiento de conexión a nivel de transporte (`3-way handshake`). Por ello, se implementó un handshake lógico (`CONECTAR` / `CONECTADO_OK`) y un **timeout** mediante `socket.setSoTimeout(3000)`. Si el servidor está apagado o se pierde un paquete, el cliente no se bloquea indefinidamente sino que captura `SocketTimeoutException` y notifica al usuario.
4. **¿Dónde residen las validaciones?**
   - Los **Value Objects** (`Kilometros`, `DestinoServidor`, `PuertoRed`) garantizan sus invariantes en el constructor. No es posible crear una distancia negativa o un puerto inválido en ninguna parte del sistema.
