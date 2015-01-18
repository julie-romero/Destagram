<?php
require_once('database/db.php');
require_once('model/user.php');
require_once('model/media.php');

$parameters = array
(
	':token' => null
);
foreach($_GET as $key => $value)
{
	$parameters[":$key"] = $value;
}

$json = array(
	'error' => true
);

if (!empty($parameters[':token'])) {
	$config = require_once('config.php');
	$db = new DB($config['dsn'], $config['username'], $config['password'], $config['options']);

	$user = $db->find('User', 'user', 'token = :token', array(':token' => $parameters[':token']));

	if($user !== false)
	{
		$sql = "SELECT m.*
				FROM media m, friend f 
				WHERE m.user_id = f.user_2
				AND f.user_1 = :user_id
				ORDER BY m.date DESC
		";
		$medias = $db->requestJoin($sql, array(":user_id" => $user->id));
		
		if($medias !== false)
		{
			$json = array(
				'error' => false,
				'medias' => $medias
			);
		}
	}
}

echo json_encode($json);