import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import raytracer.Disp;
import raytracer.Image;
import raytracer.Scene;

/**
 * Programme client qui utilise le service RMI
 */
public class Client {

    public static String aide = "Raytracer : synthèse d'image par lancé de rayons (https://en.wikipedia.org/wiki/Ray_tracing_(graphics))\n\nUsage : java LancerRaytracer [fichier-scène] [largeur] [hauteur]\n\tfichier-scène : la description de la scène (par défaut simple.txt)\n\tlargeur : largeur de l'image calculée (par défaut 512)\n\thauteur : hauteur de l'image calculée (par défaut 512)\n";

    public static void main(String[] args) {
        try {
            // Récupération de l'adresse IP locale
            String localIP = InetAddress.getLocalHost().getHostAddress();
            System.out.println("Client démarré sur " + localIP);
            
            // Paramètres du serveur (par défaut: localhost:1099)
            String serverIP = "localhost";
            int port = 1099;                      // le port de la rmiregistry par défaut 

            // Le fichier de description de la scène si pas fournie
            String fichier_description = "simple.txt";

            // largeur et hauteur par défaut de l'image à reconstruire
            int largeur = 1920, hauteur = 1080;


            // Récupération des paramètres du serveur depuis la ligne de commande
            if (args.length > 0) {
                fichier_description = args[0];
                if (args.length > 1) {
                    largeur = Integer.parseInt(args[1]);
                    if (args.length > 2)
                        hauteur = Integer.parseInt(args[2]);
                        if(args.length > 3)
                            serverIP = args[3];
                            if(args.length > 4){
                                port = Integer.parseInt(args[4]);
                            }

                }
            } else {
                System.out.println(aide);
            }
            
            System.out.println("Connexion au serveur " + serverIP + ":" + port);
            
            // Connexion au registre RMI du serveur
            Registry registry = LocateRegistry.getRegistry(serverIP, port);
            
            // Affichage des services disponibles
            System.out.println("Liste des Services disponibles:");
            String[] list = registry.list();
            for (int i = 0; i < list.length; i++){
              System.out.println("* " + list[i]);
            }
            
            ServiceDistributeur serviceDistributeur = (ServiceDistributeur)registry.lookup("Distributeur");


            List<ServiceNoeud> noeuds = serviceDistributeur.getNoeuds();
            int nbMachine = noeuds.size();
            
            // création d'une fenêtre 
            Disp disp = new Disp("Raytracer", largeur, hauteur);

            // Initialisation d'une scène depuis le modèle 
            Scene scene = new Scene(fichier_description, largeur, hauteur);

            // Calcul de l'image de la scène les paramètres : 
            
            
            // Ici on calcule toute l'image (0,0) -> (largeur, hauteur)


            // Chronométrage du temps de calcul
            Instant debut = Instant.now();

            int cases = 128; // la taille des cases 
            int casesX = (int) Math.ceil((double) largeur / cases); // calcule du nombre de casses par lignes
            int casesY = (int) Math.ceil((double) hauteur / cases); // la meme mais pour les colonnes 

            for (int caseY = 0; caseY < casesY; caseY++) {
                for (int caseX = 0; caseX < casesX; caseX++) {

                    // - x0 et y0 : correspondant au coin haut à gauche
                    int x0 = caseX * cases;
                    int y0 = caseY * cases;
                    // - l et h : hauteur et largeur de l'image calculée
                    int l = Math.min(cases, largeur - x0); // largeur de l'image (elle peucx depasser donc on prend le min)
                    int h = Math.min(cases, hauteur - y0); // meme chose 

                    // caseY * casesX permet de sauter les ligne deja calculé  et le + caseX c'est pour choper la pos actuelle 
                    int noeudIndex = (caseY * casesX + caseX) % nbMachine; // pour que la distribution des calcules soit equitable pour chaque machines


                    Thread thread = new Thread(()->{
                        try {
                            Image image = noeuds.get(noeudIndex).calcul(scene, x0, y0, l, h);
                            disp.setImage(image, x0, y0);
                        } catch (RemoteException e) {
                            System.out.println("la case  (" + x0 + "," + y0 + ")  a une erreur : " + e.getMessage());
                        }
                    });
                    thread.start();
                    
                }
            }


            
            Instant fin = Instant.now();

            long duree = Duration.between(debut, fin).toMillis();

            System.out.println("Image calculée en :" + duree + " ms");

            // Affichage de l'image calculée
            



            
            
            
        } catch (Exception e) {
            System.err.println("Erreur du client: " + e.toString());
            e.printStackTrace();
        }
    }
}