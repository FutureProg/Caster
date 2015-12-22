<?php

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
include './php_vars.php';

$q = filter_input(INPUT_POST, "q");

if($q === "USERNAME") {
    $data = filter_input(INPUT_POST, "data");
    checkUsername($data);
}
elseif ($q === "EMAIL") {
    $data = filter_input(INPUT_POST, "data");
    checkEmail($data);
}
elseif ($q == "SIGNUP") {
    $user = filter_input(INPUT_POST, "username");
    $password = filter_input(INPUT_POST, "password");
    $email = filter_input(INPUT_POST, "email");
    signUp($user,$password,$email);
}

function checkUsername($username) {
    $link = mysqli_connect(DB_HOST, DB_USER, DB_PASSWORD,DB_NAME) or die("Error: ".  mysqli_error($link));
    $query = "SELECT user_id FROM ".TABLE_USERS." WHERE username='".$username."'";
    $result = mysqli_query($link, $query) or die("Error Querying database ");
    mysqli_close($link);
    if(mysqli_num_rows($result) >= 1){
        echo "NO";
        return;
    }
    echo "OKAY";
}

function checkEmail($email){
    $link = mysqli_connect(DB_HOST, DB_USER, DB_PASSWORD,DB_NAME) or die("Error: ".  mysqli_error($link));
    $query = "SELECT user_id FROM ".TABLE_USERS." WHERE email='".$email."'";
    $result = mysqli_query($link, $query) or die("Error Querying database");
    mysqli_close($link);
    if(mysqli_num_rows($result) >= 1){
        echo "NO";
        return;
    }
    echo "OKAY";
}

function signUp($username,$password,$email){
    $link = mysqli_connect(DB_HOST, DB_USER, DB_PASSWORD, DB_NAME) or die("Error connecting to the server");
    $password = password_hash($password,PASSWORD_DEFAULT);
    $query = "INSERT INTO ".TABLE_USERS." VALUES (0,NOW(),'$username','$password','$email','','','','')";   
    $result = mysqli_query($link, $query) or die("Error querying database");
    mysqli_close($link);
    #$msg = "<html><body><p>Hello there! Welcome to <a href=''>Caster</a>!<p></body></html>";
    /*mail($email,"Welcome to Caster!",$msg,
         "To: $user <$email>\n".
         "From: Caster <Caster@caster.media>\n".
         "MIME-Version: 1.0\n".
        "Content-ype: text/html;charset=iso-8859-1");*/
    echo "OKAY";
    return;
}
