#include <stdio.h>
#include <string>
#include <iostream>
#include <vector>
#include <map>
#include <algorithm>
using namespace std;
vector<string> sort_tags(const vector<vector<string>> &v);
int main(){
	vector<vector<string>> s;
	vector<string> i;
	i.push_back("fun");
	i.push_back("music");
	i.push_back("funny");
	vector<string> j;
	j.push_back("skateboarding");
	j.push_back("fun");
	j.push_back("park");
	j.push_back("funny");
	vector<string> k;
	k.push_back("funny");
	k.push_back("party");
	s.push_back(i);
	s.push_back(j);
	s.push_back(k);
	vector<string> final;
	final = sort_tags(s);
	cout << final[0] << final[1] << final[2] << final[3] << final[4] << endl;

}

vector<string> sort_tags(const vector<vector<string>> &v){
	vector<string> type_tags;
	for (int i = 0; i < v.size(); i++){
		for (int j = 0; j < v[i].size(); j++){
			//flatten vectors
			type_tags.push_back(v[i][j]);
		}
	}
	map<string, int> m;
	for (auto const & x: type_tags){
		m[x] += 1;
	}
	vector<vector<string>> s;
	for (map<string, int>::iterator it = m.begin(); it != m.end(); it++){
		vector<string> i;
		i.push_back(to_string(it->second));
		i.push_back(it->first);
		s.push_back(i);
	}
	sort(s.begin(), s.end());
	type_tags.clear(); //reuse the same vector
	for (int i = s.size() - 1; i > 0; i--){
		type_tags.push_back(s[i][1]);	
	}
	if (type_tags.size() < 5){
		for (int i = type_tags.size(); i < 5; i++){
			type_tags.push_back("");
		}
	}
	return type_tags;
}