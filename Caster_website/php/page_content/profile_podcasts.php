<?php

include '../php_vars.php';

if(filter_input(INPUT_POST,"q") == "PDCST"){
    loadPodcasts();
}

//podcast podcast_id, image, title, description, user_id, username
function createPodcastBar($podcast,$username){    
    echo "<div class=podcast-bar>";
    echo "<div class='image-container' onclick=playSound($podcast[0],'".str_replace(' ','\%20',addslashes($podcast[5]))."');>";
    echo "<img src='/users/".$podcast[1]."/images/podcast/".$podcast[7]."'/>";
    echo " <p style='text-decoration: underline;text-style:bold;'>".$podcast[4]."</p>";
    echo "</div>";
    echo "<div class='description'>";
    echo "<p>$podcast[5]<p><br/>";
    if(filter_input(INPUT_POST,"opt") != null && filter_input(INPUT_POST,"opt") == "MYPROF"){
        echo "<p>by: <a href='profile.php?user=$podcast[1]'>$username</a><br/> length: ".secondsToTime($podcast)."<br/> uploaded ".dateDifference($podcast)." ago <br/><br/>";    
        echo "<a href='/edit.php?id=".$podcast[0]."'>Edit</a>";
        echo "</p>";
    }
    else{
        echo "<p>by: <a href='profile.php?user=$podcast[1]'>$username</a><br/> length: ".secondsToTime($podcast)."<br/> uploaded ".dateDifference($podcast)." ago</p>";     
    }
    echo "</div>";
    echo "</div>";
}

function loadPodcasts(){
    $userid = filter_input(INPUT_POST,"u");
    $link = mysqli_connect(DB_HOST,DB_USER,DB_PASSWORD,DB_NAME) or die("Unable to connect to the database");
    $query = "SELECT * FROM `".TABLE_PODCASTS."` WHERE `user_id`=".$userid;
    $result = mysqli_query($link,$query) or die("Error querying the database: ".mysqli_errno($link)."=>".mysqli_error($link));    
    mysqli_close($link);
    $username = username($userid);    
    if(mysqli_num_rows($result) >= 1){                
        while($row = mysqli_fetch_row($result)){
            createPodcastBar($row,$username);            
        }
    }
}

function secondsToTime($podcast){
    $seconds = $podcast[3];
    $hours = floor($seconds/3600);
    $mins = floor(($seconds-($hours * 3600))/60);
    $seconds = floor($seconds - ($hours*3600) - ($mins*60));
    if($hours > 0){
        return "$hours:$mins:$seconds";
    }
    else if($mins > 0){
        return "$mins:$seconds";
    }
    else{
        return "0:$seconds";
    }
}

function dateDifference($podcast){
    date_default_timezone_set('UTC');
    $podcast_date = new DateTime($podcast[2]);
    $now = new DateTime(date("Y-m-d"));
    $delta = $now->diff($podcast_date,true);
    if ($delta->y > 0){
        $end = "";
        if($delta->y > 1) $end="s";
        return $delta->y." year".end;
    }
    else if($delta->m > 0){
        $end = "";
        if($delta->m > 1) $end="s";
        return $delta->m." month".$end;
    }
    else if($delta->d == 0){
        return "under 24 hours";
    }
    else{
        $end = "";
        if($delta->d > 1) $end="s";
        return $delta->d." day".$end;
    }
}

function username($userid){    
    $link = mysqli_connect(DB_HOST,DB_USER,DB_PASSWORD,DB_NAME) or die("Error connecting to server");
    $query = "SELECT * FROM `".TABLE_USERS."` WHERE `user_id`=".$userid;    
    $result = mysqli_query($link,$query) or die("Error querying database");
    if(mysqli_num_rows($result) >= 1){
        $row = mysqli_fetch_array($result);
        return $row['username'];
    }else{
        return "UNDEFINED";
    }
    mysqli_close($link);
}

?>
