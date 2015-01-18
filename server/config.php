<?php

return array
(
	'dsn' => 'mysql:dbname=what;host=mysql.bidule',
	'username' => 'usernom',
	'password' => 'wordpass',
	'options' => array
	(
		PDO::MYSQL_ATTR_INIT_COMMAND => "SET NAMES 'utf8'"
	)
);
