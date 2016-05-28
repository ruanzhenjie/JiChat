<?php
/**
 * This file is part of workerman.
 *
 * Licensed under The MIT License
 * For full copyright and license information, please see the MIT-LICENSE.txt
 * Redistributions of files must retain the above copyright notice.
 *
 * @author walkor<walkor@workerman.net>
 * @copyright walkor<walkor@workerman.net>
 * @link http://www.workerman.net/
 * @license http://www.opensource.org/licenses/mit-license.php MIT License
 */

/**
 * 用于检测业务代码死循环或者长时间阻塞等问题
 * 如果发现业务卡死，可以将下面declare打开（去掉//注释），并执行php start.php reload
 * 然后观察一段时间workerman.log看是否有process_timeout异常
 */
//declare(ticks=1);

/**
 * 聊天主逻辑
 * 主要是处理 onMessage onClose 
 */
use \GatewayWorker\Lib\Gateway;
use \GatewayWorker\Lib\Db;

require_once 'include\DBControler.php';
require_once 'include\Account.php';

$AccountList=array();

class Events
{
	
	
	
	private static $mDBControler=null;
	
	private static $client_num=0;
	//public static $AccountList=array();
   /**
    * 有消息时
    * @param int $client_id
    * @param mixed $message
    */
   public static function onMessage($client_id, $message)
   {
   	
		global $AccountList;
        // debug
        echo "client:{$_SERVER['REMOTE_ADDR']}:{$_SERVER['REMOTE_PORT']} gateway:{$_SERVER['GATEWAY_ADDR']}:{$_SERVER['GATEWAY_PORT']}  client_id:$client_id session:".json_encode($_SESSION)." onMessage:".$message."\n";
        
        // 客户端传递的是json数据
        $message_data = json_decode($message, true);
        if(!$message_data)
        {
            return ;
        }
        
        // 根据类型执行不同的业务
        switch($message_data['type'])
        {
            // 客户端回应服务端的心跳
            case 'pong':
                return;
            // 客户端登录 message格式: {type:login, name:xx, room_id:1} ，添加到客户端，广播给所有客户端xx进入聊天室



                
            case 'Check':
                /*if(null==Events::$mDBControler){
                	
					
					Events::$mDBControler=Db::instance('db');
					$result = Events::$mDBControler->query("SELECT * FROM employees" );
						

					foreach ($result as $row) {
        				print_r($row); //你可以用 echo($GLOBAL); 来看到这些值
    				}
					
					$result = Events::$mDBControler->query("SELECT * FROM posts" );
						

					foreach ($result as $row) {
        				print_r($row); //你可以用 echo($GLOBAL); 来看到这些值
    				}
					

				}
				else{

				}*/
				return ;

			case 'SayToFriend':
				//if(isset($message_data['to_account_id']) && isset(Events::$AccountList[$message_data['to_account_id']])){
				if(isset($message_data['to_account_id']) && isset($AccountList[$message_data['to_account_id']])){

                    $account_id=$_SESSION['account_id'];
                    $account_name=$_SESSION['account_name'];
					$new_message = array(
                        'type'=>'saytofriend',
                        'from_account_id'=>$account_id,
                        'from_account_name' =>$account_name,
                        //'to_client_id'=>$message_data['to_client_id'],
                        'content'=>nl2br(htmlspecialchars($message_data['content'])),
                        //'time'=>date('Y-m-d H:i:s'),
                    );
					//$client=Events::$AccountList[$message_data['to_account_id']]->getClientID();
					$client=$AccountList[$message_data['to_account_id']]->getClientID();
                	Gateway::sendToClient($client, json_encode($new_message));
					echo "havd send \n";
				}
                return ;
                
            case 'SayToGroup':
				$account_id=$_SESSION['account_id'];
				$account_name=$_SESSION['account_name'];
				$room_id=$message_data['group_id'];
                $new_message = array(
                    'type'=>'saytogroup', 
                    'from_account_id'=>$account_id,
                    'from_account_name' =>$account_name,
                    'group_id'=>$room_id,
                    'to_account_id'=>'all',
                    'content'=>nl2br(htmlspecialchars($message_data['content'])),
                    'time'=>date('Y-m-d H:i:s'),
                );
                return Gateway::sendToGroup($room_id ,json_encode($new_message));
                        
            case 'Connect':
			
				$m_ord=$message_data['account_id'];
                $_SESSION['account_id'] = $m_ord;
                $_SESSION['account_name'] = $message_data['account_name'];
                if(!isset($AccountList[$m_ord])){
                    $AccountList[$m_ord]=new Account($m_ord,$client_id,$message_data['group_list']);
				    foreach($message_data['group_list'] as $group_id){
					   Gateway::joinGroup($client_id, $group_id);
				    }
                }
				echo $m_ord."\n";
				
                return ;
                
            case 'FindFriend':
                
                return ;
                
            case 'FindGroup':
                
                return ;
                
            case 'AddFriend':
                
                return ;
                
            case 'AddGroup':
                
                return ;
        
            case 'Login':
				$name=$message_data['account_name'];
				$psw=$message_data['account_password'];
				//Events::$mDBControler=Db::instance('db');
				$conn=Db::instance('db');
				$sqlstr="SELECT * FROM tbaccount where Name='".$name."' and Password='".$psw."';" ;
				$account_id=-1;
				
				$result = $conn->query($sqlstr);	

				foreach ($result as $row) {
        			//print_r($row); //你可以用 echo($GLOBAL); 来看到这些值
        			$account_id=$row['ID'];
    			}
				
				if($account_id<0){
					$new_message = array(
                    'type'=>'LoginFail', 
                    'time'=>date('Y-m-d H:i:s'),
                	);
                	return Gateway::sendToCurrentClient(json_encode($new_message));
				}
				
				$freind_list=array();
				$friend_id_list=array();
				$group_list=array();
				$group_id_list=array();
				
				$sqlstr="SELECT * FROM tbfriend where ToWho='".$account_id."';";
				
				$result = $conn->query($sqlstr);	

				$flag=0;
				$friendSet="";
				foreach ($result as $row) {
        			//print_r($row); //你可以用 echo($GLOBAL); 来看到这些值
        			if($flag==0){
        				$friendSet.="".$row['AsFriend'];
						$flag=1;
        			}
					else{
						$friendSet.=",".$row['AsFriend'];
					}
        			//$friend_id_list[]=$row['AsFriend'];
    			}

				$sqlstr="SELECT * FROM tbaccount where ID in (".$friendSet.");";
				
				$result = $conn->query($sqlstr);	
				
				
				foreach ($result as $row) {
        			//print_r($row); //你可以用 echo($GLOBAL); 来看到这些值
        			$freind_list[$row['ID']]=$row['Name'];
    			}

				
				$sqlstr="SELECT * FROM tbgroupmember where MemberID='".$account_id."';";
				
				$result = $conn->query($sqlstr);	


				$flag=0;
				$groupSet="";
				foreach ($result as $row) {
        			//print_r($row); //你可以用 echo($GLOBAL); 来看到这些值
        			if($flag==0){
        				$groupSet.="".$row['GroupID'];
        				$flag=1;
        			}
					else{
						$groupSet.=",".$row['GroupID'];
					}
        			//$group_id_list[]=$row['GroupID'];
    			}
				
				
				$sqlstr="SELECT * FROM tbgroup where ID in (".$groupSet.");";
				
				$result = $conn->query($sqlstr);	
				
				foreach ($result as $row) {
        			//print_r($row); //你可以用 echo($GLOBAL); 来看到这些值
        			$group_list[$row['ID']]=$row['Name'];
    			}
				
				$new_message=array(
					'type'=>'LoginSucceed',
                    'account_id'=>$account_id,
                    'account_name'=>$name,
					'friend_list'=>$freind_list,
					'group_list'=>$group_list, 
                    'time'=>date('Y-m-d H:i:s')
				);
				
								
                
                return Gateway::sendToCurrentClient(json_encode($new_message));
                





            case 'login':
                // 判断是否有房间号
                if(!isset($message_data['room_id']))
                {
                    throw new \Exception("\$message_data['room_id'] not set. client_ip:{$_SERVER['REMOTE_ADDR']} \$message:$message");
                }
                if(isset($message_data['mtype']))
                {
                    echo $message_data['mtype']."\n";
                }
                
                // 把房间号昵称放到session中
                $room_id = $message_data['room_id'];
                $client_name = htmlspecialchars($message_data['client_name']);
                $_SESSION['room_id'] = $room_id;
                $_SESSION['client_name'] = $client_name;
              
                // 获取房间内所有用户列表 
                $clients_list = Gateway::getClientInfoByGroup($room_id);
                foreach($clients_list as $tmp_client_id=>$item)
                {
                    $clients_list[$tmp_client_id] = $item['client_name'];
                }
                $clients_list[$client_id] = $client_name;
                
                // 转播给当前房间的所有客户端，xx进入聊天室 message {type:login, client_id:xx, name:xx} 
                $new_message = array('type'=>$message_data['type'], 'client_id'=>$client_id, 'client_name'=>htmlspecialchars($client_name), 'time'=>date('Y-m-d H:i:s'));
                Gateway::sendToGroup($room_id, json_encode($new_message));
                Gateway::joinGroup($client_id, $room_id);
               
                // 给当前用户发送用户列表 
                $new_message['client_list'] = $clients_list;
                Gateway::sendToCurrentClient(json_encode($new_message));
                return;

            // 客户端发言 message: {type:say, to_client_id:xx, content:xx}
            case 'say':
                // 非法请求
                if(!isset($_SESSION['room_id']))
                {
                    throw new \Exception("\$_SESSION['room_id'] not set. client_ip:{$_SERVER['REMOTE_ADDR']}");
                }
                $room_id = $_SESSION['room_id'];
                $client_name = $_SESSION['client_name'];
                
                // 私聊
                if($message_data['to_client_id'] != 'all')
                {
                    $new_message = array(
                        'type'=>'say',
                        'from_client_id'=>$client_id, 
                        'from_client_name' =>$client_name,
                        'to_client_id'=>$message_data['to_client_id'],
                        'content'=>"<b>对你说: </b>".nl2br(htmlspecialchars($message_data['content'])),
                        'time'=>date('Y-m-d H:i:s'),
                    );
                    Gateway::sendToClient($message_data['to_client_id'], json_encode($new_message));
                    $new_message['content'] = "<b>你对".htmlspecialchars($message_data['to_client_name'])."说: </b>".nl2br(htmlspecialchars($message_data['content']));
                    return Gateway::sendToCurrentClient(json_encode($new_message));
                }
                
                $new_message = array(
                    'type'=>'say', 
                    'from_client_id'=>$client_id,
                    'from_client_name' =>$client_name,
                    'to_client_id'=>'all',
                    'content'=>nl2br(htmlspecialchars($message_data['content'])),
                    'time'=>date('Y-m-d H:i:s'),
                );
                return Gateway::sendToGroup($room_id ,json_encode($new_message));
        }
   }
   
   /**
    * 当客户端断开连接时
    * @param integer $client_id 客户端id
    */
   public static function onClose($client_id)
   {
		global $AccountList;
       // debug
       echo "client:{$_SERVER['REMOTE_ADDR']}:{$_SERVER['REMOTE_PORT']} gateway:{$_SERVER['GATEWAY_ADDR']}:{$_SERVER['GATEWAY_PORT']}  client_id:$client_id onClose:''\n";
       
       // 从房间的客户端列表中删除
       if(isset($_SESSION['room_id']))
       {
           $room_id = $_SESSION['room_id'];
           $new_message = array('type'=>'logout', 'from_client_id'=>$client_id, 'from_client_name'=>$_SESSION['client_name'], 'time'=>date('Y-m-d H:i:s'));
           Gateway::sendToGroup($room_id, json_encode($new_message));
       }
       if(isset($_SESSION['account_id'])){
       		$m_account=$AccountList[$_SESSION['account_id']];
		   	$group_list=$m_account->getGroupList();
			foreach ($group_list as $group_id) {
				Gateway::leaveGroup($m_account->getClientID(),$group_id);
			}
			//$AccountList[$_SESSION['account_id']]=null;
			unset($AccountList[$_SESSION['account_id']]);
       		echo $_SESSION['account_id']."leave\n";
       }
   }
  
}
