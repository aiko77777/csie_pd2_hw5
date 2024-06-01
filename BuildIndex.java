import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.io.ObjectInputStream;
import java.io.*;





public class BuildIndex{
    public void serialize(ArrayList<String> pass_in_arr_list,String origin_doc_name){
        try {
            
            String ser_name= origin_doc_name.replaceFirst("[.][^.]+$", "");
            FileOutputStream fos = new FileOutputStream(ser_name);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(pass_in_arr_list);
            oos.close();
            fos.close();
            System.out.println("success");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void deserialize(String origin_doc_name){
        try {
            ArrayList<String> deserialized=null;
            String ser_name= origin_doc_name.replaceFirst("[.][^.]+$", "");
            FileInputStream fis = new FileInputStream(ser_name);
            ObjectInputStream ois = new ObjectInputStream(fis);
            try {
                deserialized=(ArrayList) ois.readObject();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            ois.close();
            fis.close();
            System.out.println(deserialized.get(0));
            System.out.println("deserialize success");
                
                    
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return;
            }
        

    }
    public static void main(String[] args) {
        try {
            ArrayList<String> text_section=new ArrayList<>();
            String line_docs;
            String tem="";
            int docs_order=1;
            InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(args[0]),"UTF-8");
            BufferedReader docs_reader = new BufferedReader(inputStreamReader);    //read parameter 0 : docs.txt
//spilt the docs per 5 rows and merge them to a text stored in text_section
            while ((line_docs = docs_reader.readLine()) != null) {
                line_docs=((line_docs.replaceAll("[^a-zA-Z\\s]", " ")).replaceAll("\\s+"," ")).toLowerCase(); //regular expression :replace all the non-English char and multiple space to unit space            
                tem=tem+line_docs;            
                if(docs_order%5==0){
                    text_section.add(tem.replaceAll("\\s+"," "));
                    tem="";
                }
                docs_order++;
            }
// there is one space at the first positon of each text!! take them out.
                for(int i=0;i<text_section.size();i++){
                    text_section.set(i,(text_section.get(i)).trim());
                    //System.out.println(text_section.get(i));
                }
                docs_reader.close();     

                BuildIndex tets=new BuildIndex();
                tets.serialize(text_section,args[0]);
                tets.deserialize(args[0]);
                
                

        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
}
