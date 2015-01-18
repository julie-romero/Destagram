<?php
class User
{
	public $id;
	public $login;
	public $password;
	public $token;
	public $pseudo;

	public function toDB()
	{
		$object = get_object_vars($this);
		return $object;
	}
}