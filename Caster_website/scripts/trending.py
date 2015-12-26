#calculates trending tags, views and likes
#stores them in the db
#########################TODO#########################
#sort for tags
#create db trending_list table
#create first ... fifth columns in trending_list
#implement a SQL request from php to grab trending info
#show trending info on website
#test code
import pymssql

#variables
server = ""
user = ""
password = ""
#connect to db
conn = pymssql.connect(server, user, password, "caster_db")
cursor = conn.cursor()
#select * from podcasts
cursor.execute('SELECT * FROM podcast_list')
cursor.commit()

trending_tags = []
trending_likes = []
trending_views = []

def main():
	trending = open('trending.txt', 'r')
	#check if there was a previously made file
	if(trending.read() == ""):
		trending.close()
		trending = open('trending.txt', 'w')
		for row in cursor:
			trending.write(row[0] + " " + row[8] + " " + row[9] + " " + row[10])
			#python sorts tuples by the first value
			#eg: [[1,2],[2,1],[0,5]] -> [[0,5],[1,2],[2,1]]
			#trending_tags.append([row[8],row[0]])
			trending_likes.append([row[9],row[0]])
			trending_views.append([row[10],row[0]])
		
		#pad empty values to simply code later
		while(trending_tags.length <5):
			#trending_tags.append([0,0])
			trending_views.append([0,0])
			trending_likes.append([0,0])
		
		#sorts ascending
		#trending_tags.sort()
		trending_likes.sort()
		trending_views.sort()
		x.reverse()

		#clear the table
		clearTable()

		#insert data into table
		cursor.executemany("INSERT INTO trending_list values (%d, %d, %d, %d, %d)",
			[#(trending_tags[0][0], trending_tags[1][0], trending_tags[2][0], trending_tags[3][0], trending_tags[4][0]),
			 (trending_views[0][0], trending_views[1][0], trending_views[2][0], trending_views[3][0], trending_views[4][0]),
			 (trending_likes[0][0], trending_likes[1][0], trending_likes[2][0], trending_views[3][0], trending_likes[4][0])])
		cursor.commit()
	else:
		#calculate change in view counts and likes
		for row in cursor:
			for line in trending:
				if row[0] == line.split()[0]:
					split = line.split()
					#trending_tags.append([row[8] - split[1], row[0]])
					trending_views.append([row[9] - split[2], row[0]])
					trending_likes.append([row[10] - split[3], row[0]])
					break

		#pad empty values to simply code later
		while(trending_tags.length <5):
			#trending_tags.append([0,0])
			trending_views.append([0,0])
			trending_likes.append([0,0])

		trending_views.sort()
		trending_likes.sort()
		x.reverse()

		clearTable()

		#insert data into table
		cursor.executemany("INSERT INTO trending_list values (%d, %d, %d, %d, %d)",
			[#(trending_tags[0][0], trending_tags[1][0], trending_tags[2][0], trending_tags[3][0], trending_tags[4][0]),
			 (trending_views[0][0], trending_views[1][0], trending_views[2][0], trending_views[3][0], trending_views[4][0]),
			 (trending_likes[0][0], trending_likes[1][0], trending_likes[2][0], trending_views[3][0], trending_likes[4][0])])
		cursor.commit()

		#write new values into trending.txt
		trending.close()
		trending = open("trending.txt", 'w')
		for row in cursor:
			trending.write(row[0] + " " + row[8] + " " + row[9] + " " + row[10])


def clearTable():
	cursor.execute("UPDATE trending_list SET first = 0")
	cursor.execute("UPDATE trending_list SET second = 0")
	cursor.execute("UPDATE trending_list SET third = 0")
	cursor.execute("UPDATE trending_list SET fourth = 0")
	cursor.execute("UPDATE trending_list SET fifth = 0")
	cursor.commit()	

if (__name__=="__main__"):
	main()