/*This class represents a relationship between an autonomous system and an ixp to which it is connected to directly.
Each peering relationship possesses a unique id as well as unique identifiers for the individual AS and IXP.
*/
public class PeerRelationship {

    private int id;
    private int asn;
    private int ixID;
    
    public PeerRelationship(int pid, int pasn, int pixID){
        this.id = pid;
        this.asn = pasn;
        this.ixID = pixID;
    }
/*Getter and setter methods for the peering relationship.
*/
    public int getID(){
        return this.id;
    }

    public int getASN(){
        return this.asn;
    }

    public int getIXID(){
        return this.ixID;
    }

    public void setID(int pid){
        this.id = pid;
    }

    public void setASN(int pasn){
        this.asn = pasn;
    }

    public void setIXID(int pixID){
        this.ixID = pixID;
    }

    public String toString(){
        return "ID: " + String.valueOf(this.id) + " ASN: " + String.valueOf(this.asn)
        + " connects to IXP: " + String.valueOf(this.ixID);
    }
}
