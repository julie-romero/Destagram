<?php
require_once('database/db.php');
require_once('model/user.php');

$parameters = array
(
	':login' => null,
	':password' => null,
	':pseudo' => null
);

foreach($_GET as $key => $value)
{
	$parameters[":$key"] = $value;
}

$json = array(
	'error' => true,
	'code' => 0
);

if (!empty($parameters[':login']) && !empty($parameters[':password']) && !empty($parameters[':pseudo'])) {

	$config = require_once('config.php');
	$db = new DB($config['dsn'], $config['username'], $config['password'], $config['options']);

	// vérification de l'unicité du "login"
	$userVerification = $db->find('User', 'user', 'login = :login', array(":login" => $parameters[":login"]));
	// vérification de l'unicité du "pseudo"
	$userVerification2 = $db->find('User', 'user', 'pseudo = :pseudo OR login =:pseudo', array(":pseudo" => $parameters[":pseudo"]));
	if ($userVerification !== false) {
		$json['code'] = 1;
	}if($userVerification2 !== false) {
		$json['code'] = 2;
	} else {
		$user = new User();
		$user->login = $parameters[':login'];
		$salt = substr($user->login,0,4);
		$user->password = sha1($parameters[':password'].$salt);
		$user->pseudo = $parameters[':pseudo'];
		$id = $db->insert($user, 'user');
		if($id !== false)
		{
			$user->id = (int) $id;
			
			$json = array(
				'error' => false,
				'message' => $user
			);
		}
	}
}

echo json_encode($json);