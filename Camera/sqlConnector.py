import mysql.connector

con = mysql.connector.connect(
    host ="linux.cs.ncl.ac.uk",
    user ="b9015109",
    password ="Fgdcxaz1",
    database ="t2033t17"
    )

print(con)

cursor = con.cursor()

cursor.execute("SELECT * FROM table")

for x in cursor:
	print(x)


