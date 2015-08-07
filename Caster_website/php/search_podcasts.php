<?php
include_once '../phpreq/start_session.php';
include 'php_vars.php';

if(filter_input(INPUT_GET,"q") !== null){
    loadPodcasts();
    return;
}
elseif(filter_input(INPUT_POST,"t") == "RU"){
    recentPodcasts();
}
elseif(filter_input(INPUT_POST,"t") == "SP"){
    subsPodcasts();
}
elseif(filter_input(INPUT_POST,"t") == "RUJSON"){
    print recentPodcastsJson();
}

function createPodcastBar($podcast,$username){
    echo "<div class='podcast-bar' title='$podcast[4]' id='$podcast[0]'>";
    echo "<div class='image-container' onclick=playSound($podcast[0],'".str_replace(' ','\%20',addslashes($podcast[5]))."');>";
    echo "<img src='/users/".$podcast[1]."/images/podcast/".$podcast[7]."'/>";
    echo " <p style='text-decoration: underline'>".$podcast[4]."</p>";
    echo "</div>";
    echo "<div class='description'>";
    echo "<p>$podcast[5]<p><br/>";
    echo "<p>by: <a href='profile.php?user=$podcast[1]'>$username</a><br/> length: ".secondsToTime($podcast)."<br/> uploaded ".dateDifference($podcast)." ago</p>";     
    echo "</div>";
    echo "</div>";
}

function subsPodcasts(){
    $userid = $_SESSION['user_id'];
    $link = mysqli_connect(DB_HOST,DB_USER,DB_PASSWORD,DB_NAME);
    $query = "SELECT `subscriptions` FROM `".TABLE_USERS."` WHERE `user_id`=$userid";
    $result = mysqli_query($link,$query) or die("Error querying database");
    $list = explode('.',mysqli_fetch_row($result)[0]);
    foreach($list as $user){
        $user = intval($user);
        $query = "SELECT * FROM `".TABLE_PODCASTS."` WHERE `user_id`=$user ORDER BY `post_date` DESC LIMIT 5";
        $result = mysqli_query($link,$query) or die("Error querying database:");
        $username = username($user);
        if(mysqli_num_rows($result) >= 1){
            echo "<div style=width:100%;text-align:center;cursor:pointer' onclick=loadPage('profile.php?user=$user') ><img src=/users/$user/images/".userpicture($user)." style='display:inline-block;width:20px;height:20px;'>";
            echo "<h3 style=display:inline-block; class=subscription-user-title>$username</h3>";
            echo "</div><br/>";
            while($row = mysqli_fetch_row($result)){                                
                createPodcastBar($row,$username);
            }
        }
    }
    mysqli_close($link);
}

function recentPodcasts(){
    $link = mysqli_connect(DB_HOST,DB_USER,DB_PASSWORD,DB_NAME) or die("Unable to connect to the database");    
    $query = "SELECT * FROM `".TABLE_PODCASTS."` ORDER BY `post_date` DESC LIMIT 5";        
    $result = mysqli_query($link,$query) or die("Error querying the database: ".mysqli_errno($link)." : ".mysqli_error($link));    
    mysqli_close($link);    
    if(mysqli_num_rows($result) >= 1){          
        while($row = mysqli_fetch_row($result)){
            $userid = $row[1];
            $username = username($userid);    
            createPodcastBar($row,$username);            
        }
    }
    else{
        echo "<h3 style='text-align:center;font-style:bold;'>NO RESULTS FOUND</h3>";        
    }
}

function recentPodcastsJson(){
    $link = mysqli_connect(DB_HOST,DB_USER,DB_PASSWORD,DB_NAME) or die("Unable to connect to the database");    
    $query = "SELECT * FROM `".TABLE_PODCASTS."` ORDER BY `post_date` DESC LIMIT 5";        
    $result = mysqli_query($link,$query) or die("Error querying the database: ".mysqli_errno($link)." : ".mysqli_error($link));
    mysqli_close($link);
    $re = array();
    while($r = mysqli_fetch_array($result)){
        $re[] = $r;
    }
    return json_encode($re);
}

function loadPodcasts(){    
    $search = str_replace("%20"," ",filter_input(INPUT_GET,"q"));
    $search = trim($search);
    if($search == ""){
        echo "<h3 style='text-align:center;font-style:bold;'>NO RESULTS FOUND</h3>";        
        return;
    }    
    $list = explode(' ',$search);
    $str = join('* +',$list);
    $link = mysqli_connect(DB_HOST,DB_USER,DB_PASSWORD,DB_NAME) or die("Unable to connect to the database");    
    $query = "SELECT *,MATCH(tags) AGAINST('*$str+') AS `relevance` FROM `".TABLE_PODCASTS."` WHERE MATCH(tags) AGAINST('+$str*') ORDER BY relevance DESC;";        
    /*$sqlOpt = array();
    foreach($list as $key){
        $sqlOpt[] = "tags LIKE '%".addslashes($key)."%'";
    }
    $query .= " ".join(' OR ',$sqlOpt).";";*/    
    $result = mysqli_query($link,$query) or die("Error querying the database: ".mysqli_errno($link));    
    mysqli_close($link);    
    if(mysqli_num_rows($result) >= 1){                
        while($row = mysqli_fetch_row($result)){
            $userid = $row[1];
            $username = username($userid);    
            createPodcastBar($row,$username);            
        }
    }
    else{
        echo "<h3 style='text-align:center;font-style:bold;'>NO RESULTS FOUND</h3>";        
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

function userpicture($userid){
    $link = mysqli_connect(DB_HOST,DB_USER,DB_PASSWORD,DB_NAME) or die("Error connecting to server");
    $query = "SELECT * FROM `".TABLE_USERS."` WHERE `user_id`=".$userid;    
    $result = mysqli_query($link,$query) or die("Error querying database");
    if(mysqli_num_rows($result) >= 1){
        $row = mysqli_fetch_array($result);
        return $row['picture'];
    }else{
        return "UNDEFINED";
    }
    mysqli_close($link);
}