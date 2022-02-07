/*Factory class to create a list of IXPs to be sent to the controller and inserted into the database.
*/
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStreamReader;
import java.net.URL;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.io.BufferedReader;
import java.io.IOException;

public class IXPFactory {

    private ArrayList<IXP> ixpList;

    public IXPFactory(){
        ixpList = new ArrayList<IXP>();
    }
/*Method to generate and return a full list of IXPs each with their relevant data provided.
The IXP data is retrieved from the PeeringDB database by making a REST API call with a parameter to filter by
region = to Africa.
*/
    public ArrayList<IXP> generateIXPs() throws IOException{
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
                parse(output.toString());
            }
            connection.disconnect();
            return this.ixpList;
        } catch (MalformedURLException e) {
            System.out.println("Error connecting to PeeringDB API");
            return null;
        }
    }
/*This method uses an external library to easily parse JSON data into the IXP objects being 
added to the final list, which will be sent to the controller.
*/    
    public void parse(String response){
        JSONObject body = new JSONObject(response);
        JSONArray data = new JSONArray(body.getJSONArray("data"));
        JSONObject nextIXP;
        for(int i = 0; i < data.length(); i++){
            nextIXP = data.getJSONObject(i);
            ixpList.add(new IXP(nextIXP.getInt("id"), nextIXP.getString("country"), nextIXP.getString("city"),
            nextIXP.getInt("org_id")));
        }
    }
}
