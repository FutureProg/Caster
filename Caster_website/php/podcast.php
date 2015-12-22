<?php

include_once $_SERVER['DOCUMENT_ROOT'].'/phpreq/start_session.php';
include_once $_SERVER['DOCUMENT_ROOT'].'/php/user_info.php';
include_once $_SERVER['DOCUMENT_ROOT'].'/php/php_vars.php';

$q = filter_input(INPUT_POST,"q");
if($q == "UPDATE_IMG"){
    update_image();
}
elseif ($q == "UPDATE"){
    update();
}
elseif ($q == "DLT"){
    delete();
}
elseif ($q == "CMNT"){
    comment();
}
elseif ($q == "CMNTS_JSON"){
    print get_comments_json(filter_input(INPUT_POST,"id"));
}
else if($q == "CMNTS_HTML"){
	get_comments_html();
}
elseif ($q == "TTL"){
	print get_podcast(filter_input(INPUT_POST,"id"))['title'];
}
elseif ($q == "LNGTH"){
	echo get_podcast(filter_input(INPUT_POST,"id"))['length'];
}
elseif ($q == "DESC"){
	echo get_podcast(filter_input(INPUT_POST,"id"))['description'];
}
elseif ($q == "DATE"){
	echo get_podcast(filter_input(INPUT_POST,"id"))['post_date'];
}
elseif ($q == "USR"){
	echo get_podcast(filter_input(INPUT_POST,"id"))['user_id'];
}
elseif ($q == "TAGS"){
	echo get_podcast(filter_input(INPUT_POST,"id"))['tags'];
}
elseif ($q == "IMG"){
	print get_podcast(filter_input(INPUT_POST,"id"))['image_file'];
}
elseif ($q == "AUDIO"){
	print get_podcast(filter_input(INPUT_POST,"id"))['audio_file'];
}
elseif ($q == "BY_USR"){
    print get_podcast_by_user(filter_input(INPUT_POST,"id"));
}
elseif ($q == "PDCST_JSN"){
    print get_podcast_json(filter_input(INPUT_POST,"id"));
}
elseif ($q == "URLIDTOPID"){
 	print get_podcast_by_urlid(filter_input(INPUT_POST, "id"))['podcast_id'];
}
elseif ($q == "LK"){
	like_podcast();
}
elseif ($q == "UN_LK"){
	unlike_podcast();
}
elseif ($q == "LKS"){
	print get_likes(filter_input(INPUT_POST,"id"));
}
elseif ($q == "LSTN"){
	print listen_to_podcast(filter_input(INPUT_POST,"id"));
}

function listen_to_podcast($id){
	$link = mysqli_connect(DB_HOST,DB_USER,DB_PASSWORD,DB_NAME) or die("Error connecting ot the server");
	$query = "UPDATE `".TABLE_PODCASTS."` SET listens=listens+1 WHERE `podcast_id`=$id";
	$result = mysqli_query($link,$query) or die("Error querying the server");
	echo "OKAY";
}

function get_likes($id){
	$link = mysqli_connect(DB_HOST,DB_USER,DB_PASSWORD,DB_NAME) or die("Error connecting to the server");
	$query = "SELECT `likes` FROM `".TABLE_PODCASTS."` WHERE `podcast_id`=$id";
	$result = mysqli_query($link,$query) or die("Error querying the server: $query");
	return mysqli_fetch_array($result)['likes'];
}

function like_podcast(){
	$pid = filter_input(INPUT_POST,"id");
	if(isset($_SESSION['user_id'])){
		$userid = $_SESSION['user_id'];
	}else{
		$userid = filter_input(INPUT_POST,"uid");
	}
	$link = mysqli_connect(DB_HOST,DB_USER,DB_PASSWORD,DB_NAME) or die("Error connecting to the database");
	$query = "UPDATE `".TABLE_PODCASTS."` SET likes=likes+1 where `podcast_id`=$pid";
	$result = mysqli_query($link,$query) or die("Error quering the database");
	$query = "UPDATE `".TABLE_USERS."` SET liked=CONCAT(liked,$pid,',') WHERE `user_id`=$userid";
	$result = mysqli_query($link,$query) or die("Error quering the database");
	mysqli_close($link);
	echo "OKAY";
}

function unlike_podcast(){
	$pid = filter_input(INPUT_POST,"id");
	if(isset($_SESSION['user_id'])){
		$userid = $_SESSION['user_id'];
	}else{
		$userid = filter_input(INPUT_POST,"uid");
	}
	$link = mysqli_connect(DB_HOST,DB_USER,DB_PASSWORD,DB_NAME) or die("Error connecting to the database");
	$query = "UPDATE `".TABLE_PODCASTS."` SET likes=likes-1 where `podcast_id`=$pid";
	$result = mysqli_query($link,$query) or die("Error quering the database");
	mysqli_close($link);

	$link = mysqli_connect(DB_HOST,DB_USER,DB_PASSWORD,DB_NAME) or die("Error connecting to the database");
	$query = "SELECT `liked` FROM `".TABLE_USERS."` WHERE `user_id`=$userid";
	$result = mysqli_query($link,$query) or die("Error querying the database");
	if(mysqli_num_rows($result) >= 1){
		$row = mysqli_fetch_array($result);
		$array = explode(",",$row['liked']);
		$final = array();
		for($i = 0; $i < count($array);$i++){
			if(intval($array[$i]) != $pid){
				$final[] = $array[$i];
			}
		}
		$finaljoined = implode(",",$final);
		$query = "UPDATE `".TABLE_USERS."` SET liked='$finaljoined' WHERE `user_id`=$userid";
		$result = mysqli_query($link,$query) or die("Error quering the database to update liked");
		echo "OKAY";
	}else{
		echo "NO";
	}
	mysqli_close($link);
}

function comment(){
    $user_id = "";
    if(($user_id = filter_input(INPUT_POST,'user_id')) == null){
        $user_id = $_SESSION['user_id'];
    }
    $podcast_id = filter_input(INPUT_POST,"id");
    $message = addslashes(filter_input(INPUT_POST,"msg"));
    $link = mysqli_connect(DB_HOST,DB_USER,DB_PASSWORD,DB_NAME) or die("Error connecting to the server");
    $query = "INSERT INTO `".TABLE_COMMENTS."` (`comment_id`,`user_id`,`podcast_id`,`message`) VALUES (0,$user_id,$podcast_id,'$message');";
    $result = mysqli_query($link,$query) or die("Error querying the database: ");
    echo "OKAY";
}

function get_comments_json($podcast_id){
    $link = mysqli_connect(DB_HOST,DB_USER,DB_PASSWORD,DB_NAME) or die("Error connecting to the server");
    $query = "SELECT * FROM `".TABLE_COMMENTS."` WHERE `podcast_id`=$podcast_id ORDER BY `post_date` DESC";
    $result = mysqli_query($link,$query) or die("Error querying the database");
    $re = array();
    while($r = mysqli_fetch_array($result)){
        $re[] = $r;
    }
    return json_encode($re);
}

function get_comments_html(){
	$podcast_id = filter_input(INPUT_POST,"id");
	$link = mysqli_connect(DB_HOST,DB_USER,DB_PASSWORD,DB_NAME) or die("Error connecting to the server");
	$query = "SELECT * FROM `".TABLE_COMMENTS."` WHERE `podcast_id`=$podcast_id";
	$result = mysqli_query($link,$query) or die ("Error querying server for comments");
	while($comment = mysqli_fetch_array($result)){
		$query = "SELECT * FROM `".TABLE_USERS."` WHERE `user_id`=".$comment['user_id'];
		$result2 = mysqli_query($link,$query) or die ("Error querying server for user info" . mysqli_error($link));
		$user = mysqli_fetch_array($result2);
		$profpic = $user['picture'];
		if($profpic == ""){
			$profpic = "/images/default_profile.png";
		}else{
			$profpic = "/users/".$user['user_id']."/images/".$profpic;
		}
		echo "<div onclick=loadPage('/profile.php?user=".$user['username']."') style='cursor:pointer;float:left;'>\n";
		echo "<img style='width:20px;height;20px' src='$profpic' /></div>";
		echo "<div>".$comment['post_date'].": ".$comment['message']."</div><br/>";
	};
	echo "OKAY";
}

function get_podcast($podcast_id){
    $link = mysqli_connect(DB_HOST,DB_USER,DB_PASSWORD,DB_NAME) or die("Error connecting to the server");
    $query = "SELECT * FROM `".TABLE_PODCASTS."` WHERE `podcast_id`=$podcast_id";
    $result = mysqli_query($link,$query) or die("Error querying the database");
    mysqli_close($link);
    if(mysqli_num_rows($result) == 1){
        return mysqli_fetch_array($result);
    }
    return null;
}

function get_podcast_json($podcast_id){
    $link = mysqli_connect(DB_HOST,DB_USER,DB_PASSWORD,DB_NAME) or die("Error connecting to the server");
    $query = "SELECT * FROM `".TABLE_PODCASTS."` WHERE `podcast_id`=$podcast_id";
    $result = mysqli_query($link,$query) or die("Error querying the database");
    mysqli_close($link);
    if(mysqli_num_rows($result) == 1){
        return json_encode(mysqli_fetch_array($result));
    }
    return null;
}

function get_podcast_by_user($user_id){
    $link = mysqli_connect(DB_HOST,DB_USER,DB_PASSWORD,DB_NAME) or die("Error connecting to the server");
    $query = "SELECT * FROM `".TABLE_PODCASTS."` WHERE `user_id`=$user_id";
    $result = mysqli_query($link,$query) or die("Error querying the database");
    mysqli_close($link);
    $rows = array();
    while($r = mysqli_fetch_array($result)){
        $rows[] = $r;
    }
    return json_encode($rows);
}


function podcast_cover_image($podcast_input,$queryServer = FALSE){
    if($queryServer){
        $podcast = get_podcast($podcast_input);
    }else{
        $podcast = $podcast_input;
    }
    if($podcast == null){
        return null;
    }else{
        return "/users/".$podcast['user_id'].'/images/podcast/'.$podcast['image_file'];
    }
}

function update_image(){
    $podcast_id = filter_input(INPUT_POST,"podcast_id");
    $podcast = get_podcast($podcast_id);
    $image_file = $_FILES["image_file"];
    $link = mysqli_connect(DB_HOST,DB_USER,DB_PASSWORD,DB_NAME) or die("Error connecting to the server.");
    if(sizeof($image_file) > 0 && $image_file['error'] == 0 && $image_file['size'] <= MAX_PODCAST_IMG_SIZE){
        $user_id = $_SESSION['user_id'];
        $basename = basename($image_file['tmp_name']);
        $fileloc = "../users/".$_SESSION['user_id']."/images/podcast/";
        if(!file_exists($fileloc)){
            mkdir($fileloc,0777,true) or die("Unable to create directory for image");
        }
        $filename = $basename . $image_file['name'];
        $destination = $fileloc . $filename;
        if($podcast['image_file'] !== "" && file_exists($_SERVER['DOCUMENT_ROOT'].podcast_cover_image($podcast))){
            unlink($_SERVER['DOCUMENT_ROOT'].podcast_cover_image($podcast));
        }
        if (!move_uploaded_file($image_file['tmp_name'], $destination)) {
            echo "ERROR";
            return;
        }
        $query = "UPDATE `".TABLE_PODCASTS."` SET `image_file`='$filename'";
        $result = mysqli_query($link,$query) or die("Error querying the database");
        mysqli_close($link);
        echo "OKAY";
        return;
    }
    echo "ERROR";
}

function update(){
    $col = filter_input(INPUT_POST,"col");
    $data = filter_input(INPUT_POST,"data");
    $data = addslashes($data);
    $podcast_id = filter_input(INPUT_POST,"podcast_id");
    $link = mysqli_connect(DB_HOST,DB_USER,DB_PASSWORD,DB_NAME) or die("Error connecting to the server.");
    $query = "UPDATE `".TABLE_PODCASTS."` SET `$col`='$data' WHERE `podcast_id`=$podcast_id";
    mysqli_query($link,$query) or die("Unable to query database");
    echo "OKAY";
    return;
}

function delete(){
    $podcast_id = filter_input(INPUT_POST,"podcast_id");
    $podcast = get_podcast($podcast_id);
    if($podcast == null){
        return;
    }
    if($podcast['image_file'] != "" && file_exists(podcast_cover_image($podcast))){
        unlink($_SERVER['DOCUMENT_ROOT'].podcast_cover_image($podcast));
    }
    if(file_exists("/users/".$_SESSION['user_id']."/audio/podcast/".$podcast['audio_file'])){
        unlink("/users/".$_SESSION['user_id']."/audio/podcast/".$podcast['audio_file']);
    }
    $link = mysqli_connect(DB_HOST,DB_USER,DB_PASSWORD,DB_NAME) or die("Error connecting to the server.");
    $query = "DELETE FROM `".TABLE_PODCASTS."` WHERE `podcast_id`=".$podcast_id;
    $result = mysqli_query($link,$query) or die("Error querying the database ".mysqli_error($link));
    mysqli_close($link);
    echo "OKAY";
    return;
}

function get_podcast_by_urlid($urlid){
  $link = mysqli_connect(DB_HOST, DB_USER, DB_PASSWORD, DB_NAME) or die("Error connecting to the server");
  $query = "SELECT `podcast_id` FROM `".TABLE_PODCASTS."` WHERE `urlid`='$urlid'";
  $result = mysqli_query($link, $query) or die("Error querying the database");
  mysqli_close($link);
  if($result != null){
    return mysqli_fetch_array($result);
  }
  else{
    return null;
  }

}

//get_comments_json(3);/
