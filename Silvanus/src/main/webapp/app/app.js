let avatar_data = {
  "content": "",
  "type": ""
};

let documentation_data ={
  "content": "",
}

let image_data ={
  "content": "",
}

function loadAvatar(event){
  const avatar = document.getElementById("usr_avatar_perfil_credentials");
  const src = URL.createObjectURL(event.target.files[0]);
  avatar.src = src
  
  avatar_data["content"] = event.target.files[0];

  avatar_data["type"] = event.target.files[0].type;
}

function getAvatarImageContent(){
  return avatar_data;
}

function loadDocumentationFiles(event){
  documentation_data["content"] = event.target.files;
  console.log(documentation_data);
}

function getDocumentationFiles(){
  return documentation_data;
}

function loadImageFiles(event){
  image_data["content"] = event.target.files;
  console.log(image_data);
}

function getImageFiles(){
  return image_data;
}

function toggleChangeProfileMenu(id1, id2, iconId1, iconId2){
    let elem1 = document.getElementById(id1);
    let elem2 = document.getElementById(id2);

    let icon1 = document.getElementById(iconId1);
    let icon2 = document.getElementById(iconId2);

    toggleMenu(icon1);
    toggleMenu(icon2);

    toggleMenu(elem1);
    toggleMenu(elem2);
}

function toggleExpandOffcanvas(id1){
    let elem1 = document.getElementById(id1);
    toggleExpand(elem1);
}

function setMenuID(id, value){
    let elem1 = document.getElementById(id);
    setMenu(elem1, value);
}

function setMenu(elem, value){
    set(elem, "data-app-menu-active", value);
}

function set(elem, attr, value){
    elem.setAttribute(attr, value);
}

function toggleMenuID(id){
  const elem = document.getElementById(id);
  toggleMenu(elem);
}

function toggleMenuClass(class_name){
  const elem = document.querySelector(class_name);
  toggleMenu(elem);
}

function toggleMenu(elem){
    toggle(elem, "data-app-menu-active");
}

function toggleExpand(elem){
  toggle(elem, "data-expand");
}

function toggle(elem, attr){
  let value = elem.getAttribute(attr);
    if (value === "true"){
      value = "false";
    } else {
      value = "true";
    }
    elem.setAttribute(attr, value);
}

function loader(id, value){
  let btn = document.getElementById(id);
  let loader = btn.querySelector(".spinner-border");

  loader.setAttribute("data-app-menu-active", value);
  if (value === "true"){
      value = "false";
  } else {
      value = "true";
  }

}

function loadTerrain(parent_id){
  let parent = document.getElementById(parent_id);
  createElement("p", "", parent);
}

function typeTerrain(id, menu_id){
  let elem = document.getElementById(id)
  let value = elem.value;

  if (value === "other"){
    setMenuID(menu_id, "true");
  } else {
    setMenuID(menu_id, "false");
  }
}

function initRoute(){
  const btn = document.getElementById("define-route-terrain");
  setMenu(btn, "true");
  let request_terrain_offcanvas_elem = document.getElementById('request-terrain-offcanvas');
  let request_terrain_offcanvas = new bootstrap.Offcanvas(request_terrain_offcanvas_elem)
  request_terrain_offcanvas.hide;

  toggleRouteDrawingControl(true,true);
}

function terminateRoute(confirmed){
  const btn = document.getElementById("define-route-terrain");
  setMenu(btn, "true");
  let request_terrain_offcanvas_elem = document.getElementById('request-terrain-offcanvas');
  let request_terrain_offcanvas = new bootstrap.Offcanvas(request_terrain_offcanvas_elem)
  request_terrain_offcanvas.show;

  toggleRouteDrawingControl(false, confirmed);
}

function toggleOffcanvas(value){

}

document.getElementById("search_list_user_input")
.addEventListener("keydown", function(event){
  switch (event.key){
      case "Enter":
          get();
          break;
  }
});

