import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
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
import io.ktor.util.encodeBase64
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import network.chaintech.kmp_date_time_picker.ui.datepicker.WheelDatePickerView
import network.chaintech.kmp_date_time_picker.utils.DateTimePickerView
import network.chaintech.kmp_date_time_picker.utils.WheelPickerDefaults


@Composable
fun UploadPromotion(
    imagePicker:ImagePicker,
    onAddedPromo:()->Unit,
    onDismiss:()->Unit
){

    var showPicker by remember { mutableStateOf(false) }
    var selectedImage by remember { mutableStateOf<ByteArray?>(null) }
    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var showDatePicker1 by remember { mutableStateOf(false) }
    var showDatePicker2 by remember { mutableStateOf(false) }
    var selectedDate1 by remember { mutableStateOf("") }
    var selectedDate2 by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.padding(15.dp).verticalScroll(rememberScrollState()).requiredHeight(10000.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
            text = "Загрузка акции",
            modifier = Modifier.fillMaxWidth().padding(15.dp),
            fontSize = 24.sp,
            color = Color.Black,
            //fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(15.dp))
        Text(
            text = "Рекомендуемый размер изображения 1200 ширина и 1600 высота, иное разрешение будет сжато автоматически до нужного.",
            modifier = Modifier.fillMaxWidth().padding(15.dp),
            fontSize = 16.sp,
            color = Color.Black,
            //fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(35.dp))
        Button(onClick = { showPicker = true }) {
            Text("Выбрать изображение")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Вызов FilePicker
        FilePicker(
            show = showPicker,
            fileExtensions = listOf("jpg", "jpeg", "png"), // допустимые расширения файлов
            onFileSelected = { file ->
                selectedImage = file
                showPicker = false


            }
        )

        // Показать выбранное изображение, если оно есть
        selectedImage?.let {
            Text(text = "Выбрано изображение: ${it.size} байт")
            Box(contentAlignment = Alignment.Center,
                modifier = Modifier.height(250.dp).fillMaxWidth()){

                it.toImageBitmap()?.let { it1 ->
                    Image(
                        bitmap = it1,
                        contentDescription = null,
                        modifier = Modifier
                            .size(200.dp)
                            .padding(8.dp),
                        contentScale = ContentScale.Crop
                    )
                }

            }

            // Вставьте код для отображения изображения, если нужно
        }
        Spacer(modifier = Modifier.height(15.dp))


        var isFocusedLogin by remember { mutableStateOf(false) }
        var isFocusedPassword by remember { mutableStateOf(false) }

        val coroutineScope = rememberCoroutineScope()
        var name by remember { mutableStateOf("") }
        var start by remember { mutableStateOf("") }
        var end by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }
        var errorName by remember { mutableStateOf(false) }
        var errorEnd by remember { mutableStateOf(false) }
        var errorStart by remember { mutableStateOf(false) }
        var errorDescription by remember { mutableStateOf(false) }

        OutlinedTextField(
            textStyle = TextStyle(fontSize = 18.sp),
            value = name,
            onValueChange = { name = it },
            isError = errorName,  // Пример обработки ошибок
            modifier = Modifier
                .fillMaxWidth(),
            placeholder = {
                if (name.isEmpty()) {
                    Text(text = "Введите название акции")  // Placeholder, когда поле пустое и не в фокусе
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
        Column(horizontalAlignment = Alignment.CenterHorizontally){
            Button(
                onClick = {
                    showDatePicker1 = true
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(109, 80, 255)),
            ) {
                Text(
                    text = "Выбрать начало акции",
                    modifier = Modifier.background(Color.Transparent)
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    fontSize = 16.sp
                )
            }
            Text(
                text = selectedDate1,
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(15.dp))
            Button(
                onClick = {
                    showDatePicker2 = true
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(109, 80, 255)),
            ) {
                Text(
                    text = "Выбрать конец акции",
                    modifier = Modifier.background(Color.Transparent)
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    fontSize = 16.sp
                )
            }
            Text(
                text = selectedDate2,
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }

        Text(
            text = "Описание акции",
            modifier = Modifier.fillMaxWidth().padding(bottom = 15.dp),
            fontSize = 18.sp,
            color = Color.Black,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Left
        )
        OutlinedTextField(
            textStyle = TextStyle(fontSize = 18.sp),
            value = description,
            onValueChange = { description = it },
            isError = errorDescription,  // Пример обработки ошибок
            modifier = Modifier
                .fillMaxWidth(),
            placeholder = {
                if (description.isEmpty()) {
                    Text(text = "Введите описание акции")  // Placeholder, когда поле пустое и не в фокусе
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
        Button(
            onClick = {





                coroutineScope.launch {
                    selectedImage?.let {
                        if(add_promo(name, description, selectedDate1, selectedDate2,
                                it.encodeBase64())== "Success")
                        {
                            onAddedPromo()
                        }

                    }
                }
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(109, 80, 255)),
        ) {
            Text(
                text = "Отправить акцию на сервер",
                modifier = Modifier.background(Color.Transparent)
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                fontSize = 16.sp
            )
        }

    }


    if (showDatePicker2) {
        WheelDatePickerView(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 22.dp, bottom = 26.dp),
            showDatePicker = showDatePicker2,
            title = "Выбор даты",
            doneLabel = "Готово",
            titleStyle = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333),
            ),
            doneLabelStyle = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight(600),
                color = Color(0xFF007AFF),
            ),
            dateTextColor = Color(0xff007AFF),
            selectorProperties = WheelPickerDefaults.selectorProperties(
                borderColor = Color.LightGray,
            ),
            rowCount = 5,
            height = 180.dp,
            dateTextStyle = TextStyle(
                fontWeight = FontWeight(600),
            ),
            dragHandle = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Spacer(modifier = Modifier.fillMaxWidth().height(10.dp))
                    Spacer(modifier = Modifier.width(70.dp).height(3.dp).background(Color.Black))
                    Spacer(modifier = Modifier.fillMaxWidth().height(10.dp))
                }


            },
            shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp),
            dateTimePickerView = DateTimePickerView.BOTTOM_SHEET_VIEW,
            onDoneClick = {
                selectedDate2 = it.toString()
                showDatePicker2 = false
            },
            onDismiss = {
                showDatePicker2 = false
            }
        )
    }
    if (showDatePicker1) {
        WheelDatePickerView(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 22.dp, bottom = 26.dp),
            showDatePicker = showDatePicker1,
            title = "Выбор даты",
            doneLabel = "Готово",
            titleStyle = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333),
            ),
            doneLabelStyle = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight(600),
                color = Color(0xFF007AFF),
            ),
            dateTextColor = Color(0xff007AFF),
            selectorProperties = WheelPickerDefaults.selectorProperties(
                borderColor = Color.LightGray,
            ),
            rowCount = 5,
            height = 180.dp,
            dateTextStyle = TextStyle(
                fontWeight = FontWeight(600),
            ),
            dragHandle = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Spacer(modifier = Modifier.fillMaxWidth().height(10.dp))
                    Spacer(modifier = Modifier.width(70.dp).height(3.dp).background(Color.Black))
                    Spacer(modifier = Modifier.fillMaxWidth().height(10.dp))
                }


            },
            shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp),
            dateTimePickerView = DateTimePickerView.BOTTOM_SHEET_VIEW,
            onDoneClick = {
                selectedDate1 = it.toString()
                showDatePicker1 = false
            },
            onDismiss = {
                showDatePicker1 = false
            }
        )
    }
}




suspend fun add_promo(
    name: String,
    description: String,
    start: String,
    end: String,
    image: String

): String {
    val client = createHttpClient()
    try {
        val url = "https://rst-for-milenaopt.ru/add_promotion"
        val data = mapOf(
            "start" to start,
            "end" to end,
            "name" to name,
            "description" to description,
            "image" to image
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
                println("Ошибка при добавлении акции: ${response.bodyAsText()}")
                ("Ошибка при добавлении акции: ${response.bodyAsText()}")
            }
        } catch (e: Exception) {
            println("Ошибка: $e")
            return("Ошибка: $e")
        }
    } catch (e: Exception) {
        println("Ошибка: $e")
        return ("Ошибка: $e")
    }
}





