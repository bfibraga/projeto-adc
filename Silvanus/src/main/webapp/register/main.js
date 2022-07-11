let avatar_data = {
    "content": "",
    "type": ""
};

function loadAvatar(event){
    const avatar = document.getElementById("usr_avatar");
    const src = URL.createObjectURL(event.target.files[0]);
    avatar.src = src
    
    avatar_data["content"] = event.target.files[0];

    avatar_data["type"] = event.target.files[0].type;
    console.log(avatar_data);
}

function getAvatarImageContent(){
    return avatar_data;
}