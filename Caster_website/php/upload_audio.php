<?php
include 'php_vars.php';
include_once '../phpreq/start_session.php';
require_once('getid3/getid3.php');
include_once 'podcast.php';

$q = filter_input(INPUT_GET,"q");
if($q == "CHECK" && isset($_SESSION['user_id'])){
    check();
}
else if($q == "UPLOAD" && isset($_SESSION['user_id'])){
    upload();
}

function check(){
    $title = filter_input(INPUT_POST,"title");
    $link = mysqli_connect(DB_HOST,DB_USER,DB_PASSWORD,DB_NAME) or die("Error Connecting to Server!");
    $query = "SELECT * FROM `".TABLE_PODCASTS."` WHERE `title`=".$title." AND `user_id`=".$_SESSION['user_id'];
    $result = $link->query($query);
    mysqli_close($link);
    if(mysqli_num_rows($result) >= 1){
        echo 'TITLE';
        return;
    }
    echo "OKAY";
    return;
}

function upload(){
    $title = filter_input(INPUT_POST,"title");
    $description = filter_input(INPUT_POST,"description");
    $tags = filter_input(INPUT_POST,"tags");
    $sharing = filter_input(INPUT_POST,"sharing");
    $downloadable = filter_input(INPUT_POST,"downloadable");
    $category = filter_input(INPUT_POST,"category");
    if($sharing == "GLOBAL"){
        $downloadable = true;
    }
    //echo var_dump($_FILES);
    $picture_file = $_FILES["image_file"];
    $audio_file = $_FILES["podcast_file"];
    /*
    TODO: Make picture equal to a default image if not present
    $picture_file = "../images/default_podcast_img.png";
    */
    $link = mysqli_connect(DB_HOST, DB_USER, DB_PASSWORD, DB_NAME) or die('Error connecting to server');
    $duration = audioDuration($audio_file);
    $image = "";
    if(sizeof($picture_file) > 0 && $picture_file['error'] == 0 && $picture_file['size'] <= MAX_PROFILE_PIC_SIZE){
        if(($image = uploadFile($link,$picture_file,"image_file","../users/".$_SESSION['user_id']."/images/podcast/"))==null){
            echo "Error uploading podcast image";
            return;
        }
    }else{
        echo "Problem with Podcast image";
        return;
    }
    $audio = "";
    if(sizeof($audio_file) > 0 && $audio_file['error'] == 0){
        if(($audio = uploadfile($link,$audio_file,"audio_file","../users/".$_SESSION['user_id']."/audio/podcast/"))==null){
            echo "Error uploading podcast";
            return;
        }
    }else{
        echo "No audio file uploaded: ".$audio_file['error']." with size: ".sizeof($audio_file);
        return;
    }
    $userid = $_SESSION['user_id'];
    $query = "INSERT INTO `".TABLE_PODCASTS."` (`user_id`,`post_date`,`title`,`description`,`tags`,`image_file`,`audio_file`,`length`,`sharing`,`downloadable`,`category`,`urlid`) VALUES (".$userid.",NOW(),'".addslashes($title)."','".addslashes($description)."','".addslashes($tags)."','".addslashes($image)."','".addslashes($audio)."',$duration,'$sharing',$downloadable, '$category','".uniqid()."');";
    $result = mysqli_query($link,$query) or die("Error querying database: ".mysqli_errno($link).":".mysqli_error($link).":$query");
    if($sharing == "GLOBAL"){
        $podcastid = mysqli_insert_id($link);
        $podcast = get_podcast($podcastid);
        rss($userid,$podcast);
    }
    mysqli_close($link);
    echo "OKAY";
    return;
}

function audioDuration($file){
    $getID3 = new getID3;
    $path = $file['tmp_name'];
    $mixinfo = $getID3->analyze($path);
    //var_dump($mixinfo);
    $playtime = @$mixinfo['playtime_string'];
    $mins = 0;
    $secs = 0;
    list($mins,$secs) = explode(':',$playtime);
    return ($mins * 60) + $secs;
}

function uploadFile($link,$file,$column,$dst){
    $userid = $_SESSION['user_id'];
    $basename = basename($file['tmp_name']);
    $fileloc = $dst;
    if(!file_exists($fileloc)){
        mkdir($fileloc,0777,true) or die("Unable to create directory");
    }
    $filename = $basename . $file['name'];
    $destination = $fileloc . $filename;
    if (move_uploaded_file($file['tmp_name'], $destination)) {
        return $filename;
    } else {
        return null;
    }
}
