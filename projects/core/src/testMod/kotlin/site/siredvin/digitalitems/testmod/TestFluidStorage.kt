package site.siredvin.digitalitems.testmod

import site.siredvin.broccolium.modules.storage.base.api.SomethingOperator
import site.siredvin.broccolium.modules.storage.fluid.AgnosticFluidStack
import site.siredvin.broccolium.modules.storage.fluid.FluidStorageUtils
import site.siredvin.broccolium.modules.storage.fluid.api.AgnosticFluidStorage
import java.util.function.Predicate

class TestFluidStorage(private val capacity: Double) : AgnosticFluidStorage {
    private var fluid = AgnosticFluidStack.EMPTY

    override val maxStackSize: Double
        get() = capacity
    override val operator: SomethingOperator<AgnosticFluidStack, Double>
        get() = FluidStorageUtils

    override fun getContent(): Iterator<AgnosticFluidStack> = listOf(fluid).iterator()
    override fun getCapacities(): List<Double> = listOf(capacity)

    override fun take(predicate: Predicate<AgnosticFluidStack>, limit: Double, simulate: Boolean): AgnosticFluidStack {
        if (!predicate.test(fluid)) return AgnosticFluidStack.EMPTY
        return if (simulate) fluid.copyWithCount(limit.coerceAtMost(fluid.amount)) else fluid.split(limit)
    }

    override fun store(stack: AgnosticFluidStack, simulate: Boolean): AgnosticFluidStack {
        if (!fluid.isEmpty && !AgnosticFluidStack.isSameFluidSameTags(fluid, stack)) return stack
        val accepted = minOf(capacity - fluid.amount, stack.amount)
        if (!simulate) {
            if (fluid.isEmpty) fluid = stack.copyWithCount(accepted) else fluid.grow(accepted)
        }
        stack.shrink(accepted)
        return stack
    }

    override fun setChanged() = Unit
}
