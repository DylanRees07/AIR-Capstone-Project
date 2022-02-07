/*Class to represent an internet exchange point in Africa.
Each IXP possesses an id from the PeeringDB database, a country and city for geographical representation 
and an organization which the IXP is owned by.
*/
public class IXP {

    private int id;
    private String country;
    private String city;
    private int orgID;

    public IXP(int pid, String pCountry, String pCity, int pOrgID){
        this.id = pid;
        this.country = pCountry;
        this.city = pCity;
        this.orgID = pOrgID;
    }
/*Getter and setter methods for the IXP.
*/
    public int getID(){
        return this.id;
    }

    public String getCountry(){
        return this.country;
    }

    public String getCity(){
        return this.city;
    }

    public int getOrgID(){
        return this.orgID;
    }

    public void setID(int pID){
        this.id = pID;
    }

    public void setCountry(String pCountry){
        this.country = pCountry;
    }

    public void setCity(String pCity){
        this.city = pCity;
    }

    public void setOrgID(int pOrgID){
        this.orgID = pOrgID;
    }

    public String toString(){
        return "IX_ID: " + String.valueOf(this.id) + " Country: " + this.country +
        " City: "  + this.city + " OrgID: " + String.valueOf(this.orgID);
    }
    
}
