import androidx.compose.animation.core.TwoWayConverter
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Checkbox
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import network.chaintech.kmp_date_time_picker.ui.datepicker.WheelDatePickerView
import network.chaintech.kmp_date_time_picker.utils.DateTimePickerView
import network.chaintech.kmp_date_time_picker.utils.WheelPickerDefaults
import kotlin.io.encoding.Base64

@Composable
fun AddProduct(
    onProductAdded:()->Unit,
    onDismiss:()->Unit
){

    var showPicker by remember { mutableStateOf(false) }
    var selectedImage by remember { mutableStateOf<ByteArray?>(null) }
    var selectedIamges by remember { mutableStateOf<List<ByteArray?>>(emptyList())}
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
            text = "Загрузка товара",
            modifier = Modifier.fillMaxWidth().padding(15.dp),
            fontSize = 24.sp,
            color = Color.Black,
            //fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(15.dp))
        Text(
            text = "Рекомендуемый размер изображения 1200 ширина и 1600 высота, иное разрешение будет сжато автоматически до нужного." +
                    "Несколько раз нажмите на кнопку чтобы выбрать несколько изображений.",
            modifier = Modifier.fillMaxWidth().padding(15.dp),
            fontSize = 16.sp,
            color = Color.Black,
            //fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(35.dp))
        Button(onClick = { showPicker = true }) {
            Text("Добавить изображение")
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
            selectedIamges += it
            selectedImage = null
            // Вставьте код для отображения изображения, если нужно
        }
        LazyColumn(
            modifier = Modifier.fillMaxWidth().height(250.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            items(selectedIamges.size){index->
                selectedIamges[index]?.toImageBitmap()?.let { it1 ->
                    Box(modifier = Modifier.size(200.dp), contentAlignment = Alignment.TopEnd){

                        Image(
                            bitmap = it1,
                            contentDescription = null,
                            modifier = Modifier
                                .size(200.dp)
                                .padding(8.dp),
                            contentScale = ContentScale.Crop
                        )
                        Icon(
                            Icons.Default.Close,
                            contentDescription = null,
                            modifier = Modifier.size(30.dp).clickable {
                                if (index in selectedIamges.indices) {
                                    selectedIamges = selectedIamges.toMutableList().also { it.removeAt(index) }
                                }
                            },
                            tint = Color.White
                        )
                    }

                }
            }
        }

        Spacer(modifier = Modifier.height(15.dp))



        val coroutineScope = rememberCoroutineScope()

        var name by remember { mutableStateOf("") }
        var priceLow by remember { mutableStateOf("") }
        var priceHigh by remember { mutableStateOf("") }
        var article by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }
        var number by remember { mutableStateOf("") }
        var country by remember { mutableStateOf("") }
        var type by remember { mutableStateOf("") }
        var selectedOption by remember { mutableStateOf("") }
        val selectedSizes = remember { mutableStateListOf<String>() }
        val selectedColors = remember { mutableStateListOf<String>() }
        var selectedMaterials by remember { mutableStateOf<List<String>>(emptyList()) }
        var selectedSeasons by remember { mutableStateOf<List<String>>(emptyList()) }
        var new by remember { mutableStateOf(false) }
        var hit by remember { mutableStateOf(false) }

        var errorPriceLow by remember { mutableStateOf(false) }
        var errorPriceHigh by remember { mutableStateOf(false) }
        var errorArticle by remember { mutableStateOf(false) }
        var errorName by remember { mutableStateOf(false) }
        var errorDescription by remember { mutableStateOf(false) }
        var errorNumber by remember { mutableStateOf(false) }
        var errorCountry by remember { mutableStateOf(false) }
        var errorType by remember { mutableStateOf(false) }
        // Здесь вы можете добавить флаги ошибок для списков, если нужно
        var errorSelectedSizes by remember { mutableStateOf(false) }
        var errorSelectedColors by remember { mutableStateOf(false) }
        var errorSelectedMaterials by remember { mutableStateOf(false) }
        var errorSelectedSeasons by remember { mutableStateOf(false) }
        var errorSelectedOption by remember { mutableStateOf(false) }
        Text(
            text = "Название",
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
            placeholder = {
                if (name.isEmpty()) {
                    Text(text = "Введите название товара")  // Placeholder, когда поле пустое и не в фокусе
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
            text = "Описание товара",
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
                    Text(text = "Введите описание товара")  // Placeholder, когда поле пустое и не в фокусе
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
        // Состояние для хранения состояния открытости меню
        var expanded by remember { mutableStateOf(false) }
        // Состояние для хранения выбранного элемента

        // Список доступных элементов
        val options = listOf("Женская одежда", "Одежда Premium", "Мужская одежда", "Детская одежда", "Обувь",
            "Акссесуары", "Для дома", "Товары для бани", "Отдых - развлечения")
        Text(
            text = "Категория",
            modifier = Modifier.fillMaxWidth().padding(bottom = 15.dp),
            fontSize = 18.sp,
            color = Color.Black,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Left
        )
        Box(modifier = Modifier.fillMaxWidth().padding(15.dp)) {
            // Поле для выбора с выпадающим меню
            OutlinedTextField(
                value = selectedOption,
                onValueChange = { /* Игнорируем изменения текстового поля */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true }, // Открываем меню при клике
                label = { Text("Выберите категорию") },
                isError = errorSelectedOption,
                readOnly = true, // Делаем поле только для чтения
                trailingIcon = { // Иконка стрелки вниз для индикации выпадающего меню
                    Icon(
                        Icons.Filled.ArrowDropDown,
                        contentDescription = null,
                        Modifier.clickable { expanded = !expanded } // Открываем/закрываем меню при клике
                    )
                }
            )

            // Выпадающее меню
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false } // Закрываем меню при клике вне его
            ) {
                // Элементы меню
                options.forEach { option ->
                    DropdownMenuItem(onClick = {
                        selectedOption = option // Устанавливаем выбранный элемент
                        expanded = false // Закрываем меню
                    }) {
                        Text(text = option)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(15.dp))
        Text(
            text = "Цена мелкий опт",
            modifier = Modifier.fillMaxWidth().padding(bottom = 15.dp),
            fontSize = 18.sp,
            color = Color.Black,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Left
        )
        OutlinedTextField(
            textStyle = TextStyle(fontSize = 18.sp),
            value = priceLow,
            onValueChange = { priceLow = it },
            isError = errorPriceLow,  // Пример обработки ошибок
            modifier = Modifier
                .fillMaxWidth(),
            placeholder = {
                if (description.isEmpty()) {
                    Text(text = "Введите цену за мелкий опт (только цифры)")  // Placeholder, когда поле пустое и не в фокусе
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
            text = "Цена курный опт",
            modifier = Modifier.fillMaxWidth().padding(bottom = 15.dp),
            fontSize = 18.sp,
            color = Color.Black,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Left
        )
        OutlinedTextField(
            textStyle = TextStyle(fontSize = 18.sp),
            value = priceHigh,
            onValueChange = { priceHigh = it },
            isError = errorPriceHigh,  // Пример обработки ошибок
            modifier = Modifier
                .fillMaxWidth(),
            placeholder = {
                if (description.isEmpty()) {
                    Text(text = "Введите цену за крупный опт (только цифры)")  // Placeholder, когда поле пустое и не в фокусе
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
            text = "Количество в упаковке",
            modifier = Modifier.fillMaxWidth().padding(bottom = 15.dp),
            fontSize = 18.sp,
            color = Color.Black,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Left
        )
        OutlinedTextField(
            textStyle = TextStyle(fontSize = 18.sp),
            value = number,
            onValueChange = { number = it },
            isError = errorNumber,  // Пример обработки ошибок
            modifier = Modifier
                .fillMaxWidth(),
            placeholder = {
                if (description.isEmpty()) {
                    Text(text = "Введите цену за крупный опт (только цифры)")  // Placeholder, когда поле пустое и не в фокусе
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
            text = "Артикул",
            modifier = Modifier.fillMaxWidth().padding(bottom = 15.dp),
            fontSize = 18.sp,
            color = Color.Black,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Left
        )
        OutlinedTextField(
            textStyle = TextStyle(fontSize = 18.sp),
            value = article,
            onValueChange = { article = it },
            isError = errorArticle,  // Пример обработки ошибок
            modifier = Modifier
                .fillMaxWidth(),
            placeholder = {
                if (description.isEmpty()) {
                    Text(text = "Введите артикул товара")  // Placeholder, когда поле пустое и не в фокусе
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
            text = "Страна",
            modifier = Modifier.fillMaxWidth().padding(bottom = 15.dp),
            fontSize = 18.sp,
            color = Color.Black,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Left
        )
        OutlinedTextField(
            textStyle = TextStyle(fontSize = 18.sp),
            value = country,
            onValueChange = { country = it },
            isError = errorCountry,  // Пример обработки ошибок
            modifier = Modifier
                .fillMaxWidth(),
            placeholder = {
                if (description.isEmpty()) {
                    Text(text = "Введите артикул товара")  // Placeholder, когда поле пустое и не в фокусе
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
            text = "Тип товара",
            modifier = Modifier.fillMaxWidth().padding(bottom = 15.dp),
            fontSize = 18.sp,
            color = Color.Black,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Left
        )
        OutlinedTextField(
            textStyle = TextStyle(fontSize = 18.sp),
            value = type,
            onValueChange = { type = it },
            isError = errorType,  // Пример обработки ошибок
            modifier = Modifier
                .fillMaxWidth(),
            placeholder = {
                if (description.isEmpty()) {
                    Text(text = "Введите тип товара")  // Placeholder, когда поле пустое и не в фокусе
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
            text = "Размеры",
            modifier = Modifier.fillMaxWidth().padding(bottom = 15.dp),
            fontSize = 18.sp,
            color = Color.Black,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Left
        )

        var showSelected by remember { mutableStateOf(false) }
        MultiSelectDropdownMenu(
            selectedSizes = selectedSizes,
            error = errorSelectedSizes,
            onSizeSelected = { newSizes ->
                selectedSizes.clear()
                selectedSizes.addAll(newSizes)
                showSelected = true
            }
        )
        if (showSelected){
            println(selectedSizes.joinToString(", "))
            showSelected = false
        }
        Spacer(modifier = Modifier.height(15.dp))
        Text(
            text = "Цвета",
            modifier = Modifier.fillMaxWidth().padding(bottom = 15.dp),
            fontSize = 18.sp,
            color = Color.Black,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Left
        )
        var showSelectedColors by remember { mutableStateOf(false) }
        MultiSelectColorDropdownMenu(
            selectedColors = selectedColors,
            error = errorSelectedColors,
            onColorSelected = { newColors ->
                selectedColors.clear()
                selectedColors.addAll(newColors)
                showSelectedColors = true
            }
        )
        if (showSelectedColors){
            println(selectedColors.joinToString(", "))
            showSelectedColors = false
        }
        Spacer(modifier = Modifier.height(15.dp))
        Text(
            text = "Материалы",
            modifier = Modifier.fillMaxWidth().padding(bottom = 15.dp),
            fontSize = 18.sp,
            color = Color.Black,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Left
        )
        var showColors by remember { mutableStateOf(false) }
        MaterialSelector(
            selectedMaterials = selectedMaterials,
            error = errorSelectedMaterials,
            onMaterialSelected = { newSelectedMaterials ->
                selectedMaterials = newSelectedMaterials
                showColors = true
            }
        )
        if(showColors){
            // Пример использования выбранных материалов
            println(selectedMaterials.joinToString(", "))
            showColors = false
        }
        Spacer(modifier = Modifier.height(15.dp))
        Text(
            text = "Сезон",
            modifier = Modifier.fillMaxWidth().padding(bottom = 15.dp),
            fontSize = 18.sp,
            color = Color.Black,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Left
        )
        var showSeason by remember { mutableStateOf(false) }
        SeasonSelector(
            selectedSeasons = selectedSeasons,
            error = errorSelectedSeasons,
            onSeasonSelected = { newSelectedSeasons ->
                selectedSeasons = newSelectedSeasons
                showSeason = true
            }
        )
        if (showSeason){
            println(selectedSeasons.joinToString(", "))
        }
        Spacer(modifier = Modifier.height(15.dp))
        Text(
            text = "Новинка",
            modifier = Modifier.fillMaxWidth().padding(bottom = 15.dp),
            fontSize = 18.sp,
            color = Color.Black,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Left
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth().padding(15.dp).height(60.dp)
        ){
            Checkbox(
                checked = new,
                onCheckedChange = {new = it}
            )
            Spacer(modifier = Modifier.width(15.dp))
            Text(
                text = "Это новинка",
                modifier = Modifier.fillMaxWidth(),
                fontSize = 18.sp,
                color = Color.Black,
                //fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Left)
        }
        Spacer(modifier = Modifier.height(15.dp))
        Text(
            text = "Хит недели",
            modifier = Modifier.fillMaxWidth(),
            fontSize = 18.sp,
            color = Color.Black,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Left
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth().padding(15.dp).height(60.dp)
        ){
            Checkbox(
                checked = hit,
                onCheckedChange = {hit = it}
            )
            Spacer(modifier = Modifier.width(15.dp))
            Text(
                text = "Это хит недели",
                modifier = Modifier.fillMaxWidth(),
                fontSize = 18.sp,
                color = Color.Black,
                //fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Left)
        }
        Spacer(modifier = Modifier.height(15.dp))
        Button(
            onClick = {
                coroutineScope.launch {

                    // Проверка всех полей
                    val isFormValid = selectedIamges.isNotEmpty() &&
                            name.isNotBlank() &&
                            priceLow.isNotBlank() &&
                            priceHigh.isNotBlank() &&
                            article.isNotBlank() &&
                            description.isNotBlank() &&
                            number.isNotBlank() &&
                            country.isNotBlank() &&
                            type.isNotBlank() &&
                            selectedSizes.isNotEmpty() &&
                            selectedColors.isNotEmpty() &&
                            selectedMaterials.isNotEmpty() &&
                            selectedOption.isNotBlank() &&
                            selectedSeasons.isNotEmpty()

// Установка ошибок, если поля не заполнены
                    errorName = name.isBlank()
                    errorPriceLow = priceLow.isBlank()
                    errorPriceHigh = priceHigh.isBlank()
                    errorArticle = article.isBlank()
                    errorDescription = description.isBlank()
                    errorNumber = number.isBlank()
                    errorCountry = country.isBlank()
                    errorType = type.isBlank()
                    errorSelectedOption = selectedOption.isBlank()
                    errorSelectedSizes = selectedSizes.isEmpty()
                    errorSelectedColors = selectedColors.isEmpty()
                    errorSelectedMaterials = selectedMaterials.isEmpty()
                    errorSelectedSeasons = selectedSeasons.isEmpty()



                    // Выполнение кода, если все значения корректно заполнены и выбраны
                    if (isFormValid) {
                        // Вставьте сюда нужный код, который должен выполниться
                        println("Все поля заполнены корректно, выполняем нужную логику.")
                        if(sendProductRequest(selectedIamges, name, priceLow, priceHigh, article, description, number, country,
                                type, selectedOption, selectedSizes, selectedColors, selectedMaterials,
                                selectedSeasons, new, hit) == "Success"){
                            onProductAdded()
                        }
                    }


                }
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(109, 80, 255)),
        ) {
            Text(
                text = "Отправить товар на сервер",
                modifier = Modifier.background(Color.Transparent)
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                fontSize = 16.sp
            )
        }

    }
}

@Composable
fun MultiSelectDropdownMenu(
    selectedSizes: List<String>,
    error: Boolean,
    onSizeSelected: (List<String>) -> Unit
) {
    // Состояние для управления открытием меню
    var expanded by remember { mutableStateOf(false) }

    // Текст для отображения выбранных размеров
    val selectedText = if (selectedSizes.isEmpty()) {
        "Выберите размеры"
    } else {
        "Выбрано: ${selectedSizes.sorted().joinToString(", ")}"
    }

    // Список всех размеров
    val sizes = listOf(
        "100", "105", "110", "115", "120", "2", "3", "4", "25", "25-30", "25-31", "26", "26-31",
        "27", "28", "28-30", "28-33", "29", "30", "30-36", "31", "32", "33", "34", "35", "36",
        "36-40", "36-41", "36-42", "37", "37-41", "38", "39", "40", "40-42", "40-44", "40-46",
        "40-48", "41", "42", "42-44", "42-46", "42-48", "42-50", "44", "44-46", "44-48", "44-50",
        "44-52", "44-54", "46", "46-48", "46-50", "46-54", "46-56", "48", "48-50", "48-54",
        "48-56", "48-58", "48-60", "50", "50-52", "50-54", "50-56", "50-58", "50-66", "52",
        "52-54", "52-58", "52-60", "54", "54-56", "54-58", "54-62", "56", "56-58", "58",
        "58-60", "60", "60-62", "62", "64", "66", "68", "70", "75", "80", "80B", "80х180 см",
        "85", "90", "95", "L", "M", "S", "XL", "XS", "XXL", "XXS", "XXXL", "3XL", "4XL", "5XL",
        "100 х 100 см", "100х120", "110х110 см", "110х140 см", "110х150 см", "120х65 см", "132х70 см",
        "140", "140х220 см", "140х65 см", "145х205", "145х210 см", "150х200 см", "150х220 см",
        "150х40", "160х200 см", "160х220 см", "170 х 165 см", "170х280 см", "175х205 см", "175х210 см",
        "175х280 см", "180х180 см", "180х200 см", "180х215 см", "180х220 см", "200х220 см", "200х240 см",
        "20х30", "215х125 см", "220х250 см", "22х32 см", "230х250 см", "23х15", "24х24", "25х25 см",
        "25х50 см", "280х155 см", "28х35 см", "300х180 см", "300х190 см", "300х260 см", "300х270 см",
        "300х290 см", "30x65 см", "30х30 см", "30х34 см", "30х40 см", "30х45 см", "30х65 см",
        "30х70 см", "32х28 см", "34х60 см", "34х74 см", "35*60", "35х60 см", "35х75 см", "36х36 см",
        "40х60 см", "43х45", "45х47 см", "45х60 см", "45х75 см", "47х26 см", "50х63 см", "50х70 см",
        "50х80 см", "50х90 см", "55х60 см", "60х90 см", "70х130 см", "70х140 см", "70х30 см", "70х35см",
        "70х70 см", "75х35 см", "80х200 см", "80х80 см", "90х200 см", "95*55 см", "95х110 см"
    )

    // Основной контейнер
    Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        // Поле для выбора с выпадающим меню
        OutlinedTextField(
            value = selectedText,
            onValueChange = { /* Игнорируем изменения текстового поля */ },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }, // Открываем меню при клике
            label = { Text("Выберите размеры") },
            isError = error,
            readOnly = true, // Делаем поле только для чтения
            trailingIcon = { // Иконка стрелки вниз для индикации выпадающего меню
                Icon(
                    Icons.Filled.ArrowDropDown,
                    contentDescription = null,
                    Modifier.clickable { expanded = !expanded } // Открываем/закрываем меню при клике
                )
            }
        )

        // Выпадающее меню
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            } // Закрываем меню при клике вне его
        ) {
            // Элементы меню с чекбоксами
            sizes.forEach { size ->
                DropdownMenuItem(onClick = { }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = selectedSizes.contains(size),
                            onCheckedChange = { isChecked ->
                                val newSelectedSizes = selectedSizes.toMutableList()
                                if (isChecked) {
                                    newSelectedSizes.add(size)
                                } else {
                                    newSelectedSizes.remove(size)
                                }
                                // Обновляем состояние через callback
                                onSizeSelected(newSelectedSizes)
                            }
                        )
                        Text(text = size, modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }
        }
    }
}


@Composable
fun MultiSelectColorDropdownMenu(
    selectedColors: List<String>,
    error: Boolean,
    onColorSelected: (List<String>) -> Unit
) {
    // Состояние для управления открытием меню
    var expanded by remember { mutableStateOf(false) }

    // Текст для отображения выбранных цветов
    val selectedText = if (selectedColors.isEmpty()) {
        "Выберите цвета"
    } else {
        "Выбрано: ${selectedColors.joinToString(", ")}"
    }

    // Список доступных цветов
    val colors = listOf(
        "Бежевый", "Белый", "Бирюзовый", "Бордовый", "В ассортименте", "Голубой",
        "Горчичный", "Графит", "Желтый", "Зеленый", "Золотой", "Камуфляж",
        "Коричневый", "Красный", "Кремовый", "Леопардовый", "Молочный", "Мятный",
        "Оранжевый", "Персиковый", "Разноцветный", "Розовый", "Салатовый", "Светло-серый",
        "Серебрянный", "Серый", "Сине-белая полоска", "Синий", "Сиреневый", "Тёмно-серый",
        "Телесный", "Темно-зеленый", "Темно-синий", "Фиолетовый", "Фуксия", "Хаки",
        "Черно-белый", "Черный"
    )

    // Основной контейнер
    Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        // Поле для выбора с выпадающим меню
        OutlinedTextField(
            value = selectedText,
            onValueChange = { /* Игнорируем изменения текстового поля */ },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }, // Открываем меню при клике
            label = { Text("Выберите цвета") },
            isError = error,
            readOnly = true, // Делаем поле только для чтения
            trailingIcon = { // Иконка стрелки вниз для индикации выпадающего меню
                Icon(
                    Icons.Filled.ArrowDropDown,
                    contentDescription = null,
                    Modifier.clickable { expanded = !expanded } // Открываем/закрываем меню при клике
                )
            }
        )
        val newSelectedColors = selectedColors.toMutableList()
        // Выпадающее меню
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
                onColorSelected(newSelectedColors)
            } // Закрываем меню при клике вне его
        ) {
            // Элементы меню с чекбоксами
            colors.forEach { color ->
                DropdownMenuItem(onClick = { }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = selectedColors.contains(color),
                            onCheckedChange = { isChecked ->

                                if (isChecked) {
                                    newSelectedColors.add(color)
                                } else {
                                    newSelectedColors.remove(color)
                                }
                                // Обновляем состояние через callback
                                onColorSelected(newSelectedColors)
                            }
                        )
                        Text(text = color, modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun MaterialSelector(selectedMaterials: List<String>, error:Boolean, onMaterialSelected: (List<String>) -> Unit) {
    // Состояние для хранения состояния открытости меню
    var expanded by remember { mutableStateOf(false) }

    // Состояние для хранения выбранных материалов
    val selectedMaterialState = remember { mutableStateListOf<String>().apply { addAll(selectedMaterials) } }

    // Список доступных материалов
    val materials = listOf(
        "Акрил", "Бамбук", "Велюр", "Верблюжья шерсть", "Вискоза", "Кашемир", "Кулирка",
        "Лайкра", "Лен", "Нейлон", "Полиамид", "Полиуретан", "Полиэстер", "Силикон",
        "Спандекс", "Фланель", "Футер", "Хлопок", "Шелк", "Шерсть", "Экокожа", "Эластан"
    )

    Box(modifier = Modifier.fillMaxWidth().padding(15.dp)) {
        // Поле для выбора с выпадающим меню
        OutlinedTextField(
            value = selectedMaterialState.joinToString(", "),
            onValueChange = { /* Игнорируем изменения текстового поля */ },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }, // Открываем меню при клике
            label = { Text("Выберите материал") },
            isError = error,
            readOnly = true, // Делаем поле только для чтения
            trailingIcon = { // Иконка стрелки вниз для индикации выпадающего меню
                Icon(
                    Icons.Filled.ArrowDropDown,
                    contentDescription = null,
                    Modifier.clickable { expanded = !expanded } // Открываем/закрываем меню при клике
                )
            }
        )

        // Выпадающее меню
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            } // Закрываем меню при клике вне его
        ) {
            // Элементы меню
            materials.forEach { material ->
                DropdownMenuItem(onClick = {
                    if (selectedMaterialState.contains(material)) {
                        selectedMaterialState.remove(material) // Удаляем материал, если он уже выбран
                    } else {
                        selectedMaterialState.add(material) // Добавляем материал, если он еще не выбран
                    }
                    onMaterialSelected(selectedMaterialState) // Обновляем родительский компонент
                }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = selectedMaterialState.contains(material),
                            onCheckedChange = {
                                if (it) selectedMaterialState.add(material) else selectedMaterialState.remove(material)
                                onMaterialSelected(selectedMaterialState) // Обновляем родительский компонент
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = material)
                    }
                }
            }
        }
    }
}

@Composable
fun SeasonSelector(selectedSeasons: List<String>,error:Boolean, onSeasonSelected: (List<String>) -> Unit) {
    // Состояние для хранения состояния открытости меню
    var expanded by remember { mutableStateOf(false) }

    // Состояние для хранения выбранных сезонов
    val selectedSeasonState = remember { mutableStateListOf<String>().apply { addAll(selectedSeasons) } }

    // Список доступных сезонов
    val seasons = listOf("На любой сезон", "Лето", "Демисезон", "Зима", "Весна")

    Box(modifier = Modifier.fillMaxWidth().padding(15.dp)) {
        // Поле для выбора с выпадающим меню
        OutlinedTextField(
            value = selectedSeasonState.joinToString(", "),
            onValueChange = { /* Игнорируем изменения текстового поля */ },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }, // Открываем меню при клике
            label = { Text("Выберите сезон") },
            readOnly = true, // Делаем поле только для чтения
            isError = error,
            trailingIcon = { // Иконка стрелки вниз для индикации выпадающего меню
                Icon(
                    Icons.Filled.ArrowDropDown,
                    contentDescription = null,
                    Modifier.clickable { expanded = !expanded } // Открываем/закрываем меню при клике
                )
            }
        )

        // Выпадающее меню
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false } // Закрываем меню при клике вне его
        ) {
            // Элементы меню
            seasons.forEach { season ->
                DropdownMenuItem(onClick = {
                    if (selectedSeasonState.contains(season)) {
                        selectedSeasonState.remove(season) // Удаляем сезон, если он уже выбран
                    } else {
                        selectedSeasonState.add(season) // Добавляем сезон, если он еще не выбран
                    }
                    onSeasonSelected(selectedSeasonState) // Обновляем родительский компонент
                }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = selectedSeasonState.contains(season),
                            onCheckedChange = {
                                if (it) selectedSeasonState.add(season) else selectedSeasonState.remove(season)
                                onSeasonSelected(selectedSeasonState) // Обновляем родительский компонент
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = season)
                    }
                }
            }
        }
    }
}

suspend fun add_product(
    name: String,
    priceLow: String,
    priceHigh: String,
    article: String,
    description: String,
    number: String,
    country: String,
    type: String,
    selectedOption: String,
    selectedSizes: List<String>,
    selectedColors: List<String>,
    selectedMaterials: List<String>,
    selectedSeasons: List<String>,
    new: Boolean,
    hit: Boolean,
    images: List<ByteArray?>
): String {
    val client = createHttpClient()
    try {
        val url = "https://rst-for-milenaopt.ru/add_product"

        // Кодирование изображений в base64
        val encodedImages = images.mapNotNull { it?.encodeBase64() }

        val data = mapOf(
            "name" to name,
            "priceLow" to priceLow,
            "priceHigh" to priceHigh,
            "article" to article,
            "description" to description,
            "number" to number,
            "country" to country,
            "type" to type,
            "selectedOption" to selectedOption,
            "selectedSizes" to selectedSizes.joinToString(", "),
            "selectedColors" to selectedColors.joinToString(", "),
            "selectedMaterials" to selectedMaterials.joinToString(", "),
            "selectedSeasons" to selectedSeasons.joinToString(", "),
            "new" to new.toString(),
            "hit" to hit.toString(),
            "images" to encodedImages
        )

        // Сериализация данных в JSON
        val jsonData = Json.encodeToString(data)

        try {
            val response: HttpResponse = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(jsonData)
            }

            return if (response.status == HttpStatusCode.Created) {
                "Success"
            } else {
                println("Ошибка при добавлении продукта: ${response.bodyAsText()}")
                "Ошибка при добавлении продукта: ${response.bodyAsText()}"
            }
        } catch (e: Exception) {
            println("Ошибка: $e")
            return "Ошибка: $e"
        }
    } catch (e: Exception) {
        println("Ошибка: $e")
        return "Ошибка: $e"
    }
}

@Serializable
data class ProductRequest(
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

suspend fun sendProductRequest(
    selectedImages: List<ByteArray?>,
    name: String,
    priceLow: String,
    priceHigh: String,
    article: String,
    description: String,
    number: String,
    country: String,
    type: String,
    selectedOption: String,
    selectedSizes: List<String>,
    selectedColors: List<String>,
    selectedMaterials: List<String>,
    selectedSeasons: List<String>,
    isNew: Boolean,
    isHit: Boolean
): String {
    val client = createHttpClient()
    val base64Images = selectedImages.map { image ->
        image?.encodeBase64() ?: ""
    }


    val requestData = ProductRequest(
        images = base64Images,
        name = name,
        priceLow = priceLow,
        priceHigh = priceHigh,
        article = article,
        description = description,
        number = number,
        country = country,
        type = type,
        selectedOption = selectedOption,
        selectedSizes = selectedSizes,
        selectedColors = selectedColors,
        selectedMaterials = selectedMaterials,
        selectedSeasons = selectedSeasons,
        isNew = isNew,
        isHit = isHit
    )

    try {
        val url = "https://rst-for-milenaopt.ru/add_product"

        println("Отправка данных на сервер: ${Json.encodeToString(requestData)}")

        val response: HttpResponse = client.post(url) {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(requestData))
        }

        println("Ответ от сервера: ${response.status}")
        println("Тело ответа: ${response.bodyAsText()}")

        return if (response.status == HttpStatusCode.Created) {
            "Success"
        } else {
            "Ошибка при добавлении продукта: ${response.bodyAsText()}"
        }
    } catch (e: Exception) {
        println("Ошибка: $e")
        return "Ошибка: $e"
    }
}


