import java.net.*;
import java.io.*;
import java.nio.file.Files;

public class MultiHttpServer {

    private static final int PORT = 35000;
    // Carpeta donde están los archivos a servir
    private static final String WEB_ROOT = "./www";

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Servidor web escuchando en puerto " + PORT + "...");

        // Múltiples solicitudes seguidas (no concurrentes)
        while (true) {
            Socket clientSocket = serverSocket.accept();
            handleRequest(clientSocket);
        }
    }

    private static void handleRequest(Socket clientSocket) throws IOException {
        BufferedReader in = new BufferedReader(
            new InputStreamReader(clientSocket.getInputStream()));
        OutputStream out = clientSocket.getOutputStream();

        // Leer la primera línea: "GET /archivo.html HTTP/1.1"
        String requestLine = in.readLine();
        if (requestLine == null || requestLine.isEmpty()) {
            clientSocket.close();
            return;
        }

        System.out.println("Request: " + requestLine);

        // Leer y descartar el resto de headers
        while (in.ready()) {
            in.readLine();
        }

        // Extraer el path del archivo solicitado
        String[] parts = requestLine.split(" ");
        String filePath = parts.length > 1 ? parts[1] : "/";

        // Si pide la raíz, servir index.html
        if (filePath.equals("/")) {
            filePath = "/index.html";
        }

        File file = new File(WEB_ROOT + filePath);

        if (file.exists() && !file.isDirectory()) {
            // Detectar tipo de contenido
            String contentType = getContentType(filePath);
            byte[] fileBytes = Files.readAllBytes(file.toPath());

            // Respuesta HTTP 200
            String headers = "HTTP/1.1 200 OK\r\n"
                + "Content-Type: " + contentType + "\r\n"
                + "Content-Length: " + fileBytes.length + "\r\n"
                + "Connection: close\r\n"
                + "\r\n";

            out.write(headers.getBytes());
            out.write(fileBytes);
            System.out.println("Sirviendo: " + filePath + " (" + contentType + ")");
        } else {
            // 404
            String body = "<html><body><h1>404 - Archivo no encontrado: "
                + filePath + "</h1></body></html>";
            String headers = "HTTP/1.1 404 Not Found\r\n"
                + "Content-Type: text/html\r\n"
                + "Content-Length: " + body.length() + "\r\n"
                + "Connection: close\r\n"
                + "\r\n";
            out.write(headers.getBytes());
            out.write(body.getBytes());
            System.out.println("404: " + filePath);
        }

        out.flush();
        out.close();
        in.close();
        clientSocket.close();
    }

    private static String getContentType(String path) {
        if (path.endsWith(".html")) return "text/html";
        if (path.endsWith(".css"))  return "text/css";
        if (path.endsWith(".js"))   return "application/javascript";
        if (path.endsWith(".png"))  return "image/png";
        if (path.endsWith(".jpg") || path.endsWith(".jpeg")) return "image/jpeg";
        if (path.endsWith(".gif"))  return "image/gif";
        if (path.endsWith(".ico"))  return "image/x-icon";
        return "application/octet-stream";
    }
}