<?php

include_once $_SERVER['DOCUMENT_ROOT'].'/phpreq/start_session.php';
include_once $_SERVER['DOCUMENT_ROOT'].'/php/user_info.php';
include_once $_SERVER['DOCUMENT_ROOT'].'/php/php_vars.php';


function podcastBoxByID($podcast_id){
	$link = mysqli_connect(DB_HOST,DB_USER,DB_PASSWORD,DB_NAME) or die("Error connecting to the server");
    $query = "SELECT * FROM `".TABLE_PODCASTS."` WHERE `podcast_id`=$podcast_id";
    $result = mysqli_query($link,$query) or die("Error querying the database");
    mysqli_close($link);
    if(mysqli_num_rows($result) == 1){
         return podcastBox(mysqli_fetch_array($result));
    }
    return null;
}

function podcastBox($podcast){
	$re = "<div class='podcast-object'>\n";
	$re .= "<div class='podcast-info'>\n";
	$re .= $podcast['description']."\n";
	$re .= "</div>\n";
//	$re .="<img class='podcast_image' src='/images/dummy_podcast_img.jpg'/>
	$re .= "<img class='podcast_image' src='/users/".$podcast['user_id']."/images/podcast/".$podcast['image_file']."'/>\n";
	$re .= "</div>\n";
	return $re;
}

echo '<head>';
echo '<link href="/style.css" rel="stylesheet"/>';
echo '<script src="/jquery/jquery/jquery.min.js"></script>';
echo '</head>';
echo '<body>';
echo "<div id='wplt'>\n";
echo "<div id='wplt-inner'>\n";
echo podcastBoxByID(3);
echo "</div></div>";
echo '<script>$("#wplt #wplt-inner .podcast-object .podcast-info").slideUp(0);$("#wplt #wplt-inner .podcast-object").hover(function(){
    $(this).children(".podcast-info").slideDown("fast");
},
function(){
    $(this).children(".podcast-info").slideUp("fast");
});</script>';
echo '</body>';