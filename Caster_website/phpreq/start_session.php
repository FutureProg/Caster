<?php
error_reporting(E_ALL);
session_start();
if(!isset($_SESSION['user_id'])){   
    if(filter_input(INPUT_COOKIE, 'user_id') !== null && filter_input(INPUT_COOKIE, 'username') !== null){        
        $_SESSION['user_id'] = filter_input(INPUT_COOKIE, 'user_id');
        $_SESSION['username'] = filter_input(INPUT_COOKIE, 'username');
    }
}