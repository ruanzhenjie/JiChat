<?php
    class DBControler{
    	var $conn=null;
		var $db=null;
		
		private static $mDBControler=null;
		
		
		
		
    	private function __construct()
		{
			
			$dbms='mysql';     //数据库类型
			$host='localhost'; //数据库主机名
			$dbName='mydb';    //使用的数据库
			$user='root';      //数据库连接用户名
			$pass='';          //对应的密码
			$dsn="$dbms:host=$host;dbname=$dbName";


			try {
    			$this->conn = new PDO($dsn, $user, $pass); //初始化一个PDO对象
    			//$this->conn = new PDO($dsn, $user, $pass, array(PDO::ATTR_PERSISTENT => true));
    			//echo "连接成功\n";
    			//你还可以进行一次搜索操作
    			//foreach ($dbh->query('SELECT * from employees') as $row) {
        			//print_r($row); //你可以用 echo($GLOBAL); 来看到这些值
    			//}
    		//$dbh = null;
			} catch (PDOException $e) {
    			die ("Error!: " . $e->getMessage() . "<br/>");
			}
			
			
			/*
			$this->conn=mysql_connect("localhost","root","");
			if(!$this->conn){
				die("fail connect");
			}
			
			$res=mysql_select_db('mydb',$this->conn);
			if($res)
				$this->db='mydb';*/
		}
		
		function __destruct(){
			if(!$this->conn){
				//mysql_close($this->conn);
				$this->conn=null;
			}
		}
		
		static function initialize()
		{
			if(null==DBControler::$mDBControler){
				DBControler::$mDBControler=new DBControler();
				return DBControler::$mDBControler;
			}
			else{
				return DBControler::$mDBControler;
			}
		}
		
		/*
		function queryWithDB($mDB,$mquery){
			if(!$this->conn){
				return FALSE;
			}
			mysql_select_db($mDB,$this->conn);
			$res=mysql_query($mquery,$this->conn);
			
			// $res=mysql_db_query($mDB, $mquery,$this->conn);
			if($res)
				$this->db=$mDB;
			return $res;
		}*/
		/*
		function selectDB($database_name){
			if(!$this->conn){
				return FALSE;
			}
			
			$res=mysql_select_db($database_name,$this->conn);
			if($res)
				$this->db=$database_name;
			return res;
		}
		*/
		function query($mquery){
			if(!($this->conn)){
				return FALSE;
			}
			
			//return mysql_query($mquery,$this->conn);
			return $this->conn->query($mquery);
		}
    }
	
	
	// $msql=new DBControler();
	// $res=$msql->queryWithDB("zhapodb", "select * from page;");
// 	
	// if($res)
	// {
		// while($row=mysql_fetch_array($res)){
			// var_dump($row);
		// }
	// }
?>