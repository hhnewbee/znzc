<?php
/**
 * Created by PhpStorm.
 * User: new bee
 * Date: 2017/5/16
 * Time: 10:28
 */
include_once "Data_PdoHelp.php";
$db=PdoHelps::getInstance("root","1234","phpdata");
$type=$_POST['type'];
$phone=$_POST['phone'];
$password=$_POST['password'];
$sqlselect= "select * from intelligentsocketuser";
if($type=="a"){
    if(($results=$db->ExecuteSql("dql",$sqlselect,null,1))){
        $if=0;
        for($i = 0;$i < sizeof($results);$i++){
            if($results[$i]['password']==$password){
//                header("Content-type: text/html; charset=utf-8");
//                header( "Location: http://192.168.43.189:8090/znzc/intelligentSocketPHP/gizwits-wechat-js-sdk-master/demo/index.html" );
                $if="http://192.168.43.189:8090/znzc/gizwits-wechat-js-sdk-master/demo/index.html";
                echo $if;
                break;
            }
            else{
                $if=0;
                echo $if;
            }
        }
    }
}
if($type=="i"){
    if(($results=$db->ExecuteSql("dql",$sqlselect,null,1))) {
        $if=0;
        for($i = 0;$i <sizeof($results);$i++){
            if($results[$i]['phoneNumber']!=$phone){
                $if=0;
            }else{
                $if=1;
                break;
            }
        }
        echo $if;
    }else{
        echo 0;
    }
}