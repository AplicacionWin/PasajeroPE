package com.nikola.user.login;


import android.util.Log;

import com.nikola.user.Utils.Const;
import java.util.TimeZone;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONObject;

import java.util.HashMap;


public class AuthUser {
	
	public Respuesta authUser(String user, String pass, String deviceToken) {
		Respuesta resp = new Respuesta();
		try {
			System.out.println("Inicio Auth");
			ObjectMapper mapper = new ObjectMapper();
			HashMap<String, Object> map = new HashMap<>();
			map.put("username", user);
			map.put("password", pass);
			String json = mapper.writeValueAsString(map);
			
/*
			String url = new GetAddress().getUrl(Const.AUTH_USER);
			AbstractClient serv = new AbstractClient();
			String response = serv.webJson(url, json);
			Log.v("response",response);
			JSONObject obj = new JSONObject(response);

			ResponseAuth responseObj = mapper.readValue(response, ResponseAuth.class);
			if (responseObj.getStatus().equalsIgnoreCase("200")) {
				JSONObject data = obj.getJSONObject(Const.Params.DATA);
				JSONObject posts = data.getJSONObject("posts");
				String userid = posts.getString("userid");
				GetUserData getData = new GetUserData();
				UserData userdata = getData.getUser(userid);
				userdata.setDevice_token(deviceToken);
				userdata.setDevice_type(Const.DEVICE_TYPE_ANDROID);
				userdata.setLogin_by(Const.MANUAL);
				userdata.setPassword(pass);
				userdata.setTimezone(TimeZone.getDefault().getID());
				
				resp = new LoginApp().loguearApp(userdata);
				resp.setUserId(userdata.getUserid());
				resp.setUsername(userdata.getUsername());
				
			}
			else 
			{
				resp.setCodigo(responseObj.getData().getPosts().Code);
			}*/
			
			
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			resp.setCodigo("500");
		}
		
		return resp;
	}

}
