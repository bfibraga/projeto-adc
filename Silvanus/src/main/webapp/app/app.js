var change_attr_type = [];

function convertElement(id, new_type){
    var originalELement = document.getElementById(id);
    var newElement = document.createElement(new_type);

    for (var i = 0; i < originalELement.attributes.length; i++) {
        var attr = originalELement.attributes.item(i);
        newElement.setAttribute(attr.nodeName, attr.nodeValue);
    }
}
