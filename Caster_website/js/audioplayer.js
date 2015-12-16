var drag = {
    state: false,
    x: 0
};
var dx;
var currentPID;

$("#audio-player #play-button").click(function(evt){
    if($("#audio-player audio").trigger('paused')){
        playAudio(evt);
    }else{
        pauseAudio(evt);
    }
});

function playAudio(evt){
    $("#audio-player").show();
    $("#audio-player #play-button img").attr("src","/images/pause_button.png");
    $("#audio-player audio").trigger('play');
    $("#audio-player #play-button").click(pauseAudio);  
}

function pauseAudio(evt){
    $("#audio-player audio").trigger('pause');
    $("#audio-player #play-button").click(playAudio);
    $("#audio-player #play-button img").attr("src","/images/play_button.png");
}

$("#audio-player .click-area").click(function(evt){   
    $("#audio-player #audio-player-content").slideToggle(100);
    $("#audio-player #audio-player-scrubber").slideToggle(100);
});

$(document).click(function(evt){    
    if(evt.target == $("#audio-scrub-bar")[0]){        
        var bar = evt.target;
        var player = $("#audio-player audio");
        var width = bar.offsetWidth;
        var barx = bar.offsetLeft;
        var dx = evt.pageX-barx;
        var elapsed = Math.ceil((player.prop("currentTime") / player.prop("duration")) * 100);
        elapsed += Math.ceil((dx / width)*100);
        elapsed = Math.min(elapsed,100);
        elapsed = Math.max(elapsed,0);
        console.log(barx);
        var newTime = Math.floor(player.prop("duration") * (elapsed/100));
        player.prop("currentTime",newTime).trigger('play');       
    }
});

$(document).mousedown(function(evt){
    if(evt.target == $("#audio-scrub-circle")[0]){        
        if(!drag.state){
            //pauseAudio();
            drag.x = evt.pageX
            drag.state = true;
            console.log($("#audio-player audio").prop("seekable").start(0));
        }
    }
});

$(document).mousemove(function(evt){
    if(drag.state){        
        dx = evt.pageX - drag.x;
        var width = $("#audio-scrub-bar")[0].offsetWidth;        
        var player = $("#audio-player audio");
        var elapsed = Math.ceil((player.prop("currentTime") / player.prop("duration")) * 100);
        elapsed += Math.ceil((dx / width)*100);
        elapsed = Math.min(elapsed,100);
        elapsed = Math.max(elapsed,0);        
        var newTime = Math.floor(player.prop("duration") * (elapsed/100));
        $("#audio-scrub-circle").css("left",elapsed + "%");      
        $("#audio-player-time-stamp").html(secondstoTimeStamp(newTime) + " / " + secondstoTimeStamp(player.prop("duration")));  
    }
});

$(document).mouseup(function(evt){
    if(drag.state){
        var player = $("#audio-player audio");
        dx = evt.pageX - drag.x;        
        var width = $("#audio-scrub-bar")[0].offsetWidth;
        var elapsed = Math.ceil((player.prop("currentTime") / player.prop("duration")) * 100);
        elapsed += Math.ceil((dx / width)*100);
        elapsed = Math.min(elapsed,100);
        elapsed = Math.max(elapsed,0);
        var newTime = Math.floor(player.prop("duration") * (elapsed/100));
        player.prop("currentTime",newTime).trigger('play');                
        drag.state = false;
    }
});

function updateTime(){
    if(drag.state){
        return;
    }
    $("#audio-player-time-stamp").html(secondstoTimeStamp(this.currentTime) + " / " + secondstoTimeStamp(this.duration));  
    var elapsed = Math.ceil((this.currentTime / this.duration) * 100);
    $("#audio-scrub-bar #audio-scrub-circle").css("left", elapsed + "%");
}

function secondstoTimeStamp(seconds){
    min = Math.floor(seconds/60);
    sec = Math.floor(seconds - min);
    return min + ":" + (sec < 10 ? "0"+sec : sec);
}

function comment(){
    var msg = $("#audio-player-comment-box textarea").val();  
	$.ajax({
		type: "POST",
		url: "/php/podcast.php",
		data: {"q":"CMNT","id":currentPID,"msg":msg}
	}).done(function(res){
		loadComments();
	});
}

function updateLikeButton(){
	$.ajax({
		type:"POST",
		url: "/php/user_info.php",
		data: {"q":"LKD","id":-1}	
	}).done(function(res){
		var likes = res.split(",");			
        if(res == "NULL"){
            $("#audio-player-content .like-button").css("background-color","#F9F7F7")[0].onclick = function(){
                window.href = "login.php";
            };
        }
		if($.inArray(currentPID,likes) >= 0){
			$("#audio-player-content .like-button").css("background-color","black")[0].onclick = unlike;
		}else{
			$("#audio-player-content .like-button").css("background-color","#F9F7F7")[0].onclick = like;
		}		
	});	
	$.ajax({
		type:"POST",
		url: "/php/podcast.php",
		data: {"q":"LKS","id":currentPID}
	}).done(function(res){
		$("#audio-player-content .like-counter").html(res + " likes");	
	});
}

function loadComments(){
	$("#audio-player-comment-area").html("");
	if(signedIn){
		$("#audio-player-comment-area").html("<div id='audio-player-comment-box'>"+
				"<textarea rows='3' cols='40'></textarea>"+
				"<button onclick='comment()'>Submit</button>"+                
			"</div>");  	
	}	  
	$.ajax({
		type: "POST",
		url: "/php/podcast.php",
		data: {"q":"CMNTS_HTML","id":currentPID}	
	}).done(function(res){
		if(res.endsWith("OKAY")){
			res = res.substring(0,res.length-4);
			$("#audio-player-comment-area").html($("#audio-player-comment-area").html() +
				"<br/>" + res);		
		}else{
			console.log(res);		
		}
	});    
}

$("#audio-player audio").on("loadeddata",function(){    
    $("#audio-player-time-stamp").html("0:00 / " + secondstoTimeStamp(this.duration));    
});
$("#audio-player audio").on("timeupdate",updateTime).on('ended',function(){
    this.pause();
    this.currentTime = 0; 
    this.play();
});

setTimeout(function(){
    $("#audio-player audio").load();
    $("#audio-player #audio-player-content").slideToggle(0);
    $("#audio-player #audio-player-scrubber").slideToggle(0);
},1);

