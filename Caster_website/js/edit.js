var podcast_id = document.URL.match(/id=(\d*)/)[1];

$("#image-uploader").on('change',function(){  
    $("#image-save").html("SAVE").css("background-color","red");
    var evt = this;    
    if(evt.files && evt.files[0]){
        var reader = new FileReader();
        reader.onload = function(e){
            $("#podcast-upload-image-preview")
                .attr('src',e.target.result);            
        };
        reader.readAsDataURL(evt.files[0]);
    }
});

$("#upload-title-area").on('change',function(){
    $("#title-save").html("SAVE").css("background-color","red");
});
$("#upload-description-area").on('change',function(){
    $("#desc-save").html("SAVE").css("background-color","red");
});


$("#title-save").click(function(){
    var title = $("#upload-title-area").val();
    $.ajax({
        url: "/php/podcast.php",
        type: "POST",
        data:{"q" : "UPDATE","col":"title","data":title,"podcast_id":podcast_id}        
    }).done(function(res){
        if(res == "OKAY"){
            $("#title-save").html("SAVED!").css("background-color","gray");
        }else{
            alert("Error updating podcast title: " + res);
        }
    });
});
$("#desc-save").click(function(){
    var desc = $("#upload-description-area").val();
    $.ajax({
        url: "/php/podcast.php",
        type: "POST",
        data:{"q" : "UPDATE","col":"description","data":desc,"podcast_id":podcast_id}        
    }).done(function(res){
        if(res == "OKAY"){
            $("#desc-save").html("SAVED!").css("background-color","gray");
        }else{
            alert("Error updating podcast description: " + res);
        }
    });
});
$("#tags-save").click(function(){
    var tags = $("#upload-tags-area").val();
    $.ajax({
        url: "/php/podcast.php",
        type: "POST",
        data:{"q" : "UPDATE","col":"tags","data":tags,"podcast_id":podcast_id}        
    }).done(function(res){
        if(res == "OKAY"){
            $("#tags-save").html("SAVED!").css("background-color","gray");
        }else{
            alert("Error updating podcast tags: " + res);
        }
    });
});



$("#image-save").click(function(){    
    $("#image-save").html("...");
    var file = $("#image-uploader")[0];
    var reader = new FileReader();
    var data = new FormData();
    data.append("image_file",file.files[0]); 
    data.append("podcast_id",podcast_id);
    data.append("q","UPDATE_IMG");
    $.ajax({
        url: "/php/podcast.php",
        type: "POST",
        data:data,
        cache:false,
        processData:false,
        contentType:false
    }).done(function(res){
        if(res == "OKAY"){
            $("#image-save").html("SAVED!").css("background-color","gray");
        }else{
            alert("Error updating podcast image: " + res);
        }
    });
});

$("#finish-button").click(function(evt){
    window.history.back();
});

$("#delete-button").click(function(evt){
    if(!window.confirm("This will delete your podcast. You can't undo this!")){
        return;
    }
    var data = new FormData();
    data.append("q","DLT");
    data.append("podcast_id",podcast_id);
    $.ajax({
        url: "../php/podcast.php",
        type: "POST",
        data: data,
        cache:false,
        processData:false,
        contentType:false
    }).done(function(res){
        if(res == "OKAY"){
            window.history.back();
        }else{
            alert("Error deleting podcast: " + res);
        }
    });
});
