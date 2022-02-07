/*Class to represent a given autonomous system with some ASN, located in some country and owned by some organization.
Each AS possesses a set of customers including itself which is thus represented as a cone size of at least 1.
*/

public class AS {
    
    private int asn;
    private String country;
    private int coneSize;
    private int orgID;

    public AS(int pASN, String pCountry){
        this.asn = pASN;
        this.country = pCountry;
        this.coneSize = 1;
        this.orgID = -1;
    }
/*Getters and setters for the AS attributes
*/
    public int getASN(){
        return this.asn;
    }

    public String getCountry(){
        return this.country;      
    }

    public int getConeSize(){
        return this.coneSize;
    }

    public int getOrgID(){
        return this.orgID;
    }

    public void setASN(int pASN){
        this.asn = pASN;
    }

    public void setCountry(String pCountry){
        this.country = pCountry;
    }

    public void setConeSize(int pConeSize){
        this.coneSize = pConeSize;
    }

    public void setOrgID(int pOrgID){
        this.orgID = pOrgID;
    }

    public String toString(){
        return "ASN: " + String.valueOf(this.asn) + " Country: " + this.country + " Cone Size: " +
        String.valueOf(this.coneSize) + " OrgID: " + String.valueOf(this.orgID);
    }
}
