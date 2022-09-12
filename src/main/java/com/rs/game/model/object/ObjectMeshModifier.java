// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
//  Copyright (C) 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.model.object;

import java.util.Arrays;

import com.rs.lib.util.Logger;

public class ObjectMeshModifier {
	
	public static final ObjectMeshModifier RESET = new ObjectMeshModifier();

	private transient GameObject object;
	private int slot = -1;
	private int[] modelIds;
	private int[] modifiedColors;
	private int[] modifiedTextures;
	
	private ObjectMeshModifier() {

	}

	public ObjectMeshModifier(GameObject object) {
		this.object = object;
		if (object.getDefinitions().types != null) {
            for (int i = 0; i < object.getDefinitions().types.length; i++) {
                if (object.getType() == object.getDefinitions().types[i]) {
                	slot = i;
                    break;
                }
            }
        }
		if (slot == -1)
			Logger.error(ObjectMeshModifier.class, "constructor()", "Object " + object + " has no slot type.");
	}
	
	public ObjectMeshModifier setModel(int index, int modelId) {
		if (modelIds == null) {
			modelIds = new int[object.getDefinitions().modelIds[slot].length];
			System.arraycopy(object.getDefinitions().modelIds[slot], 0, modelIds, 0, modelIds.length);
		}
		if (index < 0 || index >= modelIds.length)
			throw new RuntimeException("Index " + index + " for models of [" + slot + "] " + object.getDefinitions().id + " is out of bounds.");
		if (modelId == -1)
			modelIds[index] = object.getDefinitions().modelIds[slot][index];
		else
			modelIds[index] = modelId;
		return this;
	}

	public ObjectMeshModifier addModels(int... models) {
		if (Arrays.equals(models, object.getDefinitions().modelIds[slot]))
			return this;
		modelIds = new int[object.getDefinitions().modelIds[slot].length];
		System.arraycopy(object.getDefinitions().modelIds[slot], 0, modelIds, 0, modelIds.length);

		for (int i = 0;i < object.getDefinitions().modelIds[slot].length;i++) {
			if (i >= models.length)
				continue;
			modelIds[i] = models[i];
		}
		return this;
	}
	
	public ObjectMeshModifier setColor(int index, int color) {
		if (modifiedColors == null && object.getDefinitions().modifiedColors != null) {
			modifiedColors = new int[object.getDefinitions().modifiedColors.length];
			for (int i = 0;i < modifiedColors.length;i++)
				modifiedColors[i] = object.getDefinitions().modifiedColors[i];
		}
		if (modifiedColors == null || index < 0 || index >= modifiedColors.length)
			throw new RuntimeException("Index " + index + " for models of " + object.getDefinitions().id + " is out of bounds.");
		if (color == -1)
			modifiedColors[index] = object.getDefinitions().modifiedColors[index];
		else
			modifiedColors[index] = (short) color;
		return this;
	}
	
	public ObjectMeshModifier addColors(int... colors) {
		if (object.getDefinitions().modifiedColors != null) {
			modifiedColors = new int[object.getDefinitions().modifiedColors.length];
			for (int i = 0;i < modifiedColors.length;i++)
				modifiedColors[i] = object.getDefinitions().modifiedColors[i];
		}
		if (modifiedColors == null)
			return this;
		for (int i = 0;i < modifiedColors.length;i++) {
			if (i >= colors.length)
				break;
			modifiedColors[i] = (short) colors[i];
		}
		return this;
	}
	
	public ObjectMeshModifier setTexture(int index, int texId) {
		if (modifiedTextures == null && object.getDefinitions().modifiedTextures != null) {
			modifiedTextures = new int[object.getDefinitions().modifiedTextures.length];
			for (int i = 0;i < modifiedTextures.length;i++)
				modifiedTextures[i] = object.getDefinitions().modifiedTextures[i];
		}
		if (modifiedTextures == null || index < 0 || index >= modifiedTextures.length)
			throw new RuntimeException("Index " + index + " for models of " + object.getDefinitions().id + " is out of bounds.");
		if (texId == -1)
			modifiedTextures[index] = object.getDefinitions().modifiedTextures[index];
		else
			modifiedTextures[index] = (short) texId;
		return this;
	}

	public ObjectMeshModifier addTextures(int... textures) {
		if (object.getDefinitions().modifiedTextures != null) {
			modifiedTextures = new int[object.getDefinitions().modifiedTextures.length];
			for (int i = 0;i < modifiedTextures.length;i++)
				modifiedTextures[i] = object.getDefinitions().modifiedTextures[i];
		}
		if (modifiedTextures == null)
			return this;
		for (int i = 0;i < modifiedTextures.length;i++) {
			if (i >= textures.length)
				break;
			modifiedTextures[i] = (short) textures[i];
		}
		return this;
	}
	
	public GameObject getObject() {
		return object;
	}
	
	public int[] getModelIds() {
		return modelIds;
	}

	public int[] getModifiedColors() {
		return modifiedColors;
	}

	public int[] getModifiedTextures() {
		return modifiedTextures;
	}
}
