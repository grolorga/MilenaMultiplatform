import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.ktor.client.request.delete
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import kotlinx.coroutines.launch
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


@Composable
fun OrderDetails(
    order: Order,
    onDismiss: () -> Unit,
    isdm:Boolean?,
    onCancelled:()->Unit
) {
    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
    var productsInCart by remember { mutableStateOf<List<Product>?>(null) }
    var amounts by remember { mutableStateOf<List<Int>>(emptyList()) }

    // Загрузка продуктов и обработка данных заказа
    LaunchedEffect(Unit) {
        products = fetchProducts()
        val productIds = order.product_ids.split(",")
        val amoun = order.amounts.split(",")
        amounts = amoun.map { it.toInt() } // Преобразуем строки в Int

        // Используем `getProducts` только после загрузки `products`
        productsInCart = getProducts(productIds, products)
    }
    Column(modifier = Modifier.verticalScroll(rememberScrollState()).background(Color.White).padding(15.dp)){
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
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

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Товары в заказе от\n"+order.date,
                fontSize = 20.sp,
                color = Color.Black,
                //fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Left
            )
            Spacer(modifier = Modifier.fillMaxWidth().height(10.dp))
            Spacer(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color.Black))
            Spacer(modifier = Modifier.fillMaxWidth().height(10.dp))
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
            ) {
                itemsIndexed(productsInCart ?: emptyList()) { index, product ->
                    val amount = amounts.getOrNull(index) ?: 0

                    CartProductUnit(
                        product = product,
                        amount = amount,
                        type = order.type, // Передаем тип цен
                        onDeleteProduct = { prod ->
                            // Обработка удаления товара
                        },
                        onHigherAmount = { amo, prod ->
                            // Обработка увеличения количества
                        },
                        onLowerAmount = { amo, prod ->
                            // Обработка уменьшения количества
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.fillMaxWidth().height(10.dp))
            Spacer(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color.Black))
            Spacer(modifier = Modifier.fillMaxWidth().height(10.dp))
            Row(
                modifier = Modifier.height(60.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Тип цен: ",
                    fontSize = 18.sp,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = order.type,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.fillMaxWidth().height(10.dp))
            Spacer(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color.Black))
            Spacer(modifier = Modifier.fillMaxWidth().height(10.dp))
            Row(
                modifier = Modifier.height(60.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Итоговая цена",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = order.final_price+" Р",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.fillMaxWidth().height(10.dp))
            Spacer(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color.Black))
            Spacer(modifier = Modifier.fillMaxWidth().height(10.dp))
            Row(
                modifier = Modifier.height(60.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Состояние заказа",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = order.status,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
            }
            if(isdm == true){
                Spacer(modifier = Modifier.fillMaxWidth().height(10.dp))
                Spacer(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color.Black))
                Spacer(modifier = Modifier.fillMaxWidth().height(10.dp))
                Row(
                    modifier = Modifier.height(60.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "ФИО",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )

                    OutlinedTextField(
                        value = order.name,
                        onValueChange = {},
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done
                        )
                    )
                }
                Spacer(modifier = Modifier.fillMaxWidth().height(10.dp))
                Spacer(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color.Black))
                Spacer(modifier = Modifier.fillMaxWidth().height(10.dp))
                Row(
                    modifier = Modifier.height(60.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Номер",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )

                    OutlinedTextField(
                        value = order.phone,
                        onValueChange = {},
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done
                        )
                    )
                }
                Spacer(modifier = Modifier.fillMaxWidth().height(10.dp))
                Spacer(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color.Black))
                Spacer(modifier = Modifier.fillMaxWidth().height(10.dp))
                Row(
                    modifier = Modifier.height(60.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Комментарий",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )

                    order.comment?.let {
                        OutlinedTextField(
                            value = it,
                            onValueChange = {},
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Done
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.fillMaxWidth().height(10.dp))
                Spacer(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color.Black))
                Spacer(modifier = Modifier.fillMaxWidth().height(10.dp))
                val coroutineScope = rememberCoroutineScope()
                Spacer(modifier = Modifier.height(15.dp))
                Row(modifier = Modifier.fillMaxWidth().height(60.dp), horizontalArrangement = Arrangement.Center){
                    Button(
                        shape = RoundedCornerShape(5.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(109, 80, 255)),
                        modifier = Modifier
                            .fillMaxWidth(0.48f)
                            .fillMaxHeight(),
                        onClick = {
                            coroutineScope.launch {
                                if(deleteOrder(order.id)){
                                    onCancelled()
                                }
                            }

                        }
                    ) {
                        Text(
                            text = "ОТМЕНЁН",
                            modifier = Modifier
                                .fillMaxWidth(),
                            fontSize = 18.sp,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.width(15.dp))

                    Button(
                        shape = RoundedCornerShape(5.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
                        border = BorderStroke(1.dp, Color(109, 80, 255)),
                        elevation = ButtonDefaults.elevation(0.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        onClick = {
                            coroutineScope.launch {
                                if(updateOrderStatus(order.id, "оформлен")=="Success"){
                                    onCancelled()

                                }

                            }
                        }
                    ) {
                        Text(
                            text = "ОФОРМЛЕН",
                            modifier = Modifier
                                .fillMaxWidth(),
                            fontSize = 18.sp,
                            color = Color(109, 80, 255),
                            textAlign = TextAlign.Center
                        )
                    }

                }
            }

        }
    }

}


@Serializable
data class OrderStatusUpdate(
    val id: Int,
    val status: String
)

suspend fun updateOrderStatus(orderId: Int, newStatus: String): String {
    val client = createHttpClient()
    try {
        val url = "https://rst-for-milenaopt.ru/update_order_status"
        val data = OrderStatusUpdate(id = orderId, status = newStatus)

        val jsonData = Json.encodeToString(data) // Преобразование объекта в JSON строку

        return try {
            val response: HttpResponse = client.put(url) {
                contentType(ContentType.Application.Json)
                setBody(jsonData)  // Передача JSON-строки в запрос
            }

            val responseBody = response.bodyAsText()
            if (response.status.value == 200) {
                println("Success")
                "Success"
            } else {
                println("Ошибка при обновлении ${responseBody}")
                "Ошибка при обновлении"
            }
        } catch (e: Exception) {
            println("Ошибка: $e")
            "Ошибка: $e"
        }
    } catch (e: Exception) {
        println("Ошибка: $e")
        return "Ошибка: $e"
    } finally {
        client.close()
    }
}




// Функция для удаления заказа
suspend fun deleteOrder(orderId: Int): Boolean {
    val client = createHttpClient()
    return try {
        val url = "https://rst-for-milenaopt.ru/delete_order" // Замените на ваш URL
        val response: HttpResponse = client.delete(url) {
            contentType(ContentType.Application.Json)
            parameter("id", orderId) // Передаем ID заказа как параметр
        }
        response.status == HttpStatusCode.OK // Успех, если статус ответа 200 OK
    } catch (e: Exception) {
        println("Ошибка при удалении заказа: ${e.message}")
        false
    } finally {
        client.close()
    }
}
