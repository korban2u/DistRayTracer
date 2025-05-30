import java.rmi.Remote;
import java.rmi.RemoteException;

import raytracer.Image;
import raytracer.Scene;

/**
 * Interface pour le noeud
 */
public interface ServiceNoeud extends Remote {
    
    /**
     * Définit la scène que ce noeud utilisera pour ses calculs
     * 
     * @param scene la scène 
     * @throws RemoteException si une erreur de communication RMI se produit
     */
    void setScene(Scene scene) throws RemoteException;
    

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
    Image calcul(int x0, int y0, int largeur, int hauteur) throws RemoteException;
    
    /**
     * Méthode qui pemet de vérifier que le noeud est toujours disponible
     * 
     * @throws RemoteException si une erreur de communication RMI se produit
     */
    void ping() throws RemoteException; // Pour tester la connectivité
}