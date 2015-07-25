<?php
include_once $_SERVER['DOCUMENT_ROOT'].'/phpreq/start_session.php';
include_once 'php_vars.php';


forgotPassword();
function forgotPassword(){
    $email = trim(filter_input(INPUT_POST,"e"));
    $link = mysqli_connect(DB_HOST,DB_USER,DB_PASSWORD,DB_NAME) or die("Unable to connect to the database");    
    $opass = randomString();
    $npass = password_hash($opass,PASSWORD_DEFAULT);
    $query = "UPDATE `".TABLE_USERS."` SET `password`='$npass' WHERE `email`='$email'";
    $result = mysqli_query($link,$query) or die("Unable to query the server");
    if(mysqli_affected_rows($link) == 1){
        $to = $email;
        $subj = "Caster Password Reset";
        $headers = "From: caster.noreply@caster.media \r\n";
        $headers .= "MIME-Version: 1.0\r\n";
        $headers .= "Content-Type: text/html; charset=ISO-8859-1\r\n";
    
        $msg = "<html><body>";        
        $msg .= "<p>Hello,<br/><br/> Your password has been temporarily reset to <strong>$opass</strong>.<br/>";
        $msg .= "To change your password <a href='http://".$_SERVER['DOCUMENT_ROOT']."/login.php'>login</a> using the above password and".
                " go to your user settings to change it.<br/><br/>";
        $msg .= "Sincerely, <br/><br/> Your Friends at Caster<br/>";
        $msg .= "</body></html>";
        mail($to,$subj,$msg,$headers);
        mysqli_close($link);
        header('Location: ../iforgot_success.php?e='.$email);
        exit();
    }else{
        header('Location: ../iforgot.php?e=1');
        exit();
    }
}