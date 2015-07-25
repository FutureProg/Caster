<?php
include 'phpreq/start_session.php';
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
        <meta name="description" content="Cast your voice">
        <script src="jquery/jquery/jquery.min.js"></script>
        <link href="style.css" rel="stylesheet"/>
        <title>Castr</title>
    </head>
    <body>
        <?php include 'php/audio_player.php'; ?>
        <div id="body-container">
            <?php include 'phpreq/topbar.php';?>
            <div id="main-content">
                <!--Start Content Here-->
<div id="content-container">
         <!--Start showcase------>
    <div id="showcase">
        <div id="showcase-inner">
            <div class="showcase-button showcase-left">
                &#8678;
            </div>
            <a class="showcase-object" href="">
                <img src="images/dummy_featured_img.jpg" class="showcase-image">                        
            </a>
            <div class="showcase-button showcase-right">
                &#8680;
            </div>
        </div>
    </div>
    <!--End Showcase-------->
        <!--Start WPLT Grid----->        
        <div id="wplt">
            <div class="circle-header"><h1></h1></div>
            <hr>
            <div id="wplt-inner">
                <div class="podcast-object">
                    <div class="podcast-info">
                        Lorem ipsum dolor sit amet, curabitur tellus ligula suspendisse adipiscing est
                    </div>
                    <img src="images/dummy_podcast_img.jpg" class="podcast_image">                           
                 </div>                        
                <div class="podcast-object">
                    <div class="podcast-info">
                        Info
                    </div>
                    <img src="images/dummy_podcast_img.jpg" class="podcast_image">                           
                 </div>
                <div class="podcast-object">
                    <div class="podcast-info">
                        Info
                    </div>
                    <img src="images/dummy_podcast_img.jpg" class="podcast_image">                           
                </div>  
                <div class="podcast-object">
                    <div class="podcast-info">
                        Info
                    </div>
                    <img src="images/dummy_podcast_img.jpg" class="podcast_image">                           
                </div>  
                <div class="podcast-object">
                    <div class="podcast-info">
                        Info
                    </div>
                    <img src="images/dummy_podcast_img.jpg" class="podcast_image">                           
                </div>                                  
            </div>
        </div>
        <!--End WPLT Grid----->                
        <script src="js/index.js"></script>
    <?php include 'phpreq/footer.php'; ?>
</div>
<!--End Content Here------>
           
            </div>
            
        </div>        
    </body>
    <script src="js/main.js"></script>
</html>
