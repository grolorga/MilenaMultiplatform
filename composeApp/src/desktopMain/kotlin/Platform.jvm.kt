import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageBitmapConfig
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import io.ktor.client.*
import io.ktor.client.engine.java.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.core.toByteArray
import kotlinx.serialization.json.Json
import org.jetbrains.skiko.toBitmap
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.File
import javax.imageio.ImageIO


import javax.swing.JFileChooser


class JVMPlatform: Platform {
    override val name: String = "Java ${System.getProperty("java.version")}"
}

actual fun getPlatform(): Platform = JVMPlatform()

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun getScreenSize(): ScreenSize {
    val containerSize = LocalWindowInfo.current.containerSize
    return ScreenSize(
        widthPx = containerSize.width.toFloat(),
        heightPx = containerSize.height.toFloat()
    )
}

@Composable
actual fun PhoneNumberHandler(
    phoneNumber: String,
    onCopy: () -> Unit,
    onDial: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    val uriHandler = LocalUriHandler.current

    Column(
        modifier = Modifier
            .padding(16.dp)
    ) {
        Text(
            text = phoneNumber,
            modifier = Modifier
                .padding(16.dp)
                .clickable {
                    clipboardManager.setText(AnnotatedString(phoneNumber))
                    println("Номер скопирован")
                    onCopy()
                }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Позвонить",
            modifier = Modifier
                .padding(16.dp)
                .clickable {
                    uriHandler.openUri("tel:$phoneNumber")
                    onDial()
                }
        )
    }
}

actual fun createHttpClient(): HttpClient {
    return HttpClient(Java) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }
}



class JVMUserPreferences(private val loginFile: File, private val passwordFile: File) : UserPreferences {

    override fun saveUserCredentials(login: String, password: String) {
        loginFile.writeText(login)
        passwordFile.writeText(password)
    }

    override fun getUserLogin(): String? {
        return if (loginFile.exists()) loginFile.readText() else null
    }

    override fun getUserPassword(): String? {
        return if (passwordFile.exists()) passwordFile.readText() else null
    }

    override fun clearUserCredentials() {
        loginFile.delete()
        passwordFile.delete()
    }
}

// Реализация функции для создания UserPreferences
actual fun createUserPreferences(): UserPreferences {
    val loginFile = File(System.getProperty("user.home"), "user_login.txt")
    val passwordFile = File(System.getProperty("user.home"), "user_password.txt")
    return JVMUserPreferences(loginFile, passwordFile)
}

actual class ImagePicker {

    private var onImagePicked: (ByteArray) -> Unit = {}

    @Composable
    actual fun registerPicker(onImagePicked: (ByteArray) -> Unit) {
        this.onImagePicked = onImagePicked
    }

    actual fun pickImage() {
        val fileChooser = JFileChooser()
        fileChooser.dialogTitle = "Выбрать изображение"
        val returnValue = fileChooser.showOpenDialog(null)

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            val file: File? = fileChooser.selectedFile
            file?.let {
                onImagePicked(it.readBytes())
            }
        }
    }
}


actual class ImagePickerFactory{
    @Composable
    actual fun createPicker(): ImagePicker{
        return ImagePicker()
    }
}

public actual data class PlatformFile(
    val file: File,
)

@Composable
public actual fun FilePicker(
    show: Boolean,
    initialDirectory: String?,
    fileExtensions: List<String>,
    title: String?,
    onFileSelected: FileSelected,
) {
    /// TODO:
}

internal fun chooseFile(
    initialDirectory: String,
    fileExtension: String,
    title: String?
): String?  {
    val filters = if (fileExtension.isNotEmpty()) fileExtension.split(",") else emptyList()
    return ""

}



actual fun ByteArray.toImageBitmap(): ImageBitmap? {
    val byteArrayInputStream = ByteArrayInputStream(this)
    val bufferedImage: BufferedImage = ImageIO.read(byteArrayInputStream)
    return bufferedImage.toBitmap().asImageBitmap()
}
actual fun createCartPreferences(): CartPreferences{
    return TODO("Provide the return value")
}

actual fun generatePdfFromComposable(
    fileName: String,
    composableContent: @Composable () -> Unit,
    onComplete: (filePath: String?, error: Throwable?) -> Unit
){
    // TODO:  
}
@Composable
actual fun saveAndShareReceipt(content: String, fileName: String){
    // TODO:
}

@Composable
actual fun hideKeyboard() {
    // TODO:
}

actual class ImageCacheManager {

}
@Composable
actual fun clearMemoryCache() {
    // Очистка кэша из оперативной памяти на iOS
    // TODO:
}
@Composable
actual fun clearDiskCache() {
    // Очистка кэша на диске
    // TODO:
}