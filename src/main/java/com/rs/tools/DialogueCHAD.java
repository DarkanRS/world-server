package com.rs.tools;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Date;

import com.google.gson.GsonBuilder;
import com.rs.Settings;
import com.rs.cache.Cache;
import com.rs.game.model.entity.player.Controller;
import com.rs.lib.file.JsonFileManager;
import com.rs.lib.json.DateAdapter;
import com.rs.lib.net.packets.Packet;
import com.rs.lib.net.packets.PacketEncoder;
import com.rs.lib.util.PacketAdapter;
import com.rs.lib.util.PacketEncoderAdapter;
import com.rs.lib.util.RecordTypeAdapterFactory;
import com.rs.tools.old.WebPage;
import com.rs.utils.ChatGPT;
import com.rs.utils.json.ControllerAdapter;

public class DialogueCHAD {
	
	public static void main(String[] args) throws IOException {
		JsonFileManager.setGSON(new GsonBuilder()
				.registerTypeAdapter(Controller.class, new ControllerAdapter())
				.registerTypeAdapter(Date.class, new DateAdapter())
				.registerTypeAdapter(PacketEncoder.class, new PacketEncoderAdapter())
				.registerTypeAdapter(Packet.class, new PacketAdapter())
				.registerTypeAdapterFactory(new RecordTypeAdapterFactory())
				.disableHtmlEscaping()
				.setPrettyPrinting()
				.create());
		Settings.loadConfig();
		Cache.init(Settings.getConfig().getCachePath());
		
		poopOutDialogue("Olivia");
	}

	public static void poopOutDialogue(String name) throws MalformedURLException {
		WebPage page = new WebPage("https://runescape.wiki/w/Transcript:"+name+"?action=raw");
		try {
			page.load();
		} catch (Exception e) {
			System.out.println("Invalid page: " + name);
			return;
		}
		String transcript = "";
		for (String line: page.getLines())
			transcript += line + "\r\n";
		
		ChatGPT chat = new ChatGPT();
		
		System.out.println("Feeding code...");
		chat.fineTune("./data/dialogueTraining.jsonl");
		
		System.out.println("Getting constructor...");
		System.out.println(chat.getResponse("Can you generate me a constructor for this transcript? \r\n\r\n " + transcript));
	}
}
