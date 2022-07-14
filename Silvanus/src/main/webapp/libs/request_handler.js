const base_uri = window.location.origin;

let perfil =     {
	"username": "",
	"email": "",
	"info": {
		"name": "",
		"visibility": "PUBLIC",
		"nif": "",
		"address": "",
		"telephone": "",
		"smartphone": "",
		"avatar": "https://storage.googleapis.com/projeto-adc.appspot.com/placeholder/avatar"
	},
	"state": "ACTIVE",
	"role_name": "",
	"role_color": "#000000",
	"logoutData": {
		"center": {
			"lat": 38.659782,
			"lng": -9.202765
		},
		"zoom": 15.0
	},
	"loggedinData": {
		"time": "",
		"menus": [
		]
	}
};
let terrain_list = [];
let terrain_on_pending = [];

const resource = {
	"USER": "user",
	"TERRAIN": "parcel"
}

function checkUndefined(keyword) {
	return keyword.trim() === "" ? "UNDEFINED" : keyword;
}

async function register(){
	let loader = document.getElementById("register_spinner");
	loader.setAttribute("data-app-menu-active", "true");

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
					"smartphone": u_smartphone,
					"avatar": "https://storage.googleapis.com/projeto-adc.appspot.com/" + u_username + "/avatar"
				}
			});

		const avatar_image = getAvatarImageContent();

		if (avatar_image["content"] === ""){
			
		}

		let form = new FormData();
		form.append("file", avatar_image["content"]);
		form.append("destination", u_username);
		form.append("filename", "avatar");

		const avatar_response = await axios.post("/files/projeto-adc.appspot.com/", form, 
		{
			headers: {
			"Content-Type": "multipart/form-data",
			}
		});
		console.log(avatar_response);

		window.location.replace(base_uri + "/app");

	} catch (error) {

		const error_elems = document.getElementsByClassName("error-msg");

		for (let i = 0; i < error_elems.length; i++) {
			const element = error_elems.item(i);
			element.insertAdjacentHTML("beforeend", error.response.data.message);
		}

		console.log(error);
	
	} finally {
		//loader("false");
		loader.setAttribute("data-app-menu-active", "false");
	}
}

async function activate(){

}

async function login(){
	loader("true");
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
		terrainCard(1, "Teste 2", "Teste 3", "Teste 4", "https://storage.googleapis.com/projeto-adc.appspot.com/test/hello/there/not_avatar");
		terrainCard(2, "Teste 2", "Teste 3", "Teste 4", "https://storage.googleapis.com/projeto-adc.appspot.com/test/hello/there/not_avatar");
		terrainCard(3, "Teste 2", "Teste 3", "Teste 4", "https://storage.googleapis.com/projeto-adc.appspot.com/test/hello/there/not_avatar");
		terrainPendingCard("Teste 2", "Teste 3", "Uma descrição de teste muito fixe meu :O");
		terrainPendingCard("Teste 3", "Teste 3", "Uma descrição de teste muito fixe meu :O");
		terrainOnPending(1, 
			{
				"points": [
					{
						"lat": 40.427975,
						"lng": -7.9812384
					},
					{
						"lat": 40.42858,
						"lng": -7.9824452
					},
					{
						"lat": 40.42755,
						"lng": -7.9832716
					}
				],
				"center": {
					"lat": 38.659782,
					"lng": -9.202765
				},
				"credentials": {
					"userID": "mekie",
					"name": "tereeren gd isqbdcibcqobqwob",
					"townhall": "Carregal do Sal",
					"district": "Viseu",
					"section": "42",
					"number_article": "42",
					"id": "0:-2113247677"
				},
				"owner": {
					"name": "Bruno Braga",
					"nif": "1234",
					"address": "Rua de Braga",
					"telephone": "212 212 212",
					"smartphone": "932290047"
				},
				"info": {
					"description": "cdcsdcdcsdcsd",
					"type_of_soil_coverage": "fértil",
					"current_use": "65",
					"previous_use": "2",
					"images": [],
					"route": [
						{
							"lat": 40.428265,
							"lng": -7.9813724
						},
						{
							"lat": 40.427975,
							"lng": -7.9816995
						},
						{
							"lat": 40.428547,
							"lng": -7.9817104
						},
						{
							"lat": 40.428265,
							"lng": -7.9813724
						}
					],
					"documents": null
				},
				"color": "#124a41"
			});
			terrainOnPending(2, 
				{
					"points": [
						{
							"lat": 40.427975,
							"lng": -7.9812384
						},
						{
							"lat": 40.42858,
							"lng": -7.9824452
						},
						{
							"lat": 40.42755,
							"lng": -7.9832716
						}
					],
					"center": {
						"lat": 38.659782,
						"lng": -9.202765
					},
					"credentials": {
						"userID": "nani",
						"name": "tereeren gd isqbdcibcqobqwob",
						"townhall": "Carregal do Sal",
						"district": "Viseu",
						"section": "42",
						"number_article": "42",
						"id": "0:-2113247677"
					},
					"owner": {
						"name": "Bruno Braga",
						"nif": "1234",
						"address": "Rua de Braga",
						"telephone": "212 212 212",
						"smartphone": "932290047"
					},
					"info": {
						"description": "cdcsdcdcsdcsd",
						"type_of_soil_coverage": "fértil",
						"current_use": "65",
						"previous_use": "2",
						"images": [],
						"route": [
							{
								"lat": 40.428265,
								"lng": -7.9813724
							},
							{
								"lat": 40.427975,
								"lng": -7.9816995
							},
							{
								"lat": 40.428547,
								"lng": -7.9817104
							},
							{
								"lat": 40.428265,
								"lng": -7.9813724
							}
						],
						"documents": null
					},
					"color": "#124a41"
				});*/
	

		//document.getElementById("usr_avatar_perfil_credentials").src = "https://storage.googleapis.com/projeto-adc.appspot.com/test/hello/there/not_avatar";

		/*const avatar_elems = document.getElementsByClassName("user-avatar");
		for (let i = 0; i < avatar_elems.length; i++) {
			const element = avatar_elems.item(i);
			element.src = "https://storage.googleapis.com/projeto-adc.appspot.com/test/hello/there/not_avatar";
		}*/
		/*listUserProfile({
			"username": "bfibraga",
			"email": "brunobfi2000@gmail.com",
			"info": {
				"name": "Bruno Braga",
				"visibility": "PUBLIC",
				"nif": "1234",
				"address": "Rua de Braga",
				"telephone": "212 212 212",
				"smartphone": "932290047",
				"avatar": "https://storage.googleapis.com/projeto-adc.appspot.com/android/avatar"
			},
			"state": "ACTIVE",
			"role_name": "Administrador",
			"role_color": "#6aa84f",
			"logoutData": {
				"center": {
					"lat": 38.656372,
					"lng": -9.196873
				},
				"zoom": 14
			},
			"loggedinData": {
				"time": "2022-07-10 23:13:10",
				"menus": []
			}
		},
		{
			"username": "bfibraga",
			"email": "brunobfi2000@gmail.com",
			"info": {
				"name": "Bruno Braga",
				"visibility": "PUBLIC",
				"nif": "1234",
				"address": "Rua de Braga",
				"telephone": "212 212 212",
				"smartphone": "932290047",
				"avatar": "https://storage.googleapis.com/projeto-adc.appspot.com/android/avatar"
			},
			"state": "ACTIVE",
			"role_name": "Administrador",
			"role_color": "#6aa84f",
			"logoutData": {
				"center": {
					"lat": 38.656372,
					"lng": -9.196873
				},
				"zoom": 14
			},
			"loggedinData": {
				"time": "2022-07-10 23:13:10",
				"menus": [
					"menu05"
				]
			}
		}
		);

		listUserProfile({
			"username": "bfibraga",
			"email": "brunobfi2000@gmail.com",
			"info": {
				"name": "Bruno Braga",
				"visibility": "PUBLIC",
				"nif": "1234",
				"address": "Rua de Braga",
				"telephone": "212 212 212",
				"smartphone": "932290047",
				"avatar": "https://storage.googleapis.com/projeto-adc.appspot.com/android/avatar"
			},
			"state": "ACTIVE",
			"role_name": "Administrador",
			"role_color": "#6aa84f",
			"logoutData": {
				"center": {
					"lat": 38.656372,
					"lng": -9.196873
				},
				"zoom": 14
			},
			"loggedinData": {
				"time": "2022-07-10 23:13:10",
				"menus": []
			}
		},
		{
			"username": "bfibraga",
			"email": "brunobfi2000@gmail.com",
			"info": {
				"name": "Bruno Braga",
				"visibility": "PUBLIC",
				"nif": "1234",
				"address": "Rua de Braga",
				"telephone": "212 212 212",
				"smartphone": "932290047",
				"avatar": "https://storage.googleapis.com/projeto-adc.appspot.com/android/avatar"
			},
			"state": "ACTIVE",
			"role_name": "Funcionario Governo",
			"role_color": "#6aa84f",
			"logoutData": {
				"center": {
					"lat": 38.656372,
					"lng": -9.196873
				},
				"zoom": 14
			},
			"loggedinData": {
				"time": "2022-07-10 23:13:10",
				"menus": [
					"menu05"
				]
			}
		}
		);
		
		listUserProfile({
			"username": "bfibraga",
			"email": "brunobfi2000@gmail.com",
			"info": {
				"name": "Bruno Braga",
				"visibility": "PUBLIC",
				"nif": "1234",
				"address": "Rua de Braga",
				"telephone": "212 212 212",
				"smartphone": "932290047",
				"avatar": "https://storage.googleapis.com/projeto-adc.appspot.com/android/avatar"
			},
			"state": "ACTIVE",
			"role_name": "Administrador",
			"role_color": "#6aa84f",
			"logoutData": {
				"center": {
					"lat": 38.656372,
					"lng": -9.196873
				},
				"zoom": 14
			},
			"loggedinData": {
				"time": "2022-07-10 23:13:10",
				"menus": []
			}
		},
		{
			"username": "bfibraga",
			"email": "brunobfi2000@gmail.com",
			"info": {
				"name": "Bruno Braga",
				"visibility": "PUBLIC",
				"nif": "1234",
				"address": "Rua de Braga",
				"telephone": "212 212 212",
				"smartphone": "932290047",
				"avatar": "https://storage.googleapis.com/projeto-adc.appspot.com/android/avatar"
			},
			"state": "ACTIVE",
			"role_name": "Funcionario Distrito de Setubal",
			"role_color": "#6aa84f",
			"logoutData": {
				"center": {
					"lat": 38.656372,
					"lng": -9.196873
				},
				"zoom": 14
			},
			"loggedinData": {
				"time": "2022-07-10 23:13:10",
				"menus": [
					"menu05"
				]
			}
		}
		);
		listUserProfile({
			"username": "bfibraga",
			"email": "brunobfi2000@gmail.com",
			"info": {
				"name": "Bruno Braga",
				"visibility": "PUBLIC",
				"nif": "1234",
				"address": "Rua de Braga",
				"telephone": "212 212 212",
				"smartphone": "932290047",
				"avatar": "https://storage.googleapis.com/projeto-adc.appspot.com/android/avatar"
			},
			"state": "ACTIVE",
			"role_name": "Administrador",
			"role_color": "#6aa84f",
			"logoutData": {
				"center": {
					"lat": 38.656372,
					"lng": -9.196873
				},
				"zoom": 14
			},
			"loggedinData": {
				"time": "2022-07-10 23:13:10",
				"menus": []
			}
		},
		{
			"username": "bfibraga",
			"email": "brunobfi2000@gmail.com",
			"info": {
				"name": "Bruno Braga",
				"visibility": "PUBLIC",
				"nif": "1234",
				"address": "Rua de Braga",
				"telephone": "212 212 212",
				"smartphone": "932290047",
				"avatar": "https://storage.googleapis.com/projeto-adc.appspot.com/android/avatar"
			},
			"state": "ACTIVE",
			"role_name": "Funcionario Conselho de Almada",
			"role_color": "#6aa84f",
			"logoutData": {
				"center": {
					"lat": 38.656372,
					"lng": -9.196873
				},
				"zoom": 14
			},
			"loggedinData": {
				"time": "2022-07-10 23:13:10",
				"menus": [
					"menu05"
				]
			}
		}
		);*/

		const response = await axios.get("/api/user/info");
		const response_data = response.data[0];
		perfil = response_data;
		console.log(perfil);

		//Update User Profile
		document.getElementById("usr_username").innerHTML = String(response_data.username);
		document.getElementById("usr_email").innerHTML = String(response_data.email);

		const role_parts = response_data.role_name.split(" ");
		console.log(role_parts);
		const influence = String(role_parts[role_parts.length-1]);

		console.log(role_parts[role_parts.length-1]);
		if (role_parts[1] === "Concelho"){
			getPendingByCounty(influence);
		}

		if (role_parts[1] === "Distrito"){
			getPendingByDistrict(influence);
		}

		//Put all user badges
		badge(response_data.role_name,response_data.role_color);
		updatePerfil(response_data.info);

		const lastLogout = response_data.logoutData;
		setCenter(lastLogout.center);
		setZoom(lastLogout.zoom);
		
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
			listUserProfile(elem, perfil);
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

	const avatar_elems = document.getElementsByClassName("user-avatar");
	for (let i = 0; i < avatar_elems.length; i++) {
		const element = avatar_elems.item(i);
		element.src = String(data.avatar);
	}
	document.getElementById("usr_avatar_perfil_credentials").src = String(data.avatar);
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

		const avatar_image = getAvatarImageContent();

		console.log(avatar_image);
		let form = new FormData();
		form.append("file", avatar_image["content"]);
		form.append("destination", String(document.getElementById("usr_username").innerHTML));
		form.append("filename", "avatar");

		const avatar_response = await axios.post("/files/projeto-adc.appspot.com/", form, 
		{
			headers: {
			"Content-Type": "multipart/form-data",
			}
		});

		updatePerfil(perfil);
		loader('usr_change_profile_menu','false');
		toggleChangeProfileMenu('usr_profile_menu', 'usr_change_profile_menu', 'btn_change_usr_profile_not_active', 'btn_change_usr_profile_active');

	} catch (error){
		console.log(error);
	} finally {
		console.log("Executed successfully");
		
	}
}

async function promote(username, new_role, influence){
	const confirm_elem = document.getElementById('confirmPromotion');
	toggleMenu(confirm_elem.querySelector(".spinner-border"));
	toggleMenu(confirm_elem.querySelector(".confirm-promote"));

	try{
		const response = await axios.put("/api/user/promote/" + username + "?role=" + new_role + "&PlaceOfInfluence=" + influence);
		console.log(response);
	} catch (error){
		console.log(error);
	} finally {
		console.log("Executed successfully");
		toggleMenu(confirm_elem.querySelector(".spinner-border"));
		toggleMenu(confirm_elem.querySelector(".confirm-promote"));
		confirm_elem.innerHTML = '';
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

async function sendNotification(sender, receiver, description){
	try {
		const response = await axios.post("/api/notification/send",
		{
			"sender": sender,
			"receiver": receiver,
			"description": description
		});
	} catch (error){
		console.log(error);
	} finally {
		console.log("Executed successfully");
	}
}

//--- Terrain ----

async function submitTerrain(points_data, route_data) {

	document.getElementById("register_terrain_spinner").setAttribute("data-app-menu-active", "true");

	//TODO Remake this function
	let parcela = points_data;

	try{
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

		const username = String(document.getElementById("usr_username").innerHTML);
		const url = "terrains/" + credentials.district + "/" + credentials.townhall + "/" + username  + "/" +  credentials.name;

		const image_content = getImageFiles();
		const content = image_content["content"];
		let image_uris = [];

		for (let i = 0; i < content.length; i++) {
			const image = content[i];
			let form_image = new FormData();
			form_image.append("file", image);
				
			form_image.append("destination", url);
			const filename = "image-terrain-" + String(i);
			form_image.append("filename", filename);

			axios.post("/files/projeto-adc.appspot.com/", form_image, 
				{
					headers: {
					"Content-Type": "multipart/form-data",
					}
				});	
			
			image_uris.push("https://storage.googleapis.com/projeto-adc.appspot.com/" + url + "/" + filename);
		}

		let info = {
			"description": String(document.getElementById("description-terrain").value),
			"type_of_soil_coverage": String(document.getElementById("type-terrain").value),
			"current_use": String(document.getElementById("current-use-terrain").value),
			"previous_use": String(document.getElementById("previous-use-terrain").value),
			"images": image_uris,
			"route": route_data
		};

	
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

		const documents_data = getDocumentationFiles();

		const document_content = documents_data["content"];
		for (let i = 0; i < document_content.length; i++) {
			const file_content = document_content[i];
			
			let form = new FormData();
			form.append("file", file_content);
			form.append("destination", url);
			form.append("filename", "documentation" + String(i));

			axios.post("/files/projeto-adc.appspot.com/", form, 
			{
				headers: {
				"Content-Type": "multipart/form-data",
				}
			});
		}
		
		const status = String(credentials.townhall) + " " + String(credentials.district);
		terrainPendingCard(credentials.name, status, info.description);

		console.log("Executed successfully");

		//Disable drawing tools, hide offcanvas and clear fields
		/*var request_terrain_offcanvas = document.getElementById('request-terrain-offcanvas');
		var bsRequestTerrainOffcanvas = new bootstrap.Offcanvas(request_terrain_offcanvas);
		
		bsRequestTerrainOffcanvas.hide();*/

		document.getElementById("route_definition_menu").setAttribute("data-app-menu-active", "false");

		/* terrain_properties_offcanvas = document.getElementById('terrain-properties');
		var bsTerrainPropertiesOffcanvas = new bootstrap.Offcanvas(terrain_properties_offcanvas);
		
		bsTerrainPropertiesOffcanvas.show();*/

		clearRequestTerrainFields();
		togglePolygonDrawingControl(false);
		setPolygon(null, null);

		addPolygon(parcela, "#222222");
	} catch (error){
		//alert(error);
		console.log(error);
	} finally {
		document.getElementById("register_terrain_spinner").setAttribute("data-app-menu-active", "false");
	}
}

function clearRequestTerrainFields(){
	document.getElementById("name-terrain").value = "";
	document.getElementById("townhall-terrain").value = "";
	document.getElementById("district-terrain").value = "";
	document.getElementById("section-terrain").value = "";
	document.getElementById("number-article-terrain").value = "";

	document.getElementById("other_acc_fullname_input").value = "";
	document.getElementById("other_acc_id_input").value = "";
	document.getElementById("other_acc_address_input").value = "";
	document.getElementById("other_acc_telephone_input").value = "";
	document.getElementById("other_acc_smartphone_input").value = "";

	document.getElementById("description-terrain").value = "";
	document.getElementById("type-terrain").value = "";
	document.getElementById("current-use-terrain").value = "";
	document.getElementById("previous-use-terrain").value = "";
}

async function getOwnTerrain(){
	try{
		let response = await axios.post("/api/parcel/list");
		terrain_list = response.data;

		const terrain_counter_elem = document.getElementsByClassName("nmr_terrain");

		if (terrain_list != null && terrain_list.length > 0){
			for (let i = 0; i < terrain_list.length; i++) {
				const element = terrain_list[i];

				const status = String(element.credentials.townhall) + " " + String(element.credentials.district);
				terrainCard(String(i), element.credentials.name, status, element.info.description, element.info.images[0]);
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
		if (data !== null && data.length > 0) {
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

async function getPendingByCounty(county){
	try{
		let response = await axios.get("/api/parcel/list/pending/county/" + county);
		const data = response.data;
		terrain_on_pending = data;
		console.log(data);
		if (data !== null && data.length > 0) {
			for (let i = 0; i < data.length; i++) {
				const element = data[i];
				terrainOnPending(String(i), element);
			}
		}
		
		console.log(response);
	} catch (error){
		console.log(error);
	} finally {
		console.log("Executed successfully");
	}
}

async function getPendingByDistrict(district){
	try{
		let response = await axios.get("/api/parcel/list/pending/district/" + district);
		const data = response.data;
		terrain_on_pending = data;
		console.log(data);
		if (data !== null && data.length > 0) {
			for (let i = 0; i < data.length; i++) {
				const element = data[i];
				terrainOnPending(String(i), element);
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

async function loadChunk(pos){
	try{

		//Get from server
		let response = await axios.get("/api/parcel/list/chunk",{
			params: pos
		});

		const response_data = response.data;

		//Verify if the content exist in the browser
		const chunk = response_data.chunk;
		console.log(chunk);

		const array = response_data.data;
		console.log(array);

		saveChunk(chunk, true);
		array.forEach(element => {
			addPolygon(element.points, element.color);
			//addMarker(element.center);
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
	const ne = viewport.bounds.getNorthEast();
	const sw = viewport.bounds.getSouthWest();
	const new_center = point(center.lat, center.lng - ((ne.lng()-sw.lng())/4));
	setCenter(new_center);

	const images = terrain.info.images;

	document.getElementById("terrain_images").innerHTML = "";
	carouselTerrainImage(images[0], true);
	for (let i = 1; i < images.length; i++) {
		const image = images[i];
		carouselTerrainImage(image, false);
	}

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

function loadTerrainOnPendingInfo(id){
	const terrain = terrain_on_pending[parseInt(id)];
	console.log(terrain);
	if (terrain === null) return;

	const center = terrain.center;
	const viewport = getViewport();
	console.log(viewport);
	const ne = viewport.bounds.getNorthEast();
	const sw = viewport.bounds.getSouthWest();
	const new_center = point(center.lat, center.lng - ((ne.lng()-sw.lng())/4));
	setCenter(new_center);

	const images = terrain.info.images;

	document.getElementById("terrain_images").innerHTML = "";
	carouselTerrainImage(images[0], true);
	for (let i = 1; i < images.length; i++) {
		const image = images[i];
		carouselTerrainImage(image, false);
	}

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

async function approveTerrain(username, terrain_name){
	try{
		let response = await axios.put("/api/parcel/approve/" + username + "?terrain=" + terrain_name);
		const data = response.data;
		console.log(data);
		
		await Promise.all([
			sendNotification(perfil.username, username, "Terreno " + terrain_name + " foi aprovado por " + perfil.username)
		]);

	} catch (error){
		//alert(error);
		console.log(error);
	} finally {
		console.log("Executed successfully");
	}
}

async function denyTerrain(username, terrain_name){
	try{
		let response = await axios.put("/api/parcel/deny/" + username + "?terrain" + terrain_name);
		const data = response.data;
		console.log(data);
		
		await Promise.all([
			sendNotification(perfil.username, username, "Terreno " + terrain_name + " foi reprovado por " + perfil.username)
		]);

	} catch (error){
		//alert(error);
		console.log(error);
	} finally {
		console.log("Executed successfully");
	}
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