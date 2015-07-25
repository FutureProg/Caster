<?php

/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
include 'phpreq/start_session.php';
?>

<html>
    <head>
        <meta charset="UTF-8">
        <meta name="author" content="Nickolas Morrison">
        <meta name="description" content="Share your podcasts with the world.">
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js"></script>
        <link href="style.css" rel="stylesheet"/>
        <link rel="icon" href="images/logo.png" sizes="32x32" type="image/png"/>
        <title>Caster-Password Recovery</title>
    </head>
    <body>        
        <div id="body-container">            
            <?php include 'phpreq/topbar.php'; ?>
            <div id="content-container" style="margin:10px;margin-left:0;top:70px;height:80%;text-align:center">
                <div id="login_container">
                    <h1 style="text-align:center">Success!</h1>                                      
                    <p>A temporary password has been sent to your inbox at <?php echo filter_input(INPUT_GET,"e") ?>.<br/>
                        Please use it to login and then reset it in your user settings.</p>
                    <br/>
                    <p><small>Make sure to check your spam folder if it doesn't appear in the next five minutes</small></p>
                </div>
                <?php include 'phpreq/footer.php' ?>
            </div>            
        </div>        
    </body>
</html>