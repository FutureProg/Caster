<?php 
include_once $_SERVER['DOCUMENT_ROOT'].'/phpreq/start_session.php';
include_once 'php_vars.php';

$q = filter_input(INPUT_POST,"q");

if($q == "UN"){
    username();
}
else if($q == "SUB"){
    subscriptions();
}
else if($q == "IMG"){
    profile_pic();
}
else if($q == "UI"){
    user_id();
}
else if($q == "DESC"){
    description();
}
else if($q == "S_DESC"){
    set_description();
}
else if($q == "EDIT_EMAIL"){
    edit_email();
}
else if($q == "EDIT_PASS"){
    edit_password();
}
else if($q == "USR_JSN"){
    print user_json(filter_input(INPUT_POST,"id"));
}
else if($q == "UID"){
    userid();
}
else if($q == "UNAMETOUID"){
	print username_to_userid();
}

function user_json($userid){
    $link = mysqli_connect(DB_HOST,DB_USER,DB_PASSWORD,DB_NAME) or die("Error connecting to the server");
    $query = "SELECT * FROM `".TABLE_USERS."` WHERE `user_id`=$userid";
    $result = mysqli_query($link,$query) or die("Error quering the database");
    mysqli_close($link);
    return json_encode(mysqli_fetch_array($result));
}

function edit_email(){
    $userid = $_SESSION['user_id'];
    $email = filter_input(INPUT_POST,"val");
    $link = mysqli_connect(DB_HOST,DB_USER,DB_PASSWORD,DB_NAME) or die("Error conneting to the server");
    $query = "UPDATE `".TABLE_USERS."` SET `email`='$email' WHERE `user_id`=$userid";
    $result = mysqli_query($link,$query) or die("Error querying the database");
    echo "OKAY";
    mysqli_close($link);
}

function edit_password(){
    $userid = $_SESSION['user_id'];
    $password = filter_input(INPUT_POST,"val");
    $password = password_hash($password,PASSWORD_DEFAULT);
    $link = mysqli_connect(DB_HOST,DB_USER,DB_PASSWORD,DB_NAME) or die("Error conneting to the server");
    $query = "UPDATE `".TABLE_USERS."` SET `password`='$password' WHERE `user_id`=$userid";
    $result = mysqli_query($link,$query) or die("Error querying the database");
    mysqli_close($link);
    echo "OKAY";
}

function username(){
    $userid = filter_input(INPUT_POST,"id");
    $link = mysqli_connect(DB_HOST,DB_USER,DB_PASSWORD,DB_NAME) or die("Error connecting to server");
    $query = "SELECT `username` FROM `".TABLE_USERS."` WHERE `user_id`=".$userid;    
    $result = mysqli_query($link,$query) or die("Error querying database");
    if(mysqli_num_rows($result) >= 1){
        $row = mysqli_fetch_array($result);
        echo $row['username'];
    }else{
        echo "UNDEFINED";
    }
    mysqli_close($link);
}

function userid(){
    $username = filter_input(INPUT_POST,"name");
    $link = mysqli_connect(DB_HOST,DB_USER,DB_PASSWORD,DB_NAME) or die("Error connecting to server");
    $query = "SELECT `user_id` FROM `".TABLE_USERS."` WHERE `username`='$username'";    
    $result = mysqli_query($link,$query) or die("Error querying database");
    if(mysqli_num_rows($result) >= 1){
        $row = mysqli_fetch_array($result);
        echo $row['user_id'];
    }else{
        echo "-1";
    }
    mysqli_close($link);
}

function profile_pic(){
    $userid = filter_input(INPUT_POST,"id");
    $link = mysqli_connect(DB_HOST,DB_USER,DB_PASSWORD,DB_NAME) or die("Error connecting to server");
    $query = "SELECT `picture` FROM `".TABLE_USERS."` WHERE `user_id`=".$userid;
    $result = mysqli_query($link,$query) or die("Error queerying the database");
    if(mysqli_num_rows($result) >= 1){
        $row = mysqli_fetch_array($result);
        echo $row['picture'];        
    }
    else{
        echo "UNDEFINDED";
    }
    mysqli_close($link);        
}

function user_id(){
    if(isset($_SESSION['user_id'])){
        echo $_SESSION['user_id'];        
    }
    else{
        echo "NO";           
    }
}

function description(){
    $userid = filter_input(INPUT_POST,"id");
    $link = mysqli_connect(DB_HOST,DB_USER,DB_PASSWORD,DB_NAME) or die("Error connecting to server");
    $query = "SELECT `description` FROM `".TABLE_USERS."` WHERE `user_id`=".$userid;
    $result = mysqli_query($link,$query) or die("Error queerying the database");
    if(mysqli_num_rows($result) >= 1){
        $row = mysqli_fetch_array($result);
        echo $row['description'];        
    }
    else{
        echo "UNDEFINDED";
    }
    mysqli_close($link);
}

function set_description(){
    $userid = filter_input(INPUT_POST,"id");
    $desc = addslashes(filter_input(INPUT_POST,"desc"));
    $link = mysqli_connect(DB_HOST,DB_USER,DB_PASSWORD,DB_NAME) or die("Error connecting to server");
    $query = "UPDATE `".TABLE_USERS."` SET `description`='".$desc."' WHERE `user_id`=".$userid;
    $result = mysqli_query($link,$query) or die("Error queerying the database");
    echo "OKAY";
    mysqli_close($link);
}

function username_to_userid(){
	$username = filter_input(INPUT_POST, "id");
	$link = mysqli_connect(DB_HOST, DB_USER, DB_PASSWORD, DB_NAME) or die("Error connecting to the server");
	$query = "SELECT `user_id` FROM `".TABLE_USERS."` WHERE `username`='$username'";
	$result = mysqli_query($link, $query) or die("Error querying the database");
	$row = mysqli_fetch_array($result);
	return $row[user_id];
}
