package com.peperehobbits01.rejei.util;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

public class FluidHandlerConverter {

    public static IFluidHandler of(FluidStack stack) {
        return new IFluidHandler() {

            @Override
            public int getTanks() {
                return 1;
            }

            @Override
            public @NotNull FluidStack getFluidInTank(int tank) {
                return tank == 0 ? stack.copy() : FluidStack.EMPTY;
            }

            @Override
            public int getTankCapacity(int tank) {
                return stack.getAmount();
            }

            @Override
            public boolean isFluidValid(int tank, @NotNull FluidStack fluid) {
                return stack.getFluid() == fluid.getFluid();
            }

            @Override
            public int fill(@NotNull FluidStack resource, @NotNull FluidAction action) {
                return 0; // read-only simulation tank
            }

            @Override
            public @NotNull FluidStack drain(@NotNull FluidStack resource, @NotNull FluidAction action) {
                if (stack.getFluid() == resource.getFluid()) {
                    return stack.copy();
                }
                return FluidStack.EMPTY;
            }

            @Override
            public @NotNull FluidStack drain(int maxDrain, @NotNull FluidAction action) {
                return stack.copy();
            }
        };
    }
}