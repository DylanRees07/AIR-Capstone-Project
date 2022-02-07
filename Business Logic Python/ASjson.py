import mysql.connector
import json

def main():
    #Set up connection to database
    db = mysql.connector.connect(host="localhost", database = "air_db", user="root", password = "", autocommit=True)
    cursor = db.cursor()
	#Read all AS records from database and store
    cursor.execute("SELECT * FROM as_tb")
    records = cursor.fetchall()
    data = {}
    data["AS"] = []
	#For each record in records:
    for record in records:
		#Get asn, orgID, lon, lat and tier and store
        asn = record[0]
        orgID = record[3]
        lon = record[4]
        lat = record[5]
        tier = record[6]
        isPeered = record[7]
		#Append to JSON object
        data["AS"].append({
            "asn" : asn,
            "orgID" : orgID,
            "lon" : lon,
            "lat" : lat,
            "tier" : tier,
            "isPeered" : isPeered
        })
	#Append to file
    with open("asdata.json", "w") as outfile:
            json.dump(data, outfile)


if __name__ == "__main__":
    main()