import mysql.connector

con = mysql.connector.connect(
	host="address"
	user="username"
	password="password"
	database="databaseName"
)

print(con)

cursor = con.cursor()

cursor.execute("SELECT * FROM table")

for x in cursor:
	print(x)


