<?php

/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

session_start();
if(isset($_SESSION['user_id'])){
    unset($_SESSION);    
    setcookie('username', '0', 1);
    setcookie('user_id', '0', 1);
}
session_destroy();
header('Location: ../index.php');
exit();