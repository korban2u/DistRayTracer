import raytracer.Image;
import raytracer.Scene;
import java.rmi.RemoteException;
import java.time.Duration;
import java.time.Instant;
import java.net.InetAddress;

/**
 * Implémentation du noeud qui calculent des partie d'images
 */
public class Noeud implements ServiceNoeud {

    private Scene scene;
    private String monId;

    /**
     * Constructeur 
     */
    public Noeud() {
        try {
            // récupérer l'adresse IP locale
            String ip = InetAddress.getLocalHost().getHostAddress();
            this.monId = ip;
            System.out.println("Noeud " + monId + " créé");
        } catch (Exception e) {
            this.monId = "inconnu";
            System.out.println("Noeud " + monId + " créé (IP non détectée)");
        }
    }

    /**
     * Fait le calcul de raytracing pour un petit bloc de l'image entière.
     * 
     * @param x0 coordonnée X du coin supérieur gauche de la zone à calculer
     * @param y0 coordonnée Y du coin supérieur gauche de la zone à calculer
     * @param largeur largeur de la zone à calculer
     * @param hauteur hauteur de la zone à calculer
     * @return l'image calculée pour la zone spécifiée
     * @throws RemoteException si une erreur de communication RMI se produit
     * @throws IllegalStateException si aucune scène n'a été donnée au noeud
     */
    @Override
    public Image calcul(int x0, int y0, int largeur, int hauteur) throws RemoteException {
        if (scene == null) {
            throw new IllegalStateException("Scene non fournie au noeud " + monId);
        }

        System.out.println("Noeud " + monId + " - Calcul bloc (" + x0 + "," + y0 + ") " + largeur + "x" + hauteur);
        
        Instant debut = Instant.now();
        Image image = scene.compute(x0, y0, largeur, hauteur);
        Instant fin = Instant.now();

        long duree = Duration.between(debut, fin).toMillis();
        System.out.println("Noeud " + monId + " - Bloc calculé en : " + duree + " ms");

        return image;
    }

    /**
     * Définit la scène que ce noeud utilisera pour ses calculs
     * 
     * @param scene la scène 
     * @throws RemoteException si une erreur de communication RMI se produit
     */
    @Override
    public void setScene(Scene scene) throws RemoteException {
        this.scene = scene;
        System.out.println("Noeud " + monId + " - Scène reçue");
    }
    
    /**
     * Méthode qui pemet de vérifier que le noeud est toujours disponible
     * 
     * @throws RemoteException si une erreur de communication RMI se produit
     */
    @Override
    public void ping() throws RemoteException {
        System.out.println("Noeud " + monId + " - Ping reçu");
    }
}