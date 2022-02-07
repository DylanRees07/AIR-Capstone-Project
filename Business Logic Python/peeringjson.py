import json
import mysql.connector

def main():
    #Set up connection to database
    db = mysql.connector.connect(host="localhost", database = "air_db", user="root", password = "", autocommit=True)
    cursor = db.cursor()
	#Read all peering records from database
    cursor.execute("SELECT * FROM peering_tb")
    records = cursor.fetchall()
	#Create JSON object
    data = {}
    data["Peering"] = []
	#For each record in records
    for record in records:
		#Get asn, ixp id and tier and store
        asn = record[1]
        ixp = record[2]
        tier = record[3]
		#Query lon, lat for asn
        cursor.execute("SELECT lon, lat from as_tb WHERE asn = %s", (asn,))
        coords1 = cursor.fetchall()[0]
		#Query lon, lat for ixp id
        cursor.execute("SELECT lon, lat from ixp_tb WHERE id = %s", (ixp,))
        coords2 = cursor.fetchall()[0]
		#Append data to json object
        data["Peering"].append({
            "coords1" : coords1,
            "coords2" : coords2,
            "tier" : tier
        })
	#Write JSON to file	
    with open("peeringdata.json", "w") as outfile:
        json.dump(data, outfile)


if __name__ == "__main__":
    main()