import ImageCompressor.toByteArray
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asComposeImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.interop.LocalUIViewController
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.ComposeUIViewController
import io.ktor.client.engine.darwin.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.get
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.refTo
import kotlinx.cinterop.useContents
import kotlinx.cinterop.usePinned
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ColorSpace
import org.jetbrains.skia.ColorType
import org.jetbrains.skia.Image
import org.jetbrains.skia.Image.Companion.makeFromEncoded
import org.jetbrains.skia.ImageInfo
import org.jetbrains.skia.SamplingMode
import platform.CoreFoundation.CFDataGetBytePtr
import platform.CoreFoundation.CFDataGetLength
import platform.CoreFoundation.CFRelease
import platform.CoreGraphics.CGDataProviderCopyData
import platform.CoreGraphics.CGImageAlphaInfo
import platform.CoreGraphics.CGImageGetAlphaInfo
import platform.CoreGraphics.CGImageGetBytesPerRow
import platform.CoreGraphics.CGImageGetDataProvider
import platform.CoreGraphics.CGImageGetHeight
import platform.CoreGraphics.CGImageGetWidth
import platform.CoreGraphics.CGImageRelease
import platform.CoreGraphics.CGRect
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSizeMake
import platform.Foundation.NSData
import platform.Foundation.NSHomeDirectory
import platform.Foundation.NSString
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Foundation.NSURLCache
import platform.Foundation.NSUTF8StringEncoding
import platform.UIKit.UIAlertAction
import platform.UIKit.UIAlertActionStyleDefault
import platform.UIKit.UIAlertController
import platform.UIKit.UIAlertControllerStyleAlert
import platform.UIKit.UIApplication
import platform.UIKit.UIDevice
import platform.UIKit.UIPasteboard
import platform.Foundation.NSUserDefaults
import platform.Foundation.create
import platform.Foundation.dataWithBytes
import platform.Foundation.getBytes
import platform.Foundation.writeToURL
import platform.PDFKit.PDFDocument
import platform.PDFKit.PDFPage
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIEdgeInsetsZero
import platform.UIKit.UIGraphicsBeginImageContext
import platform.UIKit.UIGraphicsBeginImageContextWithOptions
import platform.UIKit.UIGraphicsEndImageContext
import platform.UIKit.UIGraphicsGetCurrentContext
import platform.UIKit.UIGraphicsGetImageFromCurrentImageContext
import platform.UIKit.UIGraphicsPDFRenderer
import platform.UIKit.UIGraphicsPDFRendererContext
import platform.UIKit.UIGraphicsPDFRendererFormat
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.UIKit.UIImagePNGRepresentation
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerDelegateProtocol
import platform.UIKit.UINavigationControllerDelegateProtocol
import platform.UIKit.UIView
import platform.UIKit.UIViewController
import platform.UIKit.endEditing
import platform.darwin.NSObject
import platform.posix.memcpy


class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform = IOSPlatform()

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun getScreenSize(): ScreenSize {
    val containerSize = LocalWindowInfo.current.containerSize
    val screenSize = ScreenSize(
        widthPx = containerSize.width*0.79f,
        heightPx = containerSize.height*0.76f
    )



    return screenSize
}


@Composable
actual fun PhoneNumberHandler(
    phoneNumber: String,
    onCopy: () -> Unit,
    onDial: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(15.dp)
    ) {
        Text(
            text = phoneNumber,
            fontWeight = FontWeight.SemiBold,
            fontSize = 23.sp,
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .padding(start = 15.dp)
                .clickable {
                    // Скопировать номер в буфер обмена
                    UIPasteboard.generalPasteboard().string = phoneNumber

                    // Создание и отображение UIAlertController
                    val alertController = UIAlertController.alertControllerWithTitle(
                        title = "Номер скопирован",
                        message = null,
                        preferredStyle = UIAlertControllerStyleAlert
                    )
                    alertController.addAction(
                        UIAlertAction.actionWithTitle(
                            title = "OK",
                            style = UIAlertActionStyleDefault,
                            handler = null
                        )
                    )

                    val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
                    rootViewController?.presentViewController(alertController, animated = true, completion = null)

                    onCopy()
                }
        )
    }
}

actual fun createHttpClient(): HttpClient {
    return HttpClient(Darwin) {
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


class IOSUserPreferences(private val preferences: NSUserDefaults) : UserPreferences {

    override fun saveUserCredentials(login: String, password: String) {
        preferences.setObject(login, forKey = "user_login")
        preferences.setObject(password, forKey = "user_password")
    }

    override fun getUserLogin(): String? {
        return preferences.stringForKey("user_login")
    }

    override fun getUserPassword(): String? {
        return preferences.stringForKey("user_password")
    }

    override fun clearUserCredentials() {
        preferences.removeObjectForKey("user_login")
        preferences.removeObjectForKey("user_password")
    }
}

// Реализация функции для создания UserPreferences
actual fun createUserPreferences(): UserPreferences {
    val preferences = NSUserDefaults.standardUserDefaults()
    return IOSUserPreferences(preferences)
}

class IOSCartPreferences(private val preferences: NSUserDefaults) : CartPreferences {

    override fun getProductIds(): List<String> {
        return preferences.arrayForKey("product_ids") as? List<String> ?: emptyList()
    }

    override fun getProductQuantities(): List<Int> {
        return preferences.arrayForKey("product_quantities") as? List<Int> ?: emptyList()
    }

    override fun addProduct(productId: String, quantity: Int) {
        val ids = getProductIds().toMutableList()
        val quantities = getProductQuantities().toMutableList()

        val index = ids.indexOf(productId)
        if (index != -1) {
            quantities[index] += quantity
        } else {
            ids.add(productId)
            quantities.add(quantity)
        }

        preferences.setObject(ids, forKey = "product_ids")
        preferences.setObject(quantities, forKey = "product_quantities")
    }

    override fun removeProduct(productId: String) {
        val ids = getProductIds().toMutableList()
        val quantities = getProductQuantities().toMutableList()

        val index = ids.indexOf(productId)
        if (index != -1) {
            ids.removeAt(index)
            quantities.removeAt(index)
        }

        preferences.setObject(ids, forKey = "product_ids")
        preferences.setObject(quantities, forKey = "product_quantities")
    }

    override fun clearCart() {
        preferences.removeObjectForKey("product_ids")
        preferences.removeObjectForKey("product_quantities")
    }
    // Метод для уменьшения количества товара
    override fun decreaseProductQuantity(productId: String, quantity: Int) {
        val ids = getProductIds().toMutableList()
        val quantities = getProductQuantities().toMutableList()

        val index = ids.indexOf(productId)
        if (index != -1) {
            val newQuantity = quantities[index] - quantity
            if (newQuantity > 0) {
                quantities[index] = newQuantity
            } else {
                ids.removeAt(index)
                quantities.removeAt(index)
            }
        }

        preferences.setObject(ids, forKey = "product_ids")
        preferences.setObject(quantities, forKey = "product_quantities")
    }
}

// Функция для создания CartPreferences на iOS
actual fun createCartPreferences(): CartPreferences {
    val preferences = NSUserDefaults.standardUserDefaults()
    return IOSCartPreferences(preferences)
}


actual class ImagePicker(
    private val rootController: UIViewController
) {
    private val imagePickerController = UIImagePickerController().apply {
        sourceType = platform.UIKit.UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypePhotoLibrary
    }
    private var onImagePicked: (ByteArray) -> Unit = {}

    @OptIn(ExperimentalForeignApi::class)
    private val delegate = object : NSObject(), UIImagePickerControllerDelegateProtocol, UINavigationControllerDelegateProtocol {
        override fun imagePickerController(
            picker: UIImagePickerController,
            didFinishPickingImage: UIImage,
            editingInfo: Map<Any?, *>?
        ) {
            val imageNsData = UIImageJPEGRepresentation(didFinishPickingImage, 1.0)
                ?: return
            val bytes = ByteArray(imageNsData.length.toInt())
            memcpy(bytes.refTo(0), imageNsData.bytes, imageNsData.length)

            onImagePicked(bytes)

            picker.dismissViewControllerAnimated(true, null)
        }

        override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
            picker.dismissViewControllerAnimated(true, null)
        }
    }

    actual fun pickImage() {
        val keyWindow = UIApplication.sharedApplication.keyWindow
        val viewController = keyWindow?.rootViewController ?: return
        imagePickerController.delegate = delegate
        viewController.presentViewController(imagePickerController, true, null)
    }

    @Composable
    actual fun registerPicker(onImagePicked: (ByteArray) -> Unit) {
        this.onImagePicked = onImagePicked
    }
}


actual class ImagePickerFactory(
    private val rootController: UIViewController
){
    @Composable
    actual fun createPicker(): ImagePicker{
        return remember {
            ImagePicker(rootController)
        }
    }
}











public actual data class PlatformFile(
    val nsUrl: NSURL,
) {
    public val bytes: ByteArray =
        nsUrl.dataRepresentation.toByteArray()

    @OptIn(ExperimentalForeignApi::class)
    private fun NSData.toByteArray(): ByteArray = ByteArray(this@toByteArray.length.toInt()).apply {
        usePinned {
            memcpy(it.addressOf(0), this@toByteArray.bytes, this@toByteArray.length)
        }
    }
}

@Composable
actual fun FilePicker(
    show: Boolean,
    initialDirectory: String?,
    fileExtensions: List<String>,
    title: String?,
    onFileSelected: FileSelected,
) {
    // Получаем текущий UIViewController для использования в ImagePickerFactory
    val rootController = LocalUIViewController.current

    // Создаём флаг, чтобы гарантировать, что выбор изображения вызывается только один раз
    var hasPickerBeenShown by remember { mutableStateOf(false) }

    // Хранение состояния выбранного изображения
    val selectedImage = remember { mutableStateOf<ByteArray?>(null) }

    // Проверяем флаг, чтобы запускать Picker только один раз
    if (show && !hasPickerBeenShown) {
        hasPickerBeenShown = true

        // Создание и настройка ImagePicker
        val imagePickerFactory = ImagePickerFactory(rootController)
        val imagePicker = imagePickerFactory.createPicker()

        // Регистрируем обработчик выбора изображения
        imagePicker.registerPicker { bytes ->
            selectedImage.value = bytes
            hasPickerBeenShown = false  // Сбрасываем флаг после выбора изображения
            onFileSelected(bytes) // Передаем выбранное изображение дальше
        }

        // Запускаем выбор изображения
        imagePicker.pickImage()
    }
}






@OptIn(ExperimentalForeignApi::class)
private fun UIImage.toSkiaImage(): Image? {
    val imageRef = this.CGImage ?: return null

    val width = CGImageGetWidth(imageRef).toInt()
    val height = CGImageGetHeight(imageRef).toInt()

    val bytesPerRow = CGImageGetBytesPerRow(imageRef)
    val data = CGDataProviderCopyData(CGImageGetDataProvider(imageRef))
    val bytePointer = CFDataGetBytePtr(data)
    val length = CFDataGetLength(data)

    val alphaType = when (CGImageGetAlphaInfo(imageRef)) {
        CGImageAlphaInfo.kCGImageAlphaPremultipliedFirst,
        CGImageAlphaInfo.kCGImageAlphaPremultipliedLast -> ColorAlphaType.PREMUL
        CGImageAlphaInfo.kCGImageAlphaFirst,
        CGImageAlphaInfo.kCGImageAlphaLast -> ColorAlphaType.UNPREMUL
        CGImageAlphaInfo.kCGImageAlphaNone,
        CGImageAlphaInfo.kCGImageAlphaNoneSkipFirst,
        CGImageAlphaInfo.kCGImageAlphaNoneSkipLast -> ColorAlphaType.OPAQUE
        else -> ColorAlphaType.UNKNOWN
    }

    val byteArray = ByteArray(length.toInt()) { index ->
        bytePointer!![index].toByte()
    }

    CFRelease(data)
    CGImageRelease(imageRef)

    val skiaColorSpace = ColorSpace.sRGB
    val colorType = ColorType.RGBA_8888

    // Convert RGBA to BGRA
    for (i in byteArray.indices step 4) {
        val r = byteArray[i]
        val g = byteArray[i + 1]
        val b = byteArray[i + 2]
        val a = byteArray[i + 3]

        byteArray[i] = b
        byteArray[i + 2] = r
    }

    return Image.makeRaster(
        imageInfo = ImageInfo(
            width = width,
            height = height,
            colorType = colorType,
            alphaType = alphaType,
            colorSpace = skiaColorSpace
        ),
        bytes = byteArray,
        rowBytes = bytesPerRow.toInt(),
    )
}


fun UIImage.toImageBitmap(): ImageBitmap {
    val skiaImage = this.toSkiaImage() ?: return ImageBitmap(1,1)
    return skiaImage.toComposeImageBitmap()
}


@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
fun ByteArray.toUIImage(): UIImage? {
    // Конвертируем ByteArray в NSData
    val nsData = this.usePinned { pinnedByteArray ->
        NSData.dataWithBytes(pinnedByteArray.addressOf(0), this.size.toULong())
    }

    // Используем NSData для создания UIImage
    return UIImage.imageWithData(nsData)
}


actual fun ByteArray.toImageBitmap(): ImageBitmap? {
    return try {
        val skiaImage = makeFromEncoded(this)
        skiaImage.toComposeImageBitmap()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}



@OptIn(ExperimentalForeignApi::class)
fun ByteArray.toNSData(): NSData {
    return this.usePinned { pinnedByteArray ->
        NSData.dataWithBytes(pinnedByteArray.addressOf(0), this.size.toULong())
    }
}

actual object ImageCompressor {
    actual fun compress(imageBytes: ByteArray, maxWidth: Int, maxHeight: Int): ByteArray {
        // Конвертируем ByteArray в UIImage
        val uiImage = imageBytes.toUIImage() ?: return imageBytes

        // Изменяем размер изображения
        val scaledUIImage = resizeUIImage(uiImage, maxWidth, maxHeight) ?: return imageBytes

        // Конвертируем обратно в ByteArray
        return scaledUIImage.toByteArray() ?: imageBytes
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun resizeUIImage(uiImage: UIImage, maxWidth: Int, maxHeight: Int): UIImage? {
        val widthScale = maxWidth.toFloat() / uiImage.size.useContents { this.width }
        val heightScale = maxHeight.toFloat() / uiImage.size.useContents { this.height }
        val scaleFactor = minOf(widthScale, heightScale)

        val newSize = CGSizeMake(
            uiImage.size.useContents { this.width } * scaleFactor,
            uiImage.size.useContents { this.height } * scaleFactor
        )

        UIGraphicsBeginImageContextWithOptions(newSize, false, 0.0)
        uiImage.drawInRect(CGRectMake(0.0, 0.0, newSize.useContents { this.width }, newSize.useContents { this.height }))
        val newUIImage = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()

        return newUIImage
    }

    // Функция конвертации UIImage в ByteArray
    private fun UIImage.toByteArray(): ByteArray? {
        val nsData = UIImageJPEGRepresentation(this, 0.8) ?: return null  // 0.8 - уровень сжатия JPEG
        return nsData.toByteArray()
    }

    // Функция конвертации NSData в ByteArray
    @OptIn(ExperimentalForeignApi::class)
    private fun NSData.toByteArray(): ByteArray {
        val length = this.length.toInt()
        val byteArray = ByteArray(length)

        // Получаем указатель на данные из NSData
        val bytesPointer = this.bytes

        // Используем указатель для копирования данных в массив байтов
        bytesPointer?.let { pointer ->
            // Пинним массив байтов
            val pinnedArray = byteArray.usePinned { it }
            // Копируем данные из указателя в массив
            memcpy(pinnedArray.addressOf(0), pointer, length.toULong())
        }

        return byteArray
    }
}

// Создание UIViewController для рендеринга Jetpack Compose UI
fun createComposeViewController(content: @Composable () -> Unit): UIViewController {
    return ComposeUIViewController {
        content()
    }
}

// Реализация для генерации PDF на iOS
@OptIn(ExperimentalForeignApi::class)
actual fun generatePdfFromComposable(
    fileName: String,
    composableContent: @Composable () -> Unit,
    onComplete: (filePath: String?, error: Throwable?) -> Unit
) {
    // Создаем UIViewController с композицией
    val viewController = createComposeViewController(composableContent)
    val view = viewController.view ?: return onComplete(null, Throwable("ViewController's view is null"))

    // Определяем размер PDF страницы
    val pageSize = CGRectMake(0.0, 0.0, 612.0, 792.0) // Стандартный размер A4

    // Настраиваем временный файл для сохранения PDF
    val filePath = "${NSHomeDirectory()}/Documents/${fileName}.pdf"
    val url = NSURL.fileURLWithPath(filePath)

    // Настраиваем UIGraphicsPDFRenderer для рендеринга PDF
    val format = UIGraphicsPDFRendererFormat()
    val pdfRenderer = UIGraphicsPDFRenderer(bounds = pageSize, format = format)

    // Используем writePDFToURL для записи PDF файла
    val success = pdfRenderer.writePDFToURL(url, withActions = { context: UIGraphicsPDFRendererContext? ->
        // Проверяем контекст и начинаем новую страницу
        context?.beginPageWithBounds(pageSize, emptyMap<Any?, Any>())

        // Устанавливаем фрейм представления в контекст PDF
        view.setFrame(context?.pdfContextBounds ?: pageSize)
        view.drawViewHierarchyInRect(view.frame, true)
    }, error = null)

    // Проверка успеха операции и возврат результата
    if (success) {
        onComplete(filePath, null)
    } else {
        onComplete(null, Throwable("Failed to write PDF"))
    }
}


@Composable @OptIn(ExperimentalForeignApi::class)
actual fun saveAndShareReceipt(content: String, fileName: String) {
    // Сохраняем содержимое в файл
    val filePath = NSTemporaryDirectory() + fileName
    val fileUrl = NSURL.fileURLWithPath(filePath)
    val nsStringContent = content as NSString

    nsStringContent.writeToURL(fileUrl, atomically = true, encoding = NSUTF8StringEncoding, error = null)

    // Получаем текущий ViewController
    val currentViewController = getCurrentViewController()

    // Инициализируем UIActivityViewController для шаринга файла
    val activityViewController = UIActivityViewController(listOf(fileUrl), null)
    currentViewController.presentViewController(activityViewController, animated = true, completion = null)
}

// Функция для получения текущего ViewController
fun getCurrentViewController(): UIViewController {
    val keyWindow = UIApplication.sharedApplication.keyWindow ?: throw IllegalStateException("No key window found")
    var topController = keyWindow.rootViewController
    while (topController?.presentedViewController != null) {
        topController = topController.presentedViewController
    }
    return topController!!
}

@Composable
actual fun hideKeyboard() {
    val keyWindow = UIApplication.sharedApplication.keyWindow
    keyWindow?.endEditing(true)
}

actual class ImageCacheManager {

}
@Composable
actual fun clearMemoryCache() {
    // Очистка кэша из оперативной памяти на iOS
    NSURLCache.sharedURLCache.removeAllCachedResponses()
}
@Composable
actual fun clearDiskCache() {
    // Очистка кэша на диске
    NSURLCache.sharedURLCache.diskCapacity = 0u
}


