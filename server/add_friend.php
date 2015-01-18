<?php
require_once('database/db.php');
require_once('model/user.php');
require_once('model/friend.php');
$parameters = array
(
	':token' => null,
	':username' => null
);
foreach($_GET as $key => $value)
{
	$parameters[":$key"] = $value;
}
$json = array(
	'error' => true,
	'code' => 0
);if (!empty($parameters[':token']) && !empty($parameters[':username'])) {
	$config = require_once('config.php');
	$db = new DB($config['dsn'], $config['username'], $config['password'], $config['options']);
	$user1 = $db->find('User', 'user', 'token = :token', array(":token" => $parameters[":token"]));
	$user2 = $db->find('User', 'user', 'login = :username OR pseudo = :username',array(":username" => $parameters[":username"]));
	if($user1 !== false && $user2 !== false)
	{      
		//On vérifie que la personne ne s'ajoute pas elle-même
		if($user1->id == $user2->id)
		{
			$json['code'] = 3;
		}
		else
		{
			// vérification de l'unicité de l'amitié
			$friendVerification = $db->find('Friend', 'friend', 'user_1 = :id1 AND user_2 = :id2', array(":id1" => $user1->id,":id2" => $user2->id));
			if($friendVerification === false)
			{
				$friend = new Friend();
				$friend->user_1 = $user1->id;
				$friend->user_2 = $user2->id;
				$id = $db->insert($friend, 'friend');
				if($id !== false)
				{
					$json = array(
						'error' => false,						'friend' => array(
							'pseudo' => $user2-> pseudo,
							'id' => $user2-> id						)
					);
				}
			}
			else {
			   $json['code'] = 2; 
			}
		}
	}
	else if($user2 === false)
	{
		$json['code'] = 1;
	}}
echo json_encode($json);