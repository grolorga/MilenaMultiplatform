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
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.input.pointer.PointerIcon.Companion.Text
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Composable
fun Cart(
    cartPreferences: CartPreferences,
    userPreferences: UserPreferences?,
    onOffered:()->Unit
) {
    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
    LaunchedEffect(Unit) {
        products = fetchProducts()
    }
    var productIds by remember { mutableStateOf(cartPreferences.getProductIds()) }
    var productsInCart by remember { mutableStateOf<List<Product>?>(null) }
    var amounts by remember { mutableStateOf(cartPreferences.getProductQuantities()) }
    var sumLow by remember { mutableStateOf(0) }
    var sumHigh by remember { mutableStateOf(0) }
    var economy by remember { mutableStateOf(0) }
    var left by remember { mutableStateOf(0) }
    var tfvalue by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    var showSendCart by remember { mutableStateOf(false) }
    var showSendOffer by remember { mutableStateOf(false) }
    var showAlertLowPrice by remember { mutableStateOf(false) }


    // Загрузка товаров при первой загрузке компонента
    LaunchedEffect(products) {
        productsInCart = getProducts(productIds, products)
    }


    // Функция для обновления суммы и типа цен
    fun updateTotals(productsInCart: List<Product>?, amounts: List<Int>, productIds: List<String>) {
        sumLow = productsInCart?.sumOf { it.priceLow.toInt() * amounts[productIds.indexOf(it.id)] } ?: 0
        sumHigh = productsInCart?.sumOf { it.priceHigh.toInt() * amounts[productIds.indexOf(it.id)] } ?: 0
        economy = sumLow - sumHigh
        left = 33000 - sumLow
        if (sumLow >= 33000) {
            type = "Крупный опт"
            tfvalue = sumHigh.toString()
        } else {
            type = "Мелкий опт"
            tfvalue = sumLow.toString()
        }
    }


    // Использование LaunchedEffect для обновления значений при изменении продуктов в корзине или их количества
    LaunchedEffect(productsInCart, amounts) {
        updateTotals(productsInCart, amounts, productIds)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(15.dp)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .requiredHeight(10000.dp)
    ) {
        Text(
            text = "Корзина",
            modifier = Modifier.fillMaxWidth().padding(15.dp),
            fontSize = 24.sp,
            color = Color.Black,
            textAlign = TextAlign.Center
        )

        if (productIds.isEmpty()) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(600.dp)
                    .background(Color.Transparent)
            ) {
                Text(
                    text = "Здесь пока нет товаров",
                    fontSize = 20.sp,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            if(productsInCart == null || productsInCart!!.isEmpty()){
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(600.dp)
                        .background(androidx.compose.ui.graphics.Color.Transparent)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(80.dp),
                        color = Color(109, 80, 255),
                        strokeWidth = 4.dp
                    )
                }
            }
            else{
                productsInCart!!.forEach { it->
                    sumLow += it.priceLow.toInt()
                    sumHigh += it.priceHigh.toInt()
                }
                Spacer(modifier = Modifier.fillMaxWidth().height(10.dp))
                Spacer(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color.Black))
                Spacer(modifier = Modifier.fillMaxWidth().height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().height(60.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text(
                        text = "Товары в корзине",
                        fontSize = 20.sp,
                        color = Color.Black,
                        //fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Left
                    )
                    Row(
                        modifier = Modifier.height(60.dp).clickable {
                            cartPreferences.clearCart()
                            // Обновляем состояния для LazyColumn
                            productIds = cartPreferences.getProductIds()
                            amounts = cartPreferences.getProductQuantities()
                            productsInCart = emptyList() // Очищаем список товаров
                        },
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically)
                    {
                        Text(
                            text = "Очистить",
                            fontSize = 20.sp,
                            color = Color.Gray,
                            //fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Icon(
                            Icons.Default.Close,
                            contentDescription = null,
                            modifier = Modifier.size(25.dp),
                            tint = Color.Gray
                        )
                    }


                }
                Spacer(modifier = Modifier.fillMaxWidth().height(10.dp))
                Spacer(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color.Black))
                Spacer(modifier = Modifier.fillMaxWidth().height(10.dp))

                // Показ товаров в корзине
                LazyColumn(modifier = Modifier.fillMaxWidth().height(400.dp)) {
                    items(productsInCart!!.size) { index ->
                        val product = productsInCart!![index]
                        val amount = amounts[index]

                        CartProductUnit(
                            product = product,
                            amount = amount,
                            type = type, // Передаем тип цен
                            onDeleteProduct = { prod ->
                                prod.id?.let {
                                    cartPreferences.removeProduct(it)
                                    productIds = cartPreferences.getProductIds()
                                    amounts = cartPreferences.getProductQuantities()
                                    productsInCart = getProducts(productIds, products)
                                }
                            },
                            onHigherAmount = { amo, prod ->
                                prod.id?.let {
                                    cartPreferences.addProduct(it, amo)
                                    productIds = cartPreferences.getProductIds()
                                    amounts = cartPreferences.getProductQuantities()
                                    productsInCart = getProducts(productIds, products)
                                }
                            },
                            onLowerAmount = { amo, prod ->
                                prod.id?.let {
                                    if (amount > prod.number.toInt()) {
                                        cartPreferences.decreaseProductQuantity(it, amo)
                                        productIds = cartPreferences.getProductIds()
                                        amounts = cartPreferences.getProductQuantities()
                                        productsInCart = getProducts(productIds, products)
                                    }
                                }
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
                        text = "Итого",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = tfvalue+" Р",
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
                        text = "Тип цен: ",
                        fontSize = 18.sp,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = type,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                }
                if(type=="Мелкий опт"){
                    Spacer(modifier = Modifier.fillMaxWidth().height(10.dp))
                    Spacer(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color.Black))
                    Spacer(modifier = Modifier.fillMaxWidth().height(10.dp))
                    Row(
                        modifier = Modifier.height(60.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Экономия при крупном опте:",
                            fontSize = 18.sp,
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = economy.toString()+" Р",
                            fontSize = 18.sp,
                            color = Color.Green,
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
                            text = "До крупного опта осталось: ",
                            fontSize = 18.sp,
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = left.toString()+" Р",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Red,
                            textAlign = TextAlign.Center
                        )
                    }
                }
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
                                if(tfvalue.toInt()<10000){
                                    showAlertLowPrice = true
                                }
                                else{
                                    showSendCart = true
                                }



                            }

                        }
                    ) {
                        Text(
                            text = "ПОДЕЛИТЬСЯ КОРЗИНОЙ",
                            modifier = Modifier
                                .fillMaxWidth(),
                            fontSize = 17.sp,
                            color = Color.White,
                            minLines = 2,
                            textAlign = TextAlign.Center
                        )

                    }
                    if(showAlertLowPrice){
                        AlertDialog(
                            modifier = Modifier.fillMaxWidth(0.7f).fillMaxHeight(0.3f),
                            onDismissRequest = {showAlertLowPrice = false},
                            confirmButton = {
                                Button(
                                    shape = RoundedCornerShape(5.dp),
                                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(109, 80, 255)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .fillMaxHeight(),
                                    onClick = {
                                        showAlertLowPrice = false
                                    }
                                ) {
                                    Text(
                                        text = "ПОНЯТНО",
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        fontSize = 18.sp,
                                        color = Color.White,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            },
                            text = {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally){
                                    Text(
                                        text = "Напоминаем, что сумма минимального заказа 10000 Р",
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        fontSize = 18.sp,
                                        color = Color.Black,
                                        minLines = 2,
                                        textAlign = TextAlign.Center
                                    )
                                }

                            }
                        )
                    }
                    if(showSendCart){
                        val receiptContent = generateReceipt(
                            productsInCart = productsInCart ?: emptyList(),
                            amounts = amounts,
                            sumLow = sumLow,
                            sumHigh = sumHigh,
                            type = type
                        )

                        // Сохраняем и делимся чеком
                        saveAndShareReceipt(receiptContent)
                        showSendCart = false
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
                            if(tfvalue.toInt()<10000){
                                showAlertLowPrice = true
                            }
                            else{
                                coroutineScope.launch {
                                    showSendOffer = true
                                }
                            }

                        }
                    ) {
                        Text(
                            text = "ОФОРМИТЬ ЗАКАЗ",
                            modifier = Modifier
                                .fillMaxWidth(),
                            fontSize = 17.sp,
                            color = Color(109, 80, 255),
                            minLines = 2,
                            textAlign = TextAlign.Center
                        )
                    }
                    if(showSendOffer){
                        var name by remember { mutableStateOf("") }
                        var phone by remember { mutableStateOf("") }
                        var userData by remember { mutableStateOf<User?>(null) }
                        var comment by remember { mutableStateOf("") }
                        var errorName by remember { mutableStateOf(false) }
                        var errorComment by remember { mutableStateOf(false) }
                        var errorPhone by remember { mutableStateOf(false) }
                        LaunchedEffect(true){
                            if (userPreferences != null) {
                                userData = userPreferences.getUserLogin()?.let { getUser(it) }
                            }
                            userData?.let {
                                name = it.name
                                phone = it.phone
                            }
                        }
                        val receiptContent = generateReceipt(
                            productsInCart = productsInCart ?: emptyList(),
                            amounts = amounts,
                            sumLow = sumLow,
                            sumHigh = sumHigh,
                            type = type
                        )
                        AlertDialog(
                            onDismissRequest = {showSendOffer = false},
                            modifier = Modifier.fillMaxWidth(0.9f).fillMaxHeight(0.7f),
                            confirmButton = {
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
                                            var selectedSum = if(type=="Мелкий опт") sumLow else sumHigh
                                            userData?.login?.let {
                                                if(sendOffer(name, phone, it,comment, productsInCart!!,
                                                        amounts, type, selectedSum, tfvalue, receiptContent)=="Success"){
                                                    cartPreferences.clearCart()
                                                    // Обновляем состояния для LazyColumn
                                                    productIds = cartPreferences.getProductIds()
                                                    amounts = cartPreferences.getProductQuantities()
                                                    productsInCart = emptyList() // Очищаем список товаров
                                                    onOffered()
                                                }

                                            }
                                        }
                                    }
                                ) {
                                    Text(
                                        text = "ОТПРАВИТЬ",
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        fontSize = 18.sp,
                                        color = Color(109, 80, 255),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            },
                            text = {
                                Column(modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally){
                                    Text(
                                        text = "ФИО",
                                        modifier = Modifier.fillMaxWidth().padding(bottom = 15.dp),
                                        fontSize = 18.sp,
                                        color = Color.Black,
                                        fontWeight = FontWeight.SemiBold,
                                        textAlign = TextAlign.Left
                                    )
                                    OutlinedTextField(
                                        textStyle = TextStyle(fontSize = 18.sp),
                                        value = name,
                                        onValueChange = { name = it },
                                        isError = errorName,  // Пример обработки ошибок
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        keyboardOptions = KeyboardOptions.Default.copy(
                                            imeAction = ImeAction.Done
                                        ),
                                        colors = TextFieldDefaults.outlinedTextFieldColors(
                                            unfocusedBorderColor= Color.Black,
                                            errorBorderColor = Color.Red
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(15.dp))
                                    Text(
                                        text = "Телефон",
                                        modifier = Modifier.fillMaxWidth().padding(bottom = 15.dp),
                                        fontSize = 18.sp,
                                        color = Color.Black,
                                        fontWeight = FontWeight.SemiBold,
                                        textAlign = TextAlign.Left
                                    )
                                    OutlinedTextField(
                                        textStyle = TextStyle(fontSize = 18.sp),
                                        value = phone,
                                        onValueChange = { phone = it },
                                        isError = errorPhone,  // Пример обработки ошибок
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        keyboardOptions = KeyboardOptions.Default.copy(
                                            imeAction = ImeAction.Done
                                        ),
                                        colors = TextFieldDefaults.outlinedTextFieldColors(
                                            unfocusedBorderColor= Color.Black,
                                            errorBorderColor = Color.Red
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(15.dp))
                                    Text(
                                        text = "Коментарий к заказу",
                                        modifier = Modifier.fillMaxWidth().padding(bottom = 15.dp),
                                        fontSize = 18.sp,
                                        color = Color.Black,
                                        fontWeight = FontWeight.SemiBold,
                                        textAlign = TextAlign.Left
                                    )
                                    OutlinedTextField(
                                        textStyle = TextStyle(fontSize = 18.sp),
                                        value = comment,
                                        onValueChange = { comment = it },
                                        isError = errorComment,  // Пример обработки ошибок
                                        modifier = Modifier
                                            .fillMaxWidth().height(100.dp),
                                        keyboardOptions = KeyboardOptions.Default.copy(
                                            imeAction = ImeAction.Done
                                        ),
                                        colors = TextFieldDefaults.outlinedTextFieldColors(
                                            unfocusedBorderColor= Color.Black,
                                            errorBorderColor = Color.Red
                                        )
                                    )
                                }
                            }
                        )



                    }

                }
            }

        }
    }
}



fun getProducts(
    ids: List<String>,
    products: List<Product>
):List<Product>{
    var newProds =  products.filter { it.id in ids }
    println(newProds.toString())
    return newProds
}

fun generateReceipt(
    productsInCart: List<Product>,
    amounts: List<Int>,
    sumLow: Int,
    sumHigh: Int,
    type: String
): String {
    return buildString {
        append("Корзина\n")
        append("№ | Артикул | Название | Кол-во | Цена за шт | Сумма\n")
        productsInCart.forEachIndexed { index, product ->
            val quantity = amounts[index]
            append("${index + 1}. ${product.article}| ${product.name} | $quantity | ${product.priceLow} Р | ${product.priceLow.toInt() * quantity} Р\n")
        }
        append("\n")
        if(type == "Мелкий опт"){
            append("Итого: $sumLow Р\n")
            append("Экономия при крупном опте: ${sumLow-sumHigh} Р\n")
        }
        else{
            append("Итого: $sumHigh Р\n")
        }


        append("Тип цен: $type\n")
    }
}

suspend fun sendOffer(
    name: String,
    phone: String,
    login:String,
    comment:String,
    productsInCart: List<Product>,
    amounts:List<Int>,
    type:String,
    selectedSum:Int,
    finalPrice:String,
    recipe:String
):String{
    val client = createHttpClient()

    try {
        val url = "https://rst-for-milenaopt.ru/create_order"
        val products = productsInCart.map { it.id } // Превращаем объекты в массив строк (IDs)
        val data = mapOf(
            "name" to name,
            "phone" to phone,
            "login" to login,
            "comment" to comment,
            "product_ids" to products.joinToString(","), // Список id в строку через запятую
            "amounts" to amounts.joinToString(","), // Список количеств в строку через запятую
            "type" to type,
            "selected_sum" to selectedSum.toString(),
            "final_price" to finalPrice,
            "status" to "создан", // Статус заказа
            "recipe" to recipe
        )

        val jsonData = Json.encodeToString(data)

        return try {
            val response: HttpResponse = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(jsonData) // Передача JSON-строки в запрос
            }

            val responseBody = response.bodyAsText()
            if (response.status.value == 200) {
                println("Success")
                "Success"
            } else {
                println("Ошибка при отправке ${responseBody}")
                "Ошибка при отправке"
            }
        } catch (e: Exception) {
            println("Ошибка: $e")
            "Ошибка: $e"
        }
    } catch (e: Exception) {
        println("Ошибка: $e")
        return "Ошибка: $e"
    }
}
