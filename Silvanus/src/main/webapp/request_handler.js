const HTTP_RESPONSE = {
	"OK": 200,
	"BAD_REQUEST": 400,
	"FORBIDDEN": 403,
	"INTERNAL_SERVER": 500
}

const MESSAGE_TYPE = {
	"USERNAME_INVALID": "Please enter your username",
	"EMAIL_INVALID": "Please enter a valid email",
	"NAME_INVALID": "Please enter your name",
	"PASSWORD_INVALID": "Please enter your password",
	"PASSWORD_MISMATCH": "Please enter your password again",
	"WRONG_PARAMETERS": "Wrong username or password"
}

const ROLE_COLOR = {
	USER: "rgb(200,200,200)",
	GBO: "rgb(250, 200, 200)",
	GS: "rgb(200, 250, 200)",
	SU: "rgb(200,200,250)"
}

const base_uri = "https://projeto-adc.appspot.com";
var xmlhttp = new XMLHttpRequest();

let res = "";

function getToken() {
	let res = sessionStorage.getItem("token");
	res = JSON.parse(res)
	document.getElementById("token_area").innerText = JSON.stringify(res);
}

function sleep(milliseconds) {
	const date = Date.now();
	let currentDate = null;
	do {
		currentDate = Date.now();
	} while (currentDate - date < milliseconds);
}

function checkUndefined(keyword) {
	return keyword.trim() == "" ? "UNDEFINED" : keyword;
}

function register() {
	let u_username = new String(document.getElementById("usr_identifier").value);
	let u_email = new String(document.getElementById("usr_email").value);
	let u_name = new String(document.getElementById("usr_firstname").value + " " + document.getElementById("usr_lastname").value);
	let u_password = new String(document.getElementById("usr_password").value);
	let u_confirm = new String(document.getElementById("usr_confirm").value);

	let u_role = "USER";
	let u_state = "ACTIVE";
	let u_visibility = "PUBLIC";
	let u_telephone = checkUndefined(new String(document.getElementById("usr_telephone").value));
	let u_smartphone = "911";
	let u_nif = new String(document.getElementById("usr_id").value);
	let u_address = new String(document.getElementById("usr_adress").value);

	let obj = {
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
		"smartphone": u_smartphone,
	}

	if (xmlhttp) {
		xmlhttp.onreadystatechange = function() {
			if (xmlhttp.readyState === 4) {
				switch (xmlhttp.status) {
					case HTTP_RESPONSE["OK"]:
						window.location.replace(base_uri + "/validation.html");
						break;
					default:
						console.log(JSON.parse(xmlhttp.statusText));
				}
			}
		}

		obj = JSON.stringify(obj);

		xmlhttp.open("POST", base_uri + "/api/user/register");
		xmlhttp.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
		xmlhttp.send(obj);
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

function activate() {

	let u_target_username = new String(document.getElementById("username").value);

	if (xmlhttp) {
		xmlhttp.onreadystatechange = function() {
			if (xmlhttp.readyState === 4) {
				switch (xmlhttp.status) {
					case HTTP_RESPONSE["OK"]:
						//Debug purposes

						/*let response = JSON.parse(xmlhttp.responseText);
						console.log(response);
						sessionStorage.setItem("username_token", null);
						sessionStorage.setItem("token", null);*/

						let response = JSON.parse(xmlhttp.responseText);
						console.log(response);

						break;
					default:
						console.log(xmlhttp.responseText)
				}
			}
		}


		obj = JSON.stringify(obj);
		xmlhttp.open("PUT", base_uri + "/api/user/activate/" + u_target_username);
		xmlhttp.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
		xmlhttp.send(obj);
	}
}

function login() {
	let u_identifier = new String(document.getElementById("identifier").value);
	let u_password = checkUndefined(new String(document.getElementById("password").value));

	let obj;

	//TODO: Verify if given identifier is a username or a email
	obj = {
		"username": u_identifier,
		"email": "UNDEFINED",
		"password": u_password
	}

	if (xmlhttp) {
		xmlhttp.onreadystatechange = function() {
			if (xmlhttp.readyState === 4) {
				switch (xmlhttp.status) {
					case HTTP_RESPONSE["OK"]:
						//Debug purposes

						let response = JSON.parse(xmlhttp.responseText);
						console.log(response);
						sessionStorage.setItem("username_token", response.username);
						sessionStorage.setItem("token", xmlhttp.responseText);

						window.location.replace(base_uri.concat("/app.html"));

						break;
					default:
						document.getElementById("info").innerHTML = new String(xmlhttp.response);
				}
			}
		}

		obj = JSON.stringify(obj);

		xmlhttp.open("POST", base_uri + "/api/user/login/");
		xmlhttp.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
		xmlhttp.send(obj);
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
		window.location.replace(base_uri.concat("/app.html"));
	} else {
		getInfo();
	}
}

function toTimestamp(strDate) {
	var datum = Date.parse(strDate);
	return datum / 1000;
}

function getTime() {
	if (xmlhttp) {
		xmlhttp.onreadystatechange = function() {
			if (xmlhttp.readyState === 4) {
				switch (xmlhttp.status) {
					case HTTP_RESPONSE["OK"]:
						//Debug purposes

						/*let response = JSON.parse(xmlhttp.responseText);
						console.log(response);
						sessionStorage.setItem("username_token", null);
						sessionStorage.setItem("token", null);*/

						let response = JSON.parse(xmlhttp.responseText);
						console.log(toTimestamp(response));

						sessionStorage.setItem("time", toTimestamp(response));

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

		xmlhttp.open("GET", base_uri + "/api/utils/time");
		xmlhttp.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
		xmlhttp.send(null);
	}
}

function getInfo() {
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

						document.getElementById("visibility").innerText = new String(response[0]);
						document.getElementById("username").innerText = new String("Welcome " + response[1]);
						document.getElementById("role").innerText = new String(response[5]);
						document.getElementById("main").style = "background-color: " + ROLE_COLOR[response[5]];

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
}

function logout(time) {
	let res = sessionStorage.getItem("token");
	console.log(res);
	let obj = JSON.parse(res);
	console.log(obj);
	if (xmlhttp) {
		xmlhttp.onreadystatechange = function() {
			if (xmlhttp.readyState === 4) {
				sleep(time);
				window.location.replace(base_uri.concat("/"));
			}
		}

		obj = JSON.stringify(obj);

		xmlhttp.open("POST", base_uri + "/api/user/logout/");
		xmlhttp.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
		xmlhttp.send(obj);
	}

}

function change_password() {
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

}

function changing_att() {
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
						window.location.replace(base_uri.concat("/app.html"));
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

}

function listUsers() {
	let res = sessionStorage.getItem("token");
	console.log(res);
	let obj = JSON.parse(res);
	console.log(obj);
	if (xmlhttp) {
		xmlhttp.onreadystatechange = function() {
			if (xmlhttp.readyState === 4) {
				switch (xmlhttp.status) {
					case HTTP_RESPONSE["OK"]:
						/*let response = JSON.parse(xmlhttp.responseText);
						let result = "";
						for (let i = 0 ; i < response.length ; i++){
							result += response[i];
						}
						*/
						console.log(xmlhttp.responseText);
						document.getElementById("list_users").value = xmlhttp.response != null ? JSON.stringify(xmlhttp.response) : "";
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

		xmlhttp.open("GET", base_uri + "/api/user/list/");
		xmlhttp.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
		xmlhttp.send(obj);
	}
}

function remove() {
	let u_target_username = new String(document.getElementById("username").value);


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
						window.location.replace(base_uri.concat("/app.html"));
						break;
					default:
						document.getElementById("info").innerHTML = new String(xmlhttp.response);
						if (xmlhttp.response == "Token invalid") {
							alert("Token invalid");
							logout(0); 
						}
						if (u_target_username == sessionStorage.getItem("username")){
							logout(0);
						}
				}
			}
		}

		obj = JSON.stringify(obj);

		let final_uri = base_uri + "/api/user/remove/" + u_target_username;

		xmlhttp.open("DELETE", final_uri);
		xmlhttp.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
		xmlhttp.send(obj);
	}
}


function promote() {
	let u_target_username = new String(document.getElementById("target_username").value);
	let u_role = new String(document.getElementById("roles").value);

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
						window.location.replace(base_uri.concat("/app.html"));
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

		let final_uri = base_uri + "/api/user/promote/" + u_target_username;

		xmlhttp.open("PUT", final_uri + "?role=" + u_role);
		xmlhttp.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
		xmlhttp.send(obj);
	}
}


//--- Terrain ----

function submitTerrain(){

}

