let xhttp = new XMLHttpRequest();
let parser = new DOMParser();
let elems = {};

function createElement(type, class_list, parent){
    
    let elem = document.createElement(type);
    elem.innerText = "";
    if (parent !== null){
        parent.append(elem);
    }
    return elem;
}

function create(txt){
    let parser = new DOMParser();
    let elem = parser.parseFromString(txt, "text/html");
}

function parseHTML(html) {
    var t = document.createElement('template');
    t.innerHTML = html;
    return t.content;
}

// Auxiliary function that sends an XMLHTTPREQUEST to load the contents of an external resource
// This function works across different browsers (namely, it should work with IE)
function LoadXMLDoc(dname, callback){
  if (window.XMLHttpRequest) {
      xhttp = new XMLHttpRequest();
  }
  else {
      xhttp = new ActiveXObject("Microsoft.XMLHTTP");
  }
  xhttp.overrideMimeType('text/xml');
  xhttp.onreadystatechange = function() {
      if(this.readyState == 4 && this.status == 200) {
          callback(xhttp.responseXML);
      }
  }
  xhttp.open("GET", dname, true);
  xhttp.send();
}

async function LoadHTMLDoc(dname, callback, params){
    try{
        const response = await axios.get(dname);
        callback(dname, response.data, params);
    } catch(error){
    }
  }

function addEvent(el, type, handler) {
    
    el.attachEvent ?
      el.attachEvent('on' + type, handler) :
      el.addEventListener(type, handler);
    console.log("Event created")
  }

//------

function notification(sender, avatar, content){
    const elemName = "elems/notification.html";
    let params = [sender, avatar, content];
    
    LoadHTMLDoc(elemName, handleNotification, params);
}

function handleNotification(name, xmlDoc, params){
    elems[name] = parser.parseFromString(xmlDoc, "text/html");

    //Insertion of params
    elems[name].querySelector(".me-auto").insertAdjacentHTML("beforeend",params[0]);
    elems[name].querySelector(".toast-body").insertAdjacentHTML("beforeend",params[2]);

    document.getElementById("usr_list_notification").insertAdjacentHTML("beforeend", elems[name].body.innerHTML);
}

function badge(name, color){
    const elemName = "elems/badge.html"; 
    let params = [name, color];

    LoadHTMLDoc(elemName, handleBadge, params);
}

function handleBadge(name, xmlDoc, params){
    elems[name] = parser.parseFromString(xmlDoc, "text/html");

    //Insertion of params
    let badge = elems[name].querySelector("span.badge");
    badge.insertAdjacentHTML("beforeend", params[0]);
    badge.style.backgroundColor = params[1];

    document.getElementById("usr_roles_menu").insertAdjacentHTML("beforeend", elems[name].body.innerHTML);
    document.getElementById("usr_roles_change_profile").insertAdjacentHTML("beforeend", elems[name].body.innerHTML);
    document.getElementById("usr_roles_change_password").insertAdjacentHTML("beforeend", elems[name].body.innerHTML);
}

//TODO Implement more params
function terrainCard(id, title, status, description, banner){
    const elemName = "elems/terrain_card.html"; 
    let params = [id, title, status, description, banner];

    LoadHTMLDoc(elemName, handleTerrainCard, params);
}

function handleTerrainCard(name, xmlDoc, params){
    elems[name] = parser.parseFromString(xmlDoc, "text/html");

    let target = document.getElementById("usr_terrain_count");
    let count = target.getAttribute("data-app-value");
    target.setAttribute("data-app-value", String(parseInt(count)+1));

    elems[name].querySelector(".col").setAttribute("data-app-terrain-id", params[0]);

    addEvent(target, 'click', function (event) {
        console.log(event.target);
        let parent = event.target.parentElement;
        while (parent.getAttribute("data-app-terrain-id") === null){
            parent = parent.parentElement;
        }
        console.log(parent);
        console.log(parent.getAttribute("data-app-terrain-id"));
        if (parent.getAttribute("data-app-terrain-id") === params[0]){
            console.log('Button Clicked');
            loadTerrainInfo(params[0]);
        }
      });

    elems[name].querySelector(".card__title").insertAdjacentHTML("beforeend", params[1]);
    elems[name].querySelector(".card__status").insertAdjacentHTML("beforeend", params[2]);
    elems[name].querySelector(".card__description").insertAdjacentHTML("beforeend", params[3]);
    elems[name].querySelector(".card__image").src = params[4];

    target.insertAdjacentHTML("beforeend", elems[name].body.innerHTML);
    
}

function carouselTerrainImage(image, active){
    const elemName = "elems/terrainCarouselImage.html"; 
    let params = [image, active];

    LoadHTMLDoc(elemName, handlecarouselTerrainImage, params);
}

function handlecarouselTerrainImage(name, xmlDoc, params){
    elems[name] = parser.parseFromString(xmlDoc, "text/html");

    const target = document.getElementById("terrain_images");
    const image_uri = params[0];
    const active = params[1];
    console.log(active);
    const image_carousel =  elems[name].querySelector(".carousel-image");
    image_carousel.src = image_uri;
        if (active){
            elems[name].querySelector(".carousel-item").classList.add("active");
        }
        target.insertAdjacentHTML("beforeend", elems[name].body.innerHTML);
}

function terrainPendingCard(title, status, description){
    const elemName = "elems/terrain_pending_card.html"; 
    let params = [title, status, description];

    LoadHTMLDoc(elemName, handleTerrainPendingCard, params);
}

function handleTerrainPendingCard(name, xmlDoc, params){
    elems[name] = parser.parseFromString(xmlDoc, "text/html");

    elems[name].querySelector(".card-title").insertAdjacentHTML("beforeend", params[0]);
    //elems[name].querySelector(".card__status").insertAdjacentHTML("beforeend", params[1]);

    elems[name].querySelector(".card-text").insertAdjacentHTML("beforeend", params[2]);

    let target = document.getElementById("usr_pending_terrain_scrollpsy");
    let count = target.getAttribute("data-app-value");
    target.setAttribute("data-app-value", String(parseInt(count)+1));

    document.getElementById("usr_pending_terrain_list").insertAdjacentHTML("beforeend", elems[name].body.innerHTML);
    
}

function terrainOnPending(id, element){
    const elemName = "elems/terrain_on_pending_card.html"; 
    let params = [id, element];

    LoadHTMLDoc(elemName, handleTerrainOnPendingCard, params);
}

function handleTerrainOnPendingCard(name, xmlDoc, params){
    elems[name] = parser.parseFromString(xmlDoc, "text/html");

    const terrain = params[1];
    let target = document.getElementById("pending_terrain_accept_list_scrollpsy");
    let count = target.getAttribute("data-app-value");
    target.setAttribute("data-app-value", String(parseInt(count)+1));

    elems[name].querySelector(".col-12").setAttribute("data-app-terrain-id", params[0]);

    elems[name].querySelector(".btn-success").setAttribute("data-user", terrain.credentials.userID);
    elems[name].querySelector(".btn-success").setAttribute("data-terrain", terrain.credentials.name);

    elems[name].querySelector(".btn-danger").setAttribute("data-terrain", terrain.credentials.name);

    addEvent(target, 'click', function (event) {
        //console.log(event.target);
        let parent = event.target.parentElement;
        while (parent.getAttribute("data-app-terrain-id") === null){
            parent = parent.parentElement;
        }
        console.log(event.target);

        //console.log(parent);
        //console.log(parent.getAttribute("data-app-terrain-id"));
        if (parent.getAttribute("data-app-terrain-id") === params[0]){
            console.log('Button Clicked');

            if (event.target.classList.contains("terrain-on-pending")){
                loadTerrainOnPendingInfo(params[0]);
            }
        }

        if (event.target.classList.contains("btn-success")){
            console.log("clicked on button success")
            approveTerrain(event.target.getAttribute("data-user"), event.target.getAttribute("data-terrain"))
        }

        if (event.target.classList.contains("btn-danger")){
            console.log("clicked on button danger");
            denyTerrain(event.target.getAttribute("data-user"), event.target.getAttribute("data-terrain"))
        }
      });

    const status = String(terrain.credentials.townhall) + " / " + String(terrain.credentials.district);
    elems[name].querySelector(".card-title").insertAdjacentHTML("beforeend", terrain.credentials.name);
    elems[name].querySelector(".card-text").insertAdjacentHTML("beforeend", status);

    target.insertAdjacentHTML("beforeend", elems[name].body.innerHTML);
}

function communityMember(username, email, avatar){
    const elemName = "elems/community-member.html"; 
    let params = [username, email, avatar, "list_community_members"];

    LoadHTMLDoc(elemName, handleCommunityMember, params);
}

function communityResponsible(username, email, avatar){
    const elemName = "elems/community-member.html"; 
    let params = [username, email, avatar, "community-responsible"];

    LoadHTMLDoc(elemName, handleCommunityMember, params);
}

function handleCommunityMember(name, xmlDoc, params){
    elems[name] = parser.parseFromString(xmlDoc, "text/html");

    const profile = params[0];

    elems[name].querySelector(".usr_username").insertAdjacentHTML("beforeend", profile.username);
    elems[name].querySelector(".usr_email").insertAdjacentHTML("beforeend", profile.email);
    elems[name].querySelector(".profile-img").src = profile.avatar;

    document.getElementById(params[3]).insertAdjacentHTML("beforeend", elems[name].body.innerHTML);
}

function listUserProfile(profile, user){
    const elemName = "elems/user_profile.html"; 
    let params = [profile, user, "list_search_users"];

    LoadHTMLDoc(elemName, handleListUser, params);
}

function handleListUser(name, xmlDoc, params){
    elems[name] = parser.parseFromString(xmlDoc, "text/html");

    const target = document.getElementById(params[2]);

    const profile = params[0];
    const user = params[1];
    
    const key = parseInt(target.getAttribute("data-app-value"));

    elems[name].querySelector(".toggle-info").setAttribute("data-user", key);
    elems[name].querySelector(".user-info").setAttribute("data-user", key);

    addEvent(target, 'click', function (event) {
        let parent = event.target;
        while (!parent.classList.contains("list-group-item")){
            parent = parent.parentElement;
        }

        parent.setAttribute("data-user", profile.username);

        if (event.target.classList.contains("toggle-info") ||
        event.target.classList.contains("bi-arrows-expand")){
            const array = parent.querySelectorAll(".user-info");
            array.forEach(element => {
                if (element.getAttribute("data-user") === String(key)){
                    toggleMenu(element);
                }
            });
        }

        if (event.target.classList.contains("promote-user")){
            const role_elem = parent.querySelector(".usr_role");
            const influence_elem = parent.querySelector(".promote-user-influence");
            menuPromotionConfirm(profile.username, role_elem.value, influence_elem.value);
        }

      });

    target.setAttribute("data-app-value", String(key+1));

    elems[name].querySelector(".user-avatar-profile").src = profile.info.avatar;

    //elems[name].querySelector(".user-avatar-profile").href += key;
    elems[name].querySelector(".usr_username").insertAdjacentHTML("beforeend", profile.username);
    elems[name].querySelector(".usr_username").href += key;
    console.log(elems[name].querySelector(".usr_username").href );
    elems[name].querySelector(".usr_email").insertAdjacentHTML("beforeend", profile.email);
    elems[name].querySelector(".usr_email").href += key;

    elems[name].getElementById("collapseUserPerfil").id += key;

    elems[name].querySelector(".usr_fullname").insertAdjacentHTML("beforeend", profile.info.name);
    elems[name].querySelector(".usr_id").insertAdjacentHTML("beforeend", profile.info.nif);
    elems[name].querySelector(".usr_telephone").insertAdjacentHTML("beforeend", profile.info.telephone);
    elems[name].querySelector(".usr_smartphone").insertAdjacentHTML("beforeend", profile.info.smartphone);
    elems[name].querySelector(".usr_address").insertAdjacentHTML("beforeend", profile.info.address);

    elems[name].querySelector(".usr_active").insertAdjacentHTML("beforeend", profile.state);
    console.log(profile.state === "ACTIVE");
    elems[name].querySelector(".usr_active_radio").checked = profile.state === "ACTIVE";
    console.log(elems[name].querySelector(".usr_active_radio").checked);

    console.log(user.loggedinData.menus.includes("menu04"));
    if (user.loggedinData.menus.includes("menu04")){
        elems[name].querySelector(".usr_active_radio").setAttribute("data-app-menu-active", "true");
    }

    if (user.loggedinData.menus.includes("menu05")){
        elems[name].querySelector(".usr_role_unchanged").setAttribute("data-app-menu-active", "false");
        elems[name].querySelector(".usr_role").setAttribute("data-app-menu-active", "true");
        elems[name].querySelector(".promote-user-influence").setAttribute("data-app-menu-active", "true");
        elems[name].querySelector(".promote-user").setAttribute("data-app-menu-active", "true");
    } else {
        elems[name].querySelector(".usr_role_unchanged").setAttribute("data-app-menu-active", "true");
        elems[name].querySelector(".usr_role").setAttribute("data-app-menu-active", "false");
        elems[name].querySelector(".promote-user-influence").setAttribute("data-app-menu-active", "false");
        elems[name].querySelector(".promote-user").setAttribute("data-app-menu-active", "false");
    }

    elems[name].querySelector(".usr_role_unchanged").insertAdjacentHTML("beforeend", profile.role_name);

    const available_roles = listInferiorRoles(user.role_name);
    const usr_role_elem = elems[name].querySelector(".usr_role");

    console.log(available_roles);
    available_roles.forEach(role => {
        usr_role_elem.insertAdjacentHTML("beforeend", "<option>" + role + "</option>")
    });

    

    //elems[name].querySelector(".usr_n_terrains").insertAdjacentHTML("beforeend", 0);

    //let target = ;
    target.insertAdjacentHTML("beforeend", elems[name].body.innerHTML);
}

function menuPromotionConfirm(username, role, influence){
    const elemName = "elems/confirmPromotion.html"; 
    let params = [username, role, influence];

    LoadHTMLDoc(elemName, handlePromotionConfirm, params);
}

function handlePromotionConfirm(name, xmlDoc, params){
    elems[name] = parser.parseFromString(xmlDoc, "text/html");

    const username = params[0];
    const role = params[1];
    const influence = params[2];

    elems[name].querySelector(".overlay").setAttribute("data-user", username);
    elems[name].querySelector(".overlay").setAttribute("data-role", role);
    elems[name].querySelector(".overlay").setAttribute("data-influence", influence);

    elems[name].querySelector(".confirm-promotion-username").innerHTML = username;
    elems[name].querySelector(".confirm-promotion-role").innerHTML = role;

    document.getElementById("confirmPromotion").insertAdjacentHTML("beforeend", elems[name].body.innerHTML);
}

const role_mapper={
    "Administrador":"admin",
    "Funcionario Concelho":"func-cons",
    "Funcionario Distrito":"func-dist",
    "Funcionario Governo":"gov",
    "Utilizador":"end-user",
}

function handlePromote(){
    const target = document.getElementById("confirmPromotion").querySelector(".overlay");

    const username = target.getAttribute("data-user");
    const role = role_mapper[target.getAttribute("data-role")];
    const influence = target.getAttribute("data-influence");

    console.log("Promote");
    console.log(username);
    console.log(role);
    console.log(influence);

    promote(username, role, influence);
}

function listInferiorRoles(role_name){
    let result = [];
    const parts = role_name.split(" ");
    switch (parts[0]){
        case "Administrador":
            result.push("Administrador");
        case "Funcionario":
            //TODO Make for townhall and district
            switch(parts[1]) {
                case "Governo":
                    result.push("Funcionario Governo");
                case "Distrito":
                    result.push("Funcionario Distrito");
                case "Concelho":
                    result.push("Funcionario Concelho");
            }
        case "Utilizador":
            result.push("Utilizador");
    }
    return result;
}

const menuHandler = {
    "menu03": handleMenu03,
    "menu02": handleMenu02,
    "menu01": handleMenu01
}

function menu(menu_id){
    const elemName = "elems/menus/" + menu_id + ".html"; 
    let params = [];

    LoadHTMLDoc(elemName, menuHandler[menu_id], params);
}

function handleMenu03(name, xmlDoc, params){
    elems[name] = parser.parseFromString(xmlDoc, "text/html");

    document.getElementById("usr_terrain_menu").insertAdjacentHTML("beforeend", elems[name].body.innerHTML);
}

function handleMenu02(name, xmlDoc, params){
    elems[name] = parser.parseFromString(xmlDoc, "text/html");

    document.getElementById("community-app-nav-item").insertAdjacentHTML("afterend", elems[name].body.innerHTML);
}

function handleMenu01(name, xmlDoc, params){

}

