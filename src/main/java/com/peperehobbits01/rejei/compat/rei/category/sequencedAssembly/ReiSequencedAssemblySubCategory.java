package com.peperehobbits01.rejei.compat.rei.category.sequencedAssembly;

import static com.peperehobbits01.rejei.compat.rei.category.CreateRecipeCategory.basicSlot;
import static com.peperehobbits01.rejei.compat.rei.category.CreateRecipeCategory.setFluidTooltip;

import java.util.Arrays;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.peperehobbits01.rejei.compat.rei.category.CreateRecipeCategory;
import com.peperehobbits01.rejei.compat.rei.category.animations.*;
import com.peperehobbits01.rejei.compat.rei.category.animations.*;
import com.simibubi.create.content.kinetics.deployer.DeployerApplicationRecipe;
import com.simibubi.create.content.processing.sequenced.IAssemblyRecipe;
import com.simibubi.create.content.processing.sequenced.SequencedRecipe;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.utility.CreateLang;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;

import me.shedaniel.math.Point;
import me.shedaniel.rei.api.client.gui.widgets.Slot;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;

public abstract class ReiSequencedAssemblySubCategory {

	private int width;

	public ReiSequencedAssemblySubCategory(int width) {
		this.width = width;
	}

	public int getWidth() {
		return width;
	}

	public int addItemIngredients(SequencedRecipe<?> recipe, List<Widget> widgets, int x, int index, Point origin) {
		return 0;
	}

	public int addFluidIngredients(SequencedRecipe<?> recipe, List<Widget> widgets, int x, int index, Point origin) {
		return 0;
	}

	public abstract void draw(SequencedRecipe<?> recipe, GuiGraphics graphics, double mouseX, double mouseY, int index);

	public static class AssemblyPressing extends ReiSequencedAssemblySubCategory {

		AnimatedPress press;

		public AssemblyPressing() {
			super(25);
			press = new AnimatedPress(false);
		}

		@Override
		public void draw(SequencedRecipe<?> recipe, GuiGraphics graphics, double mouseX, double mouseY, int index) {
			PoseStack ms = graphics.pose();
			press.offset = index;
			ms.pushPose();
			ms.translate(-5, 50, 0);
			ms.scale(.6f, .6f, .6f);
			press.draw(graphics, getWidth() / 2, 0);
			ms.popPose();
		}

	}

	public static class AssemblySpouting extends ReiSequencedAssemblySubCategory {

		AnimatedSpout spout;

		public AssemblySpouting() {
			super(25);
			spout = new AnimatedSpout();
		}

		@Override
		public int addFluidIngredients(SequencedRecipe<?> recipe, List<Widget> widgets, int x, int index, Point origin) {
			if (recipe.getRecipe().getFluidIngredients().isEmpty())
				return 0;
			FluidIngredient fluidIngredient = recipe.getRecipe()
				.getFluidIngredients()
				.getFirst().ingredient();
			net.neoforged.neoforge.fluids.FluidStack[] stacks = fluidIngredient.getStacks();
			if (stacks.length == 0)
				return 0;
			// Always pass 0 to the fluid ingredient get, because spouting only supports one fluid per step
			// and the passed index to this method will produce an out-of-bounds exception if used
			Slot fluidSlot = basicSlot(x + 4, 15, origin).markInput().entries(EntryIngredients.of(CreateRecipeCategory.convertToREIFluid(stacks[0])));
			CreateRecipeCategory.setFluidRenderRatio(fluidSlot);
			setFluidTooltip(fluidSlot);
			widgets.add(fluidSlot);
			return 1;
		}

		@Override
		public void draw(SequencedRecipe<?> recipe, GuiGraphics graphics, double mouseX, double mouseY, int index) {
			PoseStack ms = graphics.pose();
			spout.offset = index;
			AllGuiTextures.JEI_SLOT.render(graphics, 3, 14);
			ms.pushPose();
			ms.translate(-7, 50, 0);
			ms.scale(.75f, .75f, .75f);
			spout.withFluids(Arrays.asList(recipe.getRecipe()
				.getFluidIngredients()
				.get(0)
				.ingredient()
				.getStacks()))
				.draw(graphics, getWidth() / 2, 0);
			ms.popPose();
		}

	}

	public static class AssemblyDeploying extends ReiSequencedAssemblySubCategory {

		AnimatedDeployer deployer;

		public AssemblyDeploying() {
			super(25);
			deployer = new AnimatedDeployer();
		}

		@Override
		public int addItemIngredients(SequencedRecipe<?> recipe, List<Widget> widgets, int x, int index, Point origin) {
			EntryIngredient entryIngredient = EntryIngredients.ofItemStacks(Arrays.asList(recipe.getRecipe()
					.getIngredients()
					.get(1)
					.getItems()));
			entryIngredient.forEach(entryStack -> {
				IAssemblyRecipe contained = recipe.getAsAssemblyRecipe();
				if (contained instanceof DeployerApplicationRecipe && ((DeployerApplicationRecipe) contained).shouldKeepHeldItem()) {
					entryStack.tooltip(CreateLang.translateDirect("recipe.deploying.not_consumed")
							.withStyle(ChatFormatting.GOLD));
				}
			});
			widgets.add(basicSlot(origin.x + x + 4, origin.y + 15)
					.markInput()
					.entries(entryIngredient));

			return 1;
		}

		@Override
		public void draw(SequencedRecipe<?> recipe, GuiGraphics graphics, double mouseX, double mouseY, int index) {
			PoseStack ms = graphics.pose();
			deployer.offset = index;
			ms.pushPose();
			ms.translate(-7, 50, 0);
			ms.scale(.75f, .75f, .75f);
			deployer.draw(graphics, getWidth() / 2, 0);
			ms.popPose();
			AllGuiTextures.JEI_SLOT.render(graphics, 3, 14);
		}

	}

	public static class AssemblyCutting extends ReiSequencedAssemblySubCategory {

		AnimatedSaw saw;

		public AssemblyCutting() {
			super(25);
			saw = new AnimatedSaw();
		}

		@Override
		public void draw(SequencedRecipe<?> recipe, GuiGraphics graphics, double mouseX, double mouseY, int index) {
			PoseStack ms = graphics.pose();
			ms.pushPose();
			ms.translate(0, 51.5f, 0);
			ms.scale(.6f, .6f, .6f);
			saw.draw(graphics, getWidth() / 2, 30);
			ms.popPose();
		}

	}

	public static class AssemblyCrafting extends ReiSequencedAssemblySubCategory {

		AnimatedCrafter crafter;

		public AssemblyCrafting() {
			super(25);
			crafter = new AnimatedCrafter();
		}

		@Override
		public void draw(SequencedRecipe<?> recipe, GuiGraphics graphics, double mouseX, double mouseY, int index) {
			PoseStack ms = graphics.pose();
			ms.pushPose();
			ms.translate(-7, 50, 0);
			ms.scale(.75f, .75f, .75f);
			crafter.draw(graphics, getWidth() / 2, 0);
			ms.popPose();
		}

	}
}
