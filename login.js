document.getElementById("loginForm").addEventListener("submit" , async(e)=>{
    e.preventDefault();

    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;


    const response = await fetch("http://localhost:8080/auth/login",{
        method : "POST",
        headers:{
            "Content-Type" : "application/json"
        },
        body:JSON.stringify({
            username : username,
            password : password})
    });

    if (response.ok){
        const user = await response.json();
        localStorage.setItem("loggedIn","true");
        localStorage.setItem("role" , user.role);
        window.location.href="index.html";
    }
    else{
        alert("invalid login");
    }

});

