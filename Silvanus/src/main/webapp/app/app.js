function toggleChangeProfileMenu(id1, id2){
    let elem1 = document.getElementById(id1);
    let elem2 = document.getElementById(id2);
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

function loadTerrain(parent_id){
  let parent = document.getElementById(parent_id);
  createElement("p", "", parent);
}

document.getElementById("type_of_terrain")
    .addEventListener('change', (event) =>{
    let value = event.target.value;
    console.log(value);
    if (value === "other"){
        setMenuID("other_type_of_terrain_checked", "true")
    } else {
        setMenuID("other_type_of_terrain_checked", "false")
    }
})


