<?php
/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
include '../phpreq/start_session.php';
include 'php_vars.php';


login();

function login(){    
    $email = trim(filter_input(INPUT_POST, "e"));
    $password = trim(filter_input(INPUT_POST,"p"));    
    $link = mysqli_connect(DB_HOST,DB_USER,DB_PASSWORD,DB_NAME) or die("Error connecting to the database");
    $query = "SELECT * FROM `".TABLE_USERS."` WHERE `email`='".$email."'";
    $result = mysqli_query($link, $query) or die("Error querying database");
    if(mysqli_num_rows($result) >= 1){
        $row = mysqli_fetch_array($result);
        $hashpass = $row['password'];
        if(password_verify($password,$hashpass)){
            $_SESSION['user_id'] = $row['user_id'];
            $_SESSION['username'] = $row['username'];
            setcookie('user_id',$row['user_id'],time() + (60 * 60 * 24 * 30));
            setcookie('username',$row['username'],time() + (60 * 60 * 24 * 30));                      
            header('Location: ../index.php');
            exit();    
            return;
        }else{
            header('Location: ../login.php?e=1');   
            exit(); 
            return;
        }
    }else{
        header('Location: ../login.php?e=1');   
        exit();    
        return;
    }    
}