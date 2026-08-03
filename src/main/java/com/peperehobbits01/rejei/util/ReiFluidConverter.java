package com.peperehobbits01.rejei.util;

import dev.architectury.fluid.FluidStack;

public final class ReiFluidConverter {

    private ReiFluidConverter() {}

    public static FluidStack toREI(net.neoforged.neoforge.fluids.FluidStack stack) {
        if (stack == null || stack.isEmpty()) {
            return FluidStack.empty();
        }

        // The data components must be carried over: fluid tint colors are derived
        // from them (e.g. Create's potion fluid reads POTION_CONTENTS), so dropping
        // them makes the fluid render black.
        return FluidStack.create(
                stack.getFluid(),
                stack.getAmount(),
                stack.getComponentsPatch()
        );
    }
}