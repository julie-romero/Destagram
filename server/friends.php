<?php
require_once('database/db.php');
require_once('model/user.php');
require_once('model/friend.php');

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
		$sql = "SELECT u.pseudo, u.id
				FROM friend f, user u 
				WHERE f.user_2 = u.id
				AND f.user_1 = :user_id
				ORDER BY u.pseudo
		";
		$friends = $db->requestJoin($sql, array(":user_id" => $user->id));
		
		if($friends !== false)
		{
			
			$json = array(
				'error' => false,
				'friends' => $friends
			);
		}
	}
}

echo json_encode($json);