var userid;
var myuserid;
var ismyprofile;
var _username;
$(document).ready(function(){    
    userid = document.URL.match(/user=(\d*)/)[1];            
    $.ajax({
        url: "../php/user_info.php",
        type: "POST",
        data: { "q" : "UI"}
    }).done(function(res){
       if(res != "NO"){              
           myuserid = parseInt(res.trim());           
       }else{
           myuserid = -1;       
       }
        console.log("muid : " + res);
        init();
    });      
});

function init(){
    getUsername();
    //change user picture    
    subscribed(function(res){
        if(res == true){
            $("#profile-subscribe-button img").attr("src","images/unsubscribe_button.png");
        }
        else{
            $("#profile-subscribe-button img").attr("src","images/subscribe_button.png");    
        }
    });
    console.log("COMPARE " + myuserid  + " TO " + userid);
    if(myuserid != -1 && myuserid == userid){
        $("#profile-image").attr("id","my-profile-image");
        $("#profile-subscribe-button").hide();
        ismyprofile = true;
    }else{
        $("#profile-image-camera").hide();  
        console.log("Hide camera");          
        ismyprofile = false;
    }
    
    refreshProfileImage();        
    
    var desc = "";
    $.ajax({
        url: "../php/user_info.php",
        type: "POST",
        data: { "q" : "DESC" , "id" : userid+""}
    }).done(function(res){
        desc = res;                
        checkDescription(desc);
    });
    var opt = "";
    if(ismyprofile){
        opt = "MYPROF";
    }
    $.ajax({
        url: "../php/page_content/profile_podcasts.php",
        type: "POST",
        data: {"q" : "PDCST", "u" : userid, "opt":opt}        
    }).done(function(result){
        $("#profile-activity-area").html(result);
    });

    
    if(ismyprofile){        
        $("#my-profile-image-uploader").change(function(evt){            
            var files = evt.target.files;
            var data = new FormData();
            $.each(files,function(key,value){
                data.append(key,value);
            });

            $.ajax({
                url: "../php/upload_profimg.php?PROFIMG",
                type: "POST",
                data: data,                
                cache: false,        
                processData: false,
                contentType: false,        
            }).done( function(result){
                if(result === "OKAY"){
                    refreshProfileImage();
                }
                else if(result == "TOOLARGE"){
                    alert("Size of image must be under 200 KB.");
                }
                else{
                    console.log("Error changing profile picture: " + result);                    
                }
            });

        });
                
    }else{
        $("#settings-link").hide();
        $("#my-profile-image-uploader").click(function(evt){
            evt.preventDefault();
        });
    }

}

function refreshProfileImage(){
     $.ajax({
        url: "../php/user_info.php",
        type: "POST",
        data: {"q" : "IMG","id" : userid}
    }).done(function(result){   
        newsrc = "";
        if(result == "UNDEFINED" || result == ""){
            newsrc = "images/default_profile.png";
        }
        else{
            newsrc = "../users/" + userid + "/images/"+result;
        }        
        $("#profile-image,#my-profile-image").attr("src",newsrc);
    });
}
    
    
function getUsername(){
    $.ajax({
        url: "../php/user_info.php",
        data: {"q": "UN","id":parseInt(userid)},
        type: "POST"
    }).done(function(response){
       $("#profile-user-name h1").html(response);
        _username = response;
        document.title = "Caster - "+response;
    });
}

$("#profile-activity-item").click(function(evt){    
    nav(0);    
    evt.preventDefault();
});


$("#profile-podcasts-item").click(function(evt){   
    nav(1);
    evt.preventDefault();
});

$("#profile-playlists-item").click(function(evt){   
    nav(2); 
    evt.preventDefault();
});

$("#profile-subscribe-button").click(function(evt){
    if(myuserid == -1){
        window.location.href="login.php";
        return;
    }
   subscribed(function(res){       
       if( res == false){           
           subscribe();
        }else{            
            unsub();
        }
   });
});

$("#profile-login-sub-button").click(function(evt){    
    window.location.href="login.php";
});



function subscribe(){
    $.ajax({
        url: "../php/subscription.php",
        type: "POST",
        data:{
            "q" : "SUB",
            "s" : userid
        }
    }).done(function(res){          
        $("#profile-subscribe-button img").attr("src","images/unsubscribe_button.png");                                          
    });
}

function unsub(){
    $.ajax({
        url: "../php/subscription.php",
        type: "POST",
        data:{
            "q" : "UNSUB",
            "s" : userid
        }
    }).done(function(res){  
        console.log(res);
        $("#profile-subscribe-button img").attr("src","images/subscribe_button.png");                                          
    });
}
            

function subscribed(f){    
    $.ajax({
        url: "../php/subscription.php",
        type: "POST",
        data: {
            "q" : "CHECK",
            "s" : (userid)
        }        
    }).done(function(res){              
        if(res.indexOf("OKAY") >= 0){                        
            f(res.indexOf("YES") >= 0);
        }else{
            console.log("Error checking if subscribed: " + res);
        }
    });
}

function nav(choice){
    $("#profile-activity-item").css("background-color","white").css("color","black")
                                                               .css("text-shadow","none");
    $("#profile-podcasts-item").css("background-color","white").css("color","black")
                                                               .css("text-shadow","none");
    $("#profile-playlists-item").css("background-color","white").css("color","black")
                                                                .css("text-shadow","none");
    if(choice == 0){
        $("#profile-activity-item").css("background-color","red").css("color","white")
                                                                 .css("text-shadow","0 1px 0 gray");
    }    
    else if(choice == 1){
        $("#profile-podcasts-item").css("background-color","red").css("color","white")
                                                                 .css("text-shadow","0 1px 0 gray");
    }
    else if(choice == 2){
        $("#profile-playlists-item").css("background-color","red").css("color","white")
                                                                .css("text-shadow","0 1px 0 gray");
    }
}

function checkDescription(desc){    
    if(ismyprofile){        
        $("#profile-description").replaceWith(function(){
            var re = "";
            re += "<label id='profile-description-label'></label>";
            re += "<textarea id='profile-description' placeholder='Describe yourself' cols='32' rows='5' maxlength='200'>"
            if(desc != ""){
                re += desc;
            }
            re += "</textarea>";
            re += "\n";
            re += "<br/><button id='profile-description-button'>Save Changes</button>";
            return re;
        });        
        $("#profile-description-button")[0].disabled = true;  
        $("#profile-description-button").click(function(evt){
            $.ajax({
                url: "../php/user_info.php",
                type: "POST",
                data: { "q":"S_DESC","id":userid, "desc":$("#profile-description").val()}
            }).done(function(res){
                if(res == "OKAY"){
                    desc = $("#profile-description").val();
                    $("#profile-description-label").html("description updated");
                }
                else{
                    $("#profile-description-label").html("unable to update description");
                    console.log(res);
                }
            });
        });
        $("textarea#profile-description").bind('input propertychange',function(){                   
            if(desc != $("#profile-description").val()){                
                $("#profile-description-button")[0].disabled = false;
            }
        });        
    }
    else{
        $("#profile-description").html(desc);
    }
}

nav(0);
