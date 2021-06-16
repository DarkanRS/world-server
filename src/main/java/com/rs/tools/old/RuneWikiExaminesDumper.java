package com.rs.tools.old;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.lib.util.Utils;

public class RuneWikiExaminesDumper {

	public static final void main(String[] args) throws IOException {
		System.out.println("Starting..");
		//Cache.init();
		for (int itemId = 0; itemId < Utils.getItemDefinitionsSize(); itemId++) {
			if (!ItemDefinitions.getDefs(itemId).isNoted())
				if (dumpItem(itemId))
					System.out.println("DUMPED ITEM : " + itemId);
				else
					System.out.println("FAILED ITEM: " + itemId + ", " + ItemDefinitions.getDefs(itemId).getName());
		}
	}

	public static boolean dumpItem(int itemId) {
		String pageName = ItemDefinitions.getDefs(itemId).getName();
		if (pageName == null || pageName.equals("null"))
			return false;
		pageName = pageName.replace("(p)", "");
		pageName = pageName.replace("(p+)", "");
		pageName = pageName.replace("(p++)", "");
		pageName = pageName.replaceAll(" ", "_");
		try {
			WebPage page = new WebPage("http://runescape.wikia.com/wiki/" + pageName);
			try {
				page.load();
			} catch (Exception e) {
				System.out.println("Invalid page: " + itemId + ", " + pageName);
				return false;
			}
			boolean isNextLine = false;
			for (String line : page.getLines()) {
				if (!isNextLine) {
					if (line.equals("<th nowrap=\"nowrap\"><a href=\"/wiki/Examine\" title=\"Examine\">Examine</a>"))
						isNextLine = true;
				} else {
					String examine = line.replace("</th><td> ", "");
					examine = examine.replace("</th><td>", "");
					examine = examine.replace("<i> ", "");
					examine = examine.replace("</i> ", "");
					examine = examine.replace("&lt;colour&gt; ", "");
					examine = examine.replace("(bright/thick/warm)", "bright");
					examine = examine.replace("(Temple of Ikov) ", "");
					examine = examine.replace("(Fight Arena) ", "");
					try {
						BufferedWriter writer = new BufferedWriter(new FileWriter("itemExamines.txt", true));
						writer.write(itemId + " - " + examine);
						writer.newLine();
						writer.flush();
						writer.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					return true;
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
			return dumpItem(itemId);
		}
		return false;
	}

}
