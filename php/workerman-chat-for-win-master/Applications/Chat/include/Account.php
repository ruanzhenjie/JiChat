<?php
    /**
     * 
     */
    class Account {
        
		var $friendList=array();
		var $groupList=array();
		var $client_id=null;
		var $account_id=null;
		var $account_name=null;
		
        function __construct($m_account_id,$m_client_id,$m_group_list) {
        	$this->account_id=$m_account_id;
            $this->client_id=$m_client_id;
			$this->groupList=$m_group_list;
        }
		
		public function getClientID()
		{
			return $this->client_id;
		}
		
		public function getGroupList()
		{
			return $this->groupList;
		}
    }
    
?>