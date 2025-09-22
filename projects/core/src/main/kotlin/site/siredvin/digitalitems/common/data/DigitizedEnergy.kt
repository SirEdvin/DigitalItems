package site.siredvin.digitalitems.common.data

import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import site.siredvin.broccolium.modules.storage.energy.AgnosticEnergyStack

class DigitizedEnergy : DigitizedSomething<AgnosticEnergyStack> {

    constructor(id: ByteArrayWrapper, item: AgnosticEnergyStack, currentTime: Long, player: ServerPlayer?) : super(id, item, currentTime, player)

    constructor(compoundTag: CompoundTag) : super(compoundTag)

    override fun fromCompound(tag: CompoundTag): AgnosticEnergyStack = AgnosticEnergyStack.of(tag)

    override fun toCompound(): CompoundTag = something.save(CompoundTag())

    override fun shrink(amount: Int) {
        something.shrink(amount)
    }

    override fun grow(amount: Int) {
        something.grow(amount)
    }

    override val somethingTagName: String
        get() = "energyStack"
    override val isEmpty: Boolean
        get() = something.isEmpty
    override val amount: Long
        get() = something.amount
}
