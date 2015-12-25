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
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
        <link href="style.css" rel="stylesheet"/>
        <link rel="icon" href="images/logo.png" sizes="32x32" type="image/png"/>
        <title>Caster</title>
    </head>
    <body>
        <?php include 'php/audio_player.php'; ?>
        <div id="body-container">
            <?php include 'phpreq/topbar.php';?>
            <div id="main-content">
                <!--Start Content Here-->
                <div id="content-container" style="margin:10px;margin-left:0;top:90px;height:80%;">
                    <div style="background-color:white;margin: 20px 20px;border: 1px solid black;">
                        <div style="text-align:center;padding: 30px 0">
                            <img src="images/title.png" style="width:25%;"/>
                            <h1>Welcome to Caster!</h1>
                            Caster is a website dedicated to helping people share their stories with the world. <br/>
                            Create a podcast, upload it, share it. <br/> Cast your voice.
                        </div>
                        <hr/>
                        <div>
                            <?php if(isset($_SESSION['user_id'])){ ?>
                            <h2 style="text-align:center">Subscriptions</h2>
                            <div id="subscriptions">
                            </div>
                            <?php } ?>
                            <h2 style="text-align:center">Recent Uploads</h2>
                            <div id="recent-uploads">

                            </div>
                        </div>
                    </div>
                            <script src="js/index.js"></script>
                    <?php include 'phpreq/footer.php'; ?>
                </div>
                <!--End Content Here------>

            </div>

        </div>
    </body>
    <script src="js/main.js"></script>
</html>
