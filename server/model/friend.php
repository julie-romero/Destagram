<?php
class Friend
{
	public $user_1;
	public $user_2;
        
	public function toDB()
	{
		$object = get_object_vars($this);
		return $object;
	}
}