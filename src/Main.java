
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.ling.*;
import java.util.Properties;
import java.io.*;
import java.util.Scanner;
import java.util.HashMap;
import java.util.HashSet;


/**
 * @author Yohann Manseau-Glémot (20217138) et Alex Chevrier(20216495)
 *
 * Classe Main qui permet de lire les deux arguments donné soit un dataset et un fichier texte query. 
 * Ces deux arguments seront donc args[0] et args[1]. Le main va afficher le résultat des requetes du query
 * soit the most probable bigram of mot va donner le mot qui est le plus susceptible d'apparaitre apres le mot qu'on lui donne.
  l'autre requete search mot va afficher le document qui contient le plus de fois ce mot

 */
public class Main {
    
    public static WordMap wordMap = new WordMap();//On declare wordmap
    public static WordMap.WordMapEntry ancienMot;//Et les entrees de wordmap

    static int nbrFichier = 0;//le nombre de fichier dans le dataset

    public static HashMap<String,Integer> nbrMots = new HashMap<>();//le nombre de mots dun fichier
    public static HashSet<String> hSet = new HashSet<>();

    //Methode tfidf qui calcule la frequence de terme et la frequence de document inverse
    private static String tfidf(String mot) {
        // Déclaration et initialisation des variables
        String bonFichier = null; // Le fichier contenant le mot avec le plus grand score tf-idf
        double maxTFIDF = 0; // Le score tf-idf le plus élevé trouvé pour le mot
    
        // Calcul de la fréquence de document inverse (IDF)
        double idf = Math.log10(nbrFichier / wordMap.get(mot).size());
    
        // Récupération de la liste des fichiers contenant le mot
        FileMap contientMot = wordMap.get(mot);
    
        // Parcours de chaque fichier contenant le mot
        for (String motActuel : contientMot.keySet()) {
            // Calcul de la fréquence de terme (TF)
            double count = (double) contientMot.get(motActuel).size(); // Nombre de occurrences du mot dans le fichier
            double total = (double) nbrMots.get(motActuel); // Nombre total de mots dans le fichier
            double tf = count / total;
    
            // Calcul du score tf-idf total pour ce fichier
            double tfidfA = tf * idf;
    
            // Si aucun fichier n'a encore été sélectionné ou si le score tf-idf actuel est supérieur à celui enregistré dans maxTFIDF,
            // mettons à jour les variables bonFichier et maxTFIDF
            if ((bonFichier == null) || (maxTFIDF < tfidfA)) {
                bonFichier = motActuel;
                maxTFIDF = tfidfA;
            }
            // Si le nom du fichier actuel est alphabétiquement inférieur à celui enregistré dans bonFichier,
            // mettons à jour les variables bonFichier et maxTFIDF si le score tf-idf est égal à maxTFIDF
            else if (bonFichier.compareTo(motActuel) > 0) {
                if((maxTFIDF == tfidfA)){
                    
                    bonFichier = motActuel;
                    maxTFIDF = tfidfA;
                }
                
            }
        }
    
        // Renvoi du nom du fichier qui contient le mot avec le plus grand score tf-idf
        return bonFichier;
    }
    

    //Methode lireMot qui lit tout les mots dans un fichier txt et les ajoute au hSet
    private static void lireMot(String mot, File txt, int ind){

        hSet.add(mot);

        if(ancienMot != null){
            ancienMot.ajouterMotSuivant(mot);
        }
        
        wordMap.ajouter(mot, txt.getName(), ind);//On ajoute aussi le mot avec le nom de fichier et lindex a wordmap
        ancienMot = wordMap.getEntre(mot);
    }

    //Methode lireQuery qui permet de lire les requetes presentes dans le fichier txt query.txt
    private static void lireQuery(File fichierQ){

        try{

            String arg;
            Scanner scanner = new Scanner(fichierQ);
            
            while(scanner.hasNext() == true){

                String requete = scanner.nextLine();

                //Si la requete est de trouver un bigramme dun mot
                if(requete.matches("the most probable bigram of \\w+")){

                    arg = requete.replaceAll("the most probable bigram of ", "");
                    requeteBigram(arg);//On appel la methode requeteBigram

                }
                //si la requete est de trouver un fichier contenant un mot
                else if( requete.matches("search \\w+")){

                    arg = requete.replaceAll("search ", "");
                    requeteSearch(arg);//On appel la methode requeteSearch
                }
            }

            scanner.close();

        }
        catch(FileNotFoundException exception){

            System.out.println(exception);
        }
    }
    //Methode requeteBigram qui traite la requete de recherche dun bigram, trouve le bon fichier et limprime
    private static void requeteBigram(String arg){

        String bigram = wordMap.bigramLePlusProbable(arg);//on utilise la methode de wordmap pour trouver le bigram

        System.out.println(arg + " " + bigram); //On imprime le most probable bigram du mot en argument
    }

    //Methode requeteSearch qui traite la requete de recherche dun fichier contenant le plus de fois le mot en argument
    private static void requeteSearch( String arg){

        String fichierPertinent = tfidf(arg);// On utilise la methode tfidf pour le trouver
        
        System.out.println(fichierPertinent);//On imprime le fichier
    }

    //Methode lireTexte qui utilise la librairie CoreNLP vu dans lenonce
    private static void lireTexte(File txt) throws Exception{

        try{

            StringBuffer text = new StringBuffer();
            String line;
            FileReader fr = new FileReader(txt);
            BufferedReader reader = new BufferedReader(fr);
            

            while((line = reader.readLine()) != null){

                String ligne = line.replaceAll("[^’'a-zA-Z0-9]", " ");
                String ligne2 = ligne.replaceAll("\\s+", " ").trim();
                // set up pipeline properties
                Properties props = new Properties();
                // set the list of annotators to run
                props.setProperty("annotators", "tokenize,pos,lemma");
                // set a property for an annotator, in this case the coref annotator is being set to use the neural algorithm
                props.setProperty("coref.algorithm", "neural");
                // build pipeline
                StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
                // create a document object
                CoreDocument document = new CoreDocument(ligne2);
                // annnotate the document
                pipeline.annotate(document);

                for (CoreLabel tok : document.tokens()) {

                    String string = String.valueOf(tok.lemma());

                    //On traite le cas des s appostrophe
                    if(!(string.contains("'s") || string.contains("’s"))){

                        text.append(string).append(" ");
                    }
                }
            }
            reader.close();

            String txtMod = String.valueOf(text);
            txtMod = txtMod.replaceAll("[^a-zA-Z0-9]", " ").replaceAll("\\s+", " ").trim();

            Scanner scanMot = new Scanner(txtMod);
            scanMot.useDelimiter(" "); //lespace entre chaque mot
            int ind=0;

            while(scanMot.hasNext() == true){

                String word = scanMot.next();
                lireMot(word, txt, ind);
                ind = ind + 1;
            }

            scanMot.close();
            nbrMots.put(txt.getName(), ind);
            ancienMot = null;
            

        }
        catch(FileNotFoundException e){

        }
        catch(IOException e){

        }
        
    }
    
    //Main de la classe qui lit quel dataset on utilise dans args[0] , traite les requetes et les realise
    public static void main(String[] args) throws Exception {

      
        File dataset = new File(args[0]);
        File[] listeFichier = dataset.listFiles();//liste de tous les fichiers dans le dataset en parametre

    
        
        for(int i = 0; i<listeFichier.length; i++){

            if(listeFichier[i].isFile() == true){

                nbrFichier = nbrFichier+1;
                lireTexte(listeFichier[i]);//on lit tous les fichier text du dataset
               
            }
        }

        File query = new File(args[1]);//on lit finalement les requetes et les traite
        lireQuery(query);

    }
}
