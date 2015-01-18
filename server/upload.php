<?php
require_once('database/db.php');
require_once('model/user.php');
require_once('model/media.php');
$message = '';
$parameters = array
(
	':token' => null,
	':file' => null,
	':titre' => null,
	':description' => null
);
foreach($_POST as $key => $value)
{
	$parameters[":$key"] = $value;
	$message .= $key+" : ". $value . " | ";
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
		// ***** sauvegarde du fichier physique *****
			
		// configuration de l'upload
		$types = array('gif', 'png', 'jpg', 'jpeg', 'mp4', 'avi');
		$maxSize = 50000000; 	// TAILLE LIMITE A ADAPTER POUR LES IMAGES ET VIDEO
		$target_dir = "uploads/";
		$tmp_file = $target_dir . basename($_FILES["file"]["name"]);
		$uploadOk = 1;
		$imageFileType = pathinfo($tmp_file,PATHINFO_EXTENSION);
		$imageName = $user->id . '_' .time();
		$target_file = $target_dir . $imageName . '.' . $imageFileType;
		
		// Vérification de la taille du fichier
		if ($_FILES["file"]["size"] > $maxSize) {
			$message .= "Le fichier est trop volumineux.";
			$uploadOk = 0;
		}
		// Vérification du type du fichier
		if(!in_array($imageFileType, $types) ) {
			$message .= "Seuls les types GIF, PNG, JPEG, et JPG pour les images | MP4 et AVI pour les vidéos sont autorisés.";
			$uploadOk = 0;
		}
		// Vérification des erreurs
		if ($uploadOk == 0) {
			$json['message'] = $message;
		// Si tout est OK, on tente d'upload le fichier
		} else {
			if (move_uploaded_file($_FILES["file"]["tmp_name"], $target_file)) {
				$message .= "Fichier uploadé avec succès";
				
				// sauvegarde du fichier en base de données
				$media = new Media();
				$media->name = $imageName;
				$media->extension = $imageFileType;
				$media->titre = $parameters[':titre'];
				$media->description = $parameters[':description'];
				$media->user_id = $user->id;
				$media->date = date("Y-m-d H:i:s");
				
				$id = $db->insert($media, 'media');
				if($id !== false)
				{
					$json = array(
						'error' => false,
						'message' => $message
					);
				}
				
				
			} else {
				$message .= "Une erreur est survenue lors du processus d'upload du fichier.";
			}
		}
		 
		
	}
}
else
	$message .= "Le token est manquant!";
$json['message'] = $message;
echo json_encode($json);