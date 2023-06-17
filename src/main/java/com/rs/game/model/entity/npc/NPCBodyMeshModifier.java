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
package com.rs.game.model.entity.npc;

import com.rs.cache.loaders.NPCDefinitions;
import com.rs.lib.io.OutputStream;

import java.util.Arrays;

public class NPCBodyMeshModifier {
	
	public static final NPCBodyMeshModifier RESET = new NPCBodyMeshModifier();

	private static final byte RESET_FLAG = 0x1, MODEL_FLAG = 0x2, COLOR_FLAG = 0x4, TEXTURE_FLAG = 0x8;

	private NPCDefinitions defs;
	private int[] modelIds;
	private short[] modifiedColors;
	private short[] modifiedTextures;
	
	private NPCBodyMeshModifier() {
		
	}

	public NPCBodyMeshModifier(NPCDefinitions defs) {
		this.defs = defs;
	}
	
	public NPCBodyMeshModifier setModel(int index, int modelId) {
		if (modelIds == null) {
			modelIds = new int[defs.modelIds.length];
			System.arraycopy(defs.modelIds, 0, modelIds, 0, modelIds.length);
		}
		if (index < 0 || index >= modelIds.length)
			throw new RuntimeException("Index " + index + " for models of " + defs.id + " is out of bounds.");
		if (modelId == -1)
			modelIds[index] = defs.modelIds[index];
		else
			modelIds[index] = modelId;
		return this;
	}

	public NPCBodyMeshModifier addModels(int... models) {
		if (Arrays.equals(models, defs.modelIds))
			return this;
		modelIds = new int[defs.modelIds.length];
		System.arraycopy(defs.modelIds, 0, modelIds, 0, modelIds.length);

		for (int i = 0;i < defs.modelIds.length;i++) {
			if (i >= models.length)
				continue;
			modelIds[i] = models[i];
		}
		return this;
	}
	
	public NPCBodyMeshModifier setColor(int index, int color) {
		if (modifiedColors == null && defs.modifiedColors != null) {
			modifiedColors = new short[defs.modifiedColors.length];
			System.arraycopy(defs.modifiedColors, 0, modifiedColors, 0, modifiedColors.length);
		}
		if (modifiedColors == null || index < 0 || index >= modifiedColors.length)
			throw new RuntimeException("Index " + index + " for models of " + defs.id + " is out of bounds.");
		if (color == -1)
			modifiedColors[index] = defs.modifiedColors[index];
		else
			modifiedColors[index] = (short) color;
		return this;
	}
	
	public NPCBodyMeshModifier addColors(int... colors) {
		if (defs.modifiedColors != null) {
			modifiedColors = new short[defs.modifiedColors.length];
			System.arraycopy(defs.modifiedColors, 0, modifiedColors, 0, modifiedColors.length);
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
	
	public NPCBodyMeshModifier setTexture(int index, int texId) {
		if (modifiedTextures == null && defs.modifiedTextures != null) {
			modifiedTextures = new short[defs.modifiedTextures.length];
			System.arraycopy(defs.modifiedTextures, 0, modifiedTextures, 0, modifiedTextures.length);
		}
		if (modifiedTextures == null || index < 0 || index >= modifiedTextures.length)
			throw new RuntimeException("Index " + index + " for models of " + defs.id + " is out of bounds.");
		if (texId == -1)
			modifiedTextures[index] = defs.modifiedTextures[index];
		else
			modifiedTextures[index] = (short) texId;
		return this;
	}

	public NPCBodyMeshModifier addTextures(int... textures) {
		if (defs.modifiedTextures != null) {
			modifiedTextures = new short[defs.modifiedTextures.length];
			System.arraycopy(defs.modifiedTextures, 0, modifiedTextures, 0, modifiedTextures.length);
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

	public int encode(OutputStream stream) {
		int flags = getModificationFlags();
		if (flags == 0)
			return -1;

		stream.write128Byte(flags);
		if ((flags & MODEL_FLAG) != 0)
			encodeModels(stream);
		if ((flags & COLOR_FLAG) != 0)
			encodeColors(stream);
		if ((flags & TEXTURE_FLAG) != 0)
			encodeTextures(stream);
		return flags;
	}

	public void encodeModels(OutputStream stream) {
		for (int i = 0;i < modelIds.length;i++)
			stream.writeBigSmart(modelIds[i]);
	}

	public void encodeColors(OutputStream stream) {
		for (int i = 0;i < modifiedColors.length;i++)
			stream.writeShort(modifiedColors[i]);
	}

	public void encodeTextures(OutputStream stream) {
		for (int i = 0;i < modifiedTextures.length;i++)
			stream.writeShort(modifiedTextures[i]);
	}

	public int getModificationFlags() {
		int flags = 0;
		if (modelIds != null)
			flags |= MODEL_FLAG;
		if (modifiedColors != null)
			flags |= COLOR_FLAG;
		if (modifiedTextures != null)
			flags |= TEXTURE_FLAG;
		if (flags == 0)
			flags |= RESET_FLAG;
		return flags;
	}
}
