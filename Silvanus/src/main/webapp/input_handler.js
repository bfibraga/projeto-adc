const main_elem = document.getElementById("main");
const form_elem = document.getElementById("form");
const bg_elem = document.getElementById("bg-image");

const input_fields = {
  "curr_step": document.getElementById("step-0"),
  "id": document.getElementById("usr_identifier"),
  "firstname": document.getElementById("usr_firstname"),
  "lastname": document.getElementById("usr_lastname"),
  "email": document.getElementById("usr_email"),
  "password": document.getElementById("usr_password"),
  "password-bar": document.getElementById("password-progress"),
  "confirmation": document.getElementById("usr_confirmation")
};

document.getElementById("button-submit").addEventListener("click", function(){
  document.getElementById("loader").classList.remove("none");
  document.getElementById("loader").classList.add("block");
});

//TODO Remove this
/*document.getElementById("theme-switch").addEventListener("change", function(){
  if(this.checked){
    if(main_elem != null){
      main_elem.classList.add("dark");
    }

    if(form_elem != null){
      form_elem.classList.remove("light");
      form_elem.classList.add("op-dark");
    }

    if(bg_elem != null){
      bg_elem.classList.remove("bg-light");
      bg_elem.classList.add("bg-dark");
    }

    for(const [,value] of Object.entries(input_fields)){
      if(value != null){
        value.classList.add("input-dark");
      }
    }


    document.getElementById("password-visible").classList.add("op-dark");
    document.getElementById("check_me").classList.add("op-dark");
  } else {
    if(main_elem != null){
      main_elem.classList.remove("dark");
    }

    if(form_elem != null){
      form_elem.classList.add("light");
      form_elem.classList.remove("op-dark");
    }

    if(bg_elem != null){
      bg_elem.classList.add("bg-light");
      bg_elem.classList.remove("bg-dark");
    }

    for(const [,value] of Object.entries(input_fields)){
      if(value != null){
        value.classList.remove("input-dark");
      }
    }

    document.getElementById("password-visible").classList.remove("op-dark");
    document.getElementById("check_me").classList.remove("op-dark");
  }
});*/

document.getElementById("password-visible").addEventListener("click", function(event){
  //event.preventDefault();
  const input_type = document.getElementById("usr_password").getAttribute("type");
  if(input_type == "password") {
    document.getElementById("usr_password").setAttribute("type", "text");
    document.getElementById("check-password").classList.add("invisible");
    document.getElementById("hide-password").classList.remove("invisible");
  } else {
    document.getElementById("usr_password").setAttribute("type", "password");
    document.getElementById("check-password").classList.remove("invisible");
    document.getElementById("hide-password").classList.add("invisible");
  }
});

const min_letters = 20;
const nColors = 3;

input_fields["password"].addEventListener("input", function(event){
  const n_letters = this.value.length;
  let percentage = Math.min(n_letters/min_letters*100, 100);

  if (percentage <= 0){
    input_fields["password-bar"].parentNode.classList.add("invisible");
  } else {
    if (percentage <= 33){
      input_fields["password-bar"].classList.add("bg-danger");
      input_fields["password-bar"].classList.remove("bg-warning");
    } else if (percentage <= 66){
      input_fields["password-bar"].classList.add("bg-warning");
      input_fields["password-bar"].classList.remove("bg-success");
      input_fields["password-bar"].classList.remove("bg-danger");
    } else {
      input_fields["password-bar"].classList.add("bg-success");
      input_fields["password-bar"].classList.remove("bg-warning");
    }
    input_fields["password-bar"].parentNode.classList.remove("invisible");
  }

  input_fields["password-bar"].style.width = String(percentage + "%");
});

function previousStep(){
  const curr_number_step = input_fields["curr_step"].id;
  let previousStep = curr_number_step.split("-")[1] -1;
  console.log(nextStep);

  //Change Menu
  input_fields["curr_step"].classList.add("d-none");
  input_fields["curr_step"] = document.getElementById("step-" + previousStep);
  input_fields["curr_step"].classList.remove("d-none");
}

function nextStep(){
  const curr_number_step = input_fields["curr_step"].id;
  let nextStep = parseInt(curr_number_step.split("-")[1]) + 1;
  console.log(nextStep);

  //Change Menu
  input_fields["curr_step"].classList.add("d-none");
  input_fields["curr_step"] = document.getElementById("step-" + nextStep);
  input_fields["curr_step"].classList.remove("d-none");
}

function loadAvatar(event){
  const avatar = document.getElementById("usr_avatar");
  avatar.src = URL.createObjectURL(event.target.files[0])
}
