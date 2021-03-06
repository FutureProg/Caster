/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/*function Showcase(){
    this.imgList = ["dummy_featured_img.jpg","dummy_featured_img2.jpg"];
    this.currentImage = 0;
}
Showcase.prototype = {
    constructor: Showcase,
    nextImage:function(){
        this.currentImage++;
        if(this.currentImage >= this.imgList.length){
            this.currentImage = 0;
        }
        return this.imgList[this.currentImage];        
    },
    prevImage:function(){
        this.currentImage--;
        if(this.currentImage < 0){
            this.currentImage = this.imgList.length-1;
        }
        return this.imgList[this.currentImage];
    }
}

var showcase = new Showcase();
var timer;*/
$(document).ready(function(){
    $.ajax({
        url: "/php/search_podcasts.php",
        type: "POST",
        data: {"t":"RU"}
    }).done(function(res){
        $("#recent-uploads").html(res);
        $(".wplt .wplt-inner .podcast-object").hover(function(){
    		$(this).children(".podcast-info").slideDown("fast");
		},
		function(){
    		$(this).children(".podcast-info").slideUp("fast");
		});
		$(".wplt .wplt-inner .podcast-object .podcast-info").slideUp(0); 
    });
    $.ajax({
        url: "../php/search_podcasts.php",
        type: "POST",
        data: {"t":"SP"}
    }).done(function(res){
        if(res == ""){
            $("#subscriptions").html("<div style='text-align:center'><h3>You have no updates</h3></div><br/>");
        }
        else{
            $("#subscriptions").html(res);
        }        
        $(".wplt .wplt-inner .podcast-object").hover(function(){
    		$(this).children(".podcast-info").slideDown("fast");
		},
		function(){
    		$(this).children(".podcast-info").slideUp("fast");
		});
		$(".wplt .wplt-inner .podcast-object .podcast-info").slideUp(0);    
    });        
    restartTimer();
    /*$.ajax({
       url: "gettitle.php",
       type: "POST",
       data: {q:"title"},
       dataType: "JSON"
    }).success(function(result){                
        $("#top-bar h1").html(result.main);        
        $("#wplt h1").html(result.wplt);        
    });*/
});


$(".showcase-left").click(function(){  
    clearInterval(timer);
    imageBack(); 
});
$(".showcase-right").click(function(){  
    clearInterval(timer);
    imageNext();     
});



function imageBack(){
    $(".showcase-object").fadeOut("medium", function(){
           $(this).children(".showcase-image").attr("src","images/" + showcase.prevImage());
           $(this).fadeIn("medium",restartTimer)           
    }); 
}
function imageNext(){
    $(".showcase-object").fadeOut("medium", function(){
           $(this).children(".showcase-image").attr("src","images/" + showcase.nextImage());
           $(this).fadeIn("medium",restartTimer)           
    }); 
}
function restartTimer(){
    timer = setInterval(function(){      
       $(".showcase-object").fadeOut("medium", function(){
           $(this).children(".showcase-image").attr("src","images/" + showcase.nextImage());
           $(this).fadeIn("medium");
       });       
    },10000);
}
