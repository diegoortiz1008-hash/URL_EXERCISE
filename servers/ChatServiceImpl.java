package servers;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

public class ChatServiceImpl implements ChatService {

    private String name;

    public ChatServiceImpl(String name) {
        this.name = name;
    }

    @Override
    public void sendMessage(String message) throws RemoteException {
        System.out.println("[Mensaje recibido]: " + message);
    }

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Ingresa tu nombre: ");
        String name = scanner.nextLine();

        System.out.print("Ingresa el puerto para publicar tu servicio: ");
        int myPort = Integer.parseInt(scanner.nextLine());

        Registry registry = LocateRegistry.createRegistry(myPort);

        ChatServiceImpl impl = new ChatServiceImpl(name);
        ChatService stub = (ChatService) UnicastRemoteObject.exportObject(impl, 0);
        registry.rebind("ChatService", stub);
        System.out.println("Servicio publicado en puerto " + myPort);

        System.out.print("Ingresa la IP del otro participante: ");
        String remoteIp = scanner.nextLine();

        System.out.print("Ingresa el puerto del otro participante: ");
        int remotePort = Integer.parseInt(scanner.nextLine());

        Registry remoteRegistry = LocateRegistry.getRegistry(remoteIp, remotePort);
        ChatService remoteChatService = (ChatService) remoteRegistry.lookup("ChatService");
        System.out.println("Conectado! Puedes empezar a chatear.");
        System.out.println("(escribe 'exit' para salir)\n");

        while (true) {
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("exit")) break;
            remoteChatService.sendMessage("[" + name + "]: " + input);
        }

        scanner.close();
        System.exit(0);
    }
}