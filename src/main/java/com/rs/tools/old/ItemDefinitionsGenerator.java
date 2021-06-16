package com.rs.tools.old;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.lib.game.Item;
import com.rs.lib.util.Utils;
import com.rs.utils.ItemExamines;

public class ItemDefinitionsGenerator {

	@SuppressWarnings("resource")
	public static final void main(String[] args) throws IOException {
		ItemExamines.init();
		BufferedWriter writer = new BufferedWriter(new FileWriter("itemDefinitions.xml"));
		writer.write("<list>");
		writer.newLine();
		writer.flush();
		//Cache.init();
		for (int itemId = 0; itemId < Utils.getItemDefinitionsSize(); itemId++) {
			ItemDefinitions itemDef = ItemDefinitions.getDefs(itemId);
			writer.write("<itemDefinition>");
			writer.newLine();
			writer.flush();

			File bonuses = new File("bonuses/" + itemId + ".txt");
			if (bonuses.exists()) {
				writer.write("<bonus>");
				writer.newLine();
				writer.flush();

				BufferedReader reader = new BufferedReader(new FileReader(bonuses));
				reader.readLine();
				for (int i = 0; i < 5; i++) {
					writer.write("<int>" + Integer.valueOf(reader.readLine()) + "</int>");
					writer.newLine();
					writer.flush();
				}
				reader.readLine();
				for (int i = 0; i < 5; i++) {
					writer.write("<int>" + Integer.valueOf(reader.readLine()) + "</int>");
					writer.newLine();
					writer.flush();
				}
				reader.readLine();
				reader.readLine();
				reader.readLine();
				reader.readLine();
				reader.readLine();
				reader.readLine();
				writer.write("<int>" + Integer.valueOf(reader.readLine()) + "</int>");
				writer.newLine();
				writer.flush();
				int rangedstr = Integer.valueOf(reader.readLine());
				writer.write("<int>" + Integer.valueOf(reader.readLine()) + "</int>");
				writer.newLine();
				writer.flush();
				writer.write("<int>" + rangedstr + "</int>");
				writer.newLine();
				writer.flush();
				writer.write("</bonus>");
				writer.newLine();
				writer.flush();
			} else {
				writer.write("<bonus>");
				writer.newLine();
				writer.flush();
				for (int i = 0; i < 13; i++) {
					writer.write("<int>" + 0 + "</int>");
					writer.newLine();
					writer.flush();
				}
				writer.write("</bonus>");
				writer.newLine();
				writer.flush();
			}
			writer.write("<examine>" + ItemExamines.getExamine(new Item(itemId, 1)) + "</examine>");
			writer.newLine();
			writer.flush();
			writer.write("<id>" + itemId + "</id>");
			writer.newLine();
			writer.flush();
			String name = itemDef.getName() == null ? "null" : itemDef.getName();
			name = name.replaceAll("&", "e");
			writer.write("<name>" + name + "</name>");
			writer.newLine();
			writer.flush();

			writer.write("<stackable>" + itemDef.isStackable() + "</stackable>");
			writer.newLine();
			writer.flush();
			writer.write("<noted>" + itemDef.isNoted() + "</noted>");
			writer.newLine();
			writer.flush();
			writer.write("</itemDefinition>");
			writer.newLine();
			writer.flush();
		}
		writer.write("</list>");
		writer.newLine();
		writer.flush();

	}
}
