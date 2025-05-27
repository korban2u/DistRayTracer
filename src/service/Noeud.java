import java.io.Serializable;
import java.rmi.RemoteException;

import raytracer.Image;
import raytracer.Scene;

public class Noeud implements ServiceNoeud,Serializable{

  public Image calcul(Scene scene, int x0,int y0,int l,int h) throws RemoteException{
    Image image = scene.compute(x0, y0, l, h);

    return image;
  }

}