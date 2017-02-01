package eu.reborn_minecraft.zhorse.database;

import eu.reborn_minecraft.zhorse.ZHorse;
import eu.reborn_minecraft.zhorse.enums.DatabaseEnum;

public class SQLiteImporter extends SQLDatabaseImporter {
	
	public static boolean importData(ZHorse zh) {
		System.out.println(1); //
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
