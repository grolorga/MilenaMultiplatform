import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Checkbox
import androidx.compose.material.ChipDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.FilterChip
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalDrawer
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.SelectableChipColors
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode.Companion.Color
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
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
import milenamultiplatformtry.composeapp.generated.resources.Res
import milenamultiplatformtry.composeapp.generated.resources.logot
import org.jetbrains.compose.resources.vectorResource



@Composable
fun CatalogWithFilter(
    oldSelectedCategory: String? = null,
    login:String?,
    cartPreferences: CartPreferences
){

    var selectedCategory by remember { mutableStateOf(oldSelectedCategory) }
    if (oldSelectedCategory == "")
    {
        selectedCategory = null
    }
    var selectedSizes by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedColors by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedMaterials by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedSeasons by remember { mutableStateOf<List<String>>(emptyList()) }
    var qwery by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    var showPleaseAuth by remember { mutableStateOf(false) }
    var showFilter by remember { mutableStateOf(false) }
    /// TODO: Получать данные товаров из базы
    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
    var filteredProducts by remember { mutableStateOf<List<Product>>(emptyList()) }
    LaunchedEffect(Unit){
        filteredProducts = fetchProducts()
        products = filterProducts(
            filteredProducts,
            selectedCategory,
            selectedSizes,
            selectedColors,
            selectedMaterials,
            selectedSeasons,
            qwery
        )
    }
    // Обновляем отфильтрованные продукты при изменении фильтров
    LaunchedEffect(selectedCategory, selectedSizes, selectedColors, selectedMaterials, selectedSeasons, qwery) {
        products = filterProducts(
            filteredProducts,
            selectedCategory,
            selectedSizes,
            selectedColors,
            selectedMaterials,
            selectedSeasons,
            qwery
        )
    }
    var state = rememberLazyGridState()
    val size = getScreenSize().widthPx
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    var closeKey by remember { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxWidth().height(70.dp)){
        Row(
            modifier = Modifier
                .height(70.dp)
                .fillMaxWidth()
                .padding(horizontal = 8.dp), // Добавим немного отступов
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = qwery,
                onValueChange ={
                    qwery = it
                },
                modifier = Modifier
                    .fillMaxWidth(0.7f) // Немного увеличим ширину
                    .height(50.dp), // Увеличим высоту для удобства ввода
                placeholder = {
                    Text(
                        text = "ПОИСК",
                        fontSize = 15.sp,
                        color = androidx.compose.ui.graphics.Color.Gray,
                        textAlign = TextAlign.Left,

                        style = TextStyle(
                            textAlign = TextAlign.Center,
                            lineHeight = 50.sp // Выравнивание по высоте через высоту строки
                        )
                    )
                },
                leadingIcon = { // Иконка поиска внутри поля ввода
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        tint = androidx.compose.ui.graphics.Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),


                singleLine = true, // Поле ввода в одну строку
                shape = RoundedCornerShape(8.dp), // Скругление углов
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = androidx.compose.ui.graphics.Color.Gray,
                    unfocusedBorderColor = androidx.compose.ui.graphics.Color.LightGray
                )
            )
            Spacer(modifier = Modifier.width(8.dp)) // Отступ между полем ввода и кнопкой фильтра
            Row(
                modifier = Modifier
                    .height(50.dp)
                    .clip(RoundedCornerShape(8.dp)) // Скругление углов кнопки фильтра
                    .background(androidx.compose.ui.graphics.Color.LightGray)
                    .clickable { showFilter = true }
                    .padding(horizontal = 12.dp), // Добавим отступы внутри кнопки
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ФИЛЬТР",
                    fontSize = 14.sp,
                    color = androidx.compose.ui.graphics.Color.Gray,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    Icons.Default.Menu,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = androidx.compose.ui.graphics.Color.Gray
                )
            }
        }
    }
    if (showFilter) {
        FilterDialog(
            selectedCategory = selectedCategory,
            selectedSizes = selectedSizes,
            selectedColors = selectedColors,
            selectedMaterials = selectedMaterials,
            selectedSeasons = selectedSeasons,
            onCategorySelected = { newCategory ->
                selectedCategory = newCategory
            },
            onSizeSelected = { newSizes ->
                selectedSizes = newSizes
            },
            onColorSelected = { newColors ->
                selectedColors = newColors
            },
            onMaterialSelected = { newMaterials ->
                selectedMaterials = newMaterials
            },
            onSeasonSelected = { newSeasons ->
                selectedSeasons = newSeasons
            },
            onDismiss = { showFilter = false },
            onResetFilters = {
                selectedCategory = null
                selectedSizes = emptyList()
                selectedColors = emptyList()
                selectedMaterials = emptyList()
                selectedSeasons = emptyList()
            }
        )
    }
    if(products.isNotEmpty()){

        //здесь был фильтр

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),  // Устанавливаем фиксированное количество колонок
            state = state,
            horizontalArrangement = Arrangement.Start, // Убираем центрирование
            verticalArrangement = Arrangement.Top,     // Убираем центрирование
            modifier = Modifier.fillMaxWidth().padding(top = 72.dp)
        ) {
            items(products.size) { ind ->
                ProductUnit(
                    products[ind],
                    onProduct = { selected ->
                        selectedProduct = selected
                    },
                    onAddToBucket = {product, amount ->
                        if(login!=null){
                            coroutineScope.launch {
                                AddToBasket(product, amount, cartPreferences)
                            }
                        }
                        else{
                            showPleaseAuth = true
                        }
                    }

                )
            }
        }
        if(showPleaseAuth){
            AlertDialog(
                onDismissRequest = { showPleaseAuth = false },
                title = { Text("Пожалуйста") },
                text = { Text("Пройдите регистрацию чтобы добавлять товары в корзину") },
                confirmButton = {
                    Button(onClick = { showPleaseAuth = false }) {
                        Text("OK")
                    }
                }
            )
        }
        selectedProduct?.let {
            ProductDetails(
                it,
                onDismiss = { selectedProduct = null },
                onAddToBucket = {product, amount ->
                    if(login!=null){
                        coroutineScope.launch {
                            AddToBasket(product, amount, cartPreferences)
                        }
                    }
                    else{
                        showPleaseAuth = true
                    }
                }
            )
        }


    }
    else{
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(androidx.compose.ui.graphics.Color.Transparent).padding(top = 56.dp)
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



}


suspend fun AddToBasket(
    product: Product,
    amount: Int,
    cartPreferences: CartPreferences
):String{


    try {
        product.id?.let { cartPreferences.addProduct(it, amount) }
        return "Success"
    } catch (e: Exception) {
        println("Ошибка: $e")
        return ("Ошибка: $e")
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FilterPanel(
    selectedCategory: String?,
    selectedSizes: List<String>,
    selectedColors: List<String>,
    selectedMaterials: List<String>,
    selectedSeasons: List<String>,
    onCategorySelected: (String) -> Unit,
    onSizeSelected: (String) -> Unit,
    onColorSelected: (String) -> Unit,
    onMaterialSelected: (String) -> Unit,
    onSeasonSelected: (String) -> Unit
) {

    val categories = listOf("Женская одежда", "Одежда Premium", "Мужская одежда", "Детская одежда", "Обувь",
        "Акссесуары", "Для дома", "Товары для бани", "Отдых - развлечения")

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
    val colors = listOf("Бежевый", "Белый", "Бирюзовый", "Бордовый", "В ассортименте", "Голубой",
        "Горчичный", "Графит", "Желтый", "Зеленый", "Золотой", "Камуфляж",
        "Коричневый", "Красный", "Кремовый", "Леопардовый", "Молочный", "Мятный",
        "Оранжевый", "Персиковый", "Разноцветный", "Розовый", "Салатовый", "Светло-серый",
        "Серебрянный", "Серый", "Сине-белая полоска", "Синий", "Сиреневый", "Тёмно-серый",
        "Телесный", "Темно-зеленый", "Темно-синий", "Фиолетовый", "Фуксия", "Хаки",
        "Черно-белый", "Черный")



// Список доступных материалов
    val materials = listOf(
        "Акрил", "Бамбук", "Велюр", "Верблюжья шерсть", "Вискоза", "Кашемир", "Кулирка",
        "Лайкра", "Лен", "Нейлон", "Полиамид", "Полиуретан", "Полиэстер", "Силикон",
        "Спандекс", "Фланель", "Футер", "Хлопок", "Шелк", "Шерсть", "Экокожа", "Эластан"
    )

    val seasons = listOf("На любой сезон", "Лето", "Демисезон", "Зима", "Весна")
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Text("Категории", fontSize = 20.sp, modifier = Modifier.padding(bottom = 8.dp))
        LazyRow {
            items(categories) { category ->
                FilterChip(
                    selected = category == selectedCategory,
                    onClick = { onCategorySelected(category) },
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text(text = category)
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Размеры", fontSize = 20.sp, modifier = Modifier.padding(bottom = 8.dp))
        LazyRow {
            items(sizes) { size ->
                FilterChip(
                    selected = size in selectedSizes,
                    onClick = {
                        onSizeSelected(size)},
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text(text = size)
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Цвета", fontSize = 20.sp, modifier = Modifier.padding(bottom = 8.dp))
        LazyRow {
            items(colors) { color ->
                FilterChip(
                    selected = color in selectedColors,
                    onClick = { onColorSelected(color) },
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text(text = color)
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Материалы", fontSize = 20.sp, modifier = Modifier.padding(bottom = 8.dp))
        LazyRow {
            items(materials) { material ->
                FilterChip(
                    selected = material in selectedMaterials,
                    onClick = { onMaterialSelected(material) },
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text(text = material)
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        /*
        Text("Сезоны", fontSize = 20.sp, modifier = Modifier.padding(bottom = 8.dp))
        LazyRow {
            items(seasons) { season ->
                FilterChip(
                    selected = season in selectedSeasons,
                    onClick = { onSeasonSelected(season) },
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text(text = season)
                }
            }
        }

         */
    }
}


@Composable
fun SideMenu(onDismiss: () -> Unit) {
    val categories = listOf(
        "Женская одежда", "Одежда Premium", "Мужская одежда", "Детская одежда", "Обувь",
        "Акссесуары", "Для дома", "Товары для бани", "Отдых - развлечения"
    )

    val sizes = listOf("100", "105", "110", "115", "120", "2", "3", "4", "25", "25-30", "25-31", "26", "26-31", "27", "28", "28-30", "28-33", "29", "30", "30-36", "31", "32", "33", "34", "35", "36", "36-40", "36-41", "36-42", "37", "37-41", "38", "39", "40", "40-42", "40-44", "40-46", "40-48", "41", "42", "42-44", "42-46", "42-48", "42-50", "44", "44-46", "44-48", "44-50", "44-52", "44-54", "46", "46-48", "46-50", "46-54", "46-56", "48", "48-50", "48-54", "48-56", "48-58", "48-60", "50", "50-52", "50-54", "50-56", "50-58", "50-66", "52", "52-54", "52-58", "52-60", "54", "54-56", "54-58", "54-62", "56", "56-58", "58", "58-60", "60", "60-62", "62", "64", "66", "68", "70", "75", "80", "80B", "80х180 см", "85", "90", "95", "L", "M", "S", "XL", "XS", "XXL", "XXS", "XXXL", "3XL", "4XL", "5XL", "100 х 100 см", "100х120", "110х110 см", "110х140 см", "110х150 см", "120х65 см", "132х70 см", "140", "140х220 см", "140х65 см", "145х205", "145х210 см", "150х200 см", "150х220 см", "150х40", "160х200 см", "160х220 см", "170 х 165 см", "170х280 см", "175х205 см", "175х210 см", "175х280 см", "180х180 см", "180х200 см", "180х215 см", "180х220 см", "200х220 см", "200х240 см", "20х30", "215х125 см", "220х250 см", "22х32 см", "230х250 см", "23х15", "24х24", "25х25 см", "25х50 см", "280х155 см", "28х35 см", "300х180 см", "300х190 см", "300х260 см", "300х270 см", "300х290 см", "30x65 см", "30х30 см", "30х34 см", "30х40 см", "30х45 см", "30х65 см", "30х70 см", "32х28 см", "34х60 см", "34х74 см", "35*60", "35х60 см", "35х75 см", "36х36 см", "40х60 см", "43х45", "45х47 см", "45х60 см", "45х75 см", "47х26 см", "50х63 см", "50х70 см", "50х80 см", "50х90 см", "55х60 см", "60х90 см", "70х130 см", "70х140 см", "70х30 см", "70х35см", "70х70 см", "75х35 см", "80х200 см", "80х80 см", "90х200 см", "95*55 см", "95х110 см")

    val colors = listOf("Бежевый", "Белый", "Бирюзовый", "Бордовый", "В ассортименте", "Голубой", "Горчичный", "Графит", "Желтый", "Зеленый", "Золотой", "Камуфляж", "Коричневый", "Красный", "Кремовый", "Леопардовый", "Молочный", "Мятный", "Оранжевый", "Персиковый", "Разноцветный", "Розовый", "Салатовый", "Светло-серый", "Серебрянный", "Серый", "Сине-белая полоска", "Синий", "Сиреневый", "Тёмно-серый", "Телесный", "Темно-зеленый", "Темно-синий", "Фиолетовый", "Фуксия", "Хаки", "Черно-белый", "Черный")
    val materials = listOf(
        "Акрил", "Бамбук", "Велюр", "Верблюжья шерсть", "Вискоза", "Кашемир", "Кулирка",
        "Лайкра", "Лен", "Нейлон", "Полиамид", "Полиуретан", "Полиэстер", "Силикон",
        "Спандекс", "Фланель", "Футер", "Хлопок", "Шелк", "Шерсть", "Экокожа", "Эластан"
    )

    //val seasons = listOf("На любой сезон", "Лето", "Демисезон", "Зима", "Весна")

    ModalDrawer(
        drawerContent = {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Фильтры", fontSize = 24.sp, modifier = Modifier.padding(bottom = 16.dp))

                FilterDropdown(label = "Категории", options = categories)
                Spacer(modifier = Modifier.height(16.dp))

                FilterDropdown(label = "Размеры", options = sizes)
                Spacer(modifier = Modifier.height(16.dp))

                FilterDropdown(label = "Цвета", options = colors)
                Spacer(modifier = Modifier.height(16.dp))

                FilterDropdown(label = "Материалы", options = materials)
                Spacer(modifier = Modifier.height(16.dp))

                //FilterDropdown(label = "Сезоны", options = seasons)
            }
        },
        gesturesEnabled = true
    ) {
        // Content for the main screen
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FilterDropdown(
    label: String,
    options: List<String>,
    selectedOption: String? = null,
    onOptionSelected: (String) -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(label, fontSize = 20.sp, modifier = Modifier.padding(bottom = 8.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                readOnly = true,
                value = selectedOption ?: "Выберите $label",
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
                },
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = androidx.compose.ui.graphics.Color.White
                )
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        content = { Text(option) },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun FilterDialog(
    selectedCategory: String?,
    selectedSizes: List<String>,
    selectedColors: List<String>,
    selectedMaterials: List<String>,
    selectedSeasons: List<String>,
    onCategorySelected: (String?) -> Unit,
    onSizeSelected: (List<String>) -> Unit,
    onColorSelected: (List<String>) -> Unit,
    onMaterialSelected: (List<String>) -> Unit,
    onSeasonSelected: (List<String>) -> Unit,
    onDismiss: () -> Unit,
    onResetFilters: () -> Unit
) {

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { /*Text("Выберите фильтры")*/ },
        text = {
            Column {
                CategorySelector( selectedCategory = selectedCategory, onCategorySelected = onCategorySelected)
                SizeSelector(selectedSizes = selectedSizes, onSizeSelected = onSizeSelected)
                ColorSelector(selectedColors = selectedColors, onColorSelected = onColorSelected)
                MaterialsSelector(selectedMaterials = selectedMaterials, onMaterialSelected = onMaterialSelected)
                //SeasonSelector(selectedSeasons = selectedSeasons, error = false ,onSeasonSelected = onSeasonSelected)
                Button(
                    onClick = {
                        onResetFilters() // Сбросить все фильтры
                    },
                    shape = RoundedCornerShape(5.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(109, 80, 255)),
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Сбросить фильтры",
                        modifier = Modifier
                            .fillMaxWidth(),
                        fontSize = 15.sp,
                        color = androidx.compose.ui.graphics.Color.White,
                        textAlign = TextAlign.Center)
                }
            }
        },
        confirmButton = {

        },
        dismissButton = {

        }
    )
}


@Composable
fun MaterialsSelector(selectedMaterials: List<String>, onMaterialSelected: (List<String>) -> Unit) {
    val materials = listOf(
        "Акрил", "Бамбук", "Велюр", "Верблюжья шерсть", "Вискоза", "Кашемир", "Кулирка",
        "Лайкра", "Лен", "Нейлон", "Полиамид", "Полиуретан", "Полиэстер", "Силикон",
        "Спандекс", "Фланель", "Футер", "Хлопок", "Шелк", "Шерсть", "Экокожа", "Эластан"
    )

    MultiSelectDropdown(
        label = "Выберите материал",
        items = materials,
        selectedItems = selectedMaterials,
        onSelectionChanged = onMaterialSelected
    )
}

@Composable
fun ColorSelector(selectedColors: List<String>, onColorSelected: (List<String>) -> Unit) {
    val colors = listOf("Бежевый", "Белый", "Бирюзовый", "Бордовый", "В ассортименте", "Голубой",
        "Горчичный", "Графит", "Желтый", "Зеленый", "Золотой", "Камуфляж",
        "Коричневый", "Красный", "Кремовый", "Леопардовый", "Молочный", "Мятный",
        "Оранжевый", "Персиковый", "Разноцветный", "Розовый", "Салатовый", "Светло-серый",
        "Серебрянный", "Серый", "Сине-белая полоска", "Синий", "Сиреневый", "Тёмно-серый",
        "Телесный", "Темно-зеленый", "Темно-синий", "Фиолетовый", "Фуксия", "Хаки",
        "Черно-белый", "Черный")

    MultiSelectDropdown(
        label = "Выберите цвет",
        items = colors,
        selectedItems = selectedColors,
        onSelectionChanged = onColorSelected
    )
}
@Composable
fun SizeSelector(selectedSizes: List<String>, onSizeSelected: (List<String>) -> Unit) {
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

    MultiSelectDropdown(
        label = "Выберите размер",
        items = sizes,
        selectedItems = selectedSizes,
        onSelectionChanged = onSizeSelected
    )
}



@Composable
fun CategorySelector(
    selectedCategory: String?,
    onCategorySelected: (String?) -> Unit
) {
    // Список доступных категорий
    val categories = listOf(
        "Женская одежда", "Одежда Premium", "Мужская одежда", "Детская одежда",
        "Обувь", "Аксессуары", "Одежда по размерам", "Для дома", "Товары для бани",
        "Отдых - Развлечения", "Канцелярские товары", "Спецодежда"
    )

    // Состояние для управления выпадающим меню
    var expanded by remember { mutableStateOf(false) }

    // Отображение поля с выпадающим меню
    Box(modifier = Modifier.fillMaxWidth().padding(15.dp)) {
        OutlinedTextField(
            value = selectedCategory ?: "", // Отображаем выбранную категорию или пустую строку
            onValueChange = { /* Игнорируем изменения текстового поля */ },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }, // Открываем меню при клике
            label = { Text("Выберите категорию") },
            readOnly = true, // Делаем поле только для чтения
            trailingIcon = {
                Icon(
                    Icons.Filled.ArrowDropDown,
                    contentDescription = null,
                    Modifier.clickable { expanded = !expanded } // Переключение меню при клике
                )
            }
        )

        // Выпадающее меню для выбора категории
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false } // Закрываем меню, когда кликаем вне его
        ) {
            categories.forEach { category ->
                DropdownMenuItem(onClick = {
                    onCategorySelected(category) // Возвращаем выбранную категорию
                    expanded = false // Закрываем меню
                }) {
                    Text(text = category)
                }
            }
            // Разделитель между списком категорий и кнопкой сброса
            Divider()

            // Кнопка для сброса фильтров
            DropdownMenuItem(onClick = {
                onCategorySelected(null) // Сбрасываем выбранную категорию
                expanded = false // Закрываем меню
            }) {
                Text(text = "Сбросить фильтры", color = androidx.compose.ui.graphics.Color.Red)
            }
        }
    }
}



@Composable
fun MultiSelectDropdown(
    label: String,
    items: List<String>,
    selectedItems: List<String>,
    onSelectionChanged: (List<String>) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedItemsState = remember { mutableStateListOf<String>().apply { addAll(selectedItems) } }

    Box(modifier = Modifier.fillMaxWidth().padding(15.dp)) {
        // Поле для выбора с выпадающим меню
        OutlinedTextField(
            value = selectedItemsState.joinToString(", "),
            onValueChange = { /* Игнорируем изменения текстового поля */ },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }, // Открываем меню при клике
            label = { Text(label) },
            enabled = false,
            readOnly = true, // Делаем поле только для чтения
            trailingIcon = {
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
            items.forEach { item ->
                DropdownMenuItem(onClick = {
                    if (selectedItemsState.contains(item)) {
                        selectedItemsState.remove(item) // Удаляем элемент, если он уже выбран
                    } else {
                        selectedItemsState.add(item) // Добавляем элемент, если он еще не выбран
                    }
                    onSelectionChanged(selectedItemsState) // Обновляем родительский компонент
                }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = selectedItemsState.contains(item),
                            onCheckedChange = {
                                if (it) selectedItemsState.add(item) else selectedItemsState.remove(item)
                                onSelectionChanged(selectedItemsState) // Обновляем родительский компонент
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = item)
                    }
                }
            }
        }
    }
}



// Функция фильтрации продуктов
fun filterProducts(
    products: List<Product>,
    selectedCategory: String?,
    selectedSizes: List<String>,
    selectedColors: List<String>,
    selectedMaterials: List<String>,
    selectedSeasons: List<String>,
    query: String // Новый параметр для поиска
): List<Product> {
    return products.filter { product ->
        // Если категория не выбрана (selectedCategory == null), то фильтрация по категориям игнорируется
        val matchesCategory = selectedCategory?.let { product.type == it } ?: true

        // Если списки размеров, цветов, материалов или сезонов пусты, они игнорируются
        val matchesSizes = selectedSizes.isEmpty() || product.selectedSizes.any { it in selectedSizes }
        val matchesColors = selectedColors.isEmpty() || product.selectedColors.any { it in selectedColors }
        val matchesMaterials = selectedMaterials.isEmpty() || product.selectedMaterials.any { it in selectedMaterials }
        val matchesSeasons = selectedSeasons.isEmpty() || product.selectedSeasons.any { it in selectedSeasons }

        // Поиск по названию и описанию
        val matchesQuery = query.isBlank() ||
                product.name.contains(query, ignoreCase = true) ||
                product.description.contains(query, ignoreCase = true) ||
                product.article.contains(query, ignoreCase = true)

        // Продукт должен соответствовать всем выбранным фильтрам и запросу
        matchesCategory && matchesSizes && matchesColors && matchesMaterials && matchesSeasons && matchesQuery
    }
}

