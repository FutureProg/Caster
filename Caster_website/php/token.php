<?php
include_once $_SERVER['DOCUMENT_ROOT'].'/phpreq/start_session.php';
include_once $_SERVER['DOCUMENT_ROOT'].'/php/php_vars.php';

if(array_key_exists("q",$_POST) && filter_input(INPUT_POST,"q") == "TKN"){
    echo makeToken();
}

function makeToken(){
    $type = filter_input(INPUT_POST,"t");
    do{
        $token = randomString(20);
        $link = mysqli_connect(DB_HOST,DB_USER,DB_PASSWORD,DB_NAME) or die("Unable to connect to the database");
        $query = "SELECT * FROM `".TABLE_TOKENS."` WHERE `token_value`='$token'";
        $result = mysqli_query($link,$query) or die("Error querying the database");        
    }while(mysqli_num_rows($result) >= 1);
    $query = "INSERT INTO `".TABLE_TOKENS."` VALUES (0,NOW(),'$type','$token')";
    $result = mysqli_query($link,$query) or die("Error querying the database");      
    return $token;
}