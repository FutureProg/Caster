/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


function login(email,password){
    $("#fill_all_error").html("");
    $.ajax({
        url: "php/signin.php",
        type: "POST",
        data: { "q" : "LOGIN",
                "e" : email,
                "p" : password}
    }).done(function(msg){
        if(msg === "OKAY"){
            window.location.href="index.php";           
        }else{
            $("#fill_all_error").html("Incorrect email/password");
            if(msg !== "NO"){
                console.log("Error: "+msg);
            }
            return;
        }
    }).fail(function(jqXHR,status){
        alert("Request failed: " + status);
    });
}