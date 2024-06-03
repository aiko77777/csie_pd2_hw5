import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.io.PrintWriter;

class TrieNode{
    TrieNode[] children = new TrieNode[26];
    boolean isEndOfWord = false;
}
class Trie{
    TrieNode root =new TrieNode();
    // insert a vocabulary to Trie
    public void insert(String word) {
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            if (node.children[c - 'a'] == null) {
                node.children[c - 'a'] = new TrieNode();
            }
            node = node.children[c - 'a'];
        }
        node.isEndOfWord = true;
    }
// if the Trie exist the vocabulary
    public boolean search(String word) {
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            node = node.children[c - 'a'];
            if (node == null) {
                return false;
            }
        }
        return node.isEndOfWord;
    }
    
}
public class TFIDFSearch {
    public static double calculate_tf(String to_Find_String,String[] splited_text_section){
        double count=0;
        for(String segment : splited_text_section){
            if(to_Find_String.equals(segment)){
                count++;
            }
        }
        
        double result=count/splited_text_section.length;
        //System.out.println("tf="+result);
        return result;
    }

    public static double calculate_idf(ArrayList<String> text_sections,Double count){
        if (count==0){
            return 0;
        }
        else{
            double result=Math.log(text_sections.size()/count);
            //System.out.println("udf="+result);
            return result;
        }
        
    }

    public HashMap<Integer,Double> AND_Filter(String tc_line,ArrayList<Trie> trie_list,ArrayList<String> text_secs){
        ArrayList<Integer> del_index=new ArrayList<>();
        ArrayList<String> simplified_tc_line=new ArrayList<>();
        ArrayList<Double> simplified_count_in_idf=new ArrayList<>();
        ArrayList<Double> tf_idf_sum_of_text=new ArrayList<>();
        HashMap<Integer, Double> filtered_texts_And_tfidf_value = new HashMap<>();

        tc_line=(tc_line.replaceAll("AND","")).replaceAll("\\s+"," ");

//simplify the repeatitive words and the same value in IDF calculation
        for(String tem :tc_line.split(" ")){
            if(!simplified_tc_line.contains(tem)){
                simplified_tc_line.add(tem);
                Double count_trie=(double) 0;
                for(Trie trie : trie_list){
                    if(trie.search(tem)){   //how many text_sections can find the word
                        count_trie++;
                    }
                    if(!trie.search(tem)){  //take the intersection
                        del_index.add(trie_list.indexOf(trie));
                    }
                }
                simplified_count_in_idf.add(count_trie);
            }
        }

        // for(String simp_tc_unit : simplified_tc_line ){
        //     for(Trie trie:trie_list){
        //         if(!trie.search(simp_tc_unit)){
        //             del_index.add(trie_list.indexOf(trie)); //use for intersection set
        //         }
        //     }
        // }
        if(del_index.size()!=text_secs.size()){ //if it has intersection

            for(int i =0;i<text_secs.size();i++){
                if(del_index.contains(i)){
                    continue;
                }
                Double tf_idf_result=(double) 0;
                for(String tem:tc_line.split(" ")){
                    Double tf_result=calculate_tf(tem,(text_secs.get(i).split(" ")));
                    Double idf_result=calculate_idf(text_secs,simplified_count_in_idf.get(simplified_tc_line.indexOf(tem)));
                    tf_idf_result+=tf_result*idf_result;
                }
                //System.out.println(tf_idf_result);
                filtered_texts_And_tfidf_value.put(i,tf_idf_result);
            }
        }
        
            
        return filtered_texts_And_tfidf_value;
    }
    public HashMap<Integer,Double> OR_filter(String tc_line,ArrayList<Trie> trie_list,ArrayList<String> text_secs){
        ArrayList<Integer> union_list=new ArrayList<>();
        ArrayList<String> simplified_tc_line=new ArrayList<>();
        ArrayList<Double> simplified_count_in_idf=new ArrayList<>();
        HashMap<Integer, Double> filtered_texts_And_tfidf_value = new HashMap<>();
        tc_line=(tc_line.replaceAll("OR","")).replaceAll("\\s+"," ");
//get the union in texts
        for(String tem:tc_line.split(" ")){
            for(Trie trie : trie_list){
                if(trie.search(tem)){
                    if(!union_list.contains(trie_list.indexOf(trie))){
                        union_list.add(trie_list.indexOf(trie));    //the index of text in text_section which is union
                    }
                }
            }
        }
        //System.out.println("unionlist="+union_list);

//simplify the repeatitive words and the same value in IDF calculation      //only use for idf
        for(String tem :tc_line.split(" ")){
            if(!simplified_tc_line.contains(tem)){
                simplified_tc_line.add(tem);
                Double count_trie=(double) 0;
                for(Trie trie : trie_list){
                    if(trie.search(tem)){   //how many text_sections can find the word
                        count_trie++;
                    }
                }
                simplified_count_in_idf.add(count_trie);
            }
        }
        for(int i=0;i<union_list.size();i++){
            Double tf_idf_result=(double) 0;
            for(String tem:tc_line.split(" ")){
                Double tf_result=calculate_tf(tem,(text_secs.get(union_list.get(i)).split(" ")));
                Double idf_result=calculate_idf(text_secs,simplified_count_in_idf.get(simplified_tc_line.indexOf(tem)));
                tf_idf_result+=tf_result*idf_result;
                //System.out.println(tem+"tf="+tf_idf_result);
                //System.out.println(tem+"idf"+idf_result);
            }
            //System.out.println(tf_idf_result);
            filtered_texts_And_tfidf_value.put(union_list.get(i), tf_idf_result);
        }


        return filtered_texts_And_tfidf_value;
    }
    public static void main(String[] args) throws IOException {
        TFIDFSearch search=new TFIDFSearch();
        HashMap<Integer, Double> filtered_texts_And_tfidf_value = new HashMap<>();
        ArrayList<String> deserialized=null;
        String ser_name= args[0].replaceFirst("[.][^.]+$", "");
        FileInputStream fis = new FileInputStream(ser_name);
        ObjectInputStream ois = new ObjectInputStream(fis);
        try {
            deserialized=(ArrayList<String>) ois.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        ois.close();
        fis.close();
        System.out.println("deserialize success");
                
                    
        
//store the every text section in Trie and store all Tries in Obj Arraylist:
        ArrayList<Trie> Trie_list=new ArrayList<>();
        ArrayList<Trie> Trie_list2=new ArrayList<>();

        for(int i=0;i<deserialized.size();i++){
            Trie the_tree= new Trie();
            for(String segment :(deserialized.get(i)).split(" ")){
                if(!the_tree.search(segment)){
                    the_tree.insert(segment);
                }
            }
            Trie_list.add(the_tree);
            Trie_list2.add(the_tree);
        }
//read the tc
        try {
            BufferedReader reader = new BufferedReader(new FileReader(args[1]));
            FileWriter writer = new FileWriter(new File("output.txt"));
            Integer n;   //it's the number 
            String line2;   //the query
            n=Integer.valueOf(reader.readLine());    
            
            while ((line2 = reader.readLine()) != null) {   
                ArrayList<Double> tf_udf_value=new ArrayList<>();
                ArrayList<Integer> sorted_key=new ArrayList<>();//sorted text_sec
                if(line2.contains("AND")){
                    filtered_texts_And_tfidf_value=search.AND_Filter(line2, Trie_list2,deserialized);

                    
                    for(Double values:filtered_texts_And_tfidf_value.values()){
                        tf_udf_value.add(values);
                    }
                    tf_udf_value.sort(Comparator.reverseOrder()); //sorted tf_idf values
                    for(double values:tf_udf_value){
                        for(Integer key:filtered_texts_And_tfidf_value.keySet()){
                            if(filtered_texts_And_tfidf_value.get(key)==values){
                                sorted_key.add(key);
                            }
                        }
                    }
                    for(int i=0;i<n;i++){
                        
                        if(i>=sorted_key.size()){
                            writer.append("-1");
                        }
                        else{
                            writer.append(sorted_key.get(i).toString());
                        }
                        if(i!=n-1){
                            writer.append(" ");
                        }
                    }
                    
                }
                else {
                    filtered_texts_And_tfidf_value=search.OR_filter(line2, Trie_list2,deserialized);

                    
                    for(Double values:filtered_texts_And_tfidf_value.values()){
                        tf_udf_value.add(values);
                    }
                    tf_udf_value.sort(Comparator.reverseOrder()); //sorted tf_idf values
                    //System.out.println(tf_udf_value);
                    for(double values:tf_udf_value){
                        for(Integer key:filtered_texts_And_tfidf_value.keySet()){
                            if(filtered_texts_And_tfidf_value.get(key)==values){
                                sorted_key.add(key);
                            }
                        }
                    }
                    //System.out.println(sorted_key);
                    for(int i=0;i<n;i++){
                        
                        if(i>=sorted_key.size()){
                            writer.append("-1");
                        }
                        else{
                            writer.append(sorted_key.get(i).toString());
                        }
                        if(i!=n-1){
                            writer.append(" ");
                        }
                    }
                }
                writer.append("\r\n");
                
               

            }
            writer.close();
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
       
    }
}
