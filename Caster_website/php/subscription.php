<?php
include_once '../phpreq/start_session.php';
include_once 'php_vars.php';

$q = filter_input(INPUT_POST,"q");
if(!isset($_SESSION['user_id']) && !($q == "MOBI"))
    return;
if($q == "SUB"){
    subscribe();
}
else if($q == "UNSUB"){
    unsubscribe();
}
else if($q == "CHECK"){
    check();
}
else if($q == "MOBI"){
	$t = filter_input(INPUT_POST, "t");
	if($t == "SUB"){
		print mobi_sub();
	}
	else if($t == "UNSUB"){
		print mobi_unsub();
	}
	else if($t == "CHECK"){
		print mobi_check();
	}
}
function check(){
    $subscribe_id = filter_input(INPUT_POST,"s");
    $user_id = $_SESSION['user_id'];    
    $link = mysqli_connect(DB_HOST,DB_USER,DB_PASSWORD,DB_NAME) or die("Error connecting to server");
    $query = "SELECT `subscriptions` FROM `".TABLE_USERS."` WHERE `user_id`=".$user_id;        
    $result = mysqli_query($link,$query) or die("Error querying the server");    
    if(mysqli_num_rows($result) >= 1){
        print "OKAY ";
        $row = mysqli_fetch_array($result);   
        $string = $subscribe_id.".";
        print "/".strpos($row['subscriptions'],$string)."/";        
        if(strpos($row['subscriptions'],$string) !== FALSE){
            print "YES";
        }
        else{
            print "NO";   
        }
    }
    else{
        print "NO";   
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
        print "OKAY";   
    }
    else{
        print "NO";   
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
            print "YES";   
        }
        else{
            print "NO";   
        }
    }
    else{
        print "NO";   
    }
    mysqli_close($link);
}

function mobi_sub(){
	$subscribe_id = filter_input(INPUT_POST,"s");
    $user_id = filter_input(INPUT_POST, "u");
    $link = mysqli_connect(DB_HOST,DB_USER,DB_PASSWORD,DB_NAME) or die("Error connecting to server");    
    $query = "UPDATE `".TABLE_USERS."` SET subscriptions=CONCAT(subscriptions,'".$subscribe_id.".') WHERE `user_id`=".$user_id;   
    $result = mysqli_query($link,$query) or die ("Error querying the server");
    if(mysqli_num_rows($result) >= 1){
        return "OKAY";   
    }
    else{
        return "NO";   
    }
    mysqli_close($link);
}

function mobi_unsub(){
	$subscribe_id = filter_input(INPUT_POST,"s");
    $user_id = filter_input(INPUT_POST, "u");    
    $link = mysqli_connect(DB_HOST,DB_USER,DB_PASSWORD,DB_NAME) or die("Error connecting to the server");
    $query = "SELECT `subscriptions` FROM `".TABLE_USERS."` WHERE `user_id`=".$user_id;        
    $result = mysqli_query($link,$query) or die("Error querying the server");    
    if(mysqli_num_rows($result) >= 1){
        $row = mysqli_fetch_array($result);
        $newSubs = str_replace($subscribe_id.".","",$row['subscriptions']);
        $query = "UPDATE `".TABLE_USERS."` SET `subscriptions`='".$newSubs."'";        
        $result = mysqli_query($link,$query) or die("Error querying the server");
        if($result){
            return "YES";   
        }
        else{
            return "NO";   
        }
    }
    else{
        return "NO";   
    }
    mysqli_close($link);
}

function mobi_check(){
	$subscribe_id = filter_input(INPUT_POST,"s");
    $user_id = filter_input(INPUT_POST, "u");    
    $link = mysqli_connect(DB_HOST,DB_USER,DB_PASSWORD,DB_NAME) or die("Error connecting to server");
    $query = "SELECT `subscriptions` FROM `".TABLE_USERS."` WHERE `user_id`=".$user_id;        
    $result = mysqli_query($link,$query) or die("Error querying the server");    
    if(mysqli_num_rows($result) >= 1){
        $row = mysqli_fetch_array($result);   
        $string = $subscribe_id.".";
        if(strpos($row['subscriptions'],$string) !== FALSE){
            return "YES";
        }
        else{
            return "NO";   
        }
    }
    else{
        return "NO";   
    }
    mysqli_close($link);
}

?>