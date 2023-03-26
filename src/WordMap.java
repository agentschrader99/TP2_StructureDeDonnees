import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.lang.Math;

/**
 * @author Yohann Manseau-Glémot(20217138) et Alex Chevrier(20216495)

 *
 *WordMap permet de stocker un mot et un FileMap qui lui est associé où ses occurrences dans chaque fichier sont stockées
 L'implementation de wordmap est semblable à celle de filemap
 */
public class WordMap implements Map<String, FileMap> {

    private int tailleMax;//Taille maximal de wordmap
    private int taille;//taille de wordmap
    private WordMapEntry[] tab;
    private float facteur;
   
    //Constructeur de wordMap
    public WordMap() {

        facteur = (float) 0.75;//facteur initialise a 0.75
        tailleMax = 1;
        taille = 0;
        redimensionner();
    }

    //Déclaration de la classe deleted qui implémente WordMapEntry plus bas
    private class deleted extends WordMapEntry{

        protected deleted() {

            super();
        }

    }
    //Déclaration de la classe static WordMapEntry qui implémente Map.Entry, permet de gerer les paire dentrees String et FileMap
    static class WordMapEntry extends LectureMots implements Map.Entry<String, FileMap> {

        //Getter de la cle de wordmap
        @Override
        public String getKey() {

            return getMotActuel();
        }
        //Setter de la valeur
        @Override
        public FileMap setValue(FileMap valeur) {

            return setMapFichier(valeur);
        } 
        //Constructeur de WordMapEntry
        private WordMapEntry(String cle, FileMap valeur) {

            super(cle, valeur);
        }
        //Constructeur par defaut
        protected WordMapEntry() {}

        //Getter de la valeur de wordmap
        @Override
        public FileMap getValue() {

            return getMapFichier();
        }
        
    }
    
    // Méthode override de "containsKey" qui renvoie "true" si la clé est présente dans le tableau "tab"
    @Override
    public boolean containsKey(Object key) {

        // Recherche de la clé dans le tableau
        int index = trouverCle(key);

        // Vérification de la présence de la clé et de la valeur associée
        boolean bool = (tab[index] != null);

        boolean bool2 = !(tab[index] instanceof deleted);

        // Renvoi du résultat
        return bool && bool2;
    }

    // Méthode override de "containsValue" qui renvoie "true" si la valeur passée en paramètre est présente dans le tableau "tab"
    @Override
    public boolean containsValue(Object value) {

        // Parcours de chaque entrée du tableau
        for (WordMapEntry wme : tab) {

            // Vérification de la présence de la valeur
            if(wme != null) {

                if(wme.getValue().equals(value) == true){

                    // Renvoi de "true" si la valeur est trouvée
                    return true;
                }
            }
        }
        // Renvoi de "false" si la valeur n'a pas été trouvée
        return false;
    }

    // Méthode override de "get" qui renvoie la valeur associée à la clé passée en paramètre
    @Override
    public FileMap get(Object key) {

        // Recherche de la clé dans le tableau
        int index = trouverCle(key);

        // Vérification de la présence de la clé et de la valeur associée
        if((tab[index] != null)) {

           

            // Renvoi de la valeur associée à la clé
            FileMap valeurR = tab[index].getValue();

            return valeurR;
           
        }
        // Renvoi de "null" si la clé ou la valeur n'ont pas été trouvées
        return null;
    }
        // Méthode override de "put" qui ajoute une entrée dans "wordMap" et redimensionne le tableau si nécessaire
    @Override
    public FileMap put(String key, FileMap value) {

        // Création de l'entrée à ajouter
        WordMapEntry wme = new WordMapEntry(key, value);

        // Redimensionnement du tableau si nécessaire
        if(taille >=limite()) {

            redimensionner();
        }
        // Incrémentation de la taille de "wordMap"
        taille++;

        // Ajout de l'entrée dans le tableau
        return inserer(wme);
    }

    // Méthode override de "remove" qui retire une clé de "wordMap"
    @Override
    public FileMap remove(Object key) {

        // Recherche de la clé dans le tableau
        int index = trouverCle(key);

        // Vérification de la présence de la clé et de la valeur associée
        if((tab[index] != null)) {

            // Récupération de la valeur associée à la clé avant suppression
            FileMap prevValue = tab[index].getValue();

            // Suppression de la clé et de la valeur associée
            tab[index] = new deleted();

            // Décrémentation de la taille de "wordMap"
            taille--;

            // Renvoi de la valeur associée à la clé avant suppression
            return prevValue;
          
        }
        // Renvoi de "null" si la clé ou la valeur n'ont pas été trouvées
        return null;
    }

    // Méthode override de "putAll" qui ajoute toutes les paires clé-valeur en utilisant la méthode "put"
    @Override
    public void putAll(Map<? extends String, ? extends FileMap> m) {

        // Parcours de chaque entrée de la map passée en paramètre
        for (Map.Entry<? extends String, ? extends FileMap> entree : m.entrySet()) {

            // Ajout de l'entrée dans "wordMap"
            put(entree.getKey(), entree.getValue());
        }
    }

    // Méthode override de "clear" qui permet d'effacer "wordMap"
    @Override
    public void clear() {
        // Réinitialisation de la taille de "wordMap"
        taille = 0;
        // Effacement de toutes les entrées du tableau "tab"
        for (int i = 0; i < tailleMax; i++) {
            tab[i] = null;
        }
    }

    // Méthode override de "keySet" qui crée un set de toutes les clés de "wordMap"
    @Override
    public Set<String> keySet() {

        // Création d'un set de clés
        Set<String> kS = new HashSet<>();

        // Parcours de chaque entrée du tableau "tab"
        for (WordMapEntry wme : tab) {

            // Vérification de la présence de la clé et de la valeur associée
            if((wme != null)) {

                // Ajout de la clé dans le set
                kS.add(wme.getKey());
               
            }
        }
        // Renvoi du set de clés
        return kS;
    }
        
    // Méthode override de "values" qui crée une collection de toutes les valeurs de "wordMap"
    @Override
    public Collection<FileMap> values() {

        // Création d'une collection de valeurs
        Collection<FileMap> valeurs = new HashSet<>();

        // Parcours de chaque entrée du tableau "tab"
        for (WordMapEntry wme : tab) {

            // Vérification de la présence de la clé et de la valeur associée
            if((wme != null)) {

                // Ajout de la valeur dans la collection
                valeurs.add(wme.getValue());
                
            }
        }
        // Renvoi de la collection de valeurs
        return valeurs;
    }


    //Methode override size qui retourne la taille
    @Override
    public int size() {

        return taille;
    }
    //Methode override isEmpty qui retourne true si la taille est nulle
    @Override
    public boolean isEmpty() {

        return taille == 0;
    }
    
    // Méthode override de "entrySet" qui crée un set contenant toutes les entrées de "wordMap"
    @Override
    public Set<Map.Entry<String, FileMap>> entrySet() {

        // Création d'un set d'entrées
        Set<Map.Entry<String, FileMap>> entrySet = new HashSet<>();

        // Parcours de chaque entrée du tableau "tab"
        for (WordMapEntry wme : tab) {

            // Vérification de la présence de la clé et de la valeur associée
            if((wme != null)) {

                // Ajout de l'entrée dans le set
                entrySet.add(wme);
              
            }
        }
        // Renvoi du set d'entrées
        return entrySet;
    }

    //Declaration de la methode hachage qui retourne le calcul du hach code dune cle et de la tailleMax 
    private int hachage(Object key) {

        int hac = Math.abs(key.hashCode() % tailleMax);
        return hac;
       
    }

    // Méthode "bigramLePlusProbable" qui trouve le bigramme de la clé en paramètre en utilisant la méthode "bigrammePlusFrequent"
    public String bigramLePlusProbable(Object key){

        // Recherche de l'entrée correspondant à la clé
        WordMapEntry wme = getEntre(key);

        // Récupération du bigramme le plus fréquent de l'entrée
        String rep = wme.bigrammePlusFrequent();

        // Renvoi du bigramme le plus fréquent
        return rep;
    }

    // Méthode "getEntre" qui retourne l'entrée qui correspond à la clé en paramètre
    public WordMapEntry getEntre(Object cle){

        // Recherche de la clé dans le tableau "tab"
        int index = trouverCle(cle);

        // Récupération de l'entrée correspondant à la clé
        WordMapEntry newWME = tab[index];
        
        // Vérification de la présence de l'entrée et de sa valeur associée
        if((newWME == null) || (newWME instanceof deleted)){

            // Renvoi de "null" si l'entrée ou la valeur associée sont absentes
            return null;
        }
        else{

            // Renvoi de l'entrée si elle est présente et que la valeur associée est présente
            return newWME;
        }
    }

    //Delcaration de la methode limite qui calcule la limite selon le facteur de 0.75 et le retourne
    public int limite() {

        int lim = (int) (tailleMax * facteur);
        return lim;
    }

    // Méthode "redimensionner" qui augmente la taille du tableau sous-jacent, s'il est vide, l'initialise
    private void redimensionner() {

        // Si le tableau "tab" est vide, on l'initialise
        if(tab == null) {

            tab = (WordMapEntry[]) new WordMapEntry[tailleMax];
        }
        // Redimensionnement du tableau
        WordMapEntry[] ancienTab = tab;

        int nouvTailleMax = (tailleMax << 1) + 1; // Multiplication par 2 et ajout de 1
        WordMapEntry[] nouvTab = (WordMapEntry[]) new WordMapEntry[nouvTailleMax];

        // Mise à jour des références sur le nouveau tableau
        tab = nouvTab;
        tailleMax = nouvTailleMax;

        // Réinsération des entrées dans le nouveau tableau
        for (WordMapEntry wme : ancienTab) {

            // Vérification de la présence de la clé et de la valeur associée
            if((wme != null)) {

                if(!(wme instanceof deleted)){

                    // Réinsération de l'entrée dans le nouveau tableau
                    this.inserer(wme);
                }
            }
        }
    }

    // Méthode "ajouter" qui permet d'ajouter des paires clé-valeur à "FileMap"
    public FileMap ajouter(String cle, String cleFichier, int valFichier){

        // Recherche de la clé dans le tableau "tab"
        int index = trouverCle(cle);

        // Initialisation de la valeur précédente à "null"
        FileMap valeurP = null;

        // Si l'entrée est absente ou que la valeur associée est "null", on crée une nouvelle entrée
        if((tab[index] == null) || (tab[index] instanceof deleted)){

            // Incrémentation de la taille de la map
            taille++;

            // Mise à jour de la valeur précédente à "null"
            valeurP = null;

            // Création d'une nouvelle entrée avec une nouvelle instance de "FileMap"
            tab[index] = new WordMapEntry(cle, new FileMap());
        }
        else{

            // Récupération de la valeur précédente de l'entrée
            valeurP = tab[index].getValue();
        }

        // Ajout de la paire clé-valeur à l'instance de "FileMap" associée à l'entrée
        tab[index].getValue().ajouter(cleFichier, valFichier);

        // Si la taille de la map dépasse la limite, on redimensionne le tableau "tab"
        if(taille >=limite()){

            redimensionner();
        }
        // Renvoi de la valeur précédente de l'entrée
        return valeurP;
    }

    // Méthode "trouverCle" qui permet de trouver l'emplacement où une clé est assignée
    private int trouverCle(Object cle1) {

        // Calcul de l'indice de hachage de la clé
        int hach = hachage(cle1);

        // Initialisation de l'indice à -1
        int index = -1;

        // Initialisation des variables pour parcourir le tableau "tab"
        int i = -1;
        int temp = 0;

        // Parcours du tableau "tab"
        while(temp <= tailleMax) {

            temp++;
            i++;
            
            // Gestion du débordement de l'indice "i"
            if((hach + i) == tailleMax) {

                i = -hach;
            }
            // Si l'entrée est "null", on sort de la boucle
            if((tab[hach+i] == null )) {

                break;
            }
            // Si la clé est trouvée, on retourne l'indice de l'entrée
            if(cle1 != null) {

                if(cle1.equals(tab[hach+i].getKey())){

                    return hach+i;
                }
            }
            // Si l'indice est toujours égal à -1, on mémorise l'indice de l'entrée en cours de parcours
            if(index == -1) {

                index = hach+i;
                continue;
                
            }
        }
        // Si l'indice vaut toujours -1, on retourne l'indice de l'entrée en cours de parcours
        if(index == -1) {

            return hach+i;
        } 
        // Sinon, on retourne l'indice mémorisé précédemment
        else {

            return index;
        }
    }

    // Méthode "inserer" qui permet d'insérer une entrée dans le tableau "tab"
    private FileMap inserer(WordMapEntry wme) {

        // Calcul de l'indice de l'entrée à insérer
        int index = trouverCle(wme.getKey());

        // Si l'entrée est "null" ou est une "deleted", on insère l'entrée
        if((tab[index] == null) || (tab[index] instanceof deleted)) {

            tab[index] = wme;
            return null;
        } 
        // Sinon, on retourne la valeur de l'entrée existante
        else {

            FileMap valP = tab[index].getValue();
            return valP;
        }
    }

}