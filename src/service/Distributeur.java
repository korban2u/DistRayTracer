import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class Distributeur implements ServiceDistributeur {

  List <ServiceNoeud> serviceNoeuds = new ArrayList<ServiceNoeud>();

  public void enregistrerNoeudCalcul(ServiceNoeud noeud) throws RemoteException{
    this.serviceNoeuds.add(noeud);
  }

  public List<ServiceNoeud> getNoeuds() throws RemoteException{

    return this.serviceNoeuds;
  }
}
