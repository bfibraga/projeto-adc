let xhttp;

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

function LoadHTMLDoc(dname, callback, params){
    if (window.XMLHttpRequest) {
        xhttp = new XMLHttpRequest();
    }
    else {
        xhttp = new ActiveXObject("Microsoft.XMLHTTP");
    }
    //xhttp.overrideMimeType('text/xml');
    xhttp.onreadystatechange = function() {
        if(this.readyState == 4 && this.status == 200) {
            callback(xhttp.responseXML, params);
        }
    }
    xhttp.open("GET", dname, true);
    xhttp.responseType = "document";
    xhttp.send();
  }

//------

function notification(sender, avatar, content){
  LoadHTMLDoc("elems/notification.html", handleNotification, [sender, avatar, content]);
}

function handleNotification(xmlDoc, params){
    let elem = xmlDoc.querySelector(".toast");
    elem.querySelector(".me-auto").innerText = params[0];
    elem.querySelector(".avatar-wrapper").setAttribute("data-user", params[1]);
    console.log(elem.querySelector(".avatar-wrapper"));
    elem.querySelector(".toast-body").innerText = params[2];
    document.getElementById("usr_list_notification").append(elem);
    return elem;
}

function badge(name, color){
    LoadHTMLDoc("elems/badge.html", handleBadge, [name, color]);
}

function handleBadge(xmlDoc, params){
    let elem = xmlDoc.querySelector(".badge");
    console.log(elem);
    elem.innerText = params[0];
    elem.style.backgroundColor = params[1];
    document.getElementById("usr_roles").append(elem);
    return elem;
}

