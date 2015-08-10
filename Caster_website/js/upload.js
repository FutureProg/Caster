var audiofile;

$("#image-uploader").on('change',function(){  
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
    }); 

function submit(){
    var title = $("#upload-title-area").val();    
    var desc = $("#upload-description-area").val();
    var tags = $("#upload-tags-area").val();
    if(!tags || !desc || !title || tags.length < 1 || desc.length < 1 || title.length < 1){
        if(!tags || tags.length < 1) $("#upload-tags-area").css("border-color","red")
        if(!desc || desc.length < 1) $("#upload-description-area").css("border-color","red")
        if(!title || title.length < 1) $("#upload-title-area").css("border-color","red")
        return;
    }
    var sharing = $("#share-settings :selected").val();
    var downloadable = $("#download-checkbox")[0].checked;
    var data = new FormData();
    data.append('title',title);
    data.append('description',desc);
    data.append('tags',tags);
    data.append('sharing',""+sharing);
    data.append("downloadable",""+downloadable);
    $.ajax({
        url: "../php/upload_audio.php?q=CHECK",
        type:"POST",
        data:data,
        cache:false,
        processData:false,
        contentType:false,
    }).done(function(result){
        if(result == "TITLE"){            
            $("#upload-title-area").css("border-color","red").val("Already have a podcast with that title");
            return;
        }
    });
    var file = $("#image-uploader")[0];
    var reader = new FileReader();
    reader.readAsDataURL(file.files[0]);
    data.append("image_file",file.files[0]);
    /*$.each(files,function(key,value){
        data.append(key,value);
    });*/
    var file2 = $("#podcast-uploader")[0];  
    var reader2 = new FileReader();
    reader2.readAsDataURL(file2.files[0]);
    data.append("podcast_file",file2.files[0]);
    /*var filedata = document.getElementsByName("file");
    var len = filedata.files.length,reader,file;
    for(var i = 0;i < len;i++){
        file = filedata.files[i];
        reader = new FileReader();
        reader.readAsDataURL(file);
        data.append(i,file);
    }*/
    $.ajax({
        url: "../php/upload_audio.php?q=UPLOAD",
        type:"POST",
        data:data,
        cache:false,
        processData: false,
        contentType:false
    }).done(function(result){
        if(result != "OKAY"){
            alert("ERROR: " + result);
            console.log(result);
            return;
        }
        else{
            window.location.href="index.php";    
        }
    });
}var audiofile;

$("#image-uploader").on('change',function(){  
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
    }); 

function submit(){
    var title = $("#upload-title-area").val();    
    var desc = $("#upload-description-area").val();
    var tags = $("#upload-tags-area").val();
    if(!tags || !desc || !title || tags.length < 1 || desc.length < 1 || title.length < 1){
        if(!tags || tags.length < 1) $("#upload-tags-area").css("border-color","red")
        if(!desc || desc.length < 1) $("#upload-description-area").css("border-color","red")
        if(!title || title.length < 1) $("#upload-title-area").css("border-color","red")
        return;
    }
    var data = new FormData();
    data.append('title',title);
    data.append('description',desc);
    data.append('tags',tags);
    $.ajax({
        url: "../php/upload_audio.php?q=CHECK",
        type:"POST",
        data:data,
        cache:false,
        processData:false,
        contentType:false,
    }).done(function(result){
        if(result == "TITLE"){            
            $("#upload-title-area").css("border-color","red").val("Already have a podcast with that title");
            return;
        }
    });
    var file = $("#image-uploader")[0];
    var reader = new FileReader();
    reader.readAsDataURL(file.files[0]);
    data.append("image_file",file.files[0]);
    /*$.each(files,function(key,value){
        data.append(key,value);
    });*/
    var file2 = $("#podcast-uploader")[0];  
    var reader2 = new FileReader();
    reader2.readAsDataURL(file2.files[0]);
    data.append("podcast_file",file2.files[0]);
    /*var filedata = document.getElementsByName("file");
    var len = filedata.files.length,reader,file;
    for(var i = 0;i < len;i++){
        file = filedata.files[i];
        reader = new FileReader();
        reader.readAsDataURL(file);
        data.append(i,file);
    }*/
    $.ajax({
        url: "../php/upload_audio.php?q=UPLOAD",
        type:"POST",
        data:data,
        cache:false,
        processData: false,
        contentType:false
    }).done(function(result){
        if(result != "OKAY"){
            alert("ERROR: " + result);
            console.log(result);
            return;
        }
        else{
            window.location.href="index.php";    
        }
    });
}