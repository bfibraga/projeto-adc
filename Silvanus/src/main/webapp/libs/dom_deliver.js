function createElement(type, class_list, parent){
    
    let elem = document.createElement(type);
    elem.innerText = "wtf is going on";
    if (parent !== null){
        parent.appendChild(elem);
    }
    console.log("Created")
    return elem;
}


