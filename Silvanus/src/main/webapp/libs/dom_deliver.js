let xhttp = new XMLHttpRequest();
let parser = new DOMParser();

function createElement(type, class_list, parent){
    
    let elem = document.createElement(type);
    elem.innerText = "";
    if (parent !== null){
        parent.append(elem);
    }
    console.log("Created")
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

function LoadHTMLDoc(dname, callback, timeout, params){
    if (window.XMLHttpRequest) {
        xhttp = new XMLHttpRequest();
    }
    else {
        xhttp = new ActiveXObject("Microsoft.XMLHTTP");
    }
    //xhttp.overrideMimeType('text/xml');
    xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function() {
        if(this.readyState === 4 && this.status === 200) {
            let response = xhttp.responseXML;
            callback(response, params);
        }
    }
    xhttp.open("GET", dname, true);
    xhttp.responseType = "document";
    xhttp.send(null);
  }

//------

function notification(sender, avatar, content){
  LoadHTMLDoc("elems/notification.html", handleNotification, [sender, avatar, content]);
}

function handleNotification(xmlDoc, params){
    var doc = xmlDoc.innerHTML;
    doc = parser.parseFromString(doc, "text/html");

    /*elem.querySelector("strong.me-auto").innerText = params[0];
    //elem.querySelector(".avatar-wrapper").setAttribute("data-user", params[1]);
    elem.querySelector("div.toast-body").innerText = params[2];*/
    document.getElementById("usr_list_notification").innerHTML += doc;

}

function badge(name, color){
    LoadHTMLDoc("elems/badge.html", handleBadge, [name, color]);
}

function handleBadge(xmlDoc, params){
    let elem = xmlDoc.querySelector(".badge");
    console.log(elem);
    elem.innerText = params[0];
    elem.style.backgroundColor = params[1];
    document.getElementById("usr_roles_menu").append(elem);
    document.getElementById("usr_roles_change_profile").append(elem);
    document.getElementById("usr_roles_change_password").append(elem);
    //return elem;
}

function clone(doc){
    
}

