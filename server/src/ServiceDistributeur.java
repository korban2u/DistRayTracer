import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Interface Distributeur.
 */
public interface ServiceDistributeur extends Remote {
    
    /**
    /**
     * Enregistre un nouveau noeud chez le distributeur (serveur).
     * 
     * @param noeud le noeud à enregistrer
     * @throws RemoteException si une erreur de communication RMI se produit
     */
    void enregistrerNoeud(ServiceNoeud noeud) throws RemoteException;

    /**
     * Récupère la liste des noeuds actuellement disponibles et supprime les noeuds déconnectés
     * 
     * @return une nouvelle liste contenant tous les noeuds disponibles
     * @throws RemoteException si une erreur de communication RMI se produit
     */
    ArrayList<ServiceNoeud> getNoeudsDisponibles() throws RemoteException;

    /**
     * Supprime un noeud qui marche pas de la liste des noeuds disponibles.
     * 
     * @param noeud le noeud à supprimer
     * @throws RemoteException si une erreur de communication RMI se produit
     */
    void supprimerNoeudDefaillant(ServiceNoeud noeud) throws RemoteException;
}