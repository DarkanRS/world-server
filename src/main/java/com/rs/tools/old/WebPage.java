package com.rs.tools.old;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class WebPage {

	private URL url;
	private ArrayList<String> lines;
	private String wholePage = "";

	public WebPage(String url) throws MalformedURLException {
		this.url = new URL(url);
	}

	public void load() throws IOException {
		lines = new ArrayList<String>();
		HttpsURLConnection c = (HttpsURLConnection) url.openConnection();
		c.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
		c.setReadTimeout(3000);
		BufferedReader stream = new BufferedReader(new InputStreamReader(c.getInputStream()));
		String line;
		while ((line = stream.readLine()) != null) {
			if (line.contains("}} {{")) {
				lines.add(line.substring(0, line.indexOf("}} {{")));
				lines.add(line.substring(line.indexOf("}} {{")));
			} else if (line.contains("}}{{")) {
				lines.add(line.substring(0, line.indexOf("}}{{")));
				lines.add(line.substring(line.indexOf("}}{{")));
			} else
				lines.add(line);
			wholePage += line;
		}
		stream.close();
	}

	public void setLines(ArrayList<String> lines) {
		this.lines = lines;
	}

	public ArrayList<String> getLines() {
		return lines;
	}

	public String getPage() {
		return wholePage;
	}
}
