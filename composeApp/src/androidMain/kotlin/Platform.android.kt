import android.app.Application
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.ktor.client.*

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

import android.content.Context
import android.content.SharedPreferences
import io.ktor.http.ContentType
import org.example.milenamultiplatformtry.MainActivity

import android.app.Activity
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.core.content.FileProvider
import io.ktor.client.plugins.HttpTimeout
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.coroutines.resume

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

@Composable
actual fun getScreenSize(): ScreenSize {
    val configuration = LocalConfiguration.current
    val screenWidthPx = with(LocalDensity.current) {
        configuration.screenWidthDp.dp.toPx()
    }
    val screenHeightPx = with(LocalDensity.current) {
        configuration.screenHeightDp.dp.toPx()
    }
    return ScreenSize(
        widthPx = screenWidthPx,
        heightPx = screenHeightPx
    )
}

@Composable
actual fun PhoneNumberHandler(
    phoneNumber: String,
    onCopy: () -> Unit,
    onDial: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    Column(
        modifier = Modifier
            .padding(15.dp)
    ) {
        Text(
            text = phoneNumber,
            fontWeight = FontWeight.SemiBold,
            fontSize = 23.sp,
            modifier = Modifier
                .clickable {
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        data = android.net.Uri.parse("tel:$phoneNumber")
                    }
                    context.startActivity(intent)
                    onDial()
                }
        )
    }
}

actual fun createHttpClient(): HttpClient {
    return HttpClient(OkHttp) {
        install(HttpTimeout) {
            requestTimeoutMillis = 120_000 // Увеличиваем время ожидания до 120 секунд
            connectTimeoutMillis = 60_000 // Время ожидания подключения
            socketTimeoutMillis = 60_000 // Время ожидания ответа от сервера
        }
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }
}


class AndroidUserPreferences(private val context: Context) : UserPreferences {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    override fun getUserLogin(): String? {
        return sharedPreferences.getString("login", null)
    }

    override fun getUserPassword(): String? {
        return sharedPreferences.getString("password", null)
    }

    override fun saveUserCredentials(login: String, password: String) {
        sharedPreferences.edit().putString("login", login).apply()
        sharedPreferences.edit().putString("password", password).apply()
    }

    override fun clearUserCredentials() {
        sharedPreferences.edit().remove("login").apply()
        sharedPreferences.edit().remove("password").apply()
    }
}

// Реализация функции для создания UserPreferences
actual fun createUserPreferences(): UserPreferences {
    val appContext = MainActivity.getAppContext()
    return AndroidUserPreferences(appContext)
}

class AndroidCartPreferences(private val context: Context) : CartPreferences {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("cart_prefs", Context.MODE_PRIVATE)

    override fun getProductIds(): List<String> {
        val ids = sharedPreferences.getStringSet("product_ids", emptySet())
        return ids?.toList() ?: emptyList()
    }

    override fun getProductQuantities(): List<Int> {
        val quantitiesString = sharedPreferences.getString("product_quantities", null)

        // Проверяем, чтобы строка не была пустой, и фильтруем некорректные значения
        return quantitiesString
            ?.split(",")
            ?.mapNotNull { it.toIntOrNull() }  // Преобразуем только корректные значения
            ?: emptyList()
    }

    override fun addProduct(productId: String, quantity: Int) {
        val ids = getProductIds().toMutableSet()
        val quantities = getProductQuantities().toMutableList()

        val index = ids.indexOf(productId)
        if (index != -1) {
            quantities[index] += quantity
        } else {
            ids.add(productId)
            quantities.add(quantity)
        }

        sharedPreferences.edit().putStringSet("product_ids", ids).apply()
        sharedPreferences.edit().putString("product_quantities", quantities.joinToString(",")).apply()
    }

    override fun removeProduct(productId: String) {
        val ids = getProductIds().toMutableSet()
        val quantities = getProductQuantities().toMutableList()

        val index = ids.indexOf(productId)
        if (index != -1) {
            ids.remove(productId)
            quantities.removeAt(index)
        }

        sharedPreferences.edit().putStringSet("product_ids", ids).apply()
        sharedPreferences.edit().putString("product_quantities", quantities.joinToString(",")).apply()
    }

    override fun clearCart() {
        sharedPreferences.edit().remove("product_ids").apply()
        sharedPreferences.edit().remove("product_quantities").apply()
    }
    override fun decreaseProductQuantity(productId: String, quantity: Int) {
        val ids = getProductIds().toMutableSet()
        val quantities = getProductQuantities().toMutableList()

        val index = ids.indexOf(productId)
        if (index != -1) {
            val newQuantity = quantities[index] - quantity
            if (newQuantity > 0) {
                quantities[index] = newQuantity
            } else {
                ids.remove(productId)
                quantities.removeAt(index)
            }
        }

        sharedPreferences.edit().putStringSet("product_ids", ids).apply()
        sharedPreferences.edit().putString("product_quantities", quantities.joinToString(",")).apply()
    }
}

// Функция для создания CartPreferences на Android
actual fun createCartPreferences(): CartPreferences {
    val appContext = MainActivity.getAppContext()
    return AndroidCartPreferences(appContext)
}


actual class ImagePicker(
    private val activity: ComponentActivity
){
    private lateinit var getContent: ActivityResultLauncher<String>

    @Composable
    actual fun registerPicker(onImagePicked: (ByteArray)->Unit){
        getContent = activity.registerForActivityResult(
            ActivityResultContracts.GetContent()
        ){uri->
            uri?.let{
                activity.contentResolver.openInputStream(uri)?.use{
                    onImagePicked(it.readBytes())
                }
            }
        }
    }

    actual fun pickImage(){
        getContent.launch("image/*")
    }
}

actual class ImagePickerFactory{
    @Composable
    actual fun createPicker():ImagePicker{
        val activity = LocalContext.current as ComponentActivity
        return remember {
            ImagePicker(activity)
        }
    }
}

public actual data class PlatformFile(
    val uri: Uri,
)

@Composable
public actual fun FilePicker(
    show: Boolean,
    initialDirectory: String?,
    fileExtensions: List<String>,
    title: String?,
    onFileSelected: FileSelected
) {
    var context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocument()) { result ->
        if (result != null) {

            onFileSelected(context.contentResolver.openInputStream(result)?.readBytes() ?: byteArrayOf())
        } else {
            onFileSelected(null)
        }
    }

    val mimeTypeMap = MimeTypeMap.getSingleton()
    val mimeTypes = if (fileExtensions.isNotEmpty()) {
        fileExtensions.mapNotNull { ext ->
            mimeTypeMap.getMimeTypeFromExtension(ext)
        }.toTypedArray()
    } else {
        emptyArray()
    }

    LaunchedEffect(show) {
        if (show) {
            launcher.launch(mimeTypes)
        }
    }
}




actual fun ByteArray.toImageBitmap(): ImageBitmap? {
    val bitmap = BitmapFactory.decodeByteArray(this, 0, this.size)
    return bitmap.asImageBitmap()
}

actual object ImageCompressor {
    actual fun compress(imageBytes: ByteArray, maxWidth: Int, maxHeight: Int): ByteArray {
        val inputStream = ByteArrayInputStream(imageBytes)
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
            BitmapFactory.decodeStream(inputStream, null, this)
        }

        val inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight)
        options.inJustDecodeBounds = false
        options.inSampleSize = inSampleSize

        val compressedStream = ByteArrayOutputStream()
        val bitmap = BitmapFactory.decodeStream(ByteArrayInputStream(imageBytes), null, options)
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 80, compressedStream)

        return compressedStream.toByteArray()
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val (width, height) = options.run { outWidth to outHeight }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2

            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }
}


actual fun generatePdfFromComposable(
    fileName: String,
    composableContent: @Composable () -> Unit,
    onComplete: (filePath: String?, error: Throwable?) -> Unit
) {
    val context = MainActivity.getAppContext()
    val composeView = ComposeView(context).apply {
        setContent {
            composableContent()
        }
    }

    composeView.post {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(composeView.width, composeView.height, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        composeView.draw(page.canvas)
        pdfDocument.finishPage(page)

        val file = File(context.cacheDir, "$fileName.pdf")
        try {
            pdfDocument.writeTo(FileOutputStream(file))
            pdfDocument.close()
            onComplete(file.absolutePath, null)
        } catch (e: IOException) {
            pdfDocument.close()
            onComplete(null, e)
        }
    }
}


@Composable
actual fun saveAndShareReceipt(content: String, fileName: String) {
    // Получаем директорию для сохранения файлов
    val externalStorageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    val file = File(externalStorageDir, fileName)

    // Записываем содержимое в файл
    FileOutputStream(file).use { outputStream ->
        outputStream.write(content.toByteArray())
    }

    // Получаем контекст приложения
    val context = LocalContext.current

    // Создаем URI для файла через FileProvider (чтобы получить доступ к файлу из других приложений)
    val fileUri: Uri = FileProvider.getUriForFile(
        context,
        context.packageName + ".fileprovider",
        file
    )

    // Создаем Intent для шаринга
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_STREAM, fileUri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // Добавляем этот флаг
    }

    // Запускаем активность для выбора приложения для шаринга
    context.startActivity(Intent.createChooser(shareIntent, "Share Receipt"))
}



@Composable
actual fun hideKeyboard() {
    val keyboardController = LocalSoftwareKeyboardController.current
    keyboardController?.hide()
}

actual class ImageCacheManager {

}
@Composable
actual fun clearMemoryCache() {
    coil3.ImageLoader(LocalContext.current).memoryCache?.clear()
}

@Composable
actual fun clearDiskCache() {
    coil3.ImageLoader(LocalContext.current).diskCache?.clear()
}
