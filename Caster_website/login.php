<?php

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
include 'phpreq/start_session.php';
if(isset($_SESSION['user_id'])){
  header("/index.php");
}
?>

<html>
    <head>
        <meta charset="UTF-8">
        <meta name="author" content="Nickolas Morrison">
        <meta name="description" content="Share your podcasts with the world.">
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js"></script>
        <link href="style.css" rel="stylesheet"/>
        <link rel="icon" href="images/logo.png" sizes="32x32" type="image/png"/>
        <title>Caster-Login</title>
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
            <div id="content-container" style="margin:10px;margin-left:0;top:70px;height:80%;">
                <div id="login_container">
                    <form id="login_form" action="php/signin.php" method="post">
                        <h1 style="text-align:center">Login</h1>
                        <div style="text-align:center"><label >Not a member? Create an account <a href="signup.php">here</a>!</label></div>
                        <div style="text-align:center"><label >Forgot your password? Recover it <a href="iforgot.php">here</a>.</label></div>
                        <label id="fill_all_error" class="error-label">
                            <?php if(filter_input(INPUT_GET,"e") !== null){
                                echo "Incorrect email \\ password";
                            }?>
                        </label><br/>
                        <label><h2>*email:</h2></label>
                        <input type="email" name="e" id="email_form" class="text-input-style-1"/>
                        <br/><br/>
                        <label><h2>*Password: </h2></label><br/>
                        <input type="password" name="p" id="password_form" class="text-input-style-1"/>
                        <br/><br/>
                        <input style="width:45em;margin-top:1em;" type="submit" class="button-class-2"/>
                    </form>
                </div>
                <?php include 'phpreq/footer.php' ?>
                <script src="js/main.js"></script>
                <script>
                $("#search-button").click(function(evt){
                    window.location.href='search.php?q='+$("#search-box").val();
                });
                </script>
            </div>
        </div>
    </body>
</html>
