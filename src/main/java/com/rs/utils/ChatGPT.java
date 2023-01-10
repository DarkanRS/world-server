package com.rs.utils;

import com.rs.Settings;
import com.theokanning.openai.OpenAiService;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.CompletionResult;
import com.theokanning.openai.file.File;
import com.theokanning.openai.finetune.FineTuneRequest;
import com.theokanning.openai.finetune.FineTuneResult;

public class ChatGPT {
	private OpenAiService service;
	
	public ChatGPT() {
		service = new OpenAiService(Settings.getConfig().getChatGPTApiKey(), 0);
	}
	
	public String getResponse(String input) {
		try {
			CompletionRequest req = CompletionRequest.builder()
					.prompt(input)
					.model("text-davinci-003")
					.temperature(0.0)
					.maxTokens(300)
					.topP(1.0)
					.frequencyPenalty(0.0)
					.presencePenalty(0.0)
					.user("trenterinomemerino")
					.build();
	
			CompletionResult choices = service.createCompletion(req);
			if (choices.getChoices().isEmpty())
				return "No response available.";
			return choices.getChoices().get(0).getText();
		} catch(Throwable e) {
			e.printStackTrace();
			return "Error: " + e.getMessage();
		}
	}

	public void fineTune(String jsonlPath) {
		try {
			File file = new File();
			service.uploadFile("dialogueTraining", jsonlPath);
			FineTuneRequest req = FineTuneRequest.builder()
					.model("text-davinci-003")
					.suffix("darkan-dialogue-create")
					.trainingFile("dialogueTraining.jsonl")
					.build();
			
			FineTuneResult result = service.createFineTune(req);
			System.out.println(result.toString());
		} catch(Throwable e) {
			e.printStackTrace();
		}
	}
}
