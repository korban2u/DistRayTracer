import java.rmi.Remote;
import java.rmi.RemoteException;

import raytracer.Image;
import raytracer.Scene;

interface ServiceNoeud extends Remote{
  public Image calcul(Scene scene, int x0,int y0,int l,int h) throws RemoteException;
}