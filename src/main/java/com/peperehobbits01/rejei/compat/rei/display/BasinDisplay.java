package com.peperehobbits01.rejei.compat.rei.display;

import java.util.ArrayList;
import java.util.List;

import com.simibubi.create.Create;
import com.peperehobbits01.rejei.compat.rei.category.CreateRecipeCategory;
import com.simibubi.create.content.processing.basin.BasinRecipe;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

public class BasinDisplay extends CreateDisplay<BasinRecipe> {
	public BasinDisplay(BasinRecipe recipe, CategoryIdentifier<CreateDisplay<BasinRecipe>> id) {
		super(recipe, id,
				getInputs(recipe),
				List.of(
						EntryIngredients.ofItemStacks(recipe.getRollableResultsAsItemStacks()),
						// Explicitly declare the Forge type inside the ArrayList constructor
						EntryIngredients.of(VanillaEntryTypes.FLUID, (List<dev.architectury.fluid.FluidStack>) CreateRecipeCategory.convertToREIFluids(new ArrayList<net.neoforged.neoforge.fluids.FluidStack>(recipe.getFluidResults())))
				));
	}

	public static BasinDisplay mixing(BasinRecipe recipe) {
		return new BasinDisplay(recipe, CategoryIdentifier.of(Create.asResource("mixing")));
	}

	public static BasinDisplay autoShapeless(BasinRecipe recipe) {
		return new BasinDisplay(recipe, CategoryIdentifier.of(Create.asResource("automatic_shapeless")));
	}

	public static BasinDisplay brewing(BasinRecipe recipe) {
		return new BasinDisplay(recipe, CategoryIdentifier.of(Create.asResource("automatic_brewing")));
	}

	public static BasinDisplay packing(BasinRecipe recipe) {
		return new BasinDisplay(recipe, CategoryIdentifier.of(Create.asResource("packing")));
	}

	public static BasinDisplay autoSquare(BasinRecipe recipe) {
		return new BasinDisplay(recipe, CategoryIdentifier.of(Create.asResource("automatic_packing")));
	}

	private static List<EntryIngredient> getInputs(BasinRecipe recipe) {
		List<EntryIngredient> input = new ArrayList<>(EntryIngredients.ofIngredients(recipe.getIngredients()));
		for (SizedFluidIngredient fluidIngredient : recipe.getFluidIngredients()) {
			// Explicitly declare the Forge type inside the ArrayList constructor
			input.add(EntryIngredients.of(VanillaEntryTypes.FLUID, (List<dev.architectury.fluid.FluidStack>) CreateRecipeCategory.convertToREIFluids(new ArrayList<net.neoforged.neoforge.fluids.FluidStack>(java.util.Arrays.asList(fluidIngredient.ingredient().getStacks())))));
		}
		return input;
	}
}
