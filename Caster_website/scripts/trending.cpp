#include <stdio.h>
#include <SQLAPI.h>
#include <vector>
#include <string>
#include <iostream>
#include <fstream>
#include <algorithm>

namespace std;

vector<string> sort_tags(vector<vector<string>> &v);
vector<string> sort_likes(vector<vector<string>> &v);
void clear_table(SACommand &cmd);

int main(int argc, char* argv[]){
	SAConnection con;
	SACommand cmd;

	//The following a sorted most popular at the beginning
	//Popularity calculated by change over the last time trending.txt was updated
	vector<string> trending_tags; //contains a list of the 5 most popular tags
	vector<string> trending_likes; //contains a list of the 5 most liked podcasts by podcast_id
	vector<string> trending_views; //contains a list of the 5 most viewed podcasts by podcast_id

	vector<vector<string>> temp_trending_tags; //[[tags], [tags], [tags]]
	vector<vector<string>> temp_trending_likes; //[[podcast_id, #likes], [podcast_id, #likes], [podcast_id, #likes]]
	vector<vector<string>> temp_trending_views; //[[podcast_id, #views], [podcast_id, #views], [podcast_id, #likes]]

	try{
		string db_name;
		string uname;
		string passw;

		ifstream credentials ("credentials.txt");
		getline(credentials, db_name);
		getline(credentials, uname);
		getline(credentials, passw);
		credentials.close();

		con.Connect(
			db_name,
			uname,
			passw,
			SA_SQL_Client);

		printf("Connected\n");

		//Associate command with connection
		cmd.setConnection(&con);

		//Execute SQL Command
		cmd.setCommandText("SELECT * FROM podcast_list");
		cmd.Execute();

		string podcast_id;
		string likes;
		vector<string> tags;
		string listens;

		ifstream trending ("trending.txt");
		string line;

		if (trending.is_open() && !getline(trending, line)){
			//trending.txt is empty
			trending.close();
			ofstream trending ("trending.txt");
			while(cmd.FetchNext()){
				podcast_id = cmd.Field("podcast_id").asString();
				likes = cmd.Field("likes").asString();
				tags = cmd.Field("tags").asString().split(' ');
				listens =  cmd.Field("listens").asString();
				printf("Podcast fetched: %s, %s likes, %s tags, %s listens",
					podcast_id,
					likes,
					tags,
					listens);

				//write the row value to trending.txt
				trending << podcast_id << " " << likes << " " << cmd.Field("tags").asString() << " " << listens << endl;

				//create vectors that keep podcast id with their respective stays together
				vector<string> temp_listens;
				temp_listens.push_back(podcast_id);
				temp_listens.push_back(listens);

				vector<string> temp_likes;
				temp_likes.push_back(podcast_id);
				temp_likes.push_back(likes);

				//store the current row
				temp_trending_tags.push_back(tags);
				temp_trending_views.push_back(temp_listens);
				temp_trending_likes.push_back(temp_likes);
			}

			//append empty values if we don't make a top 5
			while(temp_trending_likes.size() < 5){
				temp_trending_likes.push_back(0);
				temp_trending_views.push_back(0);
				vector<string> v;
				temp_trending_tags.push_back(v);
			}

			//reverse the vectors
			reverse(temp_trending_tags.begin(), temp_trending_tags.end());
			reverse(temp_trending_likes.begin(), temp_trending_likes.end());
			reverse(temp_trending_views.begin(), temp_trending_views.end());

			//clear table
			clear_table(&cmd);

			//sort the vectors
			trending_tags = sort_tags(&temp_trending_tags);
			trending_likes = sort_likes(&temp_trending_likes);
			trending_views = sort_likes(&temp_trending_views);

			//insert the values into the table

			cmd.setCommandText("INSERT into trending_list values(:1, :2, :3, :4, :5");
			for(int i = 0; i < 5; i++){
				cmd.Param(i+1).setAsString() = trending_tags[i];
			}
			//Insert trending tags row
			cmd.Execute();

			cmd.setCommandText("INSERT into trending_list values(:1, :2, :3, :4, :5");
			for(int i = 0; i < 5; i++){
				cmd.Param(i+1).setAsString() = trending_likes[i];
			}
			//Insert trending likes row
			cmd.Execute();

			cmd.setCommandText("INSERT into trending_list values(:1, :2, :3, :4, :5");
			for(int i = 0; i < 5; i++){
				cmd.Param(i+1).setAsString() = trending_views[i];
			}
			//Insert trending views row
			cmd.Execute();

			cmd.Commit();

			trending.close();
		}
		else if(trending.is_open() && line){
			//trending.txt has values
			vector<string> line_split = line.split(' ');
			
		}
		else{
			cout << "trending.txt went wrong" << endl;
		}


		con.Disconnect();
		printf("Disconnected\n");
	}
	catch(SAException &x){
		try{
			con.Rollback();
		}
		catch(SAException &){
		}
		printf("%s\n", (const char*)x.ErrText());
	}
	return 0;
}

vector<string> sort_tags(vector<vector<string>> &v){

}

vector<string> sort_likes(vector<vector<string>> &v){

}

void clear_table(SACommand &cmd){
	cmd->setCommandText("UPDATE trending_list SET first = 0");
	cmd->setCommandText("UPDATE trending_list SET second = 0");
	cmd->setCommandText("UPDATE trending_list SET third = 0");
	cmd->setCommandText("UPDATE trending_list SET fourth = 0");
	cmd->setCommandText("UPDATE trending_list SET fifth = 0");
	cmd->Execute();
	cmd->Commit();
}