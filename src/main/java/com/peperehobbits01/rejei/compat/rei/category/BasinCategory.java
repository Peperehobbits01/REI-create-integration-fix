package com.peperehobbits01.rejei.compat.rei.category;

import java.util.ArrayList;
import java.util.List;

import com.peperehobbits01.rejei.compat.rei.display.CreateDisplay;
import com.peperehobbits01.rejei.util.ReiFluidConverter;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import org.apache.commons.lang3.mutable.MutableInt;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock.HeatLevel;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.utility.CreateLang;

import me.shedaniel.math.Point;
import me.shedaniel.rei.api.client.gui.widgets.Slot;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.createmod.catnip.data.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class BasinCategory extends CreateRecipeCategory<BasinRecipe> {

	private final boolean needsHeating;

	public BasinCategory(Info<BasinRecipe> info, boolean needsHeating) {
		super(info);
		this.needsHeating = needsHeating;
	}

	@Override
	public void addWidgets(CreateDisplay<BasinRecipe> display, List<Widget> widgets, Point origin) {

		BasinRecipe recipe = display.getRecipe();

		NonNullList<SizedFluidIngredient> fluidIngredients = recipe.getFluidIngredients();
		List<Pair<Ingredient, MutableInt>> ingredients =
				ItemHelper.condenseIngredients(recipe.getIngredients());

		List<ProcessingOutput> itemOutputs = recipe.getRollableResults();

		// Forge fluid outputs (raw Create data)
		List<FluidStack> fluidOutputs = new ArrayList<>(recipe.getFluidResults());

		int size = ingredients.size() + fluidIngredients.size();
		int xOffset = size < 3 ? (3 - size) * 19 / 2 : 0;

		int i = 0;

		// =========================
		// ITEM INPUTS
		// =========================
		for (; i < ingredients.size(); i++) {

			Pair<Ingredient, MutableInt> pair = ingredients.get(i);

			List<ItemStack> stacks = new ArrayList<>();
			for (ItemStack s : pair.getFirst().getItems()) {
				ItemStack copy = s.copy();
				copy.setCount(pair.getSecond().getValue());
				stacks.add(copy);
			}

			widgets.add(
					basicSlot(
							origin.x + 17 + xOffset + (i % 3) * 19,
							origin.y + 51 - (i / 3) * 19
					)
							.markInput()
							.entries(EntryIngredients.ofItemStacks(stacks))
			);
		}

		// =========================
		// FLUID INPUTS
		// =========================
		for (int j = 0; j < fluidIngredients.size(); j++) {

			int i2 = i + j;

			List<FluidStack> fluids = new ArrayList<>();
			for (FluidStack f : fluidIngredients.get(j).ingredient().getStacks()) {
				fluids.add(f);
			}

			Slot slot = basicSlot(
					origin.x + 17 + xOffset + (i2 % 3) * 19,
					origin.y + 51 - (i2 / 3) * 19
			).markInput();

			if (!fluids.isEmpty()) {
				slot.entries(
						EntryIngredients.of(
								ReiFluidConverter.toREI(fluids.get(0))
						)
				);
			}

			setFluidRenderRatio(slot);
			widgets.add(slot);
		}

		// =========================
		// OUTPUTS
		// =========================
		int outSize = itemOutputs.size() + fluidOutputs.size();

		for (int o = 0; o < outSize; o++) {

			int xPos = 141 - (outSize % 2 != 0 && o == outSize - 1
					? 0
					: o % 2 == 0 ? 10 : -9);

			int yPos = -19 * (o / 2) + 50;

			if (o < itemOutputs.size()) {

				ProcessingOutput out = itemOutputs.get(o);

				Slot slot = basicSlot(
						origin.x + xPos + 1,
						origin.y + yPos + 1
				)
						.markOutput()
						.entries(EntryIngredients.of(out.getStack()));

				widgets.add(slot);
				addStochasticTooltip(slot, out);

			} else {

				FluidStack fluid = fluidOutputs.get(o - itemOutputs.size());

				Slot slot = basicSlot(
						origin.x + xPos + 1,
						origin.y + yPos + 1
				)
						.markOutput()
						.entries(
								EntryIngredients.of(
										ReiFluidConverter.toREI(fluid)
								)
						);

				setFluidRenderRatio(slot);
				widgets.add(slot);
			}
		}

		addFluidTooltip(widgets, fluidIngredients, fluidOutputs);

		// =========================
		// HEAT INDICATORS
		// =========================
		HeatCondition heat = recipe.getRequiredHeat();

		if (!heat.testBlazeBurner(HeatLevel.NONE)) {
			widgets.add(
					basicSlot(origin.x + 134, origin.y + 81)
							.markInput()
							.entries(EntryIngredients.of(AllBlocks.BLAZE_BURNER.asStack()))
			);
		}

		if (!heat.testBlazeBurner(HeatLevel.KINDLED)) {
			widgets.add(
					basicSlot(origin.x + 153, origin.y + 81)
							.markOutput()
							.entries(EntryIngredients.of(AllItems.BLAZE_CAKE.asStack()))
			);
		}
	}

	@Override
	public void draw(BasinRecipe recipe, GuiGraphics graphics, double mouseX, double mouseY) {

		List<Pair<Ingredient, MutableInt>> ingredients =
				ItemHelper.condenseIngredients(recipe.getIngredients());

		int size = ingredients.size() + recipe.getFluidIngredients().size();
		int outSize = recipe.getFluidResults().size() + recipe.getRollableResults().size();

		int xOffset = size < 3 ? (3 - size) * 19 / 2 : 0;

		for (int i = 0; i < size; i++) {
			AllGuiTextures.JEI_SLOT.render(
					graphics,
					16 + xOffset + (i % 3) * 19,
					50 - (i / 3) * 19
			);
		}

		for (int i = 0; i < outSize; i++) {
			AllGuiTextures.JEI_SLOT.render(
					graphics,
					141 - (outSize % 2 != 0 && i == outSize - 1
							? 0 : i % 2 == 0 ? 10 : -9),
					-19 * (i / 2) + 50
			);
		}

		if (recipe.getRequiredHeat() == HeatCondition.NONE)
			return;

		AllGuiTextures.JEI_HEAT_BAR.render(graphics, 4, 80);

		graphics.drawString(
				Minecraft.getInstance().font,
				CreateLang.translateDirect(recipe.getRequiredHeat().getTranslationKey()),
				9,
				86,
				recipe.getRequiredHeat().getColor()
		);
	}
}