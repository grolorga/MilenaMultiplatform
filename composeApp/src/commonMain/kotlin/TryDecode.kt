import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import io.ktor.util.decodeBase64Bytes
import io.ktor.util.decodeBase64String
import io.ktor.util.encodeBase64
import okio.ByteString.Companion.decodeBase64

@Composable
fun tryDecode() {
    var promos by remember { mutableStateOf<List<Promotion>?>(emptyList()) }
    var img by remember { mutableStateOf<ByteArray?>(null) }
    var byte by remember { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(true) {
        promos = fetchPromos()
        try {
            val bs64 = promos?.get(0)?.image

            if (!bs64.isNullOrEmpty()) {
                println("Base64 String: $bs64")
                img = fixBase64Padding(bs64).decodeBase64String().decodeBase64Bytes()

                if (img != null) {
                    println("Decoded ByteArray size: ${img!!.size}")
                    byte = img!!.toImageBitmap()
                } else {
                    println("Ошибка: декодированный байт-массив равен null")
                }
            } else {
                println("Ошибка: Base64 строка пуста или null")
            }
        } catch (e: Exception) {
            println("Ошибка при декодировании: $e")
        }
    }

    if (byte != null) {
        Image(
            bitmap = byte!!,
            contentDescription = null,
            modifier = Modifier.width(150.dp).height(150.dp),
            contentScale = ContentScale.Crop
        )
    } else {
        Text("Изображение не загружено", modifier = Modifier.padding(16.dp))
    }
}