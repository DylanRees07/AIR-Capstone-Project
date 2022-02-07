import json
import mysql.connector

def main():
    #Set up connection to database
    db = mysql.connector.connect(host="localhost", database = "air_db", user="root", password = "", autocommit=True)
    cursor = db.cursor()
	#Read all records from ixp_tb
    cursor.execute("SELECT * FROM ixp_tb")
    records = cursor.fetchall()
    data = {}
    data["IXPs"] = []
	#For each record (ixp) in records:
    for record in records:
		#Get lon, lat and orgID and store
        orgID = record[3]
        lon = record[4]
        lat = record[5]
        #Append json object to data
        data["IXPs"].append({
            "orgID" : orgID,
            "lon" : lon,
            "lat" : lat
        })
	#Write data to file
    with open("ixpdata.json", "w") as outfile:
        json.dump(data, outfile)

if __name__ == "__main__":
    main()