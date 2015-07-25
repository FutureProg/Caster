<?php
include 'php_vars.php';

$input = explode("$",filter_input(INPUT_GET,"q"));
$token = $input[1];
$link = mysqli_connect(DB_HOST,DB_USER,DB_PASSWORD,DB_NAME) or die("error connecting to the database");
$query = "SELECT * FROM `".TABLE_TOKENS."` WHERE token_value='$token'";
$result = mysqli_query($link,$query) or die("Error querying the database: error code ".mysqli_errno($link));
if(mysqli_num_rows($result) == 1){
    $query = "DELETE FROM `".TABLE_TOKENS."` WHERE token_value='$token'";
    $result = mysqli_query($link,$query) or die("Error querying the database: error code ".mysqli_errno($link));
}else{    
    include 'page_content/error403.php';
    exit(403);//code 403 Forbidden
    return;
}

$q = $input[0];
$query = "SELECT * FROM `".TABLE_PODCASTS."` WHERE podcast_id=$q";
$result = mysqli_query($link,$query) or die("error querying the database: error code ".mysqli_errno($link));
$path = "";
if(mysqli_num_rows($result) == 1){
    $row = mysqli_fetch_row($result);
    $path = "../users/$row[1]/audio/podcast/$row[6]";    
}
if(file_exists($path)){    
    set_time_limit(0);   
    $last_mod_time = filemtime($path);
    $etag = md5_file($path);
    $file_size = filesize($path);
    $offset = 0;
    $length = $file_size;     
    if(isset($_SERVER['HTTP_RANGE'])){
        $partialContent = true;
        preg_match('/bytes=(\d+)-(\d+)?/',$_SERVER['HTTP_RANGE'],$matches);
        $offset = intval($matches[1]);
        $length = intval($matches[2] - $offset);
        if($offset + $length == 0){
            $length = $file_size - $offset - 1;
        }
    }
    else{
        $partialContent = false;
    }    
    $file = fopen($path,'rb');
    fseek($file,$offset);    
    
    if($partialContent){
        header('HTTP/1.1 206 Partial Content');
        header('Content-Range: bytes '. $offset . '-' . ($offset + $length) . '/' . $file_size);
    }
            
    header('Content-Type: audio/mpeg');        
    header('Accept-Ranges: bytes');
    header('Content-Length: '.$file_size);      
    header('Cache-Control: max-age=252460800 ');    
    /*header('Last-Modified: '.gmdate("D, d M Y H:i:s",$last_mod_time).' GMT');
    header('Etag: $etag');
    if (@strtotime($_SERVER['HTTP_IF_MODIFIED_SINCE']) == $last_mod_time || 
        trim($_SERVER['HTTP_IF_NONE_MATCH']) == $etag) { 
        header("HTTP/1.1 304 Not Modified"); 
    }*/
    //header('Content-Transfer-Encoding: binary');            
    //fpassthru($file);    
    //fclose($file);
    $out = fread($file,$file_size);
    echo $out;
    fclose($file);
}else{
    echo "AUDIO DNE";
}
                                    