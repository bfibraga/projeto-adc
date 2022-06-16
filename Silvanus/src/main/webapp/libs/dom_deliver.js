function createElement(type, class_list, parent){
    
    let elem = document.createElement(type);

    if (parent !== null){
        parent.appendChild(elem);
    }
    console.log("Created")
    return elem;
}


