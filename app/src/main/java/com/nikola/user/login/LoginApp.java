package com.nikola.user.login;

import com.nikola.user.Utils.Const;

import org.json.JSONObject;

public class LoginApp {

	public Respuesta loguearApp(UserData user) {
		Respuesta respLogin = new Respuesta();
		try {
			System.out.println("Inicio getInfoUser");

			String url = Const.ServiceType.GET_INFO_USER;
			AbstractClient serv = new AbstractClient();
			String response = serv.web(url, user.toString());
			System.out.println("response login: " + response);

			JSONObject obj = new JSONObject(response);

			Boolean status = obj.getBoolean("success");
			if(status)
			{
				JSONObject data = obj.getJSONObject("data");
				respLogin.setMensaje(data);
				respLogin.setCodigo("0");
				System.out.println(data.toString());
			}
			else if (!status)
			{
				int error_Code = obj.getInt("error_code");
				if(error_Code == 1002)
				{
					respLogin = new RegisterUser().registerUser(user);
					
				}
				else if(error_Code ==102)
					respLogin.setCodigo("102");
					
			}
			
			
		} catch (Exception e) {
			System.out.println("error Login: " + e);
		}

		return respLogin;
	}
}
