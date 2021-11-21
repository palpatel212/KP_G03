package models;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import controllers.ApiCall;
import play.mvc.Controller;
import javax.inject.Inject;
//import play.cache.*;
import play.mvc.*;
import javax.inject.Inject;

/**
 * Model class for storing Topic data
 * 
 * @author Pal Patel
 * @version 1.0
 */
public class RepoTopics{

	static public List<Repository> repotopics = new ArrayList<Repository>();
	public static void setRepoTopics(JSONObject repository) {
		Repository obj = new Repository();
		try {
			obj.setVisibility(repository.getString("visibility"));
			obj.setForks(repository.getInt("forks"));
			obj.setIssuesUrl(repository.getString("issues_url"));
			obj.setWatchers_count(repository.getInt("watchers_count"));
			obj.setScore(repository.getInt("score"));
			obj.setStars(repository.getInt("stargazers_count"));
			obj.setCreatedAt(repository.getString("created_at").substring(0,10));
			obj.setContributorURL(repository.getString("contributors_url"));
			//		obj.setRepoCollabs(repository.getString("contributors_url"));
			//		obj.setIssueList(repository.getString("issue_url"));

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
			repotopics.add(obj);

		}

		catch(Exception e) {
			System.out.println(e);
		}
	}

	public static List<Repository> getRepoDetails(String word) {
		//		CompletableFuture<List<Repository>> future = new CompletableFuture<>();

		HashMap<String, String> map = new HashMap<String, String>();
		map.put("accept", "application/vnd.github.v3+json");
		map.put("per_page", "10");

		String url = "https://api.github.com/search/repositories?q=topic:"+ word;

		ApiCall.getApiCall(url, map).thenAccept(responseBody -> {
			JSONObject json = new JSONObject(responseBody);
			System.out.println(json.toString(4));
			//			CompletionStage<Done> result = cache.set("item.key", json.toString());
			org.json.JSONArray array = json.getJSONArray("items");

			ArrayList<JSONObject> listData = new ArrayList<JSONObject>();
			for(int i=0; i<array.length();i++) {
				listData.add(array.optJSONObject(i));
			}

			listData.parallelStream().forEach(RepoTopics::setRepoTopics);
		});

		//		future.complete(repotopics);

		return repotopics;
	}


	//Calling repo_url
	public static ArrayList<String> listCollabRepos(String contributorURL)
	{
		System.out.println("In List Repo Collabs Function");
		JSONArray JsonobjectArray = null;
		try {
			URIBuilder builder = new URIBuilder(contributorURL);
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
			}
			catch (JSONException err){
				err.printStackTrace();
			}

		} 
		catch (URISyntaxException | IOException | RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



		ArrayList<String> userCollabsList= new ArrayList<String>();

		for(int i=0; i<JsonobjectArray.length();i++) {
			Repository obj = new Repository();
			JSONObject collabsOfRepo = (JSONObject)JsonobjectArray.getJSONObject(i);
			String collabname= (String)collabsOfRepo.getString("login");
			userCollabsList.add(collabname);

		}
		System.out.println("------------------"+userCollabsList);
		return userCollabsList;

	}

}



