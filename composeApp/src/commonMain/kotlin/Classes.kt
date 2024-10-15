import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive


//Это классы для построения различных объектов
// TODO: Нужно изменить эти классы, чтобы рни соответствовали табличкам в базе данных
data class Promotion(
    val start: String,
    val end: String,
    val name: String,
    val description: String,
    val image: String
)

data class Category(
    val podcategoryIDs: List<String>,
    val name: String,
    val image: String
)

data class Podcategory(
    val ID: String,
    val name: String,
    val productIDs: List<String>,
    val image: String
)
/*
data class Product(
    val ID: String,
    val name: String,
    val images: List<String>,
    val description: String
)

 */
@Serializable
data class Product(
    val id: String? = null,
    val images: List<String>, // Список строк Base64
    val name: String,
    val priceLow: String,
    val priceHigh: String,
    val article: String,
    val description: String,
    val number: String,
    val country: String,
    val type: String,
    val selectedOption: String,
    val selectedSizes: List<String>,
    val selectedColors: List<String>,
    val selectedMaterials: List<String>,
    val selectedSeasons: List<String>,
    val isNew: Boolean,
    val isHit: Boolean
)
data class User(
    val name:String,
    val login:String,
    val phone:String,
    val is_admin:Boolean

)

// Кастомная функция для парсинга продуктов
fun parseProducts(jsonString: String): List<Product> {
    val json = Json.parseToJsonElement(jsonString).jsonArray
    val products = mutableListOf<Product>()

    for (jsonElement in json) {
        val jsonObject = jsonElement.jsonObject
        val product = Product(
            id = jsonObject["id"]?.jsonPrimitive?.content ?: "",
            images = jsonObject["images"]?.jsonArray?.map { it.jsonPrimitive.content } ?: emptyList(),
            name = jsonObject["name"]?.jsonPrimitive?.content ?: "",
            priceLow = jsonObject["priceLow"]?.jsonPrimitive?.content ?: "",
            priceHigh = jsonObject["priceHigh"]?.jsonPrimitive?.content ?: "",
            article = jsonObject["article"]?.jsonPrimitive?.content ?: "",
            description = jsonObject["description"]?.jsonPrimitive?.content ?: "",
            number = jsonObject["number"]?.jsonPrimitive?.content ?: "",
            country = jsonObject["country"]?.jsonPrimitive?.content ?: "",
            type = jsonObject["type"]?.jsonPrimitive?.content ?: "",
            selectedOption = jsonObject["selectedOption"]?.jsonPrimitive?.content ?: "",
            selectedSizes = jsonObject["selectedSizes"]?.jsonArray?.map { it.jsonPrimitive.content } ?: emptyList(),
            selectedColors = jsonObject["selectedColors"]?.jsonArray?.map { it.jsonPrimitive.content } ?: emptyList(),
            selectedMaterials = jsonObject["selectedMaterials"]?.jsonArray?.map { it.jsonPrimitive.content } ?: emptyList(),
            selectedSeasons = jsonObject["selectedSeasons"]?.jsonArray?.map { it.jsonPrimitive.content } ?: emptyList(),
            isNew = jsonObject["isNew"]?.jsonPrimitive?.intOrNull == 1, // Преобразование 0/1 в Boolean
            isHit = jsonObject["isHit"]?.jsonPrimitive?.intOrNull == 1  // Преобразование 0/1 в Boolean
        )
        products.add(product)
    }

    return products
}


// Функция для получения всех продуктов
suspend fun fetchProducts(): List<Product> {
    // Инициализируем клиента с поддержкой JSON сериализации
    val client = createHttpClient()

    return try {
        // Выполняем GET-запрос к серверу
        val response: HttpResponse = client.get("https://rst-for-milenaopt.ru/products") {
            contentType(ContentType.Application.Json)
        }

        // Проверяем статус ответа
        if (response.status == HttpStatusCode.OK) {
            // Парсим тело ответа в список объектов ProductRequest
            val jsonString = response.body<String>()
            parseProducts(jsonString)
        } else {
            // Логирование ошибки, если сервер вернул неуспешный статус
            println("Ошибка при получении продуктов: ${response.status}")
            emptyList() // Возвращаем пустой список в случае ошибки
        }
    } catch (e: Exception) {
        // Логирование исключения при возникновении ошибки запроса
        println("Произошла ошибка при выполнении запроса: ${e.message}")
        emptyList() // Возвращаем пустой список в случае исключения
    } finally {
        client.close() // Закрытие клиента после выполнения запроса
    }
}



/*
suspend fun fetchProducts():List<Product>{
    // TODO: Здесь должно быть получение данных товаров из базы
    val Products = listOf(
        Product(
            ID = "1",
            name = "Женские джинсы прямые / дудочки / р-р 25-30",
            images = listOf("https://opt-milena.ru/upload/iblock/92d/op4u0w6xt8h53cvit4zpydce2wxpqwyf.jpg",
                "https://opt-milena.ru/upload/iblock/d6f/6teqct6m2byqdgvmcp5jer3ks3mprqxc.jpg"),
            description = "в упаковке 6 шт товар сертифицирован производство Россия"
        ),
        Product(
            ID = "1",
            name = "Женские джинсы прямые / дудочки / р-р 25-30",
            images = listOf("https://opt-milena.ru/upload/iblock/92d/op4u0w6xt8h53cvit4zpydce2wxpqwyf.jpg",
                "https://opt-milena.ru/upload/iblock/d6f/6teqct6m2byqdgvmcp5jer3ks3mprqxc.jpg"),
            description = "в упаковке 6 шт товар сертифицирован производство Россия"
        ),
        Product(
            ID = "1",
            name = "Женские джинсы прямые / дудочки / р-р 25-30",
            images = listOf("https://opt-milena.ru/upload/iblock/92d/op4u0w6xt8h53cvit4zpydce2wxpqwyf.jpg",
                "https://opt-milena.ru/upload/iblock/d6f/6teqct6m2byqdgvmcp5jer3ks3mprqxc.jpg"),
            description = "в упаковке 6 шт товар сертифицирован производство Россия"
        ),
        Product(
            ID = "1",
            name = "Женские джинсы прямые / дудочки / р-р 25-30",
            images = listOf("https://opt-milena.ru/upload/iblock/92d/op4u0w6xt8h53cvit4zpydce2wxpqwyf.jpg",
                "https://opt-milena.ru/upload/iblock/d6f/6teqct6m2byqdgvmcp5jer3ks3mprqxc.jpg"),
            description = "в упаковке 6 шт товар сертифицирован производство Россия"
        ),
        Product(
            ID = "1",
            name = "Женские джинсы прямые / дудочки / р-р 25-30",
            images = listOf("https://opt-milena.ru/upload/iblock/92d/op4u0w6xt8h53cvit4zpydce2wxpqwyf.jpg",
                "https://opt-milena.ru/upload/iblock/d6f/6teqct6m2byqdgvmcp5jer3ks3mprqxc.jpg"),
            description = "в упаковке 6 шт товар сертифицирован производство Россия"
        ),
        Product(
            ID = "1",
            name = "Женские джинсы прямые / дудочки / р-р 25-30",
            images = listOf("https://opt-milena.ru/upload/iblock/92d/op4u0w6xt8h53cvit4zpydce2wxpqwyf.jpg",
                "https://opt-milena.ru/upload/iblock/d6f/6teqct6m2byqdgvmcp5jer3ks3mprqxc.jpg"),
            description = "в упаковке 6 шт товар сертифицирован производство Россия"
        )
    )
    return Products
}

 */

suspend fun fetchCategories():List<Category>{
    // TODO: Здесь должно быть получение списка категорий товаров
    val Categories = listOf(
        Category(
            listOf("","",""),
            "Женская одежда",
            "https://opt-milena.ru/upload/webp/iblock/e2f/y1lfukt6mgdpyz121wuop850r3len3xs.webp"
        ),
        Category(
            listOf("","",""),
            "Мужская одежда",
            "https://opt-milena.ru/upload/webp/iblock/c59/ezv76i3tmhe1vl3e75oxodo93sfhi92x.webp"
        ),
        Category(
            listOf("","",""),
            "Детская одежда",
            "https://opt-milena.ru/upload/webp/iblock/d42/lorwm1rfn4h9oft6ka09ng3ybrl820fe.webp"
        ),
        // /*
        Category(
            listOf("","",""),
            "Товары для дома",
            "https://opt-milena.ru/upload/webp/iblock/3dc/7x5hdecttrmg50inr9vsqbx9u91cvdie.webp"
        ),
        Category(
            listOf("","",""),
            "Одежда Premium",
            "https://opt-milena.ru/upload/webp/iblock/535/ffkx12g9btm1eukgdwdmo7ev6fvq6d0k.webp"
        ),
        Category(
            listOf("","",""),
            "Новинки",
            "https://opt-milena.ru/upload/webp/iblock/511/mb7tgnakxwm01u3xjsv96uzm8o7o90xi.webp"
        )

         // */
    )
    return Categories
}

fun parsePromotions(jsonString: String): List<Promotion> {
    val json = Json.parseToJsonElement(jsonString).jsonArray
    val promotions = mutableListOf<Promotion>()

    for (jsonElement in json) {
        val jsonObject = jsonElement.jsonObject
        val promotion = Promotion(
            start = jsonObject["start"]?.jsonPrimitive?.content ?: "",
            end = jsonObject["end"]?.jsonPrimitive?.content ?: "",
            name = jsonObject["name"]?.jsonPrimitive?.content ?: "",
            description = jsonObject["description"]?.jsonPrimitive?.content ?: "",
            image = jsonObject["image"]?.jsonPrimitive?.content ?: ""
        )
        promotions.add(promotion)
    }
    println(promotions.size)
    println(promotions)
    return promotions
}

suspend fun fetchPromos():List<Promotion>?{
    // TODO: Здесь должно быть получение акций из базы
    val client = createHttpClient()
    val url = "https://rst-for-milenaopt.ru/all_promotions"
    return try {
        val response: HttpResponse = client.get(url)
        if (response.status.value == 200) {
            val jsonString = response.body<String>()
            parsePromotions(jsonString)
        } else {
            println("Ошибка: ${response.status.value} описание ${response.bodyAsText()}")
            null
        }
    } catch (e: Exception) {
        println("Произошла ошибка при выполнении запроса: ${e.message}")
        null
    } finally {
        client.close()
    }
}