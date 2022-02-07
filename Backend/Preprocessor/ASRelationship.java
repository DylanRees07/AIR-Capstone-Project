/*Class to represent a relationship between 2 African autonomous systems.
Each relationship possesses an ID which will correspond to the primary key of its entry in the database.
Each relationship will also indicate the 2 ASNs it pertains to and the type of relationship between them
*/
public class ASRelationship {

    private int id;
    private int asnStart;
    private int asnEnd;
    private String type;

    public ASRelationship(int pid, int pAsnStart, int pAsnEnd, String ptype){
        this.id = pid;
        this.asnStart = pAsnStart;
        this.asnEnd = pAsnEnd;
        this.type = ptype;
    }
/*Getter and setters methods for the AS relationship
*/
    public int getID(){
        return this.id;
    }

    public int getASNStart(){
        return this.asnStart;
    }

    public int getASNEnd(){
        return this.asnEnd;
    }

    public String getType(){
        return this.type;
    }

    public void setID(int pid){
        this.id = pid;
    }

    public void setASNStart(int pAsnStart){
        this.asnStart = pAsnStart;
    }

    public void setASNEnd(int pAsnEnd){
        this.asnEnd = pAsnEnd;
    }

    public void setType(String ptype){
        this.type = ptype;
    }

    public String toString(){
        return "ID: " + String.valueOf(this.id) + " From: " + String.valueOf(this.asnStart) +
        " To: " + String.valueOf(this.asnEnd) + " Type: " + this.type; 
    }
    
}
