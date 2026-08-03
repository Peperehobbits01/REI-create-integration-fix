package com.peperehobbits01.rejei.compat.rei;

import com.simibubi.create.AllDataComponents;
import com.simibubi.create.content.fluids.potion.PotionFluid.BottleType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.PotionContents;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

public class PotionFluidSubtypeInterpreter {

	public String apply(FluidStack ingredient) {
		if (ingredient.getComponentsPatch().isEmpty())
			return "";

		PotionContents contents = ingredient.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
		String potionTypeString = ingredient.getDescriptionId();
		String bottleType = ingredient.getOrDefault(AllDataComponents.POTION_FLUID_BOTTLE_TYPE, BottleType.REGULAR).name();

		StringBuilder stringBuilder = new StringBuilder(potionTypeString);
		List<MobEffectInstance> effects = contents.customEffects();

		stringBuilder.append(";").append(bottleType);

		contents.potion().ifPresent(p -> {
			for (MobEffectInstance effect : p.value().getEffects())
				stringBuilder.append(";").append(effect);
		});
		for (MobEffectInstance effect : effects)
			stringBuilder.append(";").append(effect);

		return stringBuilder.toString();
	}
}
