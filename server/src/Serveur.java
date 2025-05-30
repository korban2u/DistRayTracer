import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Classe pour démarrer le serveur
 */
public class Serveur {
    
    /**
     * Point d'entrée principal pour démarrer le serveur de distribution.
     * 
     * @param args (défaut : 1099)
     */
    public static void main(String[] args) {
        try {
            System.out.println("Démarrage du serveur...");
            
             int port = 1099;  // port par défaut 

            if (args.length > 0) {
                port = Integer.parseInt(args[0]);
            } 

            // recup l'annuaire 
            Registry registry = LocateRegistry.getRegistry(port);
            
            // créer le service
            ServiceDistributeur distributeur = new Distributeur();
            ServiceDistributeur rdDistributeur = (ServiceDistributeur) UnicastRemoteObject.exportObject(distributeur, 0);
            
            // enregistrer le service dans l'annuaire
            registry.bind("Distributeur", rdDistributeur);
            
            System.out.println("Service distibuteur prêt sur le port : " + port);
            System.out.println("En attente des noeuds et clients...");
            
        } catch (Exception e) {
            System.err.println("Erreur lors du démarrage du service : " + e.getMessage());
            e.printStackTrace();
        }
    }
}