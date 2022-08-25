package com.nikola.user.login;

import org.json.JSONObject;

public class Respuesta {

	public String codigo;
	public JSONObject mensaje;
	public String userId;
	public String username;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	public JSONObject getMensaje() {
		return mensaje;
	}
	public void setMensaje(JSONObject mensaje) {
		this.mensaje = mensaje;
	}
	
	
}
