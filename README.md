# Taller: Introducción a Redes, Clientes y Servicios con Java
**ARSW 2026-i — Escuela Colombiana de Ingeniería Julio Garavito**
 
Este taller cubre los conceptos fundamentales de comunicación en red usando Java: URLs, Sockets TCP, Datagramas UDP y RMI. Se implementan clientes y servidores que se comunican mediante distintos protocolos.
 
---
 
## Estructura del proyecto
 
```
URL_EXERCISE/
├── clientes/
│   ├── EchoClient.java
│   ├── DatagramTimeClient.java
│   └── ChatClient.java
├── servers/
│   ├── SquareServer.java
│   ├── FunctionServer.java
│   ├── MultiHttpServer.java
│   ├── DatagramTimeServer.java
│   ├── ChatService.java
│   └── ChatServiceImpl.java
└── www/
    ├── index.html
    └── imagen.png
```
 
> **Compilar y ejecutar siempre desde la raíz `URL_EXERCISE`:**
> ```bash
> javac -d . servers/NombreServidor.java
> java servers.NombreServidor
> ```
 
---
 
## Ejercicio 4.3.1 — Servidor que responde el cuadrado de un número
 
El servidor recibe un número por socket TCP y responde su cuadrado.
 
```java
double number = Double.parseDouble(inputLine.trim());
out.println(number * number);
```
 
**Cómo probar** — Terminal 1:
```bash
javac -d . servers/SquareServer.java && java servers.SquareServer
```
Terminal 2:
```bash
javac -d . clientes/EchoClient.java && java clientes.EchoClient
```
Escribe un número y verás el cuadrado como respuesta.
 
---
 
## Ejercicio 4.3.2 — Servidor con función matemática seleccionable
 
El servidor calcula sin/cos/tan sobre el número recibido. Por defecto usa coseno. Se cambia la función enviando `fun:sin`, `fun:cos` o `fun:tan`.
 
```java
if (inputLine.startsWith("fun:")) {
    currentFunction = inputLine.substring(4).trim();
} else {
    double number = Double.parseDouble(inputLine);
    result = Math.sin(number); // o cos, tan según currentFunction
}
```
 
**Cómo probar** — Terminal 1:
```bash
java servers.FunctionServer
```
Terminal 2:
```bash
java clientes.EchoClient
```
Ejemplo de sesión:
```
0         → 1.0          (cos por defecto)
fun:sin   → Funcion cambiada a: sin
0         → 0.0
```
 
---
 
## Ejercicio 4.5.1 — Servidor web con múltiples solicitudes
 
Servidor HTTP que atiende solicitudes seguidas (no concurrentes), sirviendo archivos HTML e imágenes desde la carpeta `./www`.
 
```java
while (true) {
    Socket clientSocket = serverSocket.accept();
    handleRequest(clientSocket);  // lee GET, busca archivo, responde
}
```
 
La respuesta incluye headers HTTP con el tipo de contenido correcto:
```java
String headers = "HTTP/1.1 200 OK\r\n"
    + "Content-Type: " + contentType + "\r\n"
    + "Content-Length: " + fileBytes.length + "\r\n\r\n";
```
 
**Cómo probar** — Terminal 1:
```bash
javac -d . servers/MultiHttpServer.java && java servers.MultiHttpServer
```
Luego en el navegador:
```
http://localhost:35000/
http://localhost:35000/imagen.png
http://localhost:35000/noexiste.html   → 404
```
 
---
 
## Ejercicio 5.2.1 — Cliente de datagramas con actualización cada 5 segundos
 
El cliente pide la hora al servidor UDP cada 5 segundos. Si el servidor no responde, mantiene la última hora recibida usando `setSoTimeout`.
 
```java
socket.setSoTimeout(3000); // si no responde en 3s → SocketTimeoutException
try {
    socket.receive(response);
    lastTime = new String(response.getData(), 0, response.getLength());
} catch (SocketTimeoutException e) {
    System.out.println("Servidor no disponible. Manteniendo: " + lastTime);
}
Thread.sleep(5000);
```
 
**Cómo probar** — Terminal 1:
```bash
javac -d . servers/DatagramTimeServer.java && java servers.DatagramTimeServer
```
Terminal 2:
```bash
javac -d . clientes/DatagramTimeClient.java && java clientes.DatagramTimeClient
```
Apaga el servidor con `Ctrl+C` → el cliente mantiene la última hora. Reinicia el servidor → el cliente se actualiza automáticamente.

Ejemplo de funcionamiento:
<img width="1567" height="331" alt="image" src="https://github.com/user-attachments/assets/49d2da83-a07a-404c-9214-e9acecd0bb39" />

 
---
 
## Ejercicio 6.4.1 — Chat con RMI
 
Aplicativo de chat bidireccional usando RMI. Cada instancia publica su propio objeto remoto en un puerto y se conecta al objeto remoto del otro participante.
 
```java
// Publicar propio servicio
Registry registry = LocateRegistry.createRegistry(myPort);
registry.rebind("ChatService", stub);
 
// Conectarse al otro
Registry remoteRegistry = LocateRegistry.getRegistry(remoteIp, remotePort);
ChatService remote = (ChatService) remoteRegistry.lookup("ChatService");
remote.sendMessage("[" + name + "]: " + input);
```
 
**Cómo probar** — Iniciar **primero** Terminal 2 (Julián), esperar a que publique su servicio, luego Terminal 1 (Diego):
 
Terminal 2:
```bash
javac -d . servers/ChatService.java servers/ChatServiceImpl.java
java servers.ChatServiceImpl
# nombre: Julián | puerto propio: 23001 | IP remota: 127.0.0.1 | puerto remoto: 23000
```
Terminal 1:
```bash
java servers.ChatServiceImpl

# nombre: Diego | puerto propio: 23000 | IP remota: 127.0.0.1 | puerto remoto: 23001
```
Una vez conectados, lo que escribas en una terminal aparece en la otra.

Ejemplo de funcionamiento:
<img width="1515" height="396" alt="image" src="https://github.com/user-attachments/assets/fb309837-5d32-4d94-92ba-2a7507a66b96" />
