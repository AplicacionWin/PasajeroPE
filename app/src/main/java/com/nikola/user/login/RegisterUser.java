package com.nikola.user.login;
import com.nikola.user.Utils.Const;

import org.json.JSONObject;


public class RegisterUser {


	public Respuesta registerUser(UserData user) {
		
		Respuesta respRegister = new Respuesta();
		try {
			System.out.println("Inicio");

			String url = Const.ServiceType.REGISTER;
			AbstractClient serv = new AbstractClient();
			String response = serv.web(url, user.toString());
			System.out.println("response register: " + response);
			JSONObject obj = new JSONObject(response);
			Boolean status = obj.getBoolean("success");
			if(status)
			{
				JSONObject data = obj.getJSONObject("data");
				respRegister.setMensaje(data);
				respRegister.setCodigo("0");

			}
			else if (!status)
			{
				int error_Code = obj.getInt("error_code");
				respRegister.setCodigo(error_Code+"");
			}

			
		} catch (Exception e) {
			System.out.println("error catch: " + e);
		}
		return respRegister;
	}
	
	

}
