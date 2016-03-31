<?php

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
define("DB_HOST", "127.0.0.1");
define("DB_USER", "root");
define("DB_PASSWORD", "Caster_2015");
define("DB_NAME", "caster_db");

define("TABLE_USERS","user_list");
define("TABLE_COMMENTS","Comment_List");
define("TABLE_PODCASTS","podcast_list");
define("TABLE_TOKENS","token_list");

define("MAX_PROFILE_PIC_SIZE",200000);
define("MAX_PODCAST_IMG_SIZE",200000);

function randomString($length = 8){
    $characters = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    $charactersLength = strlen($characters);
    $re = "";
    for($i = 0; $i < $length;$i++){
        $re .= $characters[rand(0,$charactersLength-1)];
    }
    return $re;
}

if(isset($_SESSION['user_id'])){?>
<script>
    signedIn = true;
</script>
<?php}
