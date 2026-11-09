package com.rezz.obatcheck.data

import android.content.Context
import org.json.JSONObject

data class Drug(val id: String, val name: String, val indication: String)

data class Category(val id: String, val name: String, val drugs: List<Drug>)

/**
 * Parse daftar obat dari JSON.
 * Format: {"total": 355, "categories": [{"id", "name", "drugs": [{"id", "name", "indication"}]}]}
 * Fungsi ini murni (tanpa Android) supaya bisa diuji di unit test JVM.
 */
fun parseDrugsJson(json: String): List<Category> {
    val root = JSONObject(json)
    val cats = root.getJSONArray("categories")
    val out = ArrayList<Category>(cats.length())
    for (i in 0 until cats.length()) {
        val c = cats.getJSONObject(i)
        val drugsArr = c.getJSONArray("drugs")
        val drugs = ArrayList<Drug>(drugsArr.length())
        for (j in 0 until drugsArr.length()) {
            val d = drugsArr.getJSONObject(j)
            drugs.add(Drug(d.getString("id"), d.getString("name"), d.getString("indication")))
        }
        out.add(Category(c.getString("id"), c.getString("name"), drugs))
    }
    return out
}

/** Muat daftar obat dari assets/drugs.json (disalin dari build_daftar_obat_355.py). */
fun loadDrugs(context: Context): List<Category> {
    val json = context.assets.open("drugs.json").bufferedReader().use { it.readText() }
    return parseDrugsJson(json)
}
