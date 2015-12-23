<?php
function rss($userid,$podcast){
    $filename = "/var/www/html/users/$userid/audio/feed.rss";
    if(file_exists($filename)){
        edit_rss($userid,$podcast,$filename);
    }else{
        new_rss($userid,$podcast,$filename);
    }
}

function remove_rss($userid,$podcast){
  $filename = "/var/www/html/users/$userid/audio/feed.rss";
  if(!file_exists($filename)) return;
  $file = fopen($filename, 'r');
  if(!$file) return;
  $contents = "";
  while(($line = fgets($file))){
    $contents .= $line;
  }
  fclose($file);
  $contents = str_replace("</channel>\n</rss>\n","",$contents);
  $items = explode("<item>", $contents);
  $nContents = "";
  foreach ($items as $item) {
    if(strpos($item, "<channel>") !== false){
      $nContents .= $item;
      continue;
    }
    if(strpos($item,$podcast['audio_file']) == false){
      $nContents .="<item>\n";
      $nContents .= $item;
    }
  }
  $nContents .= "</channel>\n</rss>";
  $file = fopen($filename, 'w');
  fwrite($file, $nContents);
  fclose($file);
}

function edit_rss($userid,$podcast,$filename){
    $site = "http://ec2-52-35-70-147.us-west-2.compute.amazonaws.com";
    $file = fopen($filename,'r');
    if($file == false){
        return;
    }
    $podcastfile = fopen("/var/www/html/users/$userid/audio/podcast/".$podcast['audio_file'],'r');
    if($podcastfile == false){
        return;
    }
    $filesize = filesize($podcastfile);
    $contents = "";
    while(($line = fgets($file)) != false){
        $contents .= $line;
    }
    fclose($file);
    $user = get_user($userid);
    $contents = str_replace("</channel>","",$contents);
    $contents = str_replace("</rss>","",$contents);
    $contents .= "<item>\n";
    $contents .= "<itunes:image href=\"$site/users/".$userid."/images/podcast/".$podcast['image_file']."\" />\n";
    $contents .= "<title>".$podcast['title']."</title>\n";
    $contents .= "<link>$site/users/".$user['user_id']."/audio/podcast/".$podcast['audio_file']."</link>\n";
    $contents .= "<guid>$site/users/".$user['user_id']."/audio/podcast/".$podcast['audio_file']."</guid>\n";
    $contents .= "<enclosure url=\"$site/users/$userid/audio/podcast/".$podcast['audio_file']."\" length=\"$filesize\" type=\"audio/mpeg\"/>\n";
    $contents .= "<description>".$podcast['description']."</description>\n";
    $contents .= "</item>\n";
    $contents .= "</channel>\n</rss>\n";

    $file = fopen($filename,'w');
    fwrite($file,$contents);
    fclose($file);
}

function new_rss($userid,$podcast,$filename){
    $site = "http://ec2-52-35-70-147.us-west-2.compute.amazonaws.com";
    $file = fopen($filename,'w');
    if($file == false){
        return;
    }
    $user = get_user($userid);
    $contents = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
    $contents .= "<rss version=\"2.0\" xmlns:atom=\"http://www.w3.org/2005/Atom\"\n";
    $contents .= "xmlns:itunes=\"http://www.itunes.com/dtds/podcast-1.0.dtd\">";
    $contents .= "<channel>\n";
    $contents .= "<title>".$user['username']."</title>\n";
    $contents .= "<link>$site/".$user['username']."</link>\n";
    $contents .= "<description>".$podcast['description']."</description>\n";
    $contents .= "<category>Podcasts</category>\n"; // to change later
    $contents .= "<atom:link href=\"$site/users/".$userid."/audio/feed.rss\" rel=\"self\" />\n";
    $contents .= "<image>\n";
    $contents .= "<url>$site/users/".$userid."/images/podcast/".$podcast['image_file']."</url>\n";
    $contents .= "<title>".$user['username']."</title>\n";
    $contents .= "<link>$site/".$user['username']."</link>\n";
    $contents .= "</image>\n";
    $contents .= "<itunes:image href=\"$site/users/".$userid."/images/".$user['picture']."\" />\n";
    $contents .= "</channel>\n";
    $contents .= "</rss>\n";
    fwrite($file,$contents);
    fclose($file);
    edit_rss($userid,$podcast,$filename);
}

function get_user($userid){
    $link = mysqli_connect(DB_HOST,DB_USER,DB_PASSWORD,DB_NAME) or die("Error connecting to the server");
    $query = "SELECT * FROM `".TABLE_USERS."` WHERE `user_id`=$userid";
    $result = mysqli_query($link,$query) or die("Error quering the database");
    mysqli_close($link);
    return mysqli_fetch_array($result);
}
