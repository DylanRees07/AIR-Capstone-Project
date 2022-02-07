import mysql.connector
import json
import pycristoforo as pyc
import pycountry
#Add coordinates to each AS
def main():
    db = mysql.connector.connect(host="localhost", database = "air_db", user="root", password = "", autocommit=True)
    cursor = db.cursor()
    #Get list of countries
    cursor.execute("SELECT DISTINCT country FROM ixp_tb")
    countries = cursor.fetchall()
    #Get counts of asns per country (parallel array to country list)
    cursor.execute("SELECT COUNT(id) FROM ixp_tb GROUP BY country")
    counts = cursor.fetchall()
    # #For each group of countries:
    for i in range(len(countries)) :
        #Create bounding box for country
        iso_a3 = pycountry.countries.get(alpha_2=countries[i][0]).alpha_3
        if (iso_a3.lower() == "reu"):
            continue
        else:
            country_shape = pyc.get_shape(iso_a3)
        #Create list of points for country based on the bounding box
        points = pyc.geoloc_generation(country_shape, counts[i][0], countries[i][0])
        #Get the list of ixps where country = country (same size as points list)
        cursor.execute("SELECT id FROM ixp_tb WHERE country = (%s)", (countries[i]))
        ids = cursor.fetchall()
        #For each ixp in the country, add a long and a lat value to the database
        j = 0
        for id in ids:
            cursor.execute("UPDATE ixp_tb SET lon = %s WHERE id = %s", (points[j]['geometry']['coordinates'][0], id[0]))
            cursor.execute("UPDATE ixp_tb SET lat = %s WHERE id = %s", (points[j]['geometry']['coordinates'][1], id[0]))
            j+=1


if __name__ == "__main__":
    main()