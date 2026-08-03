package com.peperehobbits01.rejei.compat.rei;

import com.simibubi.create.foundation.gui.AllGuiTextures;

import me.shedaniel.rei.api.client.gui.DrawableConsumer;
import net.minecraft.client.gui.GuiGraphics;

public class ScreenResourceWrapper implements DrawableConsumer {

	private final AllGuiTextures resource;

	public ScreenResourceWrapper(AllGuiTextures resource) {
		this.resource = resource;
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		resource.render(graphics, 0, 0);
	}

}
