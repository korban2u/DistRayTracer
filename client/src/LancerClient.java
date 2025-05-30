import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

import raytracer.Disp;
import raytracer.Scene;

/**
 * Classe pour lancer le raytracing.
 */
public class LancerClient {
    public static String aide = "Raytracer : synthèse d'image par lancé de rayons (https://en.wikipedia.org/wiki/Ray_tracing_(graphics))\n\nUsage : java LancerRaytracer [adresse ip] [port]  [largeur] [hauteur] [fichier-scène]\n\tfichier-scène : la description de la scène (par défaut simple.txt)\n\tlargeur : largeur de l'image calculée (par défaut 512)\n\thauteur : hauteur de l'image calculée (par défaut 512)\n";

    /**
     * Point d'entrée pour lancer le client de raytracing distribué.
     */
    public static void main(String[] args) {
        String ip = "localhost";
        int port = 1099;
        int largeur = 512;
        int hauteur = 512;
        String fichierScene = "simple.txt";

        boolean erreurArguments = false;

        if (args.length > 0) ip = args[0];
        if (args.length > 1) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("Port invalide : " + args[1]);
                erreurArguments = true;
            }
        }
        if (args.length > 2) {
            try {
                largeur = Integer.parseInt(args[2]);
                hauteur = largeur;
            } catch (NumberFormatException e) {
                System.err.println("Taille invalide : " + args[2]);
                erreurArguments = true;
            }
        }
        if (args.length > 3) {
            try {
                hauteur = Integer.parseInt(args[3]);
            } catch (NumberFormatException e) {
                System.err.println("Hauteur invalide : " + args[3]);
                erreurArguments = true;
            }
        }
        if (args.length > 4) {
            fichierScene = args[4];
        }

        if (erreurArguments) {
            System.out.println(aide);
            return;
        }

        try {
            System.out.println("Lancement du client raytracing :");
            System.out.println(" - Serveur : " + ip + " :" + port);
            System.out.println(" - Taille image : " + largeur + "x" + hauteur);
            System.out.println(" - Fichier scène : " + fichierScene);

            // demande à l'utilisateur s'il veut utiliser les threads ou non
            Scanner scanner = new Scanner(System.in);
            System.out.println("Souhaitez-vous utiliser les threads (+ rapide) ? (o/n) [o = oui, n = non]");
            String reponse = scanner.nextLine().trim().toLowerCase();

            Disp disp = new Disp("Raytracing", largeur, hauteur);
            Client client = new Client(disp);
            ServiceClient serviceClient = (ServiceClient) UnicastRemoteObject.exportObject(client, 0);

            // on se connecte au registry
            Registry registry = LocateRegistry.getRegistry(ip, port);
            ServiceDistributeur distributeur = (ServiceDistributeur) registry.lookup("Distributeur");

            Scene scene = new Scene(fichierScene, largeur, hauteur);


            System.out.println("Lancement du calcul...");

            if (reponse.equals("n") || reponse.equals("non")) {
                ClientCalculateurSequentiel calculateur = new ClientCalculateurSequentiel(distributeur, serviceClient);
                calculateur.calculer(scene, largeur, hauteur);
            } else {
                ClientCalculateurThread calculateur = new ClientCalculateurThread(distributeur, serviceClient);
                calculateur.calculer(scene, largeur, hauteur);
            }

            System.out.println("Calcul terminé ! " + client.getBlocsRecus() + " blocs reçus.");

        } catch (Exception e) {
            System.err.println("Erreur client : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
