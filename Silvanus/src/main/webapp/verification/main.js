let input = {}

document.querySelectorAll(".code-confirm").forEach(elem => {
    input["code-confirm"] = 0;
    elem.addEventListener("change", function(event){
        let btn = document.getElementById("verification_code_btn");
        if (elem.value.length <= 0){
            if (input["code-confirm"] > 0){
                input["code-confirm"] -= 1;
            }
        } else {
            if (input["code-confirm"] < 2){
                input["code-confirm"] += 1;
            }
        }

        btn.disabled = !(input["code-confirm"] === 2)
    })
})