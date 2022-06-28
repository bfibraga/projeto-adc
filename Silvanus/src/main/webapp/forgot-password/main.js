let input = {};

function toggleButton(btn, value){
    btn.disabled = !value;
}

document.getElementById("usr_password").addEventListener("change", function(event){
    input["usr_password"] = event.target.value;
    let btn = document.getElementById("password-btn-submit");

    toggleButton(btn, input["usr_password"] === input["usr_confirm"]);

});

document.getElementById("usr_confirm").addEventListener("change", function(event){
    input["usr_confirm"] = event.target.value;
    let btn = document.getElementById("password-btn-submit");

    toggleButton(btn, input["usr_password"] === input["usr_confirm"]);

});

document.querySelectorAll(".code-confirm").forEach(elem => {
    input["code-confirm"] = 0;
    elem.addEventListener("change", function(event){
        let btn = document.getElementById("verification_code_btn");

        if (elem.value.length <= 0){
            if (input["code-confirm"] > 0){
                input["code-confirm"] -= 1;
            }
        } else {
            if (input["code-confirm"] < 4){
                input["code-confirm"] += 1;
            }
        }

        btn.disabled = !(input["code-confirm"] === 4)
    })
})

document.getElementById("usr_identifier").addEventListener("change", function(event){
    let btn = document.getElementById("forgot-password-btn");

    btn.disabled = !(event.target.value.length > 0);
})