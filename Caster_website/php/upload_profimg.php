<?php
include '../phpreq/start_session.php';
include 'php_vars.php';

if(isset($_GET["PROFIMG"])){    
    setImage();    
}

function setImage() {    
    if($_FILES[0]['size'] > MAX_PROFILE_PIC_SIZE){
        echo "TOOLARGE";
        return;
    }    
    $picture = $_FILES[0]['name'];    
    if($picture == ''){
        echo "OKAY";
        return;
    }
    if (sizeof($_FILES[0]) > 0 && $_FILES[0]['error'] == 0 && $_FILES[0]['size'] <= MAX_PROFILE_PIC_SIZE) {
        $basename = basename($_FILES[0]['tmp_name']);
        $imgloc = "../users/".$_SESSION['user_id']."/images/";                
        if(!file_exists($imgloc)){
            mkdir($imgloc,0777,true) or die("Unable to create directory");            
        }        
        $picture = $basename . $picture;
        $destination = $imgloc . $picture;  
        $prevImgName = prev_profile_pic();
        $prevImg = $imgloc . $prevImgName;              
        if(file_exists(realpath($prevImg)) && $prevImgName != "" && $prevImgName != "default.png"){            
            $res = unlink($prevImg) or die("ERROR DELETING PREV PROFILE IMG: " + $prevImg);
            if(!$res){
                echo "ERROR DELETING PREV PROFILE IMG: " + $prevImg;
                return;
            }                     
        }
        if (move_uploaded_file($_FILES[0]['tmp_name'], $destination)) {
            $link = mysqli_connect(DB_HOST, DB_USER, DB_PASSWORD, DB_NAME) or die('Error connecting to server');
            $userid = $_SESSION['user_id'];
            $query = "UPDATE " . TABLE_USERS . " SET picture='$picture' WHERE user_id='$userid'";
            $result = mysqli_query($link, $query) or die('Error updating Profile Picture');
            echo "OKAY";
            mysqli_close($link);
        } else {
            echo "Error uploading image";
        }
    } else {
        echo "Invalid Image: " . $picture . " LENGTH OF FILES: " . print_r($_FILES);
    }
}

function prev_profile_pic(){
    $userid = $_SESSION["user_id"];
    $link = mysqli_connect(DB_HOST,DB_USER,DB_PASSWORD,DB_NAME) or die("Error connecting to server");
    $query = "SELECT `picture` FROM `".TABLE_USERS."` WHERE `user_id`=".$userid;
    $result = mysqli_query($link,$query) or die("Error queerying the database");
    if(mysqli_num_rows($result) >= 1){
        $row = mysqli_fetch_array($result);
        return $row['picture'];        
    }
    else{
        return "UNDEFINDED";
    }
    mysqli_close($link);        
}