package com.rs.utils.music;

import com.rs.Settings;
import com.rs.lib.file.JsonFileManager;
import com.rs.lib.util.Logger;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;
import com.rs.plugin.annotations.ServerStartupEvent.Priority;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

@PluginEventHandler
public class Voices {//set
	public static Set<Integer> voicesMarked = new HashSet<>();
	
	@ServerStartupEvent(Priority.FILE_IO)
	public static void init() {
		if(!Settings.getConfig().isDebug())
			return;
		try {
			Voice[] voices = JsonFileManager.loadJsonFile(new File("./developer-information/voice.json"), Voice[].class);
			for(Voice voice : voices)
				for(int voiceID : voice.getVoiceIDs())
					voicesMarked.add(voiceID);
		} catch (Exception e) {
			Logger.handle(Voices.class, "init", "Error initializing voices", e);
		}
	}
}
