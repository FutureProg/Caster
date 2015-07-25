<?php
include 'phpreq/start_session.php';
?>
<html>
    <head>
        <meta charset="UTF-8">
        <meta name="author" content="Nickolas Morrison">
        <meta name="description" content="Cast your voice">
        <script src="jquery/jquery/jquery.min.js"></script>
        <link href="style.css" rel="stylesheet"/>
        <link rel="icon" href="images/logo.png" sizes="32x32" type="image/png"/>
        <title>Caster - Settings</title>
    </head>
    <body>
        <div id="body-container">
            <?php include 'phpreq/topbar.php';?>
            <div id="main-content">
                <div id="content-container" style="margin:10px;margin-left:0;top:90px;height:80%;">                    
                    <div id="settings-container">           
                        <?php if(isset($_SESSION['user_id'])){ ?>
                        <h1 style="text-align:center">Settings</h1>
                        <p id="error-label" style="color:red"></p>
                        <div style="display:inline">                                                        
                            <h2>E-mail</h2>
                            <label>Enter new e-mail: </label><input type="email" id="settings-email" class="button-class-1"/><br/>                                               
                            <h2>Reset Password</h2>                            
                            <label>Enter new password: </label><input type="password" id="settings-password" class="button-class-1"/><br/><br/>
                            <label>Confirm password:  </label><input type="password" id="settings-password-2" class="button-class-1"/><br/>                                 
                        </div>
                        <br/><br/>
                        <div style="text-align:center">
                            <button class="button-class-2" id="submit-button" style="width:50%">SUBMIT</button><br/><br/>
                            <button id="delete-button" class="button-class-2" style="background-color:gray;border-color:black">DELETE ACCOUNT</button>
                        </div>
                        <?php }else{ ?>
                        <script>window.location.href="login.php"</script>
                        <?php } ?>
                    </div>                     
                    <?php include 'phpreq/footer.php' ?>
                </div>
            </div>            
        </div>   
        <script src="js/main.js"></script>
        <script src="js/settings.js"></script>
    </body>
</html>