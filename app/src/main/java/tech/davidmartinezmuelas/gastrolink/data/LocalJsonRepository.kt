package tech.davidmartinezmuelas.gastrolink.data

import android.content.Context
import org.json.JSONArray
import tech.davidmartinezmuelas.gastrolink.model.Branch
import tech.davidmartinezmuelas.gastrolink.model.Dish

class LocalJsonRepository(private val context: Context) {

    fun loadBranches(): List<Branch> {
        val content = readAsset("sample_data/branches.json") ?: return emptyList()
        val array = JSONArray(content)
        return List(array.length()) { index ->
            val item = array.getJSONObject(index)
            Branch(
                id = item.getString("id"),
                name = item.getString("name"),
                city = item.getString("city")
            )
        }
    }

    fun loadDishes(): List<Dish> {
        val content = readAsset("sample_data/dishes.json") ?: return emptyList()
        val array = JSONArray(content)
        return List(array.length()) { index ->
            val item = array.getJSONObject(index)
            Dish(
                id = item.getString("id"),
                name = item.getString("name"),
                kcal = item.getInt("kcal"),
                proteinG = item.getInt("protein_g"),
                carbsG = item.getInt("carbs_g"),
                fatG = item.getInt("fat_g")
            )
        }
    }

    private fun readAsset(path: String): String? {
        return runCatching {
            context.assets.open(path).bufferedReader().use { it.readText() }
        }.getOrNull()
    }
}
