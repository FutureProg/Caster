<?php
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

include_once 'phpreq/start_session.php';
if(isset($_SESSION['user_id'])){
  header("/index.php");
}
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
        <link rel="icon" href="images/logo.png" sizes="32x32" type="image/png"/>
        <title>Caster-Sign Up</title>
    </head>
    <body>
        <div id="body-container">
          <div id="top-bar">
              <a href='/index.php'><img src="../images/title.png"/></a>
              <div id="search-form">
                  <label>
                      <input id="search-box" class="tb" size="50" type="text"/>
                  </label>
                  <button id="search-button" type="submit">Search</button>
              </div>
              <div id="button-set" style="margin-right: 10px;">
                  <button class="top-bar-buttons" onclick="window.location.href='opinion.php';" style="margin-left:50px">!?   </button>
                  <?php if(!isset($_SESSION['user_id'])){ ?>
                          <button class="top-bar-buttons" onclick="window.location.href='login.php';" style="margin-left:50px;">Log in</button>
                          <button class="top-bar-buttons" onclick="window.location.href='signup.php'" style="margin-left:50px;">Sign up</button>
                  <?php }else{?>
                          <button class="top-bar-buttons" onclick="loadPage('profile.php<?php echo "?user=".$_SESSION['username'];?>');" ><?php echo $_SESSION['username']; ?></button>
                          <button class="top-bar-buttons" onclick="window.location.href='/upload.php'">Upload</button>
                          <button class="top-bar-buttons" onclick="window.location.href='../php/signout.php'">Sign Out</button>
                  <?php } ?>
              </div>
          </div>
            <!--Start Content here-->
            <div id="content-container" style="margin:10px;margin-left:0;top:70px;height:80%;">
                <div id="sign_up_container">
                    <div id="sign_up_form">
                        <h1 style="margin:0;">Sign Up</h1>
                        <br/>
                        <div><label>Already a member? Login <a href="login.php">here</a>!</label></div>
                        <br/>
                        <label id="fill_all_error" class="error-label"></label><br/>
                        <label><h2>*Username: </h2></label><br/>
                        <input style="width:20em;" class="text-input-style-2" type="text" name="user_name" id="user_name_form" />
                        <label id="user_name_error" class="error-label"></label>
                        <br/><br/>
                        <label><h2>*Password:</h2></label><br/>
                        <input style="width:20em;" class="text-input-style-2" type="password" name="password" id="password_form"/>
                        <label id="password_error" class="error-label"></label>
                        <br/><br/>
                        <label><h2>*Password Again:</h2></label><br/>
                        <input style="width:20em;" class="text-input-style-2" type="password" name="password_again" id="password_form_2"/>
                        <br/><br/>
                        <label><h2>*email:</h2></label><br/>
                        <input style="width:20em;" class="text-input-style-2" type="email" name="email" id="email_form"/>
                        <label id="email_error" class="error-label"></label>
                        <br/><br/>
                        <button style="width:50%;margin-top:3em;"class="button-class-2" onclick="submitForm()">Submit</button>
                    </div>
                    <div id="side-image" style="padding:5% 10%">
                        <img width="50%" src="images/title.png">
                        <p style="font-size:1.5em">Caster is a website dedicated to helping people share their stories with the world. <br/>
                            Create a podcast, upload it, share it. <br/> Sign up today. Cast your voice. </p>
                    </div>
                </div>
                <?php include 'phpreq/footer.php' ?>
            </div>
            <!--End Content here---->
        </div>
        <script src="js/signup.js"></script>
        <script>
        $("#search-button").click(function(evt){
            window.location.href='search.php?q='+$("#search-box").val();
        });
        </script>
    </body>
</html>
