<?php
class Media
{
	public $id;
	public $name;
	public $extension;
	public $titre;
	public $description;
	public $user_id;
	public $date;
	
	public function toDB()
	{
	
		$object = get_object_vars($this);
		return $object;
	}
}