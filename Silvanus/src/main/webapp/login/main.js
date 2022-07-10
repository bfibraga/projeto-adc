let field = 0;
let disabled = true;
let btn = document.getElementById("button-submit");

function loader(value){
    let loader = btn.querySelector(".spinner-border");

    loader.setAttribute("data-app-menu-active", value);
    if (value === "true"){
        value = "false";
    } else {
        value = "true";
    }

    btn.querySelector(".btn-text").setAttribute("data-app-menu-active", value);
}

function clearErrorMessage(){
    let message = document.getElementById("validation_error");

    message.innerHTML = "";
}

document.querySelectorAll(".usr-fields").forEach(elem => {
    elem.addEventListener("change", function(event){

        clearErrorMessage();

        if (event.target.value.length <= 0){
            if (field > 0){
                field -= 1;
            }
        } else {
            if (field < 2){
                field += 1;
            }
        }

        disabled = !(field >= 1);
        btn.disabled = disabled;
    })
});

document.addEventListener("keydown", function(event){
    switch (event.key){
        case "Enter":
            if (!disabled){
                loader(true);
                login();
            }
            
            break;
    }
});