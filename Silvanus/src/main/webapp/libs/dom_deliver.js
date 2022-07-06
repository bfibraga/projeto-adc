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
function terrainCard(id, title, status, description){
    const elemName = "elems/terrain_card.html"; 
    let params = [id, title, status, description];

    LoadHTMLDoc(elemName, handleTerrainCard, params);
}

function handleTerrainCard(name, xmlDoc, params){
    elems[name] = parser.parseFromString(xmlDoc, "text/html");

    let target = document.getElementById("usr_terrain_count");
    let count = target.getAttribute("data-app-value");
    target.setAttribute("data-app-value", String(parseInt(count)+1));

    elems[name].querySelector(".col").setAttribute("data-app-terrain-id", params[0]);

    //elems[name].querySelector(".card")

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

function listUserProfile(profile){
    const elemName = "elems/user-profile.html"; 
    let params = [profile, "list_users_promote"];

    LoadHTMLDoc(elemName, handlePromotionMember, params);
}

function handleCommunityMember(name, xmlDoc, params){
    elems[name] = parser.parseFromString(xmlDoc, "text/html");

    const profile = params[0];

    elems[name].querySelector(".usr_username").insertAdjacentHTML("beforeend", profile.username);
    elems[name].querySelector(".usr_email").insertAdjacentHTML("beforeend", profile.email);
    elems[name].querySelector(".profile-img").src = profile.avatar;

    document.getElementById(params[3]).insertAdjacentHTML("beforeend", elems[name].body.innerHTML);
}

function handlePromotionMember(name, xmlDoc, params){
    elems[name] = parser.parseFromString(xmlDoc, "text/html");

    elems[name].querySelector(".usr_username").insertAdjacentHTML("beforeend", params[0]);
    elems[name].querySelector(".usr_email").insertAdjacentHTML("beforeend", params[1]);
    elems[name].querySelector(".profile-img").src = params[2];

    //let target = ;
    document.getElementById(params[3]).insertAdjacentHTML("beforeend", elems[name].body.innerHTML);
}

