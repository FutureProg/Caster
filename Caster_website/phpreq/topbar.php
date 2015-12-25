<?php

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
?>
 <!--Start Top Bar here-->
    <div id="top-bar">
        <a href='index.php' class='inner-link'><img src="../images/title.png"/></a>
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
<!--End Top Bar here---->
