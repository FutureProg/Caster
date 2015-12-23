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
        <link rel="icon" href="images/logo.png" sizes="32x32" type="image/png"/>
        <title>Caster</title>
    </head>
    <body>
        <?php include 'php/audio_player.php'; ?>
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
            <div id="main-content">
                <!--Start Content Here-->
                <div id="content-container" style="margin:10px;margin-left:0;top:90px;height:80%;">
                    <div style="background-color:white;margin: 20px 20px;border: 1px solid black;min-height:100%;">
                        <form style="text-align:center;" method="post" action="php/o.php">
                            <h1>!?</h1>
                            <p>
                                Have a suggestion to make? Did you happen to find a bug? Wanna just say, "this is awesome!"?<br/>
                                Leave us a message below, just select the category and maybe provide an email address* for follow up.
                            </p>
                            <select>
                                <option value="SUGGESTION">Suggestion</option>
                                <option value="BUG">Bug</option>
                                <option value="OPINION">Opinion</option>
                            </select><br/><br/>
                            <label>Email: </label><input type="text" style="border: 1px solid black;"><br/><br/>
                            <textarea rows=10 cols=50 style="font-size:1em"></textarea><br/>
                            <button class="button-class-2">Submit</button>
                            <br/><br/><br/><br/>
                            <small>
                                *The provided email address will remain confidential,
                                meaning that we will not share it with any third parties.<br/>
                                By providing an email address, you give us permission to send you a follow up for any suggestion
                                or bug you report.
                            </small>
                        </form>
                    </div>
                            <script src="js/index.js"></script>
                    <?php include 'phpreq/footer.php'; ?>
                </div>
                <!--End Content Here------>

            </div>

        </div>
    </body>
    <script src="js/main.js"></script>
    <script>
      $("#search-button").click(function(evt){
          window.location.href='search.php?q='+$("#search-box").val();
      });
    </script>
</html>
