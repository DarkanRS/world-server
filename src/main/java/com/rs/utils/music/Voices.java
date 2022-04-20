package com.rs.utils.music;

import com.rs.lib.file.JsonFileManager;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@PluginEventHandler
public class Voices {
	public static Map<Integer, Boolean> voicesMarked = new HashMap<>();
	@ServerStartupEvent
	public static void init() {
		try {
			Voice[] voices = JsonFileManager.loadJsonFile(new File("./developer-information/voice.json"), Voice[].class);
			for(Voice voice : voices)
				for(int voiceID : voice.getVoiceIDs())
					voicesMarked.put(voiceID, true);
		} catch (Exception e) {
			System.out.println(e.getStackTrace());
		}
	}
}
