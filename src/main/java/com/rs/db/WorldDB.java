package com.rs.db;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.rs.Settings;
import com.rs.db.collection.Highscores;
import com.rs.db.collection.Players;

public class WorldDB {
	
	public static boolean ACTIVE = false;
	
	private static MongoClient CLIENT;
	private static MongoDatabase WORLD_DATABASE;
	
	private static ExecutorService DB_EXECUTOR = Executors.newSingleThreadExecutor(new DBThreadFactory());
	
	public static void init() {
		try {
			Logger logger = (Logger) Logger.getLogger("org.mongodb.driver.cluster");
			logger.setLevel(Level.OFF);
			Logger.getLogger("log").setLevel(Level.OFF);
			CLIENT = MongoClients.create(Settings.getConfig().getMongoDb());
			WORLD_DATABASE = CLIENT.getDatabase("darkan-server");
			Players.init();
			Highscores.init();
			ACTIVE = true;
		} catch(Exception e) {
			e.printStackTrace();
			System.out.println("Error connecting to mongo database.");
			ACTIVE = false;
		}
	}
	
	public static MongoDatabase getDatabase() {
		return WORLD_DATABASE;
	}
	
	public static void execute(Runnable task) {
		DB_EXECUTOR.execute(task);
	}

}
