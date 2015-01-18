<?php
class UserLike
{
	public $user_id;
	public $media_id;

	public function toDB()
	{
		$object = get_object_vars($this);
		return $object;
	}
}