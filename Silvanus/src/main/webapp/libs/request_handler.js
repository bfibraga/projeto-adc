const HTTP_RESPONSE = {
	"OK": 200,
	"BAD_REQUEST": 400,
	"FORBIDDEN": 403,
	"INTERNAL_SERVER": 500
}

const base_uri = window.location.origin;
var xmlhttp = new XMLHttpRequest();

let res = "";

function getToken() {
	let res = sessionStorage.getItem("token");
	res = JSON.parse(res)
	document.getElementById("token_area").innerText = JSON.stringify(res);
}

function checkUndefined(keyword) {
	return keyword.trim() == "" ? "UNDEFINED" : keyword;
}

async function register(){
	try{
		let u_username = String(document.getElementById("usr_identifier").value);
		let u_email = String(document.getElementById("usr_email").value);
		let u_name = String(document.getElementById("usr_firstname").value + " " + document.getElementById("usr_lastname").value);
		let u_password = String(document.getElementById("usr_password").value);
		let u_confirm = String(document.getElementById("usr_confirm").value);

		let u_role = "USER";
		let u_state = "ACTIVE";
		let u_visibility = "PUBLIC";
		let u_telephone = checkUndefined(String(document.getElementById("usr_telephone").value));
		let u_smartphone = "911";
		let u_nif = String(document.getElementById("usr_id").value);
		let u_address = String(document.getElementById("usr_adress").value);

		const response = await axios.post("/api/user/register",
			{
				"username": u_username,
				"email": u_email,
				"name": u_name,
				"password": u_password,
				"confirm": u_confirm,
				"role": u_role,
				"state": u_state,
				"visibility": u_visibility,
				"nif": u_nif,
				"address": u_address,
				"telephone": u_telephone,
				"smartphone": u_smartphone
		});


		console.log(response);

		//window.location.replace(base_uri + "/login");
	} catch (error){
		console.log(error);
	} finally {

	}
}

/*function notActive() {
	logout(time);
}

function isActive() {
	let res = sessionStorage.getItem("token");
	console.log(res);
	let obj = JSON.parse(res);
	console.log(obj);
	if (xmlhttp) {
		xmlhttp.onreadystatechange = function() {
			if (xmlhttp.readyState === 4) {
				switch (xmlhttp.status) {
					case HTTP_RESPONSE["OK"]:
						//Debug purposes

						let response = JSON.parse(xmlhttp.responseText);
						console.log(response);


						break;
					default:
						document.getElementById("info").innerHTML = new String(xmlhttp.response);
						if (xmlhttp.response == "Token invalid") {
							alert("Token invalid");
							logout(0); 
						}
				}
			}
		}

		obj = JSON.stringify(obj);
		let query = sessionStorage.getItem("username_token");
		xmlhttp.open("GET", base_uri + "/api/user/get?user=" + query);
		xmlhttp.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
		xmlhttp.send(null);
	}
}*/

/*function activate() {

	let u_target_username = new String(document.getElementById("username").value);

	if (xmlhttp) {
		xmlhttp.onreadystatechange = function() {
			if (xmlhttp.readyState === 4) {
				switch (xmlhttp.status) {
					case HTTP_RESPONSE["OK"]:
						//Debug purposes

						let response = JSON.parse(xmlhttp.responseText);
						console.log(response);
						sessionStorage.setItem("username_token", null);
						sessionStorage.setItem("token", null);

						let response = JSON.parse(xmlhttp.responseText);
						console.log(response);

						break;
					default:
						console.log(xmlhttp.responseText);
				}
			}
		}


		obj = JSON.stringify(obj);
		xmlhttp.open("PUT", base_uri + "/api/user/activate/" + u_target_username);
		xmlhttp.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
		xmlhttp.send(obj);
	}
}*/

async function activate(identifier){
	try{
		const response = await axios.post("/api/user/activate/" + identifier);
		console.log(response);
		window.location.replace(base_uri.concat("/app"));
	} catch (error){
		console.log(error);
		//document.getElementById("validation_error").innerHTML = "Palavra-passe ou Utilizador errado";
	} finally {
		//document.getElementById("loader").classList.add("d-none");
	}
}

/*function login() {
	let u_identifier = new String(document.getElementById("usr_identifier").value);
	let u_password = checkUndefined(new String(document.getElementById("usr_password").value));

	if (xmlhttp) {
		xmlhttp.onreadystatechange = function() {
			if (xmlhttp.readyState === 4) {
				switch (xmlhttp.status) {
					case HTTP_RESPONSE["OK"]:
						window.location.replace(base_uri.concat("/app"));
						break;
					default:
						document.getElementById("validation_error").innerHTML = "Palavra-passe ou Utilizador errado";

				}
				document.getElementById("loader").classList.add("d-none");
			}
		}

		xmlhttp.open("POST", base_uri + "/api/user/login/" + u_identifier + "?password=" + u_password);
		xmlhttp.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
		xmlhttp.send(null);
	}
}*/

async function login(){
	try{
		let u_identifier = String(document.getElementById("usr_identifier").value);
		let u_password = checkUndefined(String(document.getElementById("usr_password").value));

		const response = await axios.post("/api/user/login/" + u_identifier + "?password=" + u_password);
		console.log(response);
		window.location.replace(base_uri.concat("/app"));
	} catch (error){
		console.log(error);
		document.getElementById("validation_error").innerHTML = "Palavra-passe ou Utilizador errado";
	} finally {
		document.getElementById("loader").classList.add("d-none");
	}
}

function init() {
	getTime();

	let curr_time = sessionStorage.getItem("time");
	let token = JSON.parse(sessionStorage.getItem("token"));

	console.log(curr_time);
	console.log(token.expirationDate);

	if (token.expirationDate < curr_time) {
		alert("Session Expired! Try login again");
		window.location.replace(base_uri.concat("/app"));
	} else {
		getInfo();
	}
}

/*function getInfo() {
	let res = sessionStorage.getItem("token");
	console.log(res);
	let obj = JSON.parse(res);
	console.log(obj);
	if (xmlhttp) {
		xmlhttp.onreadystatechange = function() {
			if (xmlhttp.readyState === 4) {
				switch (xmlhttp.status) {
					case HTTP_RESPONSE["OK"]:
						//Debug purposes

						let response = JSON.parse(xmlhttp.responseText);
						console.log(response);

						if (response[2] == "INATIVE") {
							window.location.replace(base_uri.concat("/validation.html"));
						}

						document.getElementById("usr_username").innerText = new String(response[0]);
						document.getElementById("usr_mail").innerText = new String(response[1]);

						const name = response[2].split(" ");
						document.getElementById("usr_firstname").innerText = new String("Primeiro Nome: " + name[0]);
						document.getElementById("usr_lastname").innerText = new String("Último Nome:" + name[1]);
						document.getElementById("usr_id").innerText = new String("NIF: " + response[3]);
						document.getElementById("usr_phone").innerText = new String("Número de telefone: " + response[4]);
						document.getElementById("usr_address").innerText = new String("Morada: " + response[5]);

						document.getElementById("loader").classList.add("d-none");

						break;
					default:
						document.getElementById("info").innerHTML = new String(xmlhttp.response);
						if (xmlhttp.response == "Token invalid") {
							alert("Token invalid");
							logout(0);
						}
				}
			}
		}

		obj = JSON.stringify(obj);
		let query = sessionStorage.getItem("username_token");
		xmlhttp.open("GET", base_uri + "/api/user/get?username=" + query);
		xmlhttp.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
		xmlhttp.send(null);
	}
}*/

async function getInfo(){
	try{
		const response = await axios.get("/api/user/info");
		console.log(response);
	} catch (error){
		console.log(error);
	} finally {
	}
}

async function logout() {
	try{
		const response = await axios.get("/api/user/logout");
		console.log(response);
	} catch (error){
		console.log(error);
	} finally {
	}

	/*if (xmlhttp) {
		xmlhttp.onreadystatechange = function() {
			if (xmlhttp.readyState === 4) {
				switch (xmlhttp.status){
					case HTTP_RESPONSE["OK"]:
						sleep(time);
						window.location.replace(base_uri.concat("/"));
						break;
					default:
						console.log(xmlhttp.status);
				}

			}
		}

		obj = JSON.stringify(obj);

		xmlhttp.open("POST", base_uri + "/api/user/logout/");
		xmlhttp.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
		xmlhttp.send(obj);
	}*/

}

/*function change_password() {
	let u_new_password = checkUndefined(new String(document.getElementById("new_password").value));

	let res = sessionStorage.getItem("token");
	console.log(res);
	let obj = JSON.parse(res);
	console.log(obj);
	if (xmlhttp) {
		xmlhttp.onreadystatechange = function() {
			if (xmlhttp.readyState === 4) {
				switch (xmlhttp.status) {
					case HTTP_RESPONSE["OK"]:
						console.log("Password changed successfully")
						document.getElementById("info").innerText = "Password changed successfully";
						break;
					default:
						document.getElementById("info").innerHTML = new String(xmlhttp.response);
						if (xmlhttp.response == "Token invalid") {
							alert("Token invalid");
							logout(0);
						}
				}
			}
		}

		obj = JSON.stringify(obj);

		xmlhttp.open("PUT", base_uri + "/api/user/change?password=" + u_new_password);
		xmlhttp.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
		xmlhttp.send(obj);
	}

}*/

async function change_password(){
	try{
		let u_new_password = checkUndefined(String(document.getElementById("new_password").value));

		const response = await axios.put("/api/user/change", {
			params: {
				password: u_new_password
			}
		});
		console.log(response);
	} catch (error){
		console.log(error);
	} finally {
		console.log("Executed successfully");
	}
}

/*function changing_att() {
	let u_target_username = new String(document.getElementById("username").value);
	let u_name = new String(document.getElementById("name").value);
	let u_visibility = document.getElementById("visibility").checked ? "PRIVATE" : "PUBLIC";
	let u_telephone = checkUndefined(new String(document.getElementById("telephone").value));
	let u_smartphone = checkUndefined(new String(document.getElementById("smartphone").value));

	const query = "[" + "'" + u_visibility + "'" + "," + "'" + u_name + "'" + "," + "'" + u_telephone + "'" + "," + "'" + u_smartphone + "'" + "]";

	//getToken();
	let res = sessionStorage.getItem("token");
	console.log(res);
	let obj = JSON.parse(res);
	console.log(obj);
	if (xmlhttp) {
		xmlhttp.onreadystatechange = function() {
			if (xmlhttp.readyState === 4) {
				switch (xmlhttp.status) {
					case HTTP_RESPONSE["OK"]:
						console.log("Attributes changed successfully")
						document.getElementById("info").innerText = "Attributes changed successfully";
						sleep(1000);
						window.location.replace(base_uri.concat("/app"));
						break;
					default:
						document.getElementById("info").innerHTML = new String(xmlhttp.response);
						if (xmlhttp.response == "Token invalid") {
							alert("Token invalid");
							logout(0);
						}
				}
			}


		}
		obj = JSON.stringify(obj);

		let final_uri = base_uri + "/api/user/change/" + u_target_username + "?attributes=";

		xmlhttp.open("PUT", final_uri + query);
		xmlhttp.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
		xmlhttp.send(obj);
	}

}*/

async function changing_att(){
	try{
		let query = [];
		query.push(String(document.getElementById("username").value));
		query.push(String(document.getElementById("name").value));
		//query.push(document.getElementById("visibility").checked ? "PRIVATE" : "PUBLIC");
		query.push(checkUndefined(String(document.getElementById("telephone").value)));
		query.push(checkUndefined(String(document.getElementById("smartphone").value)));
		const response = await axios.put("/api/user/change", {
			params: {
				attributes: query
			}
		});
		console.log(response);
	} catch (error){
		console.log(error);
	} finally {
		console.log("Executed successfully");
	}
}

async function time(){
	try{
		const response = await axios.get("/api/utils/time");
		console.log(response);
	} catch (error){
		console.log(error);
	} finally {
		console.log("Executed successfully");
	}
}

//--- Terrain ----

async function submitTerrain(points_data) {
	
	let _id_of_owner = String(document.getElementById("usr_username").value);
	let _name_of_terrain = String(document.getElementById("name-terrain").value);
	let _description_of_terrain = String(document.getElementById("description-terrain").value);
	let _conselho_of_terrain = String(document.getElementById("conselho-terrain").value);
	let _freguesia_of_terrain = String(document.getElementById("freguesia-terrain").value);
	let _section_of_terrain = String(document.getElementById("section-terrain").value);
	let _number_article_terrain = String(document.getElementById("number-article-terrain").value);
	let _verification_document_of_terrain = String(document.getElementById("documentation-validation-terrain").value);
	let _type_of_soil_coverage = "";
	let _current_use_of_soil = "";
	let _previous_use_of_soil = "";

	try{
		const response = await axios.post("/api/parcela/create",
			{
				"parcela": points_data,
				"id_of_owner": _id_of_owner,
				"name_of_terrain": _name_of_terrain,
				"description_of_terrain": _description_of_terrain,
				"conselho_of_terrain": _conselho_of_terrain,
				"freguesia_of_terrain": _freguesia_of_terrain,
				"section_of_terrain": _section_of_terrain,
				"number_article_terrain": _number_article_terrain,
				"verification_document_of_terrain": _verification_document_of_terrain,
				"type_of_soil_coverage": _type_of_soil_coverage,
				"current_use_of_soil": _current_use_of_soil,
				"previous_use_of_soil": _previous_use_of_soil
			});
		console.log(response);
	} catch (error){
		console.log(error);
	} finally {
		console.log("Executed successfully");
	}
}

