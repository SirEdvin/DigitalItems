package site.siredvin.digitalitems.testmod

import site.siredvin.broccolium.modules.storage.base.api.SomethingOperator
import site.siredvin.broccolium.modules.storage.energy.AgnosticEnergyStack
import site.siredvin.broccolium.modules.storage.energy.EnergyStorageUtils
import site.siredvin.broccolium.modules.storage.energy.api.AgnosticEnergyStorage
import java.util.function.Predicate

class TestEnergyStorage(private val capacity: Long, initial: AgnosticEnergyStack) : AgnosticEnergyStorage {
    private var energy = initial

    override val firstEnergy: AgnosticEnergyStack
        get() = energy
    override val canExtract = true
    override val canReceive = true
    override val maxStackSize: Long
        get() = capacity
    override val operator: SomethingOperator<AgnosticEnergyStack, Long>
        get() = EnergyStorageUtils

    override fun getContent(): Iterator<AgnosticEnergyStack> = listOf(energy).iterator()

    override fun take(predicate: Predicate<AgnosticEnergyStack>, limit: Long, simulate: Boolean): AgnosticEnergyStack {
        if (!predicate.test(energy)) return AgnosticEnergyStack(energy.unit, 0)
        return if (simulate) energy.copyWithCount(limit.coerceAtMost(energy.amount)) else energy.split(limit)
    }

    override fun store(stack: AgnosticEnergyStack, simulate: Boolean): AgnosticEnergyStack {
        if (stack.unit != energy.unit) return stack
        val accepted = minOf(capacity - energy.amount, stack.amount)
        if (!simulate) energy.grow(accepted)
        stack.shrink(accepted)
        return stack
    }

    override fun setChanged() = Unit
}
