package site.siredvin.digitalitems.testmod

import net.minecraft.gametest.framework.GameTest
import net.minecraft.gametest.framework.GameTestHelper
import site.siredvin.testiarium.api.TestGroup
import site.siredvin.testiarium.cct.thenLua

@TestGroup("digitalitems")
class DigitalItemsGameTests {
    @GameTest(template = "digitalitemsgametests.peripheral_contracts", timeoutTicks = 1200)
    fun peripheralContracts(helper: GameTestHelper) = helper.thenLua().thenSucceed()

    @GameTest(template = "digitalitemsgametests.base_digitize", timeoutTicks = 1200)
    fun baseDigitize(helper: GameTestHelper) = helper.thenLua().thenSucceed()

    @GameTest(template = "digitalitemsgametests.remote_digitize", timeoutTicks = 1200)
    fun remoteDigitize(helper: GameTestHelper) = helper.thenLua().thenSucceed()

    @GameTest(template = "digitalitemsgametests.base_more_digitize", timeoutTicks = 1200)
    fun baseMoreDigitize(helper: GameTestHelper) = helper.thenLua().thenSucceed()

    @GameTest(template = "digitalitemsgametests.remote_more_digitize", timeoutTicks = 1200)
    fun remoteMoreDigitize(helper: GameTestHelper) = helper.thenLua().thenSucceed()

    @GameTest(template = "digitalitemsgametests.base_limit_digitize", timeoutTicks = 1_200_000)
    fun baseLimitDigitize(helper: GameTestHelper) = helper.thenLua().thenSucceed()

    @GameTest(template = "digitalitemsgametests.remote_limit_digitize", timeoutTicks = 1_200_000)
    fun remoteLimitDigitize(helper: GameTestHelper) = helper.thenLua().thenSucceed()

    @GameTest(template = "digitalitemsgametests.fluid_digitize", timeoutTicks = 1200)
    fun fluidDigitize(helper: GameTestHelper) = helper.thenLua().thenSucceed()

    @GameTest(template = "digitalitemsgametests.energy_digitize", timeoutTicks = 1200)
    fun energyDigitize(helper: GameTestHelper) = helper.thenLua().thenSucceed()

    @GameTest(template = "digitalitemsgametests.old_digitize", timeoutTicks = 1200)
    fun oldDigitize(helper: GameTestHelper) = helper.thenLua().thenSucceed()
}
