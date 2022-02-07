/*Factory class which produces a list of autonomous systems which will be sent to the controller to be inserted into the database.
*/
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.net.URL;

public class ASFactory{

    private File asnFile;
    private File coneFile;
    private int[] asnList;
    private String[] countryList;
    private Scanner asnScanner;
    private Scanner coneScanner;
    private ArrayList<AS> asList;
    private URL url;
    private HttpURLConnection connection;
    private BufferedReader orgReader;
/*The files used for producing the autonomous systems are the asndata file from Afrinic, and the customer cone file from CAIDA.
The Afrinic file provides the ASN and country for each AS.
*/
    public ASFactory() throws MalformedURLException{
        this.asnFile = new File("asndata.csv");
        this.coneFile = new File("20210801.ppdc-ases.txt");
        asList = new ArrayList<AS>();
        asnList = new int[2015];
        countryList = new String[2015];
    }

    public ArrayList<AS> generateAS(){
        generateASNs();
        generateCones();
        generateOrgIDs();
        return asList;
    }
/*This method reads each ASN and respective country from the Afrinic file and fills an array with this data
*/
    public void generateASNs(){
        try {
            asnScanner = new Scanner(asnFile);
            /*Skip the header in the csv
            */
            asnScanner.nextLine();
            int pos = 0;
            String sLine;
            String[] split;
            while (asnScanner.hasNext()){
                sLine = asnScanner.nextLine();
                split = sLine.split(",");
                asnList[pos] = Integer.parseInt(split[0]);
                countryList[pos] = split[2];
                AS as = new AS(asnList[pos], countryList[pos]);
                asList.add(as);
                pos++;
            }
            asnScanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("Essential file missing");
        }      
    } 
/*This method reads the ASN of each AS listed in the CAIDA cone file and if it corresponds to an AS in Africa, its cone will be set. 
*/  
    public void generateCones(){
        try {
            coneScanner = new Scanner(coneFile);
            coneScanner.nextLine();
            coneScanner.nextLine();
            String sLine;
            String[] split;
                while(coneScanner.hasNext()){
                    sLine = coneScanner.nextLine();
                    split = sLine.split(" ");
                    for (int i = 0; i < asnList.length; i++){
                        if (asnList[i] == Integer.parseInt(split[0])){
                            asList.get(i).setConeSize(split.length - 1);
                            break;
                        }  
                    }
                }
            coneScanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("Essential file missing");
        }
    }
/*This method generates API calls from the REST API at PeeringDB.com for the organization data of each AS.
*/
    public void generateOrgIDs(){
        for(int i = 0; i < asList.size(); i++){
            int asn = asList.get(i).getASN();
        try {
            url = new URL("https://www.peeringdb.com/api/net?asn="+String.valueOf(asn));
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(5000);
            connection.setConnectTimeout(5000);
            connection.setRequestMethod("GET");
            int status = connection.getResponseCode();
            StringBuffer output = new StringBuffer();
            String sLine;
            System.out.println(asn + " " + status);
            if (status > 299){
                orgReader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                /*HTTP responses are read line by line
                */
                while((sLine = orgReader.readLine()) != null){
                    output.append(sLine);
                }              
                orgReader.close();
                /*If the reponse code is 404, it implies the given ASN is not in the PeeringDB database.
                In this case, the organization ID of the AS remains its default value of -1 to indicate that 
                no known organization is associated with this AS.
                */
                if(status != 404){
                    System.out.println("Error connecting to PeeringDB API\n" + output);
                }
            }
            else{
                orgReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while((sLine = orgReader.readLine()) != null){
                    output.append(sLine);
                }
                asList.get(i).setOrgID(parse(output.toString()));
                orgReader.close();
            }
        } catch (IOException e) {
            System.out.println("Error connecting to PeeringDB API");
        } finally{
            connection.disconnect();
        }
    }
    }
/*This method uses an external library to easily parse JSON data into the AS objects being 
added to the final list, which will be sent to the controller.
*/ 
    public int parse(String response){
        JSONObject body = new JSONObject(response);
        JSONArray data = body.getJSONArray("data");
        int orgID = data.getJSONObject(0).getInt("org_id");
        return orgID;
    }

}