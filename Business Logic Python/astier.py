import mysql.connector
#Add tiers indicating scale to each AS and each AS relationship in the database

def main():
    astier()
    reltier()

def reltier():
    #Set up connection to database
    db = mysql.connector.connect(host="localhost", database = "air_db", user="root", password = "", autocommit=True)
    cursor = db.cursor()
	#Get list of ids from as_rel_tb
    cursor.execute("SELECT id FROM as_rel_tb")
    ids = cursor.fetchall()
	#For each id in ids:
    for id in ids:
		#Query the asnstart and the asnend and store each
        cursor.execute("SELECT asn_start, asn_end FROM as_rel_tb WHERE id = %s", (id))
        rel = cursor.fetchall()
        asn1 = rel[0]
        asn2 = asn1[1]
		#Query tier for each asn and store each
        cursor.execute("SELECT tier FROM as_tb WHERE asn = %s", (asn1[0],))
        tier1 = cursor.fetchall()
        cursor.execute("SELECT tier FROM as_tb WHERE asn = %s", (asn2,))
        tier2 = cursor.fetchall()
		#Update record to tier = max tier where id = id
        tier = max(tier1[0][0], tier2[0][0])
        cursor.execute("UPDATE as_rel_tb SET tier = %s WHERE id = %s", (tier, id[0]))





def astier():
    #Set up connection to database
    db = mysql.connector.connect(host="localhost", database = "air_db", user="root", password = "23@arundel", autocommit=True)
    cursor = db.cursor()
    #Get a list of all asns from as_tb
    cursor.execute("SELECT asn FROM as_tb")
    asns = cursor.fetchall()
    #For each asn in asns:
    for asn in asns:
        #Check the cone size
        cursor.execute("SELECT cone_size FROM as_tb where asn = %s", (asn))
        coneSize = cursor.fetchall()[0][0]
        #If conesize > a cutoff set tier to appropriate value:
        if coneSize > 100:
            tier = 1
        elif coneSize > 25:
            tier = 2
        elif coneSize > 1:
            tier = 3
        else:
            tier = 4
        #Update record to indicate correct tier where asn = asn
        cursor.execute("UPDATE as_tb SET tier = %s WHERE asn = %s", (tier, asn[0]))



if __name__ == "__main__":
    main()