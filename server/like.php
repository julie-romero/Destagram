<?php
require_once('database/db.php');
require_once('model/user.php');
require_once('model/media.php');
require_once('model/user_like.php');

$parameters = array
(
	':like' => null,
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

if (!empty($parameters[':like']) && !empty($parameters[':token']) && !empty($parameters[':id'])) {
	$config = require_once('config.php');
	$db = new DB($config['dsn'], $config['username'], $config['password'], $config['options']);

	$user = $db->find('User', 'user', 'token = :token', array(':token' => $parameters[':token']));

	if($user !== false)
	{
		$media = $db->find('Media', 'media', 'id = :id', array(":id" => $parameters[":id"]));
		
		if($media !== false)
		{
		
			if (filter_var($parameters[":like"], FILTER_VALIDATE_BOOLEAN)) {
			
				$like = new UserLike();
				$like->user_id = $user->id;
				$like->media_id = $parameters[":id"];
			
				$db->insert($like, 'user_like');

			} else {
				$db->delete('user_like', 'user_id = :user_id AND media_id = :media_id', array(':user_id' => $user->id, ':media_id' => $parameters[":id"]));
			}
			
			$json = array(
				'error' => false
			);
		}
	}
}

echo json_encode($json);