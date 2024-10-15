import androidx.compose.foundation.Image
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.readBytes
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.util.decodeBase64Bytes
import io.ktor.util.encodeBase64
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Composable
fun TryGetImage(){
    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var showPicker by remember { mutableStateOf(false) }
    var imageBytes by remember { mutableStateOf<ByteArray?>(null) }
    var selectedImage by remember { mutableStateOf<ByteArray?>(null) }
    LaunchedEffect(true) {
        imageBytes = fetchImage(1)
    }
    AsyncImage(
        model = imageBytes,
        contentDescription = null,
        modifier = Modifier.width(150.dp).height(150.dp),
        contentScale = ContentScale.Crop
    )
    Column(
        modifier = Modifier.padding(15.dp).verticalScroll(rememberScrollState()).requiredHeight(10000.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
                var bs64 = it.encodeBase64()
                var dec64 = bs64.decodeBase64Bytes()
                imageBitmap = dec64.toImageBitmap()
                // Отображаем изображение
                imageBitmap?.let { it1 ->
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
            var coroutineScope = rememberCoroutineScope()
            var str by remember { mutableStateOf("resp") }
            Button(onClick = {

                coroutineScope.launch{
                    str = uploadImage(it)
                }

            }){
                Text("Загрузуить картинку")
            }
            TextField(value = str, onValueChange = {})

            // Вставьте код для отображения изображения, если нужно
        }
    }

}

suspend fun fetchImage(imageId: Int): ByteArray? {
    val client = createHttpClient()
    return try {
        val response: HttpResponse = client.get("https://rst-for-milenaopt.ru/product_image/1")
        if (response.status == HttpStatusCode.OK) {
            response.readBytes()
        } else {
            null
        }
    } catch (e: Exception) {
        null
    } finally {
        client.close()
    }
}

suspend fun uploadImage(imageBytes: ByteArray): String {
    val client = createHttpClient()
    var base64img = imageBytes.encodeBase64()
    try {
        val url = "https://rst-for-milenaopt.ru/upload_image"
        val data = mapOf(
            "image" to base64img
        )

        // Ручная сериализация в JSON
        val jsonData = Json.encodeToString(data)

        try {
            val response: HttpResponse = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(jsonData)  // Передача JSON-строки в запрос
            }

            return if (response.status == HttpStatusCode.OK) {
                "Success"
            } else {
                println("Ошибка при добавлении: ${response.bodyAsText()}")
                ("Ошибка при добавлении: ${response.bodyAsText()}")
            }
        } catch (e: Exception) {
            return("Ошибка: $e")
        }
    } catch (e: Exception) {
        return ("Ошибка: $e")
    }
}