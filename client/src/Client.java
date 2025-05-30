import java.rmi.RemoteException;

import raytracer.Disp;
import raytracer.Image;

/**
 * Implémentation du client qui affiche les d'image calculées par les services Noeuds
 */
public class Client implements ServiceClient {
    private Disp disp;
    private int blocsRecus = 0;

    /**
     * Constructeur
     * 
     * @param disp l'afficheur d'image 
     */
    public Client(Disp disp) {
        this.disp = disp;
        System.out.println("Client créé");
    }

    /**
     * Reçoit et affiche une partie d'image calculée par un noeud distant (machines).
     * 
     * @param img l'image calculée à afficher
     * @param x coordonnée x de l'image dans l'interface grafique
     * @param y coordonnée y (meme chose)
     * @throws RemoteException si une erreur de communication RMI se produit genre une deconnection
     */
    @Override
    public void afficherImageCalcule(Image img, int x, int y) throws RemoteException {
        synchronized (this) {
            blocsRecus++;
            System.out.println("Client - Réception bloc " + blocsRecus + " à la position (" + x + "," + y + ")");
            disp.setImage(img, x, y);
        }
    }
    
    /**
     * Retourne le nombre total de partie d'image reçus depuis le début du calcul.
     * 
     * @return le nombre de de partie d'images reçus
     */
    public int getBlocsRecus() {
        return blocsRecus;
    }
}