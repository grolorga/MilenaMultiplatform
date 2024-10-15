import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.OutlinedTextField
import androidx.compose.ui.Alignment
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive


@Composable
fun Auth(
    onSuccess:(login:String, password:String)->Unit,
    onRegistr:()->Unit
){


    Box(modifier = Modifier
        .fillMaxSize().background(Color.LightGray),
        contentAlignment = Alignment.Center){
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(15.dp).fillMaxWidth().verticalScroll(rememberScrollState()).requiredHeight(10000.dp)){
            Text(
                text = "Авторизация",
                modifier = Modifier.fillMaxWidth().padding(15.dp),
                fontSize = 24.sp,
                color = Color.Black,
                //fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(15.dp))
            Text(
                text = "Авторизовавшись, вы сможете управлять своими данными, следить за состоянием заказов.",
                modifier = Modifier.fillMaxWidth().padding(15.dp),
                fontSize = 15.sp,
                color = Color.Black,
                //fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(35.dp))
            Column(horizontalAlignment = Alignment.Start){
                Text(
                    text = "Логин",
                    modifier = Modifier.fillMaxWidth().padding(bottom = 15.dp),
                    fontSize = 18.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Left
                )
                /*
                var title by remember { mutableStateOf("Hello, World!") }
                OutlinedTextField(title, {
                    title = it
                },
                    Modifier.fillMaxWidth())

                var text by remember { mutableStateOf("Нажмите кнопку для загрузки данных") }
                val coroutineScope = rememberCoroutineScope()
                TextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier.fillMaxWidth()
                )
                Button(onClick = {
                    coroutineScope.launch {
                        val result = fetchData()  // Вызываем функцию для загрузки данных
                        text = result  // Обновляем текст с результатом запроса
                    }
                }) {
                    Text("Загрузить данные")
                }

                 */
                var isFocusedLogin by remember { mutableStateOf(false) }
                var isFocusedPassword by remember { mutableStateOf(false) }

                val coroutineScope = rememberCoroutineScope()
                var login by remember { mutableStateOf("") }
                var password by remember { mutableStateOf("") }
                var errorLogin by remember { mutableStateOf(false) }
                var errorPassword by remember { mutableStateOf(false) }

                OutlinedTextField(
                    textStyle = TextStyle(fontSize = 18.sp),
                    value = login,
                    onValueChange = { login = it },
                    isError = errorLogin,  // Пример обработки ошибок
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { focusState ->
                            isFocusedLogin = focusState.isFocused
                        },
                    placeholder = {
                        if (!isFocusedLogin && login.isEmpty()) {
                            Text(text = "Введите логин")  // Placeholder, когда поле пустое и не в фокусе
                        }
                    },
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
                    text = "Пароль",
                    modifier = Modifier.fillMaxWidth().padding(bottom = 15.dp),
                    fontSize = 18.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Left
                )
                OutlinedTextField(
                    textStyle = TextStyle(fontSize = 18.sp),
                    value = password,
                    onValueChange = { password = it },
                    isError = errorPassword,  // Пример обработки ошибок
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { focusState ->
                            isFocusedPassword = focusState.isFocused
                        },
                    placeholder = {
                        if (!isFocusedPassword && password.isEmpty()) {
                            Text(text = "Введите пароль")  // Placeholder, когда поле пустое и не в фокусе
                        }
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done
                    ),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        unfocusedBorderColor= Color.Black,
                        errorBorderColor = Color.Red
                    )
                )
                Spacer(modifier = Modifier.height(15.dp))
                Row(modifier = Modifier.fillMaxWidth().height(60.dp), horizontalArrangement = Arrangement.Center){
                    Button(
                        shape = RoundedCornerShape(5.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(109, 80, 255)),
                        modifier = Modifier
                            .fillMaxWidth(0.48f)
                            .fillMaxHeight(),
                        onClick = {
                            if (login.isEmpty())
                                errorLogin = true
                            else if (password.isEmpty())
                                errorPassword = true
                            else
                                coroutineScope.launch {
                                    if (login(login, password) == "Авторизация успешна") {
                                        onSuccess(login, password)
                                    }
                                }
                        }
                    ) {
                        Text(
                            text = "ВОЙТИ",
                            modifier = Modifier
                                .fillMaxWidth(),
                            fontSize = 15.sp,
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
                                onRegistr()
                            }
                        }
                    ) {
                        Text(
                            text = "РЕГИСТРАЦИЯ",
                            modifier = Modifier
                                .fillMaxWidth(),
                            fontSize = 15.sp,
                            color = Color(109, 80, 255),
                            textAlign = TextAlign.Center
                        )
                    }

                }


            }


        }
    }
}


fun decodeUnicode(unicodeString: String): String {
    return unicodeString.replace(Regex("\\\\u([0-9A-Fa-f]{4})")) {
        val charCode = it.groupValues[1].toInt(16)
        charCode.toChar().toString()
    }
}



suspend fun registration(
    name: String,
    login: String,
    phone: String,
    password: String

): String {
    val client = createHttpClient()
    try {
        val url = "https://rst-for-milenaopt.ru/add_user"
        val data = mapOf(
            "name" to name,
            "username" to login,
            "phone" to phone,
            "password" to password // Не забудь использовать хэширование пароля в реальном приложении!
        )

        // Ручная сериализация в JSON
        val jsonData = Json.encodeToString(data)

        try {
            val response: HttpResponse = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(jsonData)  // Передача JSON-строки в запрос
            }

            return if (response.status == HttpStatusCode.Created) {
                "Success"
            } else {
                ("Ошибка при добавлении пользователя: ${response.bodyAsText()}")
            }
        } catch (e: Exception) {
            return("Ошибка: $e")
        }
    } catch (e: Exception) {
        return ("Ошибка: $e")
    }
}


suspend fun login(
    login: String,
    password: String
): String {
    val client = createHttpClient()
    try {
        val url = "https://rst-for-milenaopt.ru/login"
        val data = mapOf(
            "username" to login,
            "password" to password // Не забудь использовать хэширование пароля в реальном приложении!
        )

        val jsonData = Json.encodeToString(data)

        return try {
            val response: HttpResponse = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(jsonData)  // Передача JSON-строки в запрос
            }

            val responseBody = response.bodyAsText()
            if (response.status.value == 200) {
                val message = Json.parseToJsonElement(responseBody).jsonObject["message"]?.jsonPrimitive?.content
                message?.let { decodeUnicode(it) } ?: "Ошибка при авторизации"
            } else {
                "Ошибка при авторизации"
            }
        } catch (e: Exception) {
            "Ошибка: $e"
        }
    } catch (e: Exception) {
        return "Ошибка: $e"
    }
}
