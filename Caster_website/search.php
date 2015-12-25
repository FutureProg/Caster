<?php
include_once 'phpreq/start_session.php';
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
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
        <link href="style.css" rel="stylesheet"/>
        <title>Caster-Search Results</title>
        <link rel="icon" href="images/logo.png" sizes="32x32" type="image/png"/>
    </head>
    <body>
        <?php include 'php/audio_player.php'; ?>
        <div id="body-container">
            <?php include 'phpreq/topbar.php';?>
            <div id="main-content">
                <div id="content-container" style="margin:10px;margin-left:0;top:90px;height:80%;">
                    <div id="search-page">
                    </div>
                    <?php include 'phpreq/footer.php'; ?>
                    <script type='text/javascript'>
                        document.title = "Caster - Search";
                    </script>
                    <script src="js/search.js"></script>
                </div>
            </div>
        </div>
        <script src="js/main.js"></script>
        <script src="js/search.js"></script>
    </body>
</html>
