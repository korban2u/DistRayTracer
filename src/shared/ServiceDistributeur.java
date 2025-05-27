import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

interface ServiceDistributeur extends Remote {
  public void enregistrerNoeudCalcul(ServiceNoeud serviceNoeud) throws RemoteException;

  public List<ServiceNoeud> getNoeuds() throws RemoteException;
  
}