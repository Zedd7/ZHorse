package com.github.zedd7.zhorse.database;

import com.github.zedd7.zhorse.ZHorse;
import com.github.zedd7.zhorse.enums.DatabaseEnum;

public class MySQLImporter extends SQLDatabaseImporter {
	
	public static boolean importData(ZHorse zh) {
		if (zh.getCM().getDatabaseType() != DatabaseEnum.MYSQL) {
			SQLDatabaseConnector db = new MySQLConnector(zh);
			return fullImport(zh, db);
		}
		else {
			zh.getLogger().severe("Data import from MySQL to MySQL is not supported, please use a different database !");
		}
		return false;
	}

}
