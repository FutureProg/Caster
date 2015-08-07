<?php
include 'user_info.php';
include 'podcast.php';
function RSS($userid,$podcast){
    $filename = "/home/nick/www/public/users/$userid/audio/feed.rss";
    if(file_exists($filename)){
        edit_rss($userid,$podcast,$filename);
    }else{
        new_rss($userid,$podcast,$filename);
    }
}

function edit_rss($userid,$podcast){
    $file = fopen($filename,'r');
    if($file == false){
        return;
    }    
    $podcastfile = fopen("http://istrat.ddns.net/users/24/audio/podcast/phpRq1X0qMenuBGM.mp3",'r');
    if($podcast == false){
        return;
    }
    $filesize = filesize($podcastfile);
    $contents = "";
    while(($line = fgets($file)) != false){
        $contents .= $line;
    }
    fclose($file);
    $contents = str_replace("</channel>\n</rss>\n","",$contents);
    $contents .= "<item>\n";
    $contents .= "<itunes:image href=\"http://istrat.ddns.net/users/".$userid."/images/podcast/".$podcast['image_file']."\" />\n";
    $contents .= "<title>".$podcast['title']."</title>\n";
    $contents .= "<link>http://istrat.ddns.net/users/".$user['user_id']."/audio/podcast/".$podcast['audio_file']."</link>\n";
    $contents .= "<guid>http://istrat.ddns.net/users/".$user['user_id']."/audio/podcast/".$podcast['audio_file']."</guid>\n";
    $contents .= "<enclosure url=\"http://istrat.ddns.net/users/$userid/audio/podcast/".$podcast['audio_file']."\" length=\"$filesize\" type=\"audio/mpeg\"/>\n";
    $contents .= "<description>".$podcast['description']."</description>\n";
    $contents .= "</item>\n";
    $contents .= "</channel>\n</rss>\n";
    
    $file = fopen($filename,'w');
    fwrite($file,$contents);
    fclose($file);
}

function new_rss($userid,$podcast){
    $file = fopen($filename,'w');
    if($file == false){
        return;
    }
    $user = user_json($userid);
    $contents = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
    $contents .= "<rss version=\"2.0\" xmlns:atom=\"http://www.w3.org/2005/Atom\"\n";
    $contents .= "xmlns:itunes=\"http://www.itunes.com/dtds/podcast-1.0.dtd\">";
    $contents .= "<channel>\n";
    $contents .= "<title>".$user['username']."</title>\n";
    $contents .= "<link>http://istrat.ddns.net/".$user['username']."</link>\n";
    $contents .= "<description>".$podcast['description']."</description>\n";
    $contents .= "<category>Podcasts</category>\n"; // to change later
    $contents .= "<atom:link href=\"http://istrat.ddnes.net/users/".$userid."/audio/feed.rss\" rel=\"self\" />\n";
    $contents .= "<image>\n";
    $contents .= "<url>http://istrat.ddns.net/users/".$userid."/images/podcast/".$podcast['image_file']."</url>\n";
    $contents .= "<title>".$podcast['title']."</title>\n";
    $contents .= "<link>http://istrat.ddns.net/".$user['username']."</link>\n";
    $contents .= "</image>\n";
    $contents .= "<itunes:image href=\"http://istrat.ddns.net/users/".$userid."/images/".$user['picture']."\" />\n";
    $contents .= "</channel>\n";
    $contents .= "</rss>\n";
    fwrite($file,$contents);
    fclose($file);
    edit_rss($userid,$podcast);
}