import java.util.HashMap;
import java.lang.Math;

/**
 * @author Yohann Manseau-Glemot (20217138) et Alex Chevrier(20216495)
 * Classe supplementaire permettant de lire des mots et de conserver des informations sur les mots suivants dans un fichier.
 */
public class LectureMots {
  // Mot actuel en train d'être lu
  private String motActuel;
  // Mapping du fichier contenant les mots
  private FileMap mappingFichier;
  // Map contenant les mots suivants et leur fréquence d'apparition
  private HashMap<String, Integer> motsSuivants;

  /**
   * Constructeur par défaut.
   */
  public LectureMots() {

  }

  /**
   * Constructeur permettant de définir le mot actuel et le mapping du fichier.
   * @param mot Le mot actuel.
   * @param mapFichier Le mappage du fichier.
   */
  public LectureMots(String mot, FileMap mapFichier) {

    motsSuivants = new HashMap<>();
    motActuel = mot;
    mappingFichier = mapFichier;
    

  }

  /**
   * Constructeur permettant de définir le mot actuel, le mapping du fichier et le mot suivant.
   * @param mot Le mot actuel.
   * @param mapFichier Le mapping du fichier.
   * @param motSuivant Le mot suivant.
   */
  public LectureMots(String mot, FileMap mapFichier, String motSuivant) {

    this(mot, mapFichier);
    ajouterMotSuivant(motSuivant);
  }

  /**
   * Méthode permettant d'obtenir le mot actuel.
   * @return Le mot actuel.
   */
  public String getMotActuel() {

    return motActuel;
  }

  /**
   * Méthode permettant d'obtenir le mapping du fichier.
   * @return Le mappage du fichier.
   */
  public FileMap getMapFichier() {

    return mappingFichier;
  }

  /**
   * Méthode permettant d'obtenir le bigramme (paire de mots consécutifs) le plus fréquent dans la map.
   * Si la map est vide, retourne null.
   * @return Le bigramme le plus fréquent dans la map.
   */
  public String bigrammePlusFrequent() {

    // Bigramme le plus fréquent actuellement connu
    HashMap.Entry<String, Integer> bigrammeLePlusFréquent = null;

    // Parcours de tous les bigrammes de la map
    for (HashMap.Entry<String, Integer> bigramme : motsSuivants.entrySet()) {

      // Si le bigramme actuel est plus fréquent que le précédemment connu, on le définit comme le plus fréquent
      if (bigrammeLePlusFréquent == null || bigramme.getValue() > bigrammeLePlusFréquent.getValue()) {

        bigrammeLePlusFréquent = bigramme;
      }
      // Si le bigramme actuel est aussi fréquent que le précédemment connu et que son premier mot est lexicographiquement inférieur, on le définit comme le plus fréquent
      else if (bigramme.getValue() == bigrammeLePlusFréquent.getValue()) {

        if(bigrammeLePlusFréquent.getKey().compareTo(bigramme.getKey()) > 0){

          bigrammeLePlusFréquent = bigramme;
        }

       
      }
    }

    // Retour du bigramme le plus fréquent
    return bigrammeLePlusFréquent.getKey();
  }

  /**
   * Méthode permettant d'ajouter un nouveau mot suivant à la map. Si le mot suivant existe déjà,
   * on incrémente sa fréquence d'apparition. Si le mot suivant est null, aucune action n'est effectuée.
   * @param nouveauMotSuivant Le nouveau mot suivant à ajouter à la map.
   */
  public void ajouterMotSuivant(String nouveauMotSuivant) {

    if (nouveauMotSuivant == null) {

      return;
    }

    if (motsSuivants.containsKey(nouveauMotSuivant)) {

      // Incrémentation de la fréquence d'apparition du mot suivant
      int fréquence = motsSuivants.get(nouveauMotSuivant);

      fréquence++;
      motsSuivants.put(nouveauMotSuivant, fréquence);

    } 
    else {

      // Ajout du nouveau mot suivant à la map avec une fréquence d'apparition de 1
      motsSuivants.put(nouveauMotSuivant, 1);
    }
  }

  /**
   * Méthode permettant de définir le mappage du fichier et de retourner l'ancien mappage.
   * @param mapFichier Le nouveau mappage du fichier.
   * @return L'ancien mappage du fichier.
   */
  public FileMap setMapFichier(FileMap mapFichier) {

    // Enregistrement de l'ancien mappage du fichier
    FileMap mapFichierPrécédent = mappingFichier;

    // Définition du nouveau mappage du fichier
    mappingFichier = mapFichier;

    // Retour de l'ancien mappage du fichier
    return mapFichierPrécédent;
  }
}