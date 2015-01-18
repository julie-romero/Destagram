<?php
require_once('database/db.php');
require_once('model/user.php');

$parameters = array
(
	':login' => null,
	':password' => null
);
foreach($_GET as $key => $value)
{
	$parameters[":$key"] = $value;
}

$json = array(
	'error' => true
);

if (!empty($parameters[':login']) && !empty($parameters[':password'])) {

	$config = require_once('config.php');
	$db = new DB($config['dsn'], $config['username'], $config['password'], $config['options']);
	$login = $parameters[":login"];
	$salt = substr($login ,0,4);
	$parameters[":password"] = sha1($parameters[":password"].$salt);
	$user = $db->find('User', 'user', 'login = :login AND password = :password', $parameters);

	if($user !== false)
	{
		$token = md5(time() . $user->login . $user->password);
		$user->token = $token;

		if($db->update($user, 'user', 'id = :id', array(':id' => $user->id)))
		{
			$json = array(
				'error' => false,
				'token' => $token
			);
		}
	}
}

echo json_encode($json);