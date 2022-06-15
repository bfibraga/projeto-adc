function toggleChangeProfileMenu(id1, id2){
    let elem1 = document.getElementById(id1);
    let elem2 = document.getElementById(id2);
    toggleMenu(elem1);
    toggleMenu(elem2);
}

function toggleMenu(elem){
    let value = elem.getAttribute("data-app-menu-active");
    if (value === "true"){
      value = "false";
    } else {
      value = "true";
    }
    elem.setAttribute("data-app-menu-active", value);
}
