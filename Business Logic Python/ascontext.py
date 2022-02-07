import mysql.connector
import json
import pycristoforo as pyc

#Set up global connection to database, password omitted as with the backend
db = mysql.connector.connect(host="localhost", database = "air_db", user="root", password = "", autocommit=True)
cursor = db.cursor()

def main():
    #addCoords()
    addPeers()

#Add coordinates to each AS
def addCoords():
    #Get list of countries
    cursor.execute("SELECT DISTINCT country FROM as_tb")
    countries = cursor.fetchall()
    #Get counts of asns per country (parallel array to country list)
    cursor.execute("SELECT COUNT(asn) FROM as_tb GROUP BY country")
    counts = cursor.fetchall()
    #For each group of countries:
    for i in range(len(countries)) :
        #Create bounding box for country
        if countries[i][0].lower() == "tanzania":
            country_shape = pyc.get_shape("TZA")
        elif countries[i][0].lower().strip('"') == "cote divoire":
            country_shape = pyc.get_shape("CIV")
        elif countries[i][0].lower() == "reunion":
            continue
        elif countries[i][0].lower().strip('"') == "congo dr":
            country_shape = pyc.get_shape("COD")
        elif countries[i][0].lower() == "congo":
            country_shape = pyc.get_shape("COG")
        elif countries[i][0].lower().strip('"') == "guinea-bissau":
            country_shape = pyc.get_shape("GNB")
        elif countries[i][0].lower().strip('"') == "mayotte":
            continue
        else:
            country_shape = pyc.get_shape(countries[i][0].strip('"'))
        #Create list of points for country based on the bounding box
        points = pyc.geoloc_generation(country_shape, counts[i][0], countries[i][0])
        #Get the list of asns where country = country (same size as points list)
        cursor.execute("SELECT asn FROM as_tb WHERE country = (%s)", (countries[i]))
        asns = cursor.fetchall()
        #For each asn in the country, add a long and a lat value to the database
        j = 0
        for asn in asns:
            cursor.execute("UPDATE as_tb SET lon = %s WHERE asn = %s", (points[j]['geometry']['coordinates'][0], asn[0]))
            cursor.execute("UPDATE as_tb SET lat = %s WHERE asn = %s", (points[j]['geometry']['coordinates'][1], asn[0]))
            j+=1

#Adds whether or not an AS is peered at an ixp
def addPeers():
	#Get all unique asns in peering_tb
    cursor.execute("SELECT DISTINCT asn FROM peering_tb")
    records = cursor.fetchall()
	#For each asn in asns:
    for record in records:
		#Update record in as_tb to peered = true where asn = asn
        cursor.execute("UPDATE as_tb SET is_peered = %s WHERE asn = %s", (1, record[0]))

if __name__ == "__main__":
    main()