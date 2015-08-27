$(document).ready(function(){
    
    function logoutin(){
        $.ajax({
            url: "/php/signout.php",   
        }).done(function(res){
            window.location.href="/login.php";
        });
    }
    
    $("#submit-button").click(function(){
        $("#error-label").html("");
        var email = $("#settings-email").val();
        if(email != "" && email.indexOf("@") != -1){
            $.ajax({
                url: "/php/user_info.php",
                type: "POST",
                data: {"q":"EDIT_EMAIL","val":email}
            }).done(function(res){
                if(res != "OKAY"){
                    $("#error-label").html("Unable to set e-mail");
                    return;
                }
                var password = $("#settings-password").val();
                if(password != "" && password == $("#settings-password-2").val()){
                    $.ajax({
                        url: "/php/user_info.php",
                        type: "POST",
                        data: {"q":"EDIT_PASS","val":password}
                    }).done(function(res){
                        if(res != "OKAY"){
                            $("#error-label").html($("#error-label").html()+"<br/>Unable to set e-mail");                                    
                        }
                        if($("#error-label").html() == ""){
                            logoutin();
                        }
                    });
                }else{
                    if($("#error-label").html() == ""){
                        logoutin();
                    }
                }
            });
        }else{
            var password = $("#settings-password").val();
            if(password != "" && password == $("#settings-password-2").val()){
                $.ajax({
                    url: "/php/user_info.php",
                    type: "POST",
                    data: {"q":"EDIT_PASS","val":password}
                }).done(function(res){
                    if(res != "OKAY"){
                        $("#error-label").html($("#error-label").html()+"<br/>Unable to set e-mail");                                    
                    }
                    if($("#error-label").html() == ""){
                        logoutin();
                    }
                });
            }
        }
    });
});
