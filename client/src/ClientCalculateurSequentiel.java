import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import raytracer.Image;
import raytracer.Scene;

/**
 * Cette classe permet de gérer la distribution des tâches de calcul entre les noeuds disponibles mais sans utiliser de threads.
 */
public class ClientCalculateurSequentiel {

    private ServiceDistributeur distributeur;
    private ServiceClient client;

    /**
     * Constructeur 
     * 
     * @param distributeur le service de distribution qui permet de recup les noeuds disponibles
     * @param client le client qui recevra les résultats
     */
    public ClientCalculateurSequentiel(ServiceDistributeur distributeur, ServiceClient client) {
        this.distributeur = distributeur;
        this.client = client;
    }

    /**
     * Lance le calcul distribué d'une scène de raytracing sans utiliser les threads.
     * L'image est découpée en blocs de 128x128 pixels qui sont distribués
     * équitablement entre les noeuds disponibles.
     * 
     * @param scene la scène de raytracing à calculer
     * @param largeur largeur de l'image finale
     * @param hauteur hauteur de l'image finale
     */
    public void calculer(Scene scene, int largeur, int hauteur) {
        try {

            // recuperer la liste des noeuds disponibles
            ArrayList<ServiceNoeud> noeuds = distributeur.getNoeudsDisponibles();

            if (noeuds.isEmpty()) {
                System.err.println("Aucun noeud disponible pour le calcul ");
                return;
            }

            int cases = 128; // la taille des cases 
            int casesX = (int) Math.ceil((double) largeur / cases); // calcule du nombre de cases par lignes
            int casesY = (int) Math.ceil((double) hauteur / cases); // la meme mais pour les col 
            int nbMachine = noeuds.size();

            System.out.println("Calcul SEQUENTIEL de l'image :");
            System.out.println(" - Taille : " + largeur + "x" + hauteur);
            System.out.println(" - Découpage : " + cases + "x" + cases);
            System.out.println(" - Nombre de cases : " + (casesX * casesY));
            System.out.println(" - Noeuds disponibles : " + nbMachine);

            Instant debut = Instant.now();

            // envoyer la scene à tous les noeuds
            for (int i = noeuds.size() - 1; i >= 0; i--) {
                try {
                    noeuds.get(i).setScene(scene);
                } catch (Exception e) {
                    System.err.println("Noeud déconnecté lors de l'envoi de scène");
                    noeuds.remove(i);
                }
            }

            // recalculer apres suppression des noeuds qui marche pas 
            nbMachine = noeuds.size();
            if (nbMachine == 0) {
                System.err.println("aucun noeud disponible");
                return;
            }

            List<String> casesEchouees = new ArrayList<>();

            for (int caseY = 0; caseY < casesY; caseY++) {
                for (int caseX = 0; caseX < casesX; caseX++) {

                    // - x0 et y0 : correspondant au coin haut à gauche
                    int x0 = caseX * cases;
                    int y0 = caseY * cases;
                    // - l et h : hauteur et largeur de l'image calculée
                    int l = Math.min(cases, largeur - x0); // largeur de l'image (elle peut dépasser donc on prend le min)
                    int h = Math.min(cases, hauteur - y0); // même chose 

                    // caseY * casesX permet de sauter les lignes déjà calculées et le "+ caseX" c'est pour choper la pos actuelle 
                    int noeudIndex = (caseY * casesX + caseX) % nbMachine; // pour que la distribution des calculs soit équitable pour chaque machine

                    boolean succes = false;
                    int tentatives = 0;

                    while (!succes && tentatives < 3) {
                        try {
                            // essayer avec le noeud assigné d'abord
                            int nb = noeuds.size();
                            int indexActuel = (noeudIndex + tentatives) % nb;
                            ServiceNoeud noeud = noeuds.get(indexActuel);

                            System.out.println("Calcul case (" + x0 + "," + y0 + ") par noeud " + indexActuel);
                            Image image = noeud.calcul(x0, y0, l, h);
                            client.afficherImageCalcule(image, x0, y0);

                            System.out.println("Case (" + x0 + "," + y0 + ") calculée avec succès");
                            succes = true;

                        } catch (Exception e) {
                            tentatives++;
                            System.err.println("Tentative " + tentatives + " échouée pour case (" + x0 + "," + y0 + ") : " + e.getMessage());

                            if (tentatives >= 3) {
                                casesEchouees.add("(" + x0 + "," + y0 + ")");
                                System.err.println("Case (" + x0 + "," + y0 + ") échouée après 3 tentatives, fin ");
                            }
                        }
                    }
                }
            }

            Instant fin = Instant.now();
            long duree = Duration.between(debut, fin).toMillis();
            System.out.println("Image calculée en : " + duree + " ms");

            // permet de voir les calcules de cases qui ont échoué
            if (!casesEchouees.isEmpty()) {
                System.err.println("ATTENTION : " + casesEchouees.size() + " cases échouées :");
                for (String caseEchouee : casesEchouees) {
                    System.err.println("  - Case " + caseEchouee);
                }
            } else {
                System.out.println("Toutes les cases ont été calculées avec succès");
            }

        } catch (Exception e) {
            System.err.println("Erreur calcul  : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
