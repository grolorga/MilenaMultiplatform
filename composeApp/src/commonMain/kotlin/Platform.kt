import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

@Composable
expect fun PhoneNumberHandler(
    phoneNumber: String,
    onCopy: () -> Unit,
    onDial: () -> Unit
)

// Определяем общий интерфейс для клиента
expect fun createHttpClient(): HttpClient


suspend fun fetchData(): String {
    val client = createHttpClient()
    return try {
        val response: HttpResponse = client.get("https://rst-for-milenaopt.ru/users")
        if (response.status.value == 200) {
            response.bodyAsText()  // Преобразуем тело ответа в строку
        } else {
            "Ошибка при получении данных"
        }
    } catch (e: Exception) {
        e.message ?: "Ошибка запроса"
    } finally {
        client.close()
    }
}

// Общий интерфейс для UserPreferences
interface UserPreferences {
    fun getUserLogin(): String?
    fun getUserPassword(): String?
    fun saveUserCredentials(login: String, password: String)
    fun clearUserCredentials()
}

// Функция для создания UserPreferences
expect fun createUserPreferences(): UserPreferences

// Общий интерфейс для управления корзиной
interface CartPreferences {
    fun getProductIds(): List<String>
    fun getProductQuantities(): List<Int>
    fun addProduct(productId: String, quantity: Int)
    fun removeProduct(productId: String)
    fun clearCart()
    fun decreaseProductQuantity(productId: String, quantity: Int)
}
expect fun createCartPreferences(): CartPreferences

expect class ImagePicker {
    @Composable
    fun registerPicker(onImagePicked:(ByteArray)->Unit)
    fun pickImage()
}

expect class ImagePickerFactory{
    @Composable
    fun createPicker(): ImagePicker
}



public expect class PlatformFile

public typealias FileSelected = (ByteArray?) -> Unit

public typealias FilesSelected = (List<PlatformFile>?) -> Unit

@Composable
public expect fun FilePicker(
    show: Boolean,
    initialDirectory: String? = null,
    fileExtensions: List<String> = emptyList(),
    title: String? = null,
    onFileSelected: FileSelected,
)



expect fun ByteArray.toImageBitmap(): ImageBitmap?

expect object ImageCompressor {
    fun compress(imageBytes: ByteArray, maxWidth: Int, maxHeight: Int): ByteArray
}


expect fun generatePdfFromComposable(
    fileName: String,
    composableContent: @Composable () -> Unit,
    onComplete: (filePath: String?, error: Throwable?) -> Unit
)

@Composable
expect fun saveAndShareReceipt(content: String, fileName: String = "cart.txt")

// commonMain
@Composable
expect fun hideKeyboard()

expect class ImageCacheManager {

}
@Composable
expect fun clearMemoryCache()
@Composable
expect fun clearDiskCache()


