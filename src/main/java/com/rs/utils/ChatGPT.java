package com.rs.utils;

import com.rs.Settings;
import com.theokanning.openai.OpenAiService;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.CompletionResult;

public class ChatGPT {
	private OpenAiService service;
	
	public ChatGPT() {
		service = new OpenAiService(Settings.getConfig().getChatGPTApiKey(), 0);
	}
	
	public String getResponse(String input) {
		try {
			CompletionRequest completionRequest = CompletionRequest.builder()
					.prompt(input)
					.model("text-davinci-003")
					.temperature(0.7)
					.maxTokens(2048)
					.user("trenterinomemerino")
					.build();
	
			CompletionResult choices = service.createCompletion(completionRequest);
			if (choices.getChoices().isEmpty()) {
				return "No response available.";
			}
			return choices.getChoices().get(0).getText();
		} catch(Throwable e) {
			e.printStackTrace();
			return "Error: " + e.getMessage();
		}
	}
}
