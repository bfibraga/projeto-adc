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
		let u_name = String(document.getElementById("usr_fullname").value);
		let u_password = String(document.getElementById("usr_password").value);
		let u_confirm = String(document.getElementById("usr_confirm").value);

		let u_visibility = "PUBLIC";
		let u_telephone = checkUndefined(String(document.getElementById("usr_telephone").value));
		let u_smartphone = "";
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

		const avatar_image = getAvatarImageContent();

		let form = new FormData();
		form.append("file", avatar_image["content"]);
		form.append("destination", u_username + "/" + u_email);
		form.append("filename", "avatar");

		const avatar_response = await axios.post("/files/projeto-adc.appspot.com/", form, 
		{
			headers: {
			"Content-Type": "multipart/form-data",
			}
		});
		console.log(avatar_response);

		//window.location.replace(base_uri + "/verification");
	} catch (error){

		const error_elems = document.getElementsByClassName("error-msg");

		for (let i = 0; i < error_elems.length; i++) {
			const element = error_elems.item(i);
			element.insertAdjacentHTML("beforeend", error.response.data.message);
		}

		console.log(error);
	
	} finally {
	}
}

async function activate(){

}

async function login(){
	try{
		let u_identifier = String(document.getElementById("usr_identifier").value);
		let u_password = checkUndefined(String(document.getElementById("usr_password").value));

		const response = await axios.post("/api/user/login/" + u_identifier + "?password=" + u_password);

		const response_data = response.data;

		window.location.replace(base_uri.concat("/app"));
	} catch (error){
		const status = error.response.status;
		if (status === 409){
			window.location.replace(base_uri.concat("/verification"));
		}

		if (status === 403){
			document.getElementById("validation_error").innerHTML = "Palavra-passe ou Utilizador errado";
		}
	} finally {
		loader("false");
	}
}

async function logout() {
	try{
		const viewport = getViewport();
		const response = await axios.post("/api/user/logout",
			{
				"center": viewport.center,
				"zoom": viewport.zoom
			});
		window.location.replace(base_uri);
	} catch (error){
		console.log(error);
	} finally {

	}
}

async function getInfo(debug, user){

	let loader = document.getElementById("loader");

	try{
		/*menu("menu03");
		terrainCard(1, "Teste 2", "Teste 3", "Teste 4");
		terrainCard(2, "Teste 2", "Teste 3", "Teste 4");
		terrainCard(3, "Teste 2", "Teste 3", "Teste 4");
		terrainPendingCard("Teste 2", "Teste 3", "Uma descrição de teste muito fixe meu :O");
		terrainPendingCard("Teste 3", "Teste 3", "Uma descrição de teste muito fixe meu :O");*/


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

		const lastLogout = response_data.logoutData;
		setCenter(lastLogout.center);
		setZoom(lastLogout.zoom);

		//Avatar
		//TODO Alter this avatar url
		let avatar_url = "https://storage.cloud.google.com/projeto-adc.appspot.com/placeholder/user.png?authuser=2";
		/*document.querySelectorAll('.avatar-wrapper')
			.forEach(function(elem) {
				const value = elem.getAttribute("data-user");
				if (value.value === user){
					elem.firstElementChild.src = avatar_url;
					console.log(elem.firstElementChild.src);
				}
		});*/
		
		loadMenus(response_data.loggedinData.menus);

		await Promise.all([
			getOwnTerrain(),
			getPendingTerrain(),
			listNotification(),
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

async function get(){
	let spinner = document.getElementById("promote_spinner");
	spinner.setAttribute("data-app-menu-active", "true");

	try{
		const value = String(document.getElementById("search_list_user_input").value);
		const response = await axios.get("/api/user/info?identifier="+value);
		const response_data = response.data;
		console.log(response_data);
		document.getElementById("list_search_users").replaceChildren();

		response_data.forEach(elem => {
			listUserProfile(elem);
		});
	} catch (error){
		console.log(error);
		
	} finally{
		spinner.setAttribute("data-app-menu-active", "false");
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

function loadMenus(menus){
	if (menus !== null && menus.length > 0){
		menus.forEach(element => {
			menu(element);
		});
	} 
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
		toggleChangeProfileMenu('usr_profile_menu', 'usr_change_profile_menu', 'btn_change_usr_profile_not_active', 'btn_change_usr_profile_active');

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

		const terrain_counter_elem = document.getElementsByClassName("nmr_terrain");

		if (terrain_list != null && terrain_list.length > 0){
			for (let i = 0; i < terrain_list.length; i++) {
				const element = terrain_list[i];
				console.log(element);
				//const id = element.credentials.id;
				//terrain_list[i] = element;

				const status = String(element.credentials.townhall) + " " + String(element.credentials.district);
				terrainCard(String(i), element.credentials.name, status, element.info.description);
			}
			for (let i = 0; i < terrain_counter_elem.length; i++) {
				const element = terrain_counter_elem.item(i);
				let counter = parseInt(element.innerHTML);
				counter += terrain_list.length;
				element.innerHTML = counter;
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
		if (data !== null || data.length > 0) {
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

		//Get from server
		let response = await axios.get("/api/parcel/list/chunk",{
			params: pos
		});

		const response_data = response.data;

		//Verify if the content exist in the browser
		const chunk = response_data.chunk;
		if (hasChunk(chunk)){
			return;
		}

		const array = response_data.data;
		saveChunk(chunk, true);
		array.forEach(element => {
			addPolygon(element.points, element.color);
			addMarker(element.center);
		});
		//addCluster();

	} catch (error){
		console.log(error);
	} finally {
	}
}

function loadTerrainInfo(id){
	const terrain = terrain_list[parseInt(id)];
	console.log(terrain);
	if (terrain === null) return;

	const center = terrain.center;
	const viewport = getViewport();
	console.log(viewport);
	const new_center = point(center.lat, center.lng - 5e-4);
	setCenter(new_center);
	//setZoom(10);

	document.getElementById("terrain_name").innerHTML = terrain.credentials.name;
	document.getElementById("terrain_townhall").innerHTML = terrain.credentials.townhall;
	document.getElementById("terrain_district").innerHTML = terrain.credentials.district;
	document.getElementById("terrain_number_section").innerHTML = terrain.credentials.section;
	document.getElementById("terrain_number_article").innerHTML = terrain.credentials.number_article;
	//document.getElementById("terrain_documents_validation").insertAdjacentHTML("beforeend", terrain.info.district);

	document.getElementById("terrain_description").innerHTML = terrain.info.description;
	document.getElementById("terrain_type").innerHTML = terrain.info.type_of_soil_coverage;
	document.getElementById("terrain_current_use").innerHTML = terrain.info.current_use;
	document.getElementById("terrain_previous_use").innerHTML = terrain.info.previous_use;

	document.getElementById("owner_fullname").innerHTML = terrain.owner.name;
	document.getElementById("owner_id").innerHTML = terrain.owner.nif;
	document.getElementById("owner_telephone").innerHTML = terrain.owner.telephone;
	document.getElementById("owner_smartphone").innerHTML = terrain.owner.smartphone;
	document.getElementById("owner_address").innerHTML = terrain.owner.address;

}


async function getRCM(day, dico, target_id){
	try{
		day = (Math.max(Math.min(day,1),0));
		const response = await axios.get("https://api.ipma.pt/open-data/forecast/meteorology/rcm/rcm-d" + day + ".json");
		const response_data = response.data;

		document.getElementById(target_id).innerHTML = response_data.local[dico]["data"]["rcm"]
	} catch (error) {
		console.log(error);
	}
}

async function getBroadcast(day, target_id){
	try{
		day = (Math.max(Math.min(day,2),0));
 		const response = await axios.get("https://api.ipma.pt/open-data/forecast/meteorology/cities/daily/hp-daily-forecast-day" + day + ".json");
		const response_data = response.data;

		console.log(response_data)
		//document.getElementById(target_id).innerHTML = response_data.local["0101"]["data"]["rcm"]
	} catch (error){
		console.log(error);
	} 
}