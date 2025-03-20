document.addEventListener("DOMContentLoaded", () => {
    const email = document.getElementById("email")
    const password = document.getElementById("password")
    const helper = document.getElementById("helper");
    const loginButton = document.getElementById("loginButton");
    
    email.addEventListener("focusout", () => {
        helper.innerText = validateEmailPassword(email.value,password.value)
        changeBtnColor();
    })

    password.addEventListener("focusout", () => {
        helper.innerText = validateEmailPassword(email.value,password.value)
        changeBtnColor();
    })

    const changeBtnColor = () => {
        loginButton.style.backgroundColor = (helper.innerText.trim() == "") ? "#7f6aee" :  "#aca0eb"
    }

    loginButton.addEventListener("click", () => {
        let loginUser = null
        const members = JSON.parse(localStorage.getItem("members")) || [];
        for(let i = 0; i<members.length; i++){
            const item = members[i]
            if(email.value == item.email && password.value == item.password){
                loginUser = item;
            }
        }
        if(loginUser){
            sessionStorage.setItem("loginUser",JSON.stringify(loginUser));
            window.location.href = "Posts.html"
        }
    })
    
    document.addEventListener("keyup", (event) => {
        if(event.key === 'Enter'){
            let loginUser = null
            const members = JSON.parse(localStorage.getItem("members")) || [];
            for(let i = 0; i<members.length; i++){
                const item = members[i]
                console.log(item)
                if(email.value == item.email && password.value == item.password){
                    loginUser = item;
                }
            }
            if(loginUser){
                sessionStorage.setItem("loginUser",JSON.stringify(loginUser));
                window.location.href = "Posts.html"
            }
            else{
                console.log("엔터키!")
            }
        }
    })

})





const validateEmailPassword = (email, password) => {
    const emailRegex = /^[a-zA-Z0-9]+@[a-zA-Z0-9]+\.[a-zA-Z]{2,}$/;
    if(!emailRegex.test(email)){
        return "*올바른 이메일 주소 형식을 입력해주세요.(예:example@example.com)"
    }

    if(!password){
        return "*비밀번호를 입력해주세요."
    }

    const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*()-_=+])[a-zA-Z0-9!@#$%^&*()-_=+]{8,20}$/;
    if(!passwordRegex.test(password)){
        return "*비밀번호는 8자 이상 20자 이하이며, 대문자, 소문자, 숫자, 특수문자를 각각 최소 1개 포함해야 합니다."
    }

    const members = JSON.parse(localStorage.getItem("members")) || [];
    for(let i = 0; i<members.length; i++){
        const item = members[i]
        if(item.email == email && item.password == password){
            return ""
        }
    }
    
    return "*아이디 또는 비밀번호를 확인해주세요."
};