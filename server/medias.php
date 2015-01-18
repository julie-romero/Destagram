<?php
require_once('database/db.php');
require_once('model/user.php');
require_once('model/media.php');

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

if (!empty($parameters[':token'])) {
	$config = require_once('config.php');
	$db = new DB($config['dsn'], $config['username'], $config['password'], $config['options']);

	$user = $db->find('User', 'user', 'token = :token', array(':token' => $parameters[':token']));

	if($user !== false)
	{
		// si l'id n'est pas renseigné, on redirige vers le profil de l'utilisateur courant
		if (empty($parameters[':id'])) {
			$parameters[':id'] = $user->id;
		}
	
		$medias = $db->search('Media', 'media', 'user_id = :user_id', array(":user_id" => $parameters[':id']), "media.date DESC");
		
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