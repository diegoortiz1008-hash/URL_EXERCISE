import java.io.*;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class URLReaderSpecific {
    
        public static void main(String[] args) throws Exception {
            Scanner console = new Scanner(System.in);
            System.out.print("Introduce la url: ");
            String url = console.nextLine();
            String archivoSalida = "resultado.html";
            HttpClient cliente = HttpClient.newHttpClient();

        HttpRequest peticion = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        try {
            System.out.println("Conectando a " + url + "...");
            
            HttpResponse<String> respuesta = cliente.send(peticion, HttpResponse.BodyHandlers.ofString());

            int codigoEstado = respuesta.statusCode();
            
            if (codigoEstado == 200) {
                String contenidoHTML = respuesta.body();
                Files.write(Paths.get(archivoSalida), contenidoHTML.getBytes());
                
                System.out.println("¡Éxito! El HTML de la página se ha guardado en: " + archivoSalida);
            } else {
                System.out.println("No se pudo descargar. El servidor respondió con el código: " + codigoEstado);
            }

        } catch (IOException | InterruptedException e) {
            System.out.println("Ocurrió un error en la comunicación o al guardar el archivo.");
            e.printStackTrace();
        }
    }

}