<?php
include_once '../phpreq/start_session.php';
include_once 'php_vars.php';

if(!isset($_SESSION['user_id']))
    return;
$q = filter_input(INPUT_POST,"q");
if($q == "SUB"){
    subscribe();
}
else if($q == "UNSUB"){
    unsubscribe();
}
else if($q == "CHECK"){
    check();
}

function check(){
    $subscribe_id = filter_input(INPUT_POST,"s");
    $user_id = $_SESSION['user_id'];    
    $link = mysqli_connect(DB_HOST,DB_USER,DB_PASSWORD,DB_NAME) or die("Error connecting to server");
    $query = "SELECT `subscriptions` FROM `".TABLE_USERS."` WHERE `user_id`=".$user_id;        
    $result = mysqli_query($link,$query) or die("Error querying the server");    
    if(mysqli_num_rows($result) >= 1){
        echo "OKAY ";
        $row = mysqli_fetch_array($result);   
        $string = $subscribe_id.".";
        echo "/".strpos($row['subscriptions'],$string)."/";        
        if(strpos($row['subscriptions'],$string) !== FALSE){
            echo "YES";
        }
        else{
            echo "NO";   
        }
    }
    else{
        echo "NO";   
    }
    mysqli_close($link);
}

function subscribe(){
    $subscribe_id = filter_input(INPUT_POST,"s");
    $user_id = $_SESSION['user_id'];
    $link = mysqli_connect(DB_HOST,DB_USER,DB_PASSWORD,DB_NAME) or die("Error connecting to server");    
    $query = "UPDATE `".TABLE_USERS."` SET subscriptions=CONCAT(subscriptions,'".$subscribe_id.".') WHERE `user_id`=".$user_id;   
    $result = mysqli_query($link,$query) or die ("Error querying the server");
    if(mysqli_num_rows($result) >= 1){
        echo "OKAY";   
    }
    else{
        echo "NO";   
    }
    mysqli_close($link);
}

function unsubscribe(){    
    $subscribe_id = filter_input(INPUT_POST,"s");
    $user_id = $_SESSION['user_id'];    
    $link = mysqli_connect(DB_HOST,DB_USER,DB_PASSWORD,DB_NAME) or die("Error connecting to the server");
    $query = "SELECT `subscriptions` FROM `".TABLE_USERS."` WHERE `user_id`=".$user_id;        
    $result = mysqli_query($link,$query) or die("Error querying the server");    
    if(mysqli_num_rows($result) >= 1){
        $row = mysqli_fetch_array($result);
        $newSubs = str_replace($subscribe_id.".","",$row['subscriptions']);
        $query = "UPDATE `".TABLE_USERS."` SET `subscriptions`='".$newSubs."'";        
        $result = mysqli_query($link,$query) or die("Error querying the server");
        if($result){
            echo "YES";   
        }
        else{
            echo "NO";   
        }
    }
    else{
        echo "NO";   
    }
    mysqli_close($link);
}

?>