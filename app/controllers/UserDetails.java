package controllers;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import models.Repository;
import models.User;
import models.UserRepos;
import play.mvc.Controller;
import play.mvc.Result;

public class UserDetails {
	
	static public User userInfo=new User();
	
	//Calling User api for getting JSON object of user information
	public static JSONObject UserApiCall(String login) {
		JSONObject jsonObject = null;
		try {
			URIBuilder builder = new URIBuilder("https://api.github.com/users/"+login);
			builder.addParameter("accept", "application/vnd.github.v3+json");
			CloseableHttpClient httpclient = HttpClients.createDefault();

			HttpResponse resp = null;
			
			
			HttpGet getAPI = new HttpGet(builder.build());
			resp = httpclient.execute(getAPI);
			
			StatusLine statusLine = resp.getStatusLine();
	        System.out.println(statusLine.getStatusCode() + " " + statusLine.getReasonPhrase());
	        String responseBody = EntityUtils.toString(resp.getEntity(), StandardCharsets.UTF_8);
	        System.out.println(responseBody.length());
	        
			try {
			     jsonObject = new JSONObject(responseBody);
			}catch (JSONException err){
			     err.printStackTrace();
			}
			
		} catch (URISyntaxException | IOException | RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		System.out.println(jsonObject);
//		System.out.println(jsonObject.getString("repos_url"));
		return jsonObject;
	}
	
public static User storeUserInfo(JSONObject user) {
		
 		userInfo.setName((String)(user.getString("name")));
		userInfo.setFollowers((Integer)(user.getNumber("followers")));
     	userInfo.setFollowing((Integer)user.getNumber("following"));
		userInfo.setPublicRepos((Integer)user.getNumber("public_repos"));
		userInfo.setRepoURL(user.getString("repos_url"));
		userInfo.setFollowersURL(user.getString("followers_url"));
		userInfo.setFollowingURL(user.getString("following_url"));
		userInfo.setHtmlURL(user.getString("html_url"));
		userInfo.setLogin(user.getString("login"));
		userInfo.setAvatarURL(user.getString("avatar_url"));
		userInfo.setUserReposlist(user.getString("repos_url"));
		return userInfo;
		
	}

//Calling repo_url
public static  ArrayList<UserRepos> listUserRepos(String repourl)
{
	JSONArray JsonobjectArray = null;
	try {
		URIBuilder builder = new URIBuilder(repourl);
		builder.addParameter("accept", "application/vnd.github.v3+json");
		CloseableHttpClient httpclient = HttpClients.createDefault();

		HttpResponse resp = null;
		
		
		HttpGet getAPI = new HttpGet(builder.build());
		resp = httpclient.execute(getAPI);
		
		StatusLine statusLine = resp.getStatusLine();
        System.out.println(statusLine.getStatusCode() + " " + statusLine.getReasonPhrase());
        String responseBody = EntityUtils.toString(resp.getEntity(), StandardCharsets.UTF_8);
        System.out.println(responseBody.length());
        
		try {
			JsonobjectArray = new JSONArray(responseBody);
		}catch (JSONException err){
		     err.printStackTrace();
		}
		
	} catch (URISyntaxException | IOException | RuntimeException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
//	HashMap<String,String> userReposList= new HashMap<String,String>();
	ArrayList<UserRepos> userReposlist=new ArrayList<UserRepos>();
	
	for(int i=0; i<JsonobjectArray.length();i++) {
		JSONObject repoOfUser = (JSONObject)JsonobjectArray.getJSONObject(i);
		UserRepos r=new UserRepos();
		String reponame= (String)repoOfUser.getString("name");
		r.setRepoName(reponame);
		String repoid= String.valueOf(repoOfUser.getInt("id"));
		r.setRepoId(repoid);
		userReposlist.add(r);
		
	}
	return userReposlist;
	
}

public static Repository setUserReposDetails(JSONObject repository) {
//	System.out.println("Setting user repository details**");
	Repository obj = new Repository();
	
	obj.setVisibility(repository.getString("visibility"));
	obj.setForks(repository.getInt("forks"));
	obj.setIssuesUrl(repository.getString("issues_url"));
	obj.setWatchers_count(repository.getInt("watchers_count"));
//	obj.setScore(repository.getInt("score"));
	obj.setStars(repository.getInt("stargazers_count"));
	obj.setCreatedAt(repository.getString("created_at").substring(0,10));
	obj.setContributorURL(repository.getString("contributors_url"));
//	obj.setRepoCollabs(repository.getString("contributors_url"));
//	obj.setIssueList(repository.getString("issue_url"));

	JSONObject owner = (JSONObject) repository.get("owner");
	obj.setLogin(owner.getString("login"));
	obj.setRepourl(owner.getString("repos_url"));
	obj.setRepoName(repository.getString("name"));
	
	Number id= repository.getNumber("id");
	String idtemp=id.toString();
	System.out.println("ID String"+idtemp);
	obj.setId(idtemp);
	obj.setGitCommitsurl(repository.getString("git_commits_url"));
	obj.setCommitsUrl(repository.getString("commits_url"));
	obj.setIssuesUrl(repository.getString("issues_url"));
	
	JSONArray arr = repository.getJSONArray("topics");
	ArrayList<String> topics = new ArrayList<String>();
	for(int i = 0;i< arr.length();i++) {
		topics.add(arr.getString(i));
	}
	
	obj.setTopics(topics);

	return obj;
	
}

public static JSONObject UserReposApiCall(String id) {
//	System.out.println("Setting user repository details**");
	System.out.println("Inside UserRepo API");
	JSONObject jsonObject = null;
	try {
		URIBuilder builder = new URIBuilder("https://api.github.com/repositories/"+id);
		builder.addParameter("accept", "application/vnd.github.v3+json");
		CloseableHttpClient httpclient = HttpClients.createDefault();

		HttpResponse resp = null;
		
		
		HttpGet getAPI = new HttpGet(builder.build());
		resp = httpclient.execute(getAPI);
		
		StatusLine statusLine = resp.getStatusLine();
        System.out.println(statusLine.getStatusCode() + " " + statusLine.getReasonPhrase());
        String responseBody = EntityUtils.toString(resp.getEntity(), StandardCharsets.UTF_8);
        System.out.println(responseBody.length());
        
		try {
		     jsonObject = new JSONObject(responseBody);
		}catch (JSONException err){
		     err.printStackTrace();
		}
		
	} catch (URISyntaxException | IOException | RuntimeException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	System.out.println(jsonObject);

	return jsonObject;
}

}