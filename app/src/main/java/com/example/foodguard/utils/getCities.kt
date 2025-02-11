// Utils.kt

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale

suspend fun getCities(): List<String> {
    return withContext(Dispatchers.IO) {
        val url = URL("https://data.gov.il/api/action/datastore_search") // API URL
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "POST"

        connection.setRequestProperty("Content-Type", "application/json")
        connection.doOutput = true
        connection.doInput = true

        try {
            val jsonBody = """
                {
                    "resource_id": "b7cf8f14-64a2-4b33-8d4b-edb286fdbd37",
                    "limit": 1500
                }
            """.trimIndent()

            val outputStream: OutputStream = connection.outputStream
            outputStream.write(jsonBody.toByteArray())
            outputStream.flush()

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val inputStream = connection.inputStream
                val reader = InputStreamReader(inputStream)
                val response = reader.readText()

                val cityList = parseCityNames(response)

                return@withContext cityList
            } else {
                // Handle error (non-200 HTTP response)
                return@withContext emptyList<String>()
            }
        } catch (e: Exception) {
            // Handle exception (network or parsing errors)
            return@withContext emptyList<String>()
        } finally {
            connection.disconnect()
        }
    }
}

fun parseCityNames(response: String): List<String> {
    // Use Gson to parse the JSON response
    val gson = Gson()
    val recordsType = object : TypeToken<Map<String, Any>>() {}.type
    val responseMap: Map<String, Any> = gson.fromJson(response, recordsType)

    // Extract records from the response map
    val records = (responseMap["result"] as? Map<*, *>)?.get("records") as? List<Map<String, Any>>

    val cityNames = mutableListOf<String>()

    // Iterate through records and extract the city names
    records?.forEach { record ->
        val cityName = record["שם_ישוב_לועזי"] as? String
        cityName?.let { cityNames.add(formatCityName(it)) }
    }

    return cityNames
}

fun formatCityName(input: String): String {
    return input
        .trim()  // Remove leading and trailing spaces
        .replace("\\s+".toRegex(), " ")  // Replace multiple spaces with a single space
        .lowercase(Locale.getDefault())  // Convert all characters to lowercase
        .split(" ")  // Split the string into words
        .joinToString(" ") { it.capitalize() }  // Capitalize the first letter of each word and join them back
}
