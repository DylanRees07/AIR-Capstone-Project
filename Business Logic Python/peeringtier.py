import mysql.connector

def main():
    #Set up connection to database
    db = mysql.connector.connect(host="localhost", database = "air_db", user="root", password = "", autocommit=True)
    cursor = db.cursor()
	#Read all peering records from database
    cursor.execute("SELECT * FROM peering_tb")
    records = cursor.fetchall()
	#For each record in records
    for record in records:
		#Get the id and asn
        id = record[0]
        asn = record[1]
		#Query the tier of the asn
        cursor.execute("SELECT tier FROM as_tb WHERE asn = %s", (asn,))
        tier = cursor.fetchall()
		#Update and set the tier of the peering relationship to the asn tier for that id
        cursor.execute("UPDATE peering_tb SET tier = %s WHERE id = %s", (tier[0][0], id))
	



if __name__ == "__main__":
    main()