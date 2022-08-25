package com.nikola.user.login;


import com.nikola.user.Utils.Const;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONObject;

import java.util.HashMap;


public class GetUserData {

	public UserData getUser(String userid) {
		
		UserData user = new UserData();
		try {
			System.out.println("Inicio get data");
			ObjectMapper mapper = new ObjectMapper();
			HashMap<String, Object> map = new HashMap<>();
			map.put("userid", userid);
			String json = mapper.writeValueAsString(map);
			
/*
			String url = new GetAddress().getUrl(Const.USER_GET);
			AbstractClient serv = new AbstractClient();
			String response = serv.webJson(url, json);
			JSONObject obj = new JSONObject(response);
			JSONObject data = obj.getJSONObject("data");
			JSONObject posts = data.getJSONObject("posts");
			JSONObject details = posts.getJSONObject("details");
			JSONObject profile = posts.getJSONObject("profile");

			System.out.println("Response: " + response);
			user.setUserid(profile.getString("id"));
			user.setEmail(details.getString("email"));
			user.setUsername(profile.getString("username"));
			user.setFirst_name(details.getString("first_name"));
			user.setLast_name(details.getString("last_name"));
			user.setMobile(details.getString("phone_number"));
			user.setCurrency("1");
			user.setReferral_code("");
			user.setCountry_code("+57");*/
			
			
		}
		catch(Exception e)
		{
			System.out.println("error getUser: " + e.getMessage());
		}
		
		
		
		return user;
	}

}
