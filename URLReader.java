import java.io.*;
import java.net.*;

public class URLReader {

    public static void main(String[] args) throws Exception {
        URL google = new URL("https://www.google.com");
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(google.openStream()))) {
            String inputLine = null;
            while ((inputLine = reader.readLine()) != null) {
                System.out.println(inputLine);
            }
        } catch (IOException x) {   
            System.err.println("X");
        }
        System.out.println(google.getProtocol());
        System.out.println(google.getAuthority());
        System.out.println(google.getHost());
        int port = google.getPort();
        System.out.println(port == -1 ? google.getDefaultPort() : port);
        System.out.println(google.getPath());
        System.out.println(google.getQuery());
        System.out.println(google.getFile());
        System.out.println(google.getRef());
    }
}