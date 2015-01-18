<?php
class DB
{
	private $pdo;

	public function DB($dsn, $username, $password, $options = null)
	{
		$this->pdo = new PDO($dsn, $username, $password, $options);
	}

	public function find($class, $table, $where, $whereArgs = array(), $order = null)
	{
		$sql = "SELECT * FROM $table WHERE $where";
		if($order != null)
		{
			$sql .= " ORDER BY $order";
		}
		$sql .= ' LIMIT 1';
		$pdoStatement = $this->pdo->prepare($sql);
		$pdoStatement->execute($whereArgs);
		return $pdoStatement->fetchObject($class);
	}

	public function search($class, $table, $where, $whereArgs = array(), $order = null)
	{
		$sql = "SELECT * FROM $table WHERE $where";
		if($order != null)
		{
			$sql .= " ORDER BY $order";
		}
		$pdoStatement = $this->pdo->prepare($sql);
		$pdoStatement->execute($whereArgs);
		return $pdoStatement->fetchAll(PDO::FETCH_CLASS, $class);
	}
	
	public function requestJoin($sql, $whereArgs = array())
	{
		$pdoStatement = $this->pdo->prepare($sql);
		$pdoStatement->execute($whereArgs);
		return $pdoStatement->fetchAll(PDO::FETCH_ASSOC);
	}

	public function insert($model, $table)
	{
		$fields = '';
		$values = '';
		$whereArgs = array();
		foreach($model->toDB() as $name => $value)
		{
			if($fields != '')
			{
				$fields .= ', ';
				$values .= ', ';
			}
			$fields .= $name;
			$values .= ":$name";
			$whereArgs[":$name"] = $value;
		}
		$sql = "INSERT INTO $table ($fields) VALUES ($values)";
		$pdoStatement = $this->pdo->prepare($sql);
		$result = $pdoStatement->execute($whereArgs);
		if(!$result)
		{
			return false;
		}
		return $this->pdo->lastInsertId();

	}

	public function update($model, $table, $where, $whereArgs = array())
	{
		$set = '';
		foreach($model as $name => $value)
		{
			if($set != '')
			{
				$set .= ', ';
			}
			$set .= "$name = :$name";
			$whereArgs[":$name"] = $value;
		}
		$sql = "UPDATE $table SET $set WHERE $where";
		$pdoStatement = $this->pdo->prepare($sql);
		return $pdoStatement->execute($whereArgs);
	}

	public function delete($table, $where, $whereArgs = array())
	{
		$sql = "DELETE FROM $table WHERE $where";
		$pdoStatement = $this->pdo->prepare($sql);
		return $pdoStatement->execute($whereArgs);
	}
}