import java.net.InetAddress;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ServeurNoeud {

    public static void main(String[] args) {
        try {
            // Récupération de l'adresse IP locale
            String localIP = InetAddress.getLocalHost().getHostAddress();
            System.out.println("Client démarré sur " + localIP);

            // Paramètres du serveur (par défaut: localhost:1099)
            String serverIP = "localhost";
            int port = 1099;                      // le port de la rmiregistry par défaut 
			if(args.length > 0){
				serverIP=args[0];
				if(args.length > 1){
					port = Integer.parseInt(args[1]);
				}
			}

            System.out.println("Connexion au serveur " + serverIP + ":" + port);

            // Connexion au registre RMI du serveur
            Registry registry = LocateRegistry.getRegistry(serverIP, port);


            ServiceDistributeur serviceDistributeur = (ServiceDistributeur) registry.lookup("Distributeur");

			ServiceNoeud noeud = new Noeud();

            ServiceNoeud rd = (ServiceNoeud) UnicastRemoteObject.exportObject(noeud, 0);


			serviceDistributeur.enregistrerNoeudCalcul(rd);
			
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


}