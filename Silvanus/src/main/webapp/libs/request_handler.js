const base_uri = window.location.origin;

let perfil;

const resource = {
	"USER": "user",
	"TERRAIN": "parcel"
}

function checkUndefined(keyword) {
	return keyword.trim() === "" ? "UNDEFINED" : keyword;
}

async function register(){
	try{
		let u_username = String(document.getElementById("usr_identifier").value);
		let u_email = String(document.getElementById("usr_email").value);
		let u_name = String(document.getElementById("usr_firstname").value + " " + document.getElementById("usr_lastname").value);
		let u_password = String(document.getElementById("usr_password").value);
		let u_confirm = String(document.getElementById("usr_confirm").value);

		//let u_role = "USER";
		//let u_state = "ACTIVE";
		let u_visibility = "PUBLIC";
		let u_telephone = checkUndefined(String(document.getElementById("usr_telephone").value));
		let u_smartphone = "911";
		let u_nif = String(document.getElementById("usr_id").value);
		let u_address = String(document.getElementById("usr_adress").value);

		//TODO Change this
		const response = await axios.post("/api/user/register",
			{
				"credentials": {
					"username": u_username,
					"email": u_email,
					"password": u_password
				},
				"confirm_password": u_confirm,
				"info": {
					"name": u_name,
					"visibility": u_visibility,
					"nif": u_nif,
					"address": u_address,
					"telephone": u_telephone,
					"smartphone": u_smartphone
				}
			});
		window.location.replace(base_uri + "/app");
	} catch (error){
		console.log(error);
	} finally {
	}
}

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

async function logout() {
	try{
		const response = await axios.post("/api/user/logout");
		console.log(response);
		window.location.replace(base_uri);
	} catch (error){
		console.log(error);
	} finally {

	}
}

async function getInfo(debug, user){
	try{

		const response = await axios.get("/api/user/info");
		const response_data = response.data[0];
		perfil = response_data;
		console.log(perfil);

		//Update User Profile
		document.getElementById("usr_username").innerHTML = String(response_data.username);
		document.getElementById("usr_email").innerHTML = String(response_data.email);

		//Put all user badges
		badge(response_data.role_name,response_data.role_color);

		updatePerfil(response_data);

		//Avatar
		//TODO Alter this avatar url
		let avatar_url = "https://storage.cloud.google.com/projeto-adc.appspot.com/placeholder/user.png?authuser=2";
		document.querySelectorAll('.avatar-wrapper')
			.forEach(function(elem) {
				const value = elem.getAttribute("data-user");
				if (value.value === user){
					elem.firstElementChild.src = avatar_url;
					console.log(elem.firstElementChild.src);
				}
		});
		
		await Promise.all([
			getOwnTerrain(),
			listNotification()
		]);

	} catch (error){
		console.log(error);
		if (!debug){
			window.location.replace(base_uri);
		}
	}
}

function updatePerfil(data){
	//User Visible Data
	document.getElementById("usr_fullname").innerHTML = String(data.name);

	document.getElementById("usr_id").innerHTML = String(data.nif);
	document.getElementById("usr_telephone").innerHTML = String(data.telephone);
	document.getElementById("usr_smartphone").innerHTML = String(data.smartphone);
	document.getElementById("usr_address").innerHTML = String(data.address);

	document.getElementById("usr_fullname_input").value = String(data.name);

	document.getElementById("usr_id_input").value = String(data.nif);
	document.getElementById("usr_telephone_input").value = String(data.telephone);
	document.getElementById("usr_smartphone_input").value = String(data.smartphone);
	document.getElementById("usr_address_input").value = String(data.address);
}

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

async function change_password(){
	try{
		let u_new_password = String(document.getElementById("usr_new_password_change_password").value);

		const response = await axios.put("/api/user/change/password?password=" + u_new_password);
		console.log(response);
	} catch (error){
		console.log(error);
	} finally {
		console.log("Executed successfully");
	}
}

async function changing_att(){
	try{
		let u_name = String(document.getElementById("usr_fullname_input").value);
		let u_nif = String(document.getElementById("usr_id_input").value);
		let u_telephone = String(document.getElementById("usr_telephone_input").value);
		let u_smartphone = String(document.getElementById("usr_smartphone_input").value);
		let u_address = String(document.getElementById("usr_address_input").value);

		let obj = {
			"name": u_name,
			"visibility": "",
			"nif": u_nif,
			"address": u_address,
			"telephone": u_telephone,
			"smartphone": u_smartphone
		}

		console.log(obj);

		//TODO Add more attributes to change
		const response = await axios.put("/api/user/change/attributes", obj);
		console.log(response);

		const response_data = response.data;
		perfil = response_data;
		updatePerfil(response_data);

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

//--- Notifications ---

async function listNotification(){
	try{
		const response = await axios.get("/api/notification/list/" + perfil.username);
		console.log(response.data);
		response.data.forEach(element => {
			notification(element.sender, '', element.description);
		});
	} catch (error){
		console.log(error);
	} finally {
		console.log("Executed successfully");
	}
}

//--- Terrain ----

async function submitTerrain(points_data, route_data) {
	
	//TODO Remake this function
	let parcela = [];

	let credentials = {
		"name": String(document.getElementById("name-terrain").value),
		"townhall": String(document.getElementById("townhall-terrain").value),
		"district": String(document.getElementById("district-terrain").value),
		"section": String(document.getElementById("section-terrain").value),
		"number_article": String(document.getElementById("number-article-terrain").value),
	};

	let checked = document.getElementById("this_acc_terrain_option").checked;
	console.log(checked);
	let owner = checked ? perfil : {
		"name": String(document.getElementById("other_acc_fullname_input").value),
		"visibility": "",
		"nif": String(document.getElementById("other_acc_id_input").value),
		"address": String(document.getElementById("other_acc_address_input").value),
		"telephone": String(document.getElementById("other_acc_telephone_input").value),
		"smartphone": String(document.getElementById("other_acc_smartphone_input").value),
	};
	console.log(owner);

	let info = {
		"description": String(document.getElementById("description-terrain").value),
		"type_of_soil_coverage": String(document.getElementById("type-terrain").value),
		"current_use": String(document.getElementById("current-use-terrain").value),
		"previous_use": String(document.getElementById("previous-use-terrain").value),
		"images": [
			//String(document.getElementById("image-terrain").src)
		], //TODO
		"route": route_data
	};

	try{
		let check = await axios.put("/api/" + resource.TERRAIN + "/intersect", parcela)
		console.log(check);

		let response = await axios.post("/api/parcel/create",
			{
				parcela,
				credentials,
				owner,
				info
			});
		console.log(response);
	} catch (error){
		alert(error);
		console.log(error);
	} finally {
		console.log("Executed successfully");
	}
}

async function getOwnTerrain(){

}
