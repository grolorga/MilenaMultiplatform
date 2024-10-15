import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType

@Composable
fun CkeckOrders(
    userPreferences: UserPreferences,
    onCencelled:()->Unit,
    onDismiss:()->Unit
){
    var orders by remember { mutableStateOf<List<Order>>(emptyList()) }
    var selectedOrder by remember { mutableStateOf<Order?>(null) }
    var userData by remember { mutableStateOf<User?>(null) }
    LaunchedEffect(true){
        userData = userPreferences.getUserLogin()?.let { getUser(it) }
        println(userData)
    }
    LaunchedEffect(Unit){
        orders = getOrders()
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .padding(6.dp),
        contentAlignment = Alignment.TopStart
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
            contentDescription = "",
            modifier = Modifier
                .size(40.dp)
                .clickable { onDismiss() },
            tint = Color.LightGray
        )
    }
    LazyColumn(modifier = Modifier.fillMaxWidth().padding(top = 52.dp)) {
        items(orders){order->
            OrderedUnit(
                order = order,
                onSelectedOrder = {selected->
                    selectedOrder = selected
                }
                )
            Spacer(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color.Gray))
        }
    }
    if (selectedOrder!=null){
        userData.let{
            OrderDetails(
                selectedOrder!!,
                onDismiss = {selectedOrder = null},
                isdm = userData?.is_admin?:false,
                onCancelled = {
                    onCencelled()
                })
        }
    }
}



suspend fun getOrders(): List<Order> {
    // Инициализируем клиента с поддержкой JSON сериализации
    val client = createHttpClient()

    return try {
        // Выполняем GET-запрос к серверу
        val url = "https://rst-for-milenaopt.ru/get_orders"
        val response: HttpResponse = client.get(url) {
            contentType(ContentType.Application.Json)
        }

        // Проверяем статус ответа
        if (response.status == HttpStatusCode.OK) {
            // Парсим тело ответа в список объектов Order
            val jsonString = response.body<String>()
            parseOrders(jsonString)
        } else {
            // Логирование ошибки, если сервер вернул неуспешный статус
            println("Ошибка при получении заказов: ${response.status}")
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
