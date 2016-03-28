var token;
var podcast;
var onnav = null;
var signedIn = false;
$(".inner-link").click(function(evt){
    evt.preventDefault();
    loadPage(this.getAttribute("href"));
});

window.mobilecheck = function(){
    var check = false;
      (function(a){if(/(android|bb\d+|meego).+mobile|avantgo|bada\/|blackberry|blazer|compal|elaine|fennec|hiptop|iemobile|ip(hone|od)|iris|kindle|lge |maemo|midp|mmp|mobile.+firefox|netfront|opera m(ob|in)i|palm( os)?|phone|p(ixi|re)\/|plucker|pocket|psp|series(4|6)0|symbian|treo|up\.(browser|link)|vodafone|wap|windows ce|xda|xiino/i.test(a)||/1207|6310|6590|3gso|4thp|50[1-6]i|770s|802s|a wa|abac|ac(er|oo|s\-)|ai(ko|rn)|al(av|ca|co)|amoi|an(ex|ny|yw)|aptu|ar(ch|go)|as(te|us)|attw|au(di|\-m|r |s )|avan|be(ck|ll|nq)|bi(lb|rd)|bl(ac|az)|br(e|v)w|bumb|bw\-(n|u)|c55\/|capi|ccwa|cdm\-|cell|chtm|cldc|cmd\-|co(mp|nd)|craw|da(it|ll|ng)|dbte|dc\-s|devi|dica|dmob|do(c|p)o|ds(12|\-d)|el(49|ai)|em(l2|ul)|er(ic|k0)|esl8|ez([4-7]0|os|wa|ze)|fetc|fly(\-|_)|g1 u|g560|gene|gf\-5|g\-mo|go(\.w|od)|gr(ad|un)|haie|hcit|hd\-(m|p|t)|hei\-|hi(pt|ta)|hp( i|ip)|hs\-c|ht(c(\-| |_|a|g|p|s|t)|tp)|hu(aw|tc)|i\-(20|go|ma)|i230|iac( |\-|\/)|ibro|idea|ig01|ikom|im1k|inno|ipaq|iris|ja(t|v)a|jbro|jemu|jigs|kddi|keji|kgt( |\/)|klon|kpt |kwc\-|kyo(c|k)|le(no|xi)|lg( g|\/(k|l|u)|50|54|\-[a-w])|libw|lynx|m1\-w|m3ga|m50\/|ma(te|ui|xo)|mc(01|21|ca)|m\-cr|me(rc|ri)|mi(o8|oa|ts)|mmef|mo(01|02|bi|de|do|t(\-| |o|v)|zz)|mt(50|p1|v )|mwbp|mywa|n10[0-2]|n20[2-3]|n30(0|2)|n50(0|2|5)|n7(0(0|1)|10)|ne((c|m)\-|on|tf|wf|wg|wt)|nok(6|i)|nzph|o2im|op(ti|wv)|oran|owg1|p800|pan(a|d|t)|pdxg|pg(13|\-([1-8]|c))|phil|pire|pl(ay|uc)|pn\-2|po(ck|rt|se)|prox|psio|pt\-g|qa\-a|qc(07|12|21|32|60|\-[2-7]|i\-)|qtek|r380|r600|raks|rim9|ro(ve|zo)|s55\/|sa(ge|ma|mm|ms|ny|va)|sc(01|h\-|oo|p\-)|sdk\/|se(c(\-|0|1)|47|mc|nd|ri)|sgh\-|shar|sie(\-|m)|sk\-0|sl(45|id)|sm(al|ar|b3|it|t5)|so(ft|ny)|sp(01|h\-|v\-|v )|sy(01|mb)|t2(18|50)|t6(00|10|18)|ta(gt|lk)|tcl\-|tdg\-|tel(i|m)|tim\-|t\-mo|to(pl|sh)|ts(70|m\-|m3|m5)|tx\-9|up(\.b|g1|si)|utst|v400|v750|veri|vi(rg|te)|vk(40|5[0-3]|\-v)|vm40|voda|vulc|vx(52|53|60|61|70|80|81|83|85|98)|w3c(\-| )|webc|whit|wi(g |nc|nw)|wmlb|wonu|x700|yas\-|your|zeto|zte\-/i.test(a.substr(0,4)))check = true})(navigator.userAgent||navigator.vendor||window.opera);
return check;
};

window.onload = function(){
    window.setTimeout(function(){
        $(window).on("popstate",function(e){
            backPage(location.pathname);
            return false;
        });
    },1);
}

$(document).ready(function(){
    if($("#audio-player audio source").attr("src") == ""){
        $("#audio-player").hide();
    }
    $.ajaxPrefilter(function( options, originalOptions, jqXHR ) {
      options.async = true;
    });
})

function loadPage(url,scroll){
	if(onnav != null){
		onnav();
	}
  $(body).fadeOut(100);
    $.ajax({
        type: "GET",
        url: "/php/page_content/" + url.split("/").pop()
    }).done(function(html){
        scroll = scroll || true;
        if(scroll)
            $("html,body").scrollTop(0);
        if(url.indexOf("profile.php") != -1){
            url = url.replace("profile.php?user=","");
        }
        url = "/" + url;
        history.pushState(null,null,url);
        $("#main-content").html(html);
        $(body).fadeIn(100);
    });
}

function backPage(url){
	if(onnav != null){
		onnav();
	}
    if(url.indexOf("error403.php") < 0 || url.indexOf("index.php") < 0 || url.indexOf("profile_podcasts.php") < 0 ||
       url.indexOf("profile.php") < 0 || url.indexOf("search.php") < 0){
        window.location.href = url;
        return;
    }
    else if(url.indexOf(".php") < 0){
        url = "profile.php?user=" + url;
    }
    else if(url == ""){
        url = "index.php";
    }
    console.log("GO " + url);
    $.ajax({
        type: "GET",
        url: "php/page_content/" + url.split("/").pop()
    }).done(function(html){
        $("html,body").scrollTop(0);
        console.log("Loaded " + url);
        $("#main-content").html(html);
    });
}

$("#search-button").click(function(evt){
    loadPage('search.php?q='+$("#search-box").val());
});
$("#search-box").keyup(function(evt){
    if(evt.keyCode == 13){
        $("#search-button").click();
    }
});

/*$(".podcast-bar").click(function(evt){
    if(evt.target == $(".image-container")[0]){
        playSound(this.getAttribute("id"),this.getAttribute("title"));
    }
});*/

function like(){
	if(currentPID != 0){
		$.ajax({
			type: "POST",
			url: "/php/podcast.php",
			data: {"q":"LK","id":currentPID}
		}).done(function(res){
			console.log(res);
		});
	}
	updateLikeButton(); // defined in audioplayer
}

function unlike(){
	if(currentPID != 0){
		$.ajax({
			type: "POST",
			url: "/php/podcast.php",
			data: {"q":"UN_LK","id":currentPID}
		}).done(function(res){
			console.log(res);
		});
	}
	updateLikeButton(); // defined in audioplayer
}

function likeList(){
	$.ajax({
		type:"POST",
		url: "/php/user_info.php",
		data: {"q":"LKD","id":-1}
	}).done(function(res){
		return res.split(",");
	});
}

function playSound(id,title){
    if(token == null){
        $.ajax({
            type: "POST",
            url: "/php/token.php",
            data: {"q":"TKN","t":"PDCST"}
        }).done(function(res){
            token = res;
            playSound(id,title);
        });
        return;
    }
    if(podcast == null){
        $.ajax({
            type: "POST",
            url: "/php/podcast.php",
            data: {"q":"PDCST_JSN","id":id}
        }).done(function(res){
            podcast = JSON.parse(res);
            $.ajax({
                type: "POST",
                url: "/php/user_info.php",
                data: {"q":"USR_JSN","id":podcast.user_id}
            }).done(function(res){
            	$.ajax({
            		type: "POST",
            		url: "/php/podcast.php",
            		data: {"q":"LSTN","id":podcast.podcast_id}
            	});
                var user = JSON.parse(res);
                if(user.picture != ""){
                  var imgtag = "<div style='float:left'><img style='cursor:pointer;border-radius:25%' width='25%' onclick=loadPage('profile.php?user="+user.username+"') src='/users/"+podcast.user_id+"/images/"+user.picture+"'>";
                }else{
                  var imgtag = "<div style='float:left'><img style='cursor:pointer;border-radius:25%' width='25%' onclick=loadPage('profile.php?user="+user.username+"') src='/images/default_profile.png'>";
                }
                imgtag += "<a style='font-size:2em;margin-left:10px;cursor:pointer' onclick=loadPage('profile.php?user="+user.username+"')>"+user.username+"</a></div>";
                var sidebar = "<div style='position: absolute;right:10px;'>";
                var likebutton = "<img class='like-button' src='/images/heart.png'/><p class='like-counter'>0 Likes</p>";
                var viewSection = "<div class='listen-counter'>10 listens</div>";
                sidebar += likebutton + viewSection;
                sidebar += "</div>";
                $("#audio-player #podcast-art").attr("src","/users/"+user.user_id+"/images/podcast/"+podcast.image_file);
                $("#audio-player-comment-area").html("");
                $("#audio-player #audio-player-content").html(imgtag + sidebar + "<br/><br/><br/><br/><div><p>"+podcast.description+"</p></div>" + "<br/>" + "<div id='audio-player-comment-area'></div>");
                currentPID = podcast.podcast_id;
                updateLikeButton();
                /*var title = podcast.title;
                title = title.replace(/\%20/g," ");
                $("#now-playing").html(title);*/
                playSound(id,title);
            });
        });
        return;
    }
	title = title.replace(/\%20/g," ");
	$("#now-playing").html(title);
	$("#audio-player-content .listen-counter").html(podcast.listens + " Listens");
    $("#audio-player").show();
    window.setTimeout(function(){
        $("#audio-player #audio-player-content").slideToggle(100);
        window.setTimeout(function(){
            $("#audio-player #audio-player-content").slideToggle(100);
        },1000);
    },500);
    $("#audio-player audio source").attr("src","/php/audio_file.php?q="+id+"$"+token);
    $("#audio-player audio").trigger("load").on("canplay",function(){$("#audio-player #play-button img").attr("src","/images/pause_button.png");
                                                                     $("#audio-player audio").trigger('play');
                                                                     $("#audio-player #play-button").click(pauseAudio);    });

    token = null;
    podcast = null;
    loadComments();
}

if(window.mobilecheck() == true){
    window.location.href = "/mobile.php";
}
