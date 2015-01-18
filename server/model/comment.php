<?php
class Comment
{
	public $id;
	public $user_id;
	public $media_id;
	public $date;
	public $message;

	public function toDB()
	{
		$object = get_object_vars($this);
		return $object;
	}
}