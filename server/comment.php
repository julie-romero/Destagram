<?php
require_once('database/db.php');
require_once('model/user.php');
require_once('model/media.php');
require_once('model/comment.php');

$parameters = array
(
	':comment' => null,
	':token' => null,
	':id' => null
);
foreach($_POST as $key => $value)
{
	$parameters[":$key"] = $value;
}

$json = array(
	'error' => true
);

if (!empty($parameters[':comment']) && !empty($parameters[':token']) && !empty($parameters[':id'])) {
	$config = require_once('config.php');
	$db = new DB($config['dsn'], $config['username'], $config['password'], $config['options']);

	$user = $db->find('User', 'user', 'token = :token', array(':token' => $parameters[':token']));

	if($user !== false)
	{
		$media = $db->find('Media', 'media', 'id = :id', array(":id" => $parameters[":id"]));
		
		if($media !== false)
		{
			$comment = new Comment();
			$comment->user_id = $user->id;
			$comment->media_id = $parameters[":id"];
			$comment->date = date("Y-m-d H:i:s");
			$comment->message = $parameters[":comment"];
		
			$id = $db->insert($comment, 'comment');
			
			if($id !== false)
			{
				$json = array(
					'error' => false
				);
			}
		}
	}
}

echo json_encode($json);