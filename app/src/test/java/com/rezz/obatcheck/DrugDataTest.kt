package com.rezz.obatcheck

import com.rezz.obatcheck.data.Category
import com.rezz.obatcheck.data.Drug
import com.rezz.obatcheck.data.parseDrugsJson
import com.rezz.obatcheck.ui.Filter
import com.rezz.obatcheck.ui.ObatUiState
import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DrugDataTest {

    private fun loadCategories(): List<Category> {
        val file = File("src/main/assets/drugs.json")
        assertTrue("assets/drugs.json harus ada", file.exists())
        return parseDrugsJson(file.readText())
    }

    @Test
    fun `daftar obat berisi tepat 355 obat`() {
        val categories = loadCategories()
        assertEquals(20, categories.size)
        assertEquals(355, categories.sumOf { it.drugs.size })
    }

    @Test
    fun `id obat unik dan nama serta indikasi tidak kosong`() {
        val drugs = loadCategories().flatMap { it.drugs }
        assertEquals(355, drugs.map { it.id }.toSet().size)
        for (drug in drugs) {
            assertTrue(drug.name.isNotBlank())
            assertTrue(drug.indication.isNotBlank())
        }
    }

    @Test
    fun `pencarian cocok substring dan abaikan kapital`() {
        val categories = listOf(
            Category("c01", "Test", listOf(
                Drug("d001", "Paracetamol", "Demam"),
                Drug("d002", "Ibuprofen", "Nyeri"),
            )),
        )
        val byLower = ObatUiState(categories, query = "para").visibleCategories()
        assertEquals(1, byLower.sumOf { it.drugs.size })
        assertEquals("Paracetamol", byLower[0].drugs[0].name)

        val byUpper = ObatUiState(categories, query = "PARA").visibleCategories()
        assertEquals(1, byUpper.sumOf { it.drugs.size })

        val noMatch = ObatUiState(categories, query = "zzz").visibleCategories()
        assertTrue(noMatch.isEmpty())
    }

    @Test
    fun `filter sudah dan belum dicentang`() {
        val categories = listOf(
            Category("c01", "Test", listOf(
                Drug("d001", "Paracetamol", "Demam"),
                Drug("d002", "Ibuprofen", "Nyeri"),
            )),
        )
        val state = ObatUiState(categories, checkedIds = setOf("d001"))
        assertEquals(1, state.checkedCount)
        assertEquals(2, state.total)

        val sudah = state.copy(filter = Filter.SUDAH).visibleCategories()
        assertEquals(listOf("Paracetamol"), sudah.flatMap { it.drugs }.map { it.name })

        val belum = state.copy(filter = Filter.BELUM).visibleCategories()
        assertEquals(listOf("Ibuprofen"), belum.flatMap { it.drugs }.map { it.name })
    }
}
