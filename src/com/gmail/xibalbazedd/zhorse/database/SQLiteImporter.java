package com.gmail.xibalbazedd.zhorse.database;

import com.gmail.xibalbazedd.zhorse.ZHorse;
import com.gmail.xibalbazedd.zhorse.enums.DatabaseEnum;

public class SQLiteImporter extends SQLDatabaseImporter {
	
	public static boolean importData(ZHorse zh) {
		if (zh.getCM().getDatabaseType() != DatabaseEnum.SQLITE) {
			SQLDatabaseConnector db = new SQLiteConnector(zh);
			return fullImport(zh, db);
		}
		else {
			zh.getLogger().severe("Data import from SQLite to SQLite is not supported, please use a different database !");
		}
		return false;
	}

}
