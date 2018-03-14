<?php
/**
 * Created by PhpStorm.
 * User: new bee
 * Date: 2017/5/16
 * Time: 23:35
 */
include_once "Data_PdoHelp.php";
$db=PdoHelps::getInstance("root","1234","phpdata");
$data=$_POST["data"];
$type=$_POST['type'];
$d1=$data[0];
$d2=$data[1];
$d3=$data[2];
$d4=$data[3];
$d5=$data[4];
$d6=$data[5];
$d7=$data[6];
$d8=$data[7];

if($type=="add") {
    $sqlinsert = "insert into intelligentsocket (name,time,温度, 湿度, 电流,电压,电功率,功耗) VALUES ('$d1','$d2','$d3','$d4','$d5','$d6','$d7','$d8')";
    $db->ExecuteSql("dml", $sqlinsert, null, 1);
}else if($type=="show"){
    $sqlselect = "select * from intelligentsocketuser";
    $results = $db->ExecuteSql("dql", $sqlselect, null, 2);
    echo $results;
}
echo $type;