/*Factory class to produce a list of relationships between autonomous systems and ixps named peering relationships,
and then send this list to the controller for insertion into the database.
*/
import java.util.ArrayList;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

public class PeerRelationshipFactory {

    private int[] asnList;
    private ArrayList<Integer> ixpList;
    private ArrayList<PeerRelationship> peerRelList;
    private File asnFile;
    private Scanner asnScanner;
    private int count;
/*The file used for this class is the Afrinic asndata file to identify the scope of autonomous systems to find relationships 
for given their asns.
*/
    public PeerRelationshipFactory(){
        this.asnFile = new File("asndata.csv");
        asnList = new int[2015];
        ixpList = new ArrayList<Integer>();
        peerRelList = new ArrayList<PeerRelationship>();
        count = 0;
    }
/*Method to generate the final list of peering relationships to send to the controller. 
The specification of which autonomous systems and ixps to include is defined first.
The data for which ixps an AS can directly connect to is provided by the PeeringDB REST API with the 
asn given as a filtering parameter.
*/
    public ArrayList<PeerRelationship> generatePeerRelationships() throws IOException{
        generateASNs();
        generateIXPs();
        URL url;
        for(int i = 0; i < asnList.length; i++){
        try{
            int asn = asnList[i];
            url = new URL("https://peeringdb.com/api/netixlan?asn="+String.valueOf(asn));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(5000);
            connection.setConnectTimeout(5000);
            connection.setRequestMethod("GET");
            int status = connection.getResponseCode();
            BufferedReader peerReader;
            StringBuffer output = new StringBuffer();
            String sLine;

            if (status > 299){
                peerReader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                while((sLine = peerReader.readLine()) != null){
                    output.append(sLine);
                }
                peerReader.close();
                if(status != 404){
                    System.out.println("Error connecting to PeeringDB API\n" + output);
                }
            }
            else{
                peerReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while((sLine = peerReader.readLine()) != null){
                    output.append(sLine);
                }
                parse(output.toString());
                peerReader.close();
            }

        }
        catch(IOException e){
            System.out.println("Error connecting to PeeringDB API");
        }
    }
        return this.peerRelList;
    }
/*This method uses an external library to easily parse JSON data into the peering relationship objects being 
added to the final list, which will be sent to the controller.
It is ensured that only peering relationships to African ixps are alloted into the list.
*/ 
    public void parse(String response){
        JSONObject body = new JSONObject(response);
        JSONArray data = body.getJSONArray("data");
        JSONObject peerRel;
        int ixID;
        int asn;
        for (int i = 0; i < data.length(); i++){
            peerRel = data.getJSONObject(i);
            ixID = peerRel.getInt("ix_id");
            asn = peerRel.getInt("asn");
            for(int j = 0; j < ixpList.size(); j++){
                if (ixID == ixpList.get(j)){
                System.out.println("ID: " + count + " ASN: " + asn + " connects to IXP: " + ixID);
                peerRelList.add(new PeerRelationship(count, asn, ixID));
                count++;
                }
            }
        }

    }
/*This method uses an external library to easily parse JSON data into the ixp id list such that 
it can be used in the final parsing method to filter for African IXPs
*/ 
    public void ixpParse(String response){
        JSONObject body = new JSONObject(response);
        JSONArray data = new JSONArray(body.getJSONArray("data"));
        JSONObject nextIXP;
        for(int i = 0; i < data.length(); i++){
            nextIXP = data.getJSONObject(i);
            ixpList.add(nextIXP.getInt("id"));
        }
    }
/*Method to read through the Afrinic asndata file and identify all African autonomous systems to use in the API calls.
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
/*Method to generate and return a full list of IXPs to use for filtering in the final parsing method.
The IXP data is retrieved from the PeeringDB database by making a REST API call with a parameter to filter by
region = to Africa.
*/
    public void generateIXPs() throws IOException{
        try {
            URL url = new URL("https://peeringdb.com/api/ix?region_continent=Africa");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(5000);
            connection.setConnectTimeout(5000);
            connection.setRequestMethod("GET");
            int status = connection.getResponseCode();
            BufferedReader ixpReader;
            StringBuffer output = new StringBuffer();
            String sLine;

            if (status > 299){
                ixpReader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                while ((sLine = ixpReader.readLine()) != null){
                    output.append(sLine);
                }
                System.out.println("Error connecting to PeeringDB API" + output);
                ixpReader.close();
            }
            else{
                ixpReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while((sLine = ixpReader.readLine()) != null){
                    output.append(sLine);
                }
                System.out.println("Successfully received JSON data");
                ixpParse(output.toString());
            }
            connection.disconnect();
        } catch (MalformedURLException e) {
            System.out.println("Error connecting to PeeringDB API");
        }
    }   
}
