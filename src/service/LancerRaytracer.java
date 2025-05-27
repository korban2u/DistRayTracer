import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.time.Duration;
import java.time.Instant;

import raytracer.Disp;
import raytracer.Image;
import raytracer.Scene;

public class LancerRaytracer {

    public static String aide = "Raytracer : synthèse d'image par lancé de rayons (https://en.wikipedia.org/wiki/Ray_tracing_(graphics))\n\nUsage : java LancerRaytracer [fichier-scène] [largeur] [hauteur]\n\tfichier-scène : la description de la scène (par défaut simple.txt)\n\tlargeur : largeur de l'image calculée (par défaut 512)\n\thauteur : hauteur de l'image calculée (par défaut 512)\n";

    public static void main(String args[]) {

        try {
            int port = 1099;                      // le port de la rmiregistry par défaut 

            // Le fichier de description de la scène si pas fournie
            String fichier_description = "simple.txt";

            // largeur et hauteur par défaut de l'image à reconstruire
            int largeur = 1920, hauteur = 1080;

            if (args.length > 0) {
                fichier_description = args[0];
                if (args.length > 1) {
                    largeur = Integer.parseInt(args[1]);
                    if (args.length > 2)
                        hauteur = Integer.parseInt(args[2]);
                }
            } else {
                System.out.println(aide);
            }


            Registry registry = LocateRegistry.getRegistry(port);

            ServiceNoeud noeud = new Noeud();

            ServiceDistributeur rdDistributeur = (ServiceDistributeur) UnicastRemoteObject.exportObject(noeud, 0);

            registry.rebind("Distributeur", rdDistributeur);

            // création d'une fenêtre 
            Disp disp = new Disp("Raytracer", largeur, hauteur);

            // Initialisation d'une scène depuis le modèle 
            Scene scene = new Scene(fichier_description, largeur, hauteur);

            // Calcul de l'image de la scène les paramètres : 
            // - x0 et y0 : correspondant au coin haut à gauche
            // - l et h : hauteur et largeur de l'image calculée
            // Ici on calcule toute l'image (0,0) -> (largeur, hauteur)

            int x0 = 0, y0 = 0;
            int l = largeur / 2, h = hauteur / 2;

            // Chronométrage du temps de calcul
            Instant debut = Instant.now();
            System.out.println("Calcul de l'image :\n - Coordonnées : " + x0 + "," + y0
                    + "\n - Taille " + largeur + "x" + hauteur);
            Image image = scene.compute(x0, y0, l, h);

            Image image2 = scene.compute(l, h, l, h);
            Instant fin = Instant.now();

            long duree = Duration.between(debut, fin).toMillis();

            System.out.println("Image calculée en :" + duree + " ms");

            // Affichage de l'image calculée
            disp.setImage(image, x0, y0);
            disp.setImage(image2, l, h);

    } catch( Exception e) {
        System.err.println("Erreur client: " + e.getMessage());
    }

    }

}
