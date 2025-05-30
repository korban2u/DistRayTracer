import java.rmi.Remote;
import java.rmi.RemoteException;

import raytracer.Image;

/**
 * Interface du services client 
 * Permet aux noeuds (les machines) d'envoyer leurs résultats au client.
 */
public interface ServiceClient extends Remote {
    
    /**
     * Reçoit et affiche une partie d'image calculée par un noeud distant (machines).
     * 
     * @param img l'image calculée à afficher
     * @param x coordonnée x de l'image dans l'interface grafique
     * @param y coordonnée y (meme chose)
     * @throws RemoteException si une erreur de communication RMI se produit genre une deconnection
     */
    void afficherImageCalcule(Image img, int x, int y) throws RemoteException;
}