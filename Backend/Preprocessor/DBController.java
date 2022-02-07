/*This is the Driver class of the preprocessor program which gathers all relevant data for the backend of the system.
Once the data has been gathered it is inserted into relevant tables in the local MySQL database.
*/
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class DBController {

    static ArrayList<AS> asList;
    static ArrayList<IXP> ixpList;
    static ArrayList<ASRelationship> asRelationshipList;
    static ArrayList<PeerRelationship> peerRelationshipList;

/*Main method to execute all insertion operations.
NB: Due to the nature of the multiple file scans and many, many web API calls, this method takes a very long time to execute.
*/
    public static void main(String args[]) throws SQLException, IOException{
        ASFactory asFactory = new ASFactory();
        asList = asFactory.generateAS();
        fillAS_TB(asList);
        IXPFactory ixpFactory = new IXPFactory();
        ixpList = ixpFactory.generateIXPs();
        fillIXP_TB(ixpList);
        ASRelationshipFactory asRelFactory = new ASRelationshipFactory();
        asRelationshipList = asRelFactory.generateRelationships();
        fillAS_Rel_TB(asRelationshipList);
        PeerRelationshipFactory peerRelFactory = new PeerRelationshipFactory();
        peerRelationshipList = peerRelFactory.generatePeerRelationships();
        fillPeering_tb(peerRelationshipList);

    }
/*Method to insert all peering relationship records into the database.
*/
    public static void fillPeering_tb(ArrayList<PeerRelationship> peerRelList) throws SQLException{
        Connection connection = getConnection();
        PreparedStatement insert;
        try{
            for (int i = 0; i < peerRelList.size(); i++){
                insert = connection.prepareStatement("INSERT INTO peering_tb (id, asn, ix_id) VALUES (?,?,?)");
                insert.setInt(1, peerRelList.get(i).getID());
                insert.setInt(2, peerRelList.get(i).getASN());
                insert.setInt(3, peerRelList.get(i).getIXID());
                insert.executeUpdate();
            }
        }
        catch(Exception e){
            System.out.println(e);
        }
        finally{
            connection.close();
        }
    }
/*Method to insert all autonomous system relationship records into the database.
*/
    public static void fillAS_Rel_TB(ArrayList<ASRelationship> relList) throws SQLException{
        Connection connection = getConnection();
        PreparedStatement insert;
        try{
            for (int i = 0; i < relList.size(); i++){
                insert = connection.prepareStatement("INSERT INTO as_rel_tb (id, asn_start, asn_end, rel_type) VALUES (?,?,?,?)");
                insert.setInt(1, relList.get(i).getID());
                insert.setInt(2, relList.get(i).getASNStart());
                insert.setInt(3, relList.get(i).getASNEnd());
                insert.setString(4, relList.get(i).getType());
                insert.executeUpdate();
            }
        }
        catch(Exception e){
            System.out.println(e);
        }
        finally{
            connection.close();
        }
    }
/*Method to insert all IXP records into the database.
*/
    public static void fillIXP_TB(ArrayList<IXP> ixpList) throws SQLException{
        Connection connection = getConnection();
        PreparedStatement insert;
        try{
            for(int i = 0; i < ixpList.size(); i++){
                insert = connection.prepareStatement("INSERT INTO ixp_tb (id, country, city, org_id) VALUES (?,?,?,?)");
                insert.setInt(1, ixpList.get(i).getID());
                insert.setString(2, ixpList.get(i).getCountry());
                insert.setString(3, ixpList.get(i).getCity());
                insert.setInt(4, ixpList.get(i).getOrgID());
                insert.executeUpdate();
            }
        }
        catch(Exception e){
            System.out.println(e);
        }
        finally{
            connection.close();
        }

    }
/*Method to insert all autonomous system records into the database.
*/
    public static void fillAS_TB(ArrayList<AS> asList) throws MalformedURLException, SQLException{
        Connection connection = getConnection();
        PreparedStatement insert;
        try{
            for (int i = 0; i < asList.size(); i++){
                insert = connection.prepareStatement("INSERT INTO as_tb (asn, country, cone_size, org_id) VALUES (?,?,?,?)");
                insert.setInt(1, asList.get(i).getASN());
                insert.setString(2, asList.get(i).getCountry());
                insert.setInt(3, asList.get(i).getConeSize());
                insert.setInt(4, asList.get(i).getOrgID());
                insert.executeUpdate();
            }          
        }
        catch(Exception e){
            System.out.println(e);
        }
        finally{
            connection.close();
        }
    }
/*Method to identify, establish and return a connection to a local MySQL database.
For obvious reasons, the password field has been left blank and will not work until a correct one is provided.
*/
    public static Connection getConnection(){
        try{
            String driver = "com.mysql.cj.jdbc.Driver";
            String url = "jdbc:mysql://localhost:3306/air_db";
            String username = "root";
            String password = "";
            Class.forName(driver);

            Connection connection = DriverManager.getConnection(url,username,password);
            System.out.println("Connection Successful");
            return connection;
        }
        catch(Exception e){
            System.out.println("Connection to database could not be established");
            return null;
        }
    }  
}
