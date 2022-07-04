const base_uri = window.location.origin;

let perfil;
let terrain_list = [];

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
		loader("false");
	}
}

async function logout() {
	try{
		const viewport = getViewport();
		console.log(viewport);
		const response = await axios.post("/api/user/logout",
			{
				"center": viewport.center,
				"zoom": viewport.zoom
			});
		console.log(response);
		window.location.replace(base_uri);
	} catch (error){
		console.log(error);
	} finally {

	}
}

async function getInfo(debug, user){

	let loader = document.getElementById("loader");

	try{
		
		/*communityResponsible("Grande", "grande@email.com", "https://cdn.discordapp.com/attachments/963781705100066836/991750011773779968/unknown.png")
		communityMember("Teste", "teste@gmail.com", "https://media.discordapp.net/attachments/519977496117248012/982784973062942770/petpet.gif");
		communityMember("Teste1", "teste@gmail.com", "https://media.discordapp.net/attachments/519977496117248012/982783082623029258/petpet.gif");
		communityMember("Teste2", "teste@gmail.com", "mekie");
		communityMember("Teste3", "teste@gmail.com", "mekie");
		communityMember("Teste4", "teste@gmail.com", "mekie");
		terrainCard('1:1', 'Teste', 'Status', 'Description');
		terrainCard('1:1', 'Teste', 'Status', 'Description');
		terrainCard('1:1', 'Teste', 'Status', 'Description');
		terrainCard('1:1', 'Teste', 'Status', 'Description');
		terrainCard('1:1', 'Teste', 'Status', 'Description');

		terrainPendingCard("1","2","3s");*/

		const response = await axios.get("/api/user/info");
		const response_data = response.data[0];
		perfil = response_data;
		console.log(perfil);

		//Update User Profile
		document.getElementById("usr_username").innerHTML = String(response_data.username);
		document.getElementById("usr_email").innerHTML = String(response_data.email);

		//Put all user badges
		badge(response_data.role_name,response_data.role_color);
		updatePerfil(response_data.info);

		let lastLogout = response_data.logoutData;
		setCenter(lastLogout.center);
		setZoom(lastLogout.zoom);

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
		
		Promise.all([
			getOwnTerrain(),
			getPendingTerrain(),
			listNotification()
		]);

	} catch (error){
		console.log(error);
		if (!debug){
			window.location.replace(base_uri);
		}
	} finally{
		loader.setAttribute("data-app-menu-active", "false");
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
	try {
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
		loader('usr_change_profile_menu','false');
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
	try {
		console.log(perfil);
		const response = await axios.get("/api/notification/list/" + perfil.username);
		console.log(response.data);
		response.data.forEach(element => {
			console.log(element);
			notification(element.receiver, '', element.description);
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
	let parcela = points_data;
	console.log(points_data);

	let credentials = {
		"name": String(document.getElementById("name-terrain").value),
		"townhall": String(document.getElementById("townhall-terrain").value),
		"district": String(document.getElementById("district-terrain").value),
		"section": String(document.getElementById("section-terrain").value),
		"number_article": String(document.getElementById("number-article-terrain").value),
	};

	let checked = document.getElementById("this_acc_terrain_option").checked;
	console.log(checked);
	let owner = checked ? 
	{
		"name": String(perfil.info.name),
		"nif": String(perfil.info.nif),
		"address": String(perfil.info.address),
		"telephone": String(perfil.info.telephone),
		"smartphone": String(perfil.info.smartphone),
	} :
	 {
		"name": String(document.getElementById("other_acc_fullname_input").value),
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
		/*let check = await axios.put("/api/" + resource.TERRAIN + "/intersect", parcela)
		console.log(check);*/

		let parcel = {
			parcela,
			credentials,
			owner,
			info
		};
		let response = await axios.post("/api/parcel/create", parcel);
		console.log(response);
		const status = String(credentials.townhall) + " " + String(credentials.district);
		terrainPendingCard(credentials.name, status, info.description);

	} catch (error){
		//alert(error);
		console.log(error);
	} finally {
		console.log("Executed successfully");
	}
}

async function getOwnTerrain(){
	try{
		let response = await axios.post("/api/parcel/list");
		terrain_list = response.data;
		if (terrain_list != null || terrain_list !== []){
			for (let i = 0; i < terrain_list.length; i++) {
				const element = terrain_list[i];
				//const id = element.credentials.id;
				//terrain_list[i] = element;

				const status = String(element.credentials.townhall) + " " + String(element.credentials.district);
				terrainCard(String(i), element.credentials.name, status, element.info.description);
			}
		}
		
		console.log(response);
	} catch (error){
		//alert(error);
		console.log(error);
	} finally {
		console.log("Executed successfully");
	}
}

async function getPendingTerrain(){
	try{
		let response = await axios.post("/api/parcel/list/pending");
		const data = response.data;
		console.log(data);
		if (data != null || data != []) {
			data.forEach(element => {
				console.log(element);
				const status = String(element.credentials.townhall) + " " + String(element.credentials.district);
				terrainPendingCard(element.credentials.name, status, element.info.description);
			});
		}
		
		console.log(response);
	} catch (error){
		//alert(error);
		console.log(error);
	} finally {
		console.log("Executed successfully");
	}
}

async function loadChunk(pos){
	try{
		let response = await axios.get("/api/parcel/list/chunk",{
			params: pos
		});
		console.log(response);
		const response_data = response.data;
		console.log(response_data.chunk);

		const array = response_data.data;
		array.forEach(element => {
			console.log(element);
			addPolygon(element.points, element.color);
		});
	} catch (error){
		console.log(error);
	} finally {
	}
}

function loadTerrainInfo(id){
	const terrain = terrain_list[parseInt(id)];
	console.log(terrain);
	if (terrain == null) return;

	document.getElementById("terrain_name").innerHTML += terrain.credentials.name;
	document.getElementById("terrain_description").innerHTML += terrain.info.description;
	document.getElementById("terrain_townhall").innerHTML += terrain.info.townhall;
	document.getElementById("terrain_district").innerHTML += terrain.info.district;
	document.getElementById("terrain_number_section").innerHTML += terrain.info.section;
	document.getElementById("terrain_number_article").innerHTML += terrain.info.number_article;
	//document.getElementById("terrain_documents_validation").insertAdjacentHTML("beforeend", terrain.info.district);

}
