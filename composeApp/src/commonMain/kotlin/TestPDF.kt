import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PdfGenerationScreen() {
    // Переменные для отображения статуса генерации
    var filePath by remember { mutableStateOf<String?>(null) }
    var error by remember { mutableStateOf<Throwable?>(null) }
    var isGenerating by remember { mutableStateOf(false) }

    // Контент, который мы будем рендерить в PDF
    val composableContent: @Composable () -> Unit = {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(text = "Это тестовый контент для PDF", fontSize = 20.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Еще один элемент", fontSize = 16.sp)
        }
    }

    // Вызов функции генерации PDF
    fun generatePdf() {
        isGenerating = true
        generatePdfFromComposable("testDocument", composableContent) { path, genError ->
            filePath = path
            error = genError
            isGenerating = false
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        if (isGenerating) {
            CircularProgressIndicator() // Показать индикатор загрузки пока идет генерация PDF
        } else {
            Button(onClick = { generatePdf() }) {
                Text(text = "Сгенерировать PDF")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Отображение результата
            Column {
                if (filePath != null) {
                    Text(text = "PDF успешно сохранен по пути:", fontSize = 18.sp)
                    TextField(
                        value = filePath!!,
                        onValueChange = {},  // Пустая функция, так как поле только для чтения
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                } else if (error != null) {
                    Text(text = "Ошибка:", fontSize = 18.sp)
                    TextField(
                        value = error.toString(),
                        onValueChange = {},  // Пустая функция, так как поле только для чтения
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                }
            }

        }
    }
}
