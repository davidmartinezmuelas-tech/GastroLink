package tech.davidmartinezmuelas.gastrolink.domain

import org.junit.Assert.assertEquals
import org.junit.Test
import tech.davidmartinezmuelas.gastrolink.model.CartItem
import tech.davidmartinezmuelas.gastrolink.model.Dish

class NutritionCalculatorTest {

    private val dishA = Dish(
        id = "d1",
        name = "Dish A",
        kcal = 100,
        proteinG = 10,
        carbsG = 20,
        fatG = 5
    )

    private val dishB = Dish(
        id = "d2",
        name = "Dish B",
        kcal = 250,
        proteinG = 25,
        carbsG = 15,
        fatG = 10
    )

    @Test
    fun calculateTotals_returnsExpectedGlobalTotals() {
        val items = listOf(
            CartItem(dish = dishA, qty = 2),
            CartItem(dish = dishB, qty = 3)
        )

        val totals = NutritionCalculator.calculateTotals(items)

        assertEquals(950, totals.kcal)
        assertEquals(95, totals.proteinG)
        assertEquals(85, totals.carbsG)
        assertEquals(40, totals.fatG)
    }

    @Test
    fun calculateTotalsByParticipant_groupsOnlyAssignedItems() {
        val items = listOf(
            CartItem(dish = dishA, qty = 2, participantId = "p1"),
            CartItem(dish = dishB, qty = 1, participantId = "p1"),
            CartItem(dish = dishB, qty = 2, participantId = "p2"),
            CartItem(dish = dishA, qty = 1, participantId = null)
        )

        val totalsByParticipant = NutritionCalculator.calculateTotalsByParticipant(items)

        val p1 = totalsByParticipant.getValue("p1")
        assertEquals(450, p1.kcal)
        assertEquals(45, p1.proteinG)
        assertEquals(55, p1.carbsG)
        assertEquals(20, p1.fatG)

        val p2 = totalsByParticipant.getValue("p2")
        assertEquals(500, p2.kcal)
        assertEquals(50, p2.proteinG)
        assertEquals(30, p2.carbsG)
        assertEquals(20, p2.fatG)

        assertEquals(2, totalsByParticipant.size)
    }
}
