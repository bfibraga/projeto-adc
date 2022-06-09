const base_uri = window.location.origin;

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

async function getInfo(debug){
	try{
		const response = await axios.get("/api/user/info/");
		const response_data = response.data[0];
		console.log(response_data);

		//Avatar
		//TODO Alter this avatar url
		let avatar_url = "https://storage.googleapis.com/projeto-adc.appspot.com/92b2843145c13d62106f68d0b11153.jpg";
		document.querySelectorAll('.avatar-wrapper')
			.forEach(function(image) {
				// Now do something with my button
				image.src = avatar_url;
			});

		//User Visible Data
		document.getElementById("usr_username").innerHTML = String(response_data.username);
		document.getElementById("usr_email").innerHTML = String(response_data.email);

		let name_parts = response_data.name.split(" ");
		document.getElementById("usr_firstname").innerHTML = String(name_parts[0]);
		document.getElementById("usr_lastname").innerHTML = String(name_parts[name_parts.length-1]);

		document.getElementById("usr_id").innerHTML = String(response_data.nif);
		document.getElementById("usr_phone").innerHTML = String(response_data.telephone);
		document.getElementById("usr_address").innerHTML = String(response_data.address);

	} catch (error){
		console.log(error);
		if (!debug){
			window.location.replace(base_uri);
		}
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

async function changing_att(){

	//TODO Remake this
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
	
	//TODO Remake this function

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

