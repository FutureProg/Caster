<?php 
/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

include_once 'phpreq/start_session.php';
include 'php/podcast.php';
$podcast = get_podcast(filter_input(INPUT_GET,"id"));
?>

<!DOCTYPE html>
<!--
To change this license header, choose License Headers in Project Properties.
To change this template file, choose Tools | Templates
and open the template in the editor.
-->
<html>
    <head>
        <meta charset="UTF-8">
        <meta name="author" content="Nickolas Morrison">
        <meta name="description" content="Share your podcasts with the world.">
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js"></script>
        <link href="style.css" rel="stylesheet"/>
        <link rel="icon" href="images/logo.png" sizes="32x32" type="image/png"/>
        <title>Caster-Edit</title>
    </head>
    <body>
        <?php if($_SESSION['user_id'] != $podcast['user_id']){
            include '/php/page_content/error403.php';
            exit();
            return;
         }?>
        <div id="body-container">
            <?php include 'phpreq/topbar.php';?>
            <!--Start Content here-->
            <div id="content-container" style="margin:10px;margin-left:0;top:80px;height:80%;">
                <div id="edit-container">                    
                    <h1 style="text-align:center">Edit</h1>
                    <div style="display:inline">                            
                        <div style="display:inline-text">
                            <h2>Cover image</h2>
                            <span style="position: relative;">
                                <img id="podcast-upload-image-preview" src="<?php echo podcast_cover_image($podcast); ?>"></img>    
                            <span style="position:absolute;margin-left:2px;width:100%;">
                                <label>400x400px recommended</label>
                                <input type="file" id="image-uploader" class="button-class-1" name="file[]" accept="image/png, image/jpg, image/jpeg" type="file"></input>                                    
                            <button id="image-save" style="margin-top:10px" class="button-class-2">SAVE</button>
                            </span>
                        </span>
                    <br/>                            
                    <h2>Edit Title</h2>
                    <input type="text" value="<?php echo $podcast['title']; ?>"id="upload-title-area"></input>
                <button id="title-save" class="button-class-2">SAVE</button>
                <br/>
                <h2>Edit Description</h2> 
                <textarea id="upload-description-area" rows='10'><?php echo $podcast['description']?></textarea> 
                <br/>
                <button id="desc-save" class="button-class-2">SAVE</button>
                <h2>Edit Tags</h2>
                <p>Seperated by spaces</p>
                <input type="text" value="<?php echo $podcast['tags']; ?>" id="upload-tags-area"></input>
            <button id="tags-save" class="button-class-2" style="margin-left:15px">SAVE</button>
            <div style="text-align:center">
                <br/><br/>
                <button id="finish-button" class="button-class-2" style="width:50%">DONE</button>
                <br/><br/>
                <button id="delete-button"class="button-class-2" style="background-color:gray;border-color:black">DELETE</button>
            </div>                    
        </div>                                
        </div>  
    <!--End Content here---->
    </div>
<?php include 'phpreq/footer.php' ?>
<script src="js/main.js"></script>
<script src="js/edit.js"></script>        
</body>
</html>
