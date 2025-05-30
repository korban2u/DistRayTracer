import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Classe pour lancer un noeud qui va se connecter au serveur Distributeur
 */
public class LancerNoeud {
    
    /**
     * Lance un noeud et l'enregistrer auprès du distributeur.
     * 
     * @param args arguments de ligne de commande :
     *             args[0] : adresse IP du serveur (défaut : localhost)
     *             args[1] : port du serveur (défaut : 1099)
     */
    public static void main(String[] args) {
        String ip = "localhost";
        int port = 1099;
        
        if (args.length > 0) {
            ip = args[0];
        }
        if (args.length > 1) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("Port invalide : " + args[1] + ". Utilisation de 1099.");
            }
        }
        
        try {
            System.out.println("Connexion au service central sur " + ip + " : " + port);
            
            Noeud noeud = new Noeud();
            ServiceNoeud rd = (ServiceNoeud) UnicastRemoteObject.exportObject(noeud, 0);
            
            // connection au registre
            Registry registry = LocateRegistry.getRegistry(ip, port);
            ServiceDistributeur serviceDistributeur = (ServiceDistributeur) registry.lookup("Distributeur");
            
            // s'enregistrer chez le disztributeur
            serviceDistributeur.enregistrerNoeud(rd);
            
            System.out.println("Noeud enregistré avec succès!");
            System.out.println("En attente de tâches de calcul...");

            
        } catch (Exception e) {
            System.err.println("Erreur lors de l'enregistrement du noeud : " + e.getMessage());
            e.printStackTrace();
        }
    }
}