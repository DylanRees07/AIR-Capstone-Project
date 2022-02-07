/*Factory class which produces a list of relationships between African autonomous systems and sends it to the 
controller to be inserted into the database.
*/
import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;

public class ASRelationshipFactory {

    private int[] asnList;
    private ArrayList<ASRelationship> relationshipList;
    private ArrayList<Integer> asnStartList;
    private ArrayList<Integer> asnEndList;
    private ArrayList<Integer> typeList;
    private File asnFile;
    private File relFile;
    private Scanner asnScanner;
    private Scanner relScanner;
/*The files used for generating relationship data is the Afrinic asndata file to identify the scope
of autonomous systems included in the relationships and the as-relationship file from CAIDA which
contains each relationship itself and its type.
*/
    public ASRelationshipFactory(){
        this.asnFile = new File("asndata.csv");
        this.relFile = new File("20210701.as-rel2.txt");
        this.asnList = new int[2015];
        this.relationshipList = new ArrayList<ASRelationship>();
        this.asnStartList = new ArrayList<Integer>();
        this.asnEndList = new ArrayList<Integer>();
        this.typeList = new ArrayList<Integer>();
    }
/*This method returns the list of AS relationships to the controller. It does so by scanning the relationship file
and identifying each relationship and its respective type.
*/
    public ArrayList<ASRelationship> generateRelationships(){
        generateASNs();
        try{
            relScanner = new Scanner(relFile);
            String sLine;
            String[] split;
            while(relScanner.hasNext()){
                sLine = relScanner.nextLine();
                split = sLine.split("\\|");
                asnStartList.add(Integer.parseInt(split[0]));
                asnEndList.add(Integer.parseInt(split[1]));
                typeList.add(Integer.parseInt(split[2]));
            }
            relScanner.close();
            String type;
            /* Once all relationship data has been stored in ArrayLists, only the ones pertaining to African autonomous
            systems get passed into the final returned list.
            */
            int count = 0;
            for (int i = 0; i < asnStartList.size(); i++){
                for (int j = 0; j < asnList.length; j++){
                    if (asnStartList.get(i) == asnList[j]){
                        for (int k = 0; k < asnList.length; k++){
                            if (asnEndList.get(i) == asnList[k]){
                                if(typeList.get(i) == 0){
                                    type = "p2p";
                                }
                                else{
                                    type = "p2c";
                                }
                                relationshipList.add(new ASRelationship(count,asnStartList.get(i), asnEndList.get(i), type));
                                count++;
                            }
                        }
                    }
                }
            }
        }
        catch(FileNotFoundException e){
            System.out.println("Essential file missing");
        }
        return this.relationshipList;
    }
/*This method reads each ASN from the Afrinic file and fills an array with this data
*/
    public void generateASNs(){
        try {
            asnScanner = new Scanner(asnFile);
            asnScanner.nextLine();
            int pos = 0;
            String sLine;
            String[] split;
            while (asnScanner.hasNext()){
                sLine = asnScanner.nextLine();
                split = sLine.split(",");
                asnList[pos] = Integer.parseInt(split[0]);
                pos++;
            }
            asnScanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("Essential file missing");
        }
    }  
}
