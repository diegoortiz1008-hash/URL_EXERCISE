package servers;

import java.net.*;
import java.io.*;

public class FunctionServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(35000);
        System.out.println("Servidor de funciones esperando...");

        Socket clientSocket = serverSocket.accept();
        System.out.println("Cliente conectado.");

        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(
            new InputStreamReader(clientSocket.getInputStream()));

        String currentFunction = "cos";
        String inputLine;

        while ((inputLine = in.readLine()) != null) {
            System.out.println("Recibido: " + inputLine);
            inputLine = inputLine.trim();

            if (inputLine.startsWith("fun:")) {
                String newFun = inputLine.substring(4).trim().toLowerCase();
                if (newFun.equals("sin") || newFun.equals("cos") || newFun.equals("tan")) {
                    currentFunction = newFun;
                    out.println("Funcion cambiada a: " + currentFunction);
                } else {
                    out.println("Funcion no reconocida. Usa: sin, cos, tan");
                }
            } else {
                try {
                    double number = Double.parseDouble(inputLine);
                    double result;
                    switch (currentFunction) {
                        case "sin": result = Math.sin(number); break;
                        case "tan": result = Math.tan(number); break;
                        default:    result = Math.cos(number); break;
                    }
                    out.println(result);  // solo el número
                } catch (NumberFormatException e) {
                    out.println("Error: envia un numero o fun:sin|cos|tan");
                }
            }
        }

        out.close();
        in.close();
        clientSocket.close();
        serverSocket.close();
    }
}