#Create json file for the front end to read with relationship data
import json
import mysql.connector

def main():
    #Set up connection to database
    db = mysql.connector.connect(host="localhost", database = "air_db", user="root", password = "", autocommit=True)
    cursor = db.cursor()
    #Read all relationship records from database
    cursor.execute("SELECT * FROM as_rel_tb")
    records = cursor.fetchall()
    data = {}
    data["AS_rel"] = []
    #For each record in records
    for record in records:
        #Get asnstart, asnend, type and tier
        asn1 = record[1]
        asn2 = record[2]
        #Query asnstart lon, lat and store
        cursor.execute("SELECT lon, lat from as_tb WHERE asn = %s", (asn1,))
        coords1 = cursor.fetchall()[0]
        #Query asnend lon, lat and store
        cursor.execute("SELECT lon, lat from as_tb WHERE asn = %s", (asn2,))
        coords2 = cursor.fetchall()[0]
        #Query asnstart orgID
        cursor.execute("SELECT org_id from as_tb WHERE asn = %s", (asn1,))
        org1 = cursor.fetchall()
        #Query asnend orgID
        cursor.execute("SELECT org_id from as_tb WHERE asn = %s", (asn2,))
        org2 = cursor.fetchall()
        #If orgIDs match set type to s2s
        if org1 == org2:
            type = "s2s"
        else:
            type = record[3]
        tier = record[4]
        #Create JSON object with data
        data["AS_rel"].append({
            "coords1" : coords1,
            "coords2" : coords2,
            "type" : type,
            "tier" : tier
        })
    #Append JSON object to file
    with open("asreldata.json", "w") as outfile:
            json.dump(data, outfile)
if __name__ == "__main__":
    main()