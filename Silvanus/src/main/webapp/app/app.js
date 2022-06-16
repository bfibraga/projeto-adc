function toggleChangeProfileMenu(id1, id2){
    let elem1 = document.getElementById(id1);
    let elem2 = document.getElementById(id2);
    toggleMenu(elem1);
    toggleMenu(elem2);
}

function toggleExpandOffcanvas(id1){
    console.log("Toggle Expand")
    let elem1 = document.getElementById(id1);
    console.log(elem1);
    toggleExpand(elem1);
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


