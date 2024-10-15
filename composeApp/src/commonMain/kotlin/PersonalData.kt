import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

@Composable
fun PersonalData(
    userPreferences: UserPreferences,
    cartPreferences: CartPreferences,
    onUpdatedUserData:()->Unit,
    onDismiss:()->Unit
){
    val coroutineScope = rememberCoroutineScope()
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var userData by remember { mutableStateOf<User?>(null) }
    LaunchedEffect(true){
        userData = userPreferences.getUserLogin()?.let { getUser(it) }
        userData?.let {
            name = it.name
            phone = it.phone
        }
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(15.dp).fillMaxWidth().verticalScroll(rememberScrollState()).requiredHeight(10000.dp)){
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
        Text(
            text = "Личные данные",
            modifier = Modifier.fillMaxWidth().padding(15.dp),
            fontSize = 24.sp,
            color = Color.Black,
            //fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(35.dp))
        Column(horizontalAlignment = Alignment.Start){


            var email by remember { mutableStateOf(userPreferences.getUserLogin()!!) }
            var errorName by remember { mutableStateOf(false) }
            var errorEmail by remember { mutableStateOf(false) }
            var errorPhone by remember { mutableStateOf(false) }
            Text(
                text = "Фамилия Имя Отчество",
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
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                isError = errorName,  // Пример обработки ошибок
                modifier = Modifier
                    .fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    unfocusedBorderColor= Color.Black,
                    errorBorderColor = Color.Red
                )
            )
            Spacer(modifier = Modifier.height(15.dp))
            Text(
                text = "E-mail",
                modifier = Modifier.fillMaxWidth().padding(bottom = 15.dp),
                fontSize = 18.sp,
                color = Color.Black,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Left
            )
            OutlinedTextField(
                textStyle = TextStyle(fontSize = 18.sp),
                value = email,
                onValueChange = { email = it },
                isError = errorEmail,  // Пример обработки ошибок
                modifier = Modifier
                    .fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    unfocusedBorderColor= Color.Black,
                    errorBorderColor = Color.Red
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
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
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    unfocusedBorderColor= Color.Black,
                    errorBorderColor = Color.Red
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                )
            )

            Spacer(modifier = Modifier.height(30.dp))
            Row(modifier = Modifier.fillMaxWidth().height(70.dp)){
                Button(
                    shape = RoundedCornerShape(5.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(109, 80, 255)),
                    modifier = Modifier
                        .fillMaxWidth(0.48f)
                        .height(60.dp),
                    onClick = {
                        coroutineScope.launch {
                            if(saveNewUserData(userPreferences.getUserLogin()!!, name, email, phone)=="Success"){
                                val pass = userPreferences.getUserPassword()!!
                                userPreferences.clearUserCredentials()
                                userPreferences.saveUserCredentials(email, pass)
                                onUpdatedUserData()
                            }

                        }
                    }
                ) {
                    Text(
                        text = "СОХРАНИТЬ",
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
                        .fillMaxWidth().height(60.dp),
                    onClick = {
                        coroutineScope.launch {
                            userPreferences.clearUserCredentials()
                            cartPreferences.clearCart()
                            onUpdatedUserData()
                        }
                    }
                ) {
                    Text(
                        text = "ВЫХОД",
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

suspend fun saveNewUserData(
    currentUsername:String,
    name:String,
    email:String,
    phone:String
):String{
    val client = createHttpClient()
    try {
        val url = "https://rst-for-milenaopt.ru/update_user"
        val data = mapOf(
            "current_username" to currentUsername,
            "new_username" to email,
            "name" to name,
            "phone" to phone
            )

        val jsonData = Json.encodeToString(data)

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
    }
}

suspend fun getUser(email: String): User? {
    val client = createHttpClient()
    val url = "https://rst-for-milenaopt.ru/get_user_info"
    return try {
        // Формирование POST-запроса
        val response: HttpResponse = client.get(url) {
            parameter("current_username", email)  // Передача параметра в URL
        }

        if (response.status.value == 200) {
            val jsonString = response.body<String>()
            parseUser(jsonString)
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

fun parseUser(jsonString: String): User {
    val jsonObject = Json.parseToJsonElement(jsonString).jsonObject

    return User(
        name = jsonObject["name"]?.jsonPrimitive?.content ?: "",
        login = jsonObject["username"]?.jsonPrimitive?.content ?: "",
        phone = jsonObject["phone"]?.jsonPrimitive?.content ?: "",
        is_admin = jsonObject["is_admin"]?.jsonPrimitive?.content == "1"
    )
}



