<?php

/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

$q = $_REQUEST["q"];
if ($q !== ""){
    if($q === "title"){
        $result = array("main"=>"Caster","wplt"=>"What People are Listening To");
        header("Content-Type: application/json");
        echo json_encode($result);
    }
}
