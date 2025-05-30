import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Implémentation du service de distribution
 */
public class Distributeur implements ServiceDistributeur {
    
    private ArrayList<ServiceNoeud> noeuds = new ArrayList<>();
    
    /**
     * Enregistre un nouveau noeud chez le distributeur (serveur).
     * 
     * @param noeud le noeud à enregistrer
     * @throws RemoteException si une erreur de communication RMI se produit
     */
    @Override
    public void enregistrerNoeud(ServiceNoeud noeud) throws RemoteException {
        synchronized (noeuds) {
            noeuds.add(noeud);
            System.out.println("Noeud enregistré. Total : " + noeuds.size());
        }
    }
    
    /**
     * Récupère la liste des noeuds actuellement disponibles et supprime les noeuds déconnectés
     * 
     * @return une nouvelle liste contenant tous les noeuds disponibles
     * @throws RemoteException si une erreur de communication RMI se produit
     */
    @Override
    public ArrayList<ServiceNoeud> getNoeudsDisponibles() throws RemoteException {
        synchronized (noeuds) {
            // on nettoit les noeuds déconnecté 
            for (int i = noeuds.size() - 1; i >= 0; i--) {
                try {
                    // ping pour voir si le noeud est dispo
                    noeuds.get(i).ping();
                } catch (Exception e) {
                    System.out.println("Noeud déconnecté détecté, suppression...");
                    noeuds.remove(i);
                }
            }
            System.out.println("Liste des noeuds demandée. Noeuds disponibles : " + noeuds.size());
            return new ArrayList<>(noeuds);
        }
    }
    
    /**
     * Supprime un noeud qui marche pas de la liste des noeuds disponibles.
     * 
     * @param noeud le noeud à supprimer
     * @throws RemoteException si une erreur de communication RMI se produit
     */
    @Override
    public void supprimerNoeudDefaillant(ServiceNoeud noeud) throws RemoteException {
        synchronized (noeuds) {
            if (noeuds.remove(noeud)) {
                System.out.println("Noeud défaillant supprimé. Noeuds restants: " + noeuds.size());
            }
        }
    }
}