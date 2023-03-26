import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.Collection;
import java.lang.Math;


/**
 * @author Yohann Manseau-Glémot(20217138) et Alex Chevrier(20216495)
 *
 * FileMap qui conserve les occurences de chaque mots dans les fichiers
 * La clé correspond au nom du mot et la valeur est une liste contenant la position de ce mot dans le fichier
 */

//Déclaration de la classe FileMap
public class FileMap implements Map<String,ArrayList<Integer>> {


    private FileMapEntry[] tableau;
    private float facteurR;
    private int tailleMax;
    private int taille;

    //Constructeur de la classe FileMap
    public FileMap(){
        
        facteurR = (float) 0.75;//Facteur initialisé é 0.75
        redimensionner();
        tailleMax = 1;
        taille = 0;
        
    }
    //Déclaration de la classe deleted qui implémente FileMapEntry plus bas
    private class deleted extends FileMapEntry{

        protected deleted(){

            super();
        }

    }
    //Déclaration de la classe static FileMapEntry qui implémente Map.Entry, permet de gerer les paire dentrees
    static class FileMapEntry implements Map.Entry<String,ArrayList<Integer>>{

        private ArrayList<Integer> valeur; //Positions
        private String cle; //nom du fichier
        

        //Constructeur de FileMapEntry
        protected FileMapEntry(String key, ArrayList<Integer> value){

            this.valeur = value;
            this.cle = key;
            
        }

        //Constructeur par defaut
        protected FileMapEntry(){}

        //Déclaration des méthodes override de FileMapEntry
        @Override
        public ArrayList<Integer> getValue() {

            return valeur;//Getter de la valeur
        }

        @Override
        public ArrayList<Integer> setValue(ArrayList<Integer> value) {

            this.valeur = value;//Setter de la valeur
            return this.valeur;

        }

        @Override
        public String getKey() {

            return cle;//Getter de la cle

        }

    }

    // Méthode override de putAll qui ajoute toutes les clefs et valeurs dans filemap
    @Override
    public void putAll(Map<? extends String, ? extends ArrayList<Integer>> m) {

        // Pour chaque entrée dans la map m
        for (Map.Entry<? extends String,? extends ArrayList<Integer>> mapEntry : m.entrySet()) {

            // Ajouter la clef et la valeur dans filemap
            put(mapEntry.getKey(), mapEntry.getValue());
        };
    }

    //Methode override isEmpty qui retourne true si la taille de filemap est de 0
    @Override
    public boolean isEmpty() { 
        return taille == 0;
    
    }
    //Methode override size qui retourne la taille de filemap
    @Override
    public int size() { 

        return taille;
    }

    // Méthode override de containsKey qui retourne true si la cle1 est dans filemap
    @Override
    public boolean containsKey(Object cle1) {

        // Trouver l'index de la cle1 dans le tableau
        int index = trouverCle(cle1);

        // Si l'élément à cet index est null ou une instance de deleted, retourner false
        if((tableau[index] == null) || (tableau[index] instanceof deleted)){
            return false;

        }
        // Sinon, retourner true si la cle1 est égale à la clef de l'élément à cet index
        else{
            return cle1.equals(tableau[index].getKey());
        }
    }
    
    // Méthode override de containsValue qui retourne true si la valeur est dans filemap
    @Override
    public boolean containsValue(Object valeur) {

        // Pour chaque entrée dans le tableau
        for (FileMapEntry fme : tableau) {

            // Si la valeur de l'entrée est égale à la valeur recherchée, retourner true
            if(fme.getValue() == valeur){
                return true;
            }
        }
        // Si aucune entrée n'a été trouvée, retourner false
        return false;
    }

    // Méthode override de get qui retourne null ou la valeur qui correspond à la cle2
    @Override
    public ArrayList<Integer> get(Object cle2) {

        // Trouver l'index de la cle2 dans le tableau
        int index = trouverCle(cle2);

        // Si l'élément à cet index est null ou une instance de deleted, retourner null
        if((tableau[index] == null) || (tableau[index] instanceof deleted)){
            return null;
        }
        // Sinon, retourner la valeur de l'élément à cet index
        else{
            return tableau[index].getValue();
        }
    }
    
    // Méthode put qui ajoute une entrée dans filemap
    @Override
    public ArrayList<Integer> put(String cle4, ArrayList<Integer> valeur4) {

        // Trouver l'index de la cle4 dans le tableau
        int index = trouverCle(cle4);
        ArrayList<Integer> ancVal = null;

        // Si l'élément à cet index est null ou une instance de deleted
        if((tableau[index] == null) || (tableau[index] instanceof deleted)){

            // Incrémenter la taille et ajouter une nouvelle entrée avec la cle4 et la valeur4 dans le tableau
            taille++;
            ancVal = null;
            tableau[index] = new FileMapEntry(cle4, valeur4);

        }
        // Sinon, mettre à jour l'entrée avec la cle4 et la valeur4 dans le tableau et enregistrer la valeur précédente
        else{

            ancVal = tableau[index].getValue();
            tableau[index] = new FileMapEntry(cle4, valeur4);
        }

        // Si la taille de la filemap dépasse la limite, redimensionner la filemap
        if(taille >= limite()){

            redimensionner();
        }
        return ancVal;
    }

    // Méthode override remove qui retire une entrée de filemap et réduit la taille
    @Override
    public ArrayList<Integer> remove(Object cle6) {

        // Nous stockons l'indice de l'entrée à retirer dans la variable index en utilisant la méthode trouverCle 
        int index = trouverCle(cle6);

        // Nous déclarons une variable ancVal qui sera utilisée pour stocker la valeur de l'entrée avant sa suppression 
        ArrayList<Integer> ancVal = null;

        // Si la case du tableau à l'indice index est vide ou si c'est une valeur de suppression, ancVal vaudra null 
        if((tableau[index] == null) || (tableau[index] instanceof deleted)){

            ancVal = null;
        }
        // Sinon, nous réduisons la taille de filemap de 1 et stockons la valeur de l'entrée dans ancVal 
        // Enfin, nous remplaçons l'entrée par une valeur de suppression dans le tableau 
        else{

            taille--;
            ancVal = tableau[index].getValue();
            tableau[index] = new deleted();
        }

        // La méthode retourne ensuite la valeur de l'entrée avant sa suppression sous forme d'ArrayList d'entiers
        return ancVal;
    }


    // Méthode override clear qui efface la filemap
    @Override
    public void clear() {

        // Nous mettons le tableau à null et réinitialisons la taille à 0
        tableau = null;
        taille = 0;
    }

    // Méthode override keySet qui ajoute les clefs de filemap dans un Set
    @Override
    public Set<String> keySet() {

        // Nous créons un nouvel objet Set de type HashSet pour stocker les clefs de filemap
        Set<String> cles = new HashSet<String>();

        // Nous parcourons toutes les entrées du tableau 
        for(int indexCle = 0; indexCle < tableau.length; indexCle++){

            // Si l'entrée existe et n'est pas une valeur de suppression, nous ajoutons sa clef au Set
            if(tableau[indexCle] != null){

               

                FileMapEntry fme = tableau[indexCle];
                String cle7 = fme.getKey();
                cles.add(cle7);
               
            }
        }
        // Nous retournons le Set contenant toutes les clefs de filemap
        return cles;
    }

    // Méthode override values qui ajoute les valeurs des clefs de filemap dans une collection et les retourne
    @Override
    public Collection<ArrayList<Integer>> values() {

        // Nous créons une nouvelle collection de type HashSet pour stocker les valeurs des clefs de filemap
        Collection<ArrayList<Integer>> valeurs = new HashSet<>();

        // Nous parcourons toutes les entrées du tableau
        for(int indexCle = 0; indexCle < tableau.length; indexCle++){

            // Nous récupérons la valeur de l'entrée et l'ajoutons à la collection
            FileMapEntry fme = tableau[indexCle];
            ArrayList<Integer> val = fme.getValue();
            valeurs.add(val);
        }

        // Nous retournons la collection contenant toutes les valeurs des clefs de filemap
        return valeurs;
    }

    // Méthode override entrySet qui ajoute les paires clefs-valeurs à un Set et les retourne
    @Override
    public Set<Map.Entry<String,ArrayList<Integer>>> entrySet() {

        // Nous créons un nouvel objet Set pour stocker les paires clefs-valeurs
        Set<Entry<String,ArrayList<Integer>>> entrees = new HashSet<Entry<String,ArrayList<Integer>>>();

        // Nous parcourons toutes les entrées du tableau
        for(int indexCle = 0; indexCle < tableau.length; indexCle++){

            // Nous ajoutons l'entrée au Set
            FileMapEntry fme = tableau[indexCle];
            entrees.add(fme);
        }

        // Nous retournons le Set contenant toutes les paires clefs-valeurs
        return entrees;
    }


    //Delcaration de la methode limite qui calcule la limite selon le facteur de 0.75 et le retourne
    public int limite(){

        int lim = (int) (tailleMax*facteurR);
        return lim;
    }
    //Declaration de la methode hachage qui retroune le calcul du hachage dune cle et de la tailleMax de FileMap
    private int hachage(Object cle0){

        int val = Math.abs(cle0.hashCode() % tailleMax);
        return val;
        
    }

    // Déclaration de la méthode redimensionner qui ajuste la taille de FileMapEntry
    private void redimensionner(){

        // Si le tableau est vide, nous le créons avec la taille maximale initiale
        if(tableau == null){

            tableau = (FileMapEntry[]) new FileMapEntry[tailleMax];
        }
        // Nous calculons la nouvelle taille maximale du tableau en utilisant l'opérateur de décalage de bits (<<)
        int nouvSizeMax = (tailleMax << 1)+1;

        // Nous créons un nouveau tableau de FileMapEntry avec la nouvelle taille maximale
        FileMapEntry[]nouvTab = (FileMapEntry[]) new FileMapEntry[nouvSizeMax];

        // Nous stockons l'ancien tableau dans une variable temporaire
        FileMapEntry[] ancienTab = tableau;
            
        // Nous changeons la nouvelle taille maximale de FileMapEntry
        tailleMax = nouvSizeMax;

        // Et le nouveau tableau
        tableau = nouvTab;

        // On ajoute les valeurs dans FileMap
        for (FileMapEntry fme : ancienTab) {

            // Si l'entrée existe et n'est pas une valeur de suppression, nous l'insérons dans le nouveau tableau
            if(fme != null) {

                
                this.inserer(fme);
            }
        }
    }

  
    // Déclaration de la méthode trouverCle qui retourne l'index de la clef dans le tableau
    private int trouverCle(Object cleR){

        // Nous calculons l'index de la clef en utilisant la méthode hachage
        int hach = hachage(cleR);
        int index = -1;

        // Nous déclarons les variables utilisées pour parcourir le tableau
        int i = -1;
        int temp = 0;

        // Nous parcourons le tableau jusqu'à ce que temp soit supérieur à la taille maximale
        while(temp <= tailleMax){

            // Nous incrémentons i et temp à chaque itération
            i++;
            temp++;

            // Si l'index de la clef dépasse la taille maximale, nous retournons au début du tableau
            if((hach + i) == tailleMax){

                i = -hach;
            }

            // Si l'entrée est vide, nous sortons de la boucle
            if((tableau[hach+i] == null )){

                break;
            }
            // Si la clef existe, nous retournons son index
            if(cleR != null){

                if((cleR.equals(tableau[hach+i].getKey()))){

                    return hach+i;
                }
            }
            // Si l'index n'a pas encore été défini et que l'entrée est une valeur de suppression, nous stockons l'index et continuons la boucle
            if(index == -1){

                if((tableau[hach+i] instanceof deleted)){

                    index = hach+i;
                    continue;
                }
            }
        }
        // Si l'index est -1, nous retournons hach+i, sinon nous retournons l'index
        if(index == -1){

            return hach+i;
        }
        else{

            return index;
        }
    }


    // Méthode add qui est similaire à put mais qui ajoute une entrée à filemap
    public ArrayList<Integer> ajouter(String cle5, int valeurA){

        // Nous récupérons l'index de l'entrée à ajouter
        int index = trouverCle(cle5);
        ArrayList<Integer> ancVal = null;

        // Si l'entrée n'existe pas ou est une valeur de suppression, nous créons une nouvelle entrée
        if((tableau[index] == null) || (tableau[index] instanceof deleted)){

            // Nous incrémentons la taille de filemap et créons une nouvelle entrée
            taille++;
            ancVal = null;
            tableau[index] = new FileMapEntry(cle5, new ArrayList<>());//On ajoute l'entrée à filemap
        }
        else{

            // Sinon, nous stockons la valeur existante dans ancVal
            ancVal = tableau[index].getValue();
        }

        // Nous ajoutons la valeur à l'entrée
        tableau[index].getValue().add(valeurA);

        // Si la taille de filemap est supérieure ou égale à la limite, nous redimensionnons filemap
        if(taille >= limite()){

            redimensionner();
        }
        return ancVal;
    }

    // Déclaration de la méthode inserer qui insère un FileMapEntry dans filemap
    private ArrayList<Integer> inserer(FileMapEntry fileMapEntry){

        // Nous récupérons l'index de l'entrée à ajouter
        int index = trouverCle(fileMapEntry.getKey());

        // Si l'entrée n'existe pas ou est une valeur de suppression, nous insérons l'entrée
        if((tableau[index] == null) || (tableau[index] instanceof deleted)){

            tableau[index] = fileMapEntry;
            return null;
        }
        else{

            // Sinon, nous stockons la valeur existante dans ancVal et la retournons
            ArrayList<Integer> ancVal = tableau[index].getValue();
            return ancVal;
        }
    }

    
}