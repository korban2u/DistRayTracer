import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Serveur {

    public static String aide = "Raytracer : synthèse d'image par lancé de rayons (https://en.wikipedia.org/wiki/Ray_tracing_(graphics))\n\nUsage : java LancerRaytracer [fichier-scène] [largeur] [hauteur]\n\tfichier-scène : la description de la scène (par défaut simple.txt)\n\tlargeur : largeur de l'image calculée (par défaut 512)\n\thauteur : hauteur de l'image calculée (par défaut 512)\n";

    public static void main(String args[]) {

        try {
            int port = 1099;                      // le port de la rmiregistry par défaut 

            if (args.length > 0) {
                port = Integer.parseInt(args[0]);
            } else {
                System.out.println(aide);
            }


            Registry registry = LocateRegistry.getRegistry(port);

            ServiceDistributeur distributeur = new Distributeur();

            ServiceDistributeur rdDistributeur = (ServiceDistributeur) UnicastRemoteObject.exportObject(distributeur, 0);

            registry.rebind("Distributeur", rdDistributeur);

			System.out.println("Service prêt sur le port " + port);

        } catch (Exception e) {
            System.err.println("Erreur client: " + e.getMessage());
        }


    }
}
