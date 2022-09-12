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
package com.rs.game.model.entity.player;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.lib.io.OutputStream;

public class ItemMeshModifier {

	private static final byte BODY_MODEL_FLAG = 0x1, HEAD_MODEL_FLAG = 0x2, COLOR_FLAG = 0x4, TEXTURE_FLAG = 0x8;
	private static final byte EMPTY_VALUE = 0xF;

	private ItemDefinitions defs;
	private int slotFlag;
	private int[] maleBody;
	private int[] femaleBody;
	private int[] maleHead;
	private int[] femaleHead;
	private int[] modifiedColors;
	private int[] modifiedTextures;

	public ItemMeshModifier(ItemDefinitions defs, int slotFlag) {
		this.defs = defs;
		this.slotFlag = slotFlag;
	}

	public ItemMeshModifier addBodyModels(int[] maleBodies, int[] femaleBodies) {
		maleBody = new int[3];
		femaleBody = new int[3];
		maleBody[0] = defs.maleEquip1;
		maleBody[1] = defs.maleEquip2;
		maleBody[2] = defs.maleEquip3;
		femaleBody[0] = defs.femaleEquip1;
		femaleBody[1] = defs.femaleEquip2;
		femaleBody[2] = defs.femaleEquip3;

		for (int i = 0;i < maleBody.length;i++) {
			if (i >= maleBodies.length)
				continue;
			maleBody[i] = maleBodies[i];
			femaleBody[i] = femaleBodies[i];
		}
		return this;
	}

	public ItemMeshModifier addBodyModels(int... bodies) {
		return addBodyModels(bodies, bodies);
	}

	public ItemMeshModifier addHeadModels(int[] maleHeads, int[] femaleHeads) {
		maleHead = new int[2];
		femaleHead = new int[2];
		maleHead[0] = defs.maleHead1;
		maleHead[1] = defs.maleHead2;
		femaleHead[0] = defs.femaleHead1;
		femaleHead[1] = defs.femaleHead2;

		for (int i = 0;i < maleHead.length;i++) {
			if (i >= maleHeads.length)
				continue;
			maleHead[i] = maleHeads[i];
			femaleHead[i] = femaleHeads[i];
		}
		return this;
	}

	public ItemMeshModifier addHeadModels(int... heads) {
		return addHeadModels(heads, heads);
	}

	public ItemMeshModifier addColors(int... colors) {
		if (defs.modifiedModelColors != null) {
			modifiedColors = new int[defs.modifiedModelColors.length];
			System.arraycopy(defs.modifiedModelColors, 0, modifiedColors, 0, modifiedColors.length);
		}
		for (int i = 0;i < modifiedColors.length;i++) {
			if (i >= colors.length)
				break;
			modifiedColors[i] = colors[i];
		}
		return this;
	}

	public ItemMeshModifier addTextures(int... textures) {
		if (defs.modifiedTextureIds != null) {
			modifiedTextures = new int[defs.modifiedTextureIds.length];
			System.arraycopy(defs.modifiedTextureIds, 0, modifiedTextures, 0, modifiedTextures.length);
		}
		if (modifiedTextures == null)
			return this;
		for (int i = 0;i < modifiedTextures.length;i++) {
			if (i >= textures.length)
				break;
			modifiedTextures[i] = textures[i];
		}
		return this;
	}

	public int encode(OutputStream stream) {
		int flags = getModificationFlags();
		if (flags == 0)
			return -1;

		stream.writeByte(flags);
		if ((flags & BODY_MODEL_FLAG) != 0)
			encodeBodyModels(stream);
		if ((flags & HEAD_MODEL_FLAG) != 0)
			encodeHeadModels(stream);
		if ((flags & COLOR_FLAG) != 0)
			encodeColors(stream);
		if ((flags & TEXTURE_FLAG) != 0)
			encodeTextures(stream);
		return slotFlag;
	}

	public void encodeBodyModels(OutputStream stream) {
		stream.writeBigSmart(maleBody[0]);
		stream.writeBigSmart(femaleBody[0]);
		if (defs.maleEquip2 != -1 || defs.femaleEquip2 != -1) {
			stream.writeBigSmart(maleBody[1]);
			stream.writeBigSmart(femaleBody[1]);
		}
		if (defs.maleEquip3 != -1 || defs.femaleEquip3 != -1) {
			stream.writeBigSmart(maleBody[2]);
			stream.writeBigSmart(femaleBody[2]);
		}
	}

	public void encodeHeadModels(OutputStream stream) {
		stream.writeBigSmart(maleHead[0]);
		stream.writeBigSmart(femaleHead[0]);
		if (defs.maleHead2 != -1 || defs.femaleHead2 != -1) {
			stream.writeBigSmart(maleHead[1]);
			stream.writeBigSmart(femaleHead[1]);
		}
	}

	public void encodeColors(OutputStream stream) {
		int slots = 0;
		for (int i = 1;i < 4;i++)
			if (i < modifiedColors.length)
				slots |= i << (i * 4);
			else
				slots |= EMPTY_VALUE << (i * 4);
		stream.writeShort(slots);
		for (int i = 0;i < 4;i++)
			if (i < modifiedColors.length)
				stream.writeShort(modifiedColors[i]);
	}

	public void encodeTextures(OutputStream stream) {
		int slots = 0;
		for (int i = 1;i < 2;i++)
			if (i < modifiedTextures.length)
				slots |= i << (i * 4);
			else
				slots |= EMPTY_VALUE << (i * 4);
		stream.writeByte(slots);
		for (int i = 0;i < 2;i++)
			if (i < modifiedTextures.length)
				stream.writeShort(modifiedTextures[i]);
	}

	public int getModificationFlags() {
		int flags = 0;
		if (maleBody != null || femaleBody != null)
			flags |= BODY_MODEL_FLAG;
		if (maleHead != null || femaleHead != null)
			flags |= HEAD_MODEL_FLAG;
		if (modifiedColors != null)
			flags |= COLOR_FLAG;
		if (modifiedTextures != null)
			flags |= TEXTURE_FLAG;
		return flags;
	}
}
