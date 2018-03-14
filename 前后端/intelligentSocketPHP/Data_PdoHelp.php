<?php
class PdoHelps
{
    private static $instance = array();//该类中的唯一一个实例
    private $db;
    private $stmts=array();
    private $sqls=array();
    //如果只有一个对象那就只能链接一个数据库。
    public static function getInstance($name,$password,$dataname,$host="localhost",$port="3306",$mycharaset="utf8"){
        $key = "{$name}:{$password}:{$port}:{$dataname}";
        if(self::$instance[$key] == null){
            self::$instance[$key]= new self($name,$password,$dataname,$host,$port,$mycharaset);
        }
        return self::$instance[$key];
    }
    private function __construct($name,$password,$dataname,$host,$port,$mycharaset)
    {
        $dsn = "mysql:host=".$host.";dbname=".$dataname.";charset=".$mycharaset.";port={$port}";
        try {
            $this->db = new PDO($dsn,$name,$password, array(PDO::ATTR_PERSISTENT => true));
        } catch (PDOException $e) {
            print "PDO-Error: " . $e->getMessage() . "<br/>";
            echo $e->getLine();
            die();
        }
    }

    private function __clone(){}//禁止通过复制的方式实例化该类

    public function ExecuteSql($action,$sql,$bindvalue,$resulttype)
    {
        switch ($action) {
            case "Edql":
                $row=$this->ExecuteSql_DQL($sql, $bindvalue, $resulttype);
                break;
            case "Edml":
                $row=$this->ExecuteSql_DML($sql, $bindvalue, $resulttype);
                break;
            case "dql":
                $row=$this->Sql_DQL($sql,$resulttype);
                break;
            case "dml":
                $row=$this->Sql_DML($sql,$resulttype);
                break;
            default:
                return false;
        }
        return $row;
    }

    private function ExecuteSql_DQL($sql, $bindvalue, $type){
        if(!$this->checksql($sql)){
            $this->sqls[]=$sql;
//            动态增加一个关联形数组
            $this->stmts= Array_merge(array($sql => $this->db->prepare($sql),$this->stmts));
        }
        $this->stmts[$sql]->execute($bindvalue);
        $this->showerro();
        switch ($type) {
            //关联数组：
            case 1:
                $row = $this->stmts[$sql]->fetchall(PDO::FETCH_ASSOC);
                break;
            //索引数组：
            case 2:
                $row =$this->stmts[$sql]->fetchall(PDO::FETCH_NUM);
                break;
            //返回查到的所有记录
            default:
                return false;
        }
        return $row;
    }

    private function ExecuteSql_DML($sql, $bindvalue, $type){
        if(!$this->checksql($sql)){
            $this->sqls[]=$sql;
            $this->stmts= Array_merge(array($sql => $this->db->prepare($sql),$this->stmts));
        }
        $this->stmts[$sql]->execute($bindvalue);
        $this->showerro();
        switch ($type) {
            //返回受影响的行数
            case 1:
                $row = $this->stmts[$sql]->rowCount();
                break;
            //返回插入成功的id号
            case 2:
                $row=$this->db->LastInsertId();
                break;
            default:return false;
        }
        return $row;
    }

    private function Sql_DQL($sql,$type){
        $rs = $this->db->query($sql);
        $this->showerro();
        switch($type){
            case 1:
                $row = $rs->fetchAll(PDO::FETCH_ASSOC);
                break;
            case 2:
                $row = $rs->fetchAll(PDO::FETCH_NUM);
                break;
            default:return false;
        }
        return $row;
    }

    private function Sql_DML($sql,$type){
        $row = $this->db->exec($sql);
        $this->showerro();
        switch($type){
            case 1:
                break;
            case 2:
                 $row=$this->db->lastInsertId();
                break;
            default:return false;
        }
        return $row;
    }

    private function showerro(){
        if ($this->db->errorCode() != '00000') {
            exit($this->db->errorInfo());
        }
    }

    private function checksql($sql){
        foreach($this->sqls as $value){
            if($value==$sql){
                return true;
            }
        }
        return false;
    }
}


//$sqlselect = 'SELECT password FROM user where nickname = :name';
//$sqlinsert1 ="insert into user (nickname,password) values(:nickname,:password)";
//$sqlinsert2="INSERT INTO user SET nickname ='1',password='女'";
//$sqlupdata="update user set num=:num WHERE nickname=:nickname AND password=:password";
//$sqldelect="delete from user where nickname=:nickname";
