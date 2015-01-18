<?php
require_once('database/db.php');
require_once('model/user.php');
require_once('model/media.php');
require_once('model/comment.php');
require_once('model/user_like.php');

$parameters = array
(
	':token' => null,
	':id' => null
);
foreach($_GET as $key => $value)
{
	$parameters[":$key"] = $value;
}

$json = array(
	'error' => true
);

if (!empty($parameters[':token']) && !empty($parameters[':id'])) {
	$config = require_once('config.php');
	$db = new DB($config['dsn'], $config['username'], $config['password'], $config['options']);

	$user = $db->find('User', 'user', 'token = :token', array(':token' => $parameters[':token']));

	if($user !== false)
	{
		$media = $db->find('Media', 'media', 'id = :id', array(":id" => $parameters[":id"]));
		$mediaAuthor = $db->find('User', 'user', 'id = :id', array(":id" => $media->user_id));
		
		$isLike = false;
		$userLike = $db->search('UserLike', 'user_like', 'media_id = :id and user_id = :user_id', array(":id" => $parameters[":id"], ":user_id" => $user->id));
		if (count($userLike) > 0) {
			$isLike = true;
		}
		
		$sql = "SELECT u.pseudo, c.*
				FROM comment c, user u 
				WHERE c.user_id = u.id
				AND c.media_id = :media_id
				ORDER BY c.date DESC
		";
		$comments = $db->requestJoin($sql, array(":media_id" => $parameters[":id"]));
		
		if($media !== false)
		{
			$json = array(
				'error' => false,
				'media' => $media,
				'author' => array(
					'id' => $mediaAuthor->id,
					'username' => $mediaAuthor->pseudo
				),
				'comments' => $comments,
				'likes' => count($db->search('UserLike', 'user_like', 'media_id = :id', array(":id" => $parameters[":id"]))),
				'isLike' => $isLike
			);
		}
	}
}

echo json_encode($json);