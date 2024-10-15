import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import io.ktor.client.request.delete
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import network.chaintech.kmp_date_time_picker.utils.dateTimeToString

@Composable
fun DeletePromotion(
    onDeletedPromo:()->Unit,
    onDismiss: () -> Unit
){
    val currentMoment = Clock.System.now()
    val currentDateTime = currentMoment.toLocalDateTime(TimeZone.currentSystemDefault())
    val year = currentDateTime.year
    val month = currentDateTime.month.number
    val day = currentDateTime.dayOfMonth
    println("Сейчас День: $day Месяц: $month Год: $year")
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(15.dp)
    ){
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
        var promos by remember { mutableStateOf<List<Promotion>?>(emptyList()) }
        LaunchedEffect(true){
            promos=fetchPromos()
        }
        var showDialog by remember { mutableStateOf(false) }
        var selectedPromo by remember { mutableStateOf<Promotion?>(null) }
        Text(
            text = "Удаление промоакций",
            modifier = Modifier.fillMaxWidth().padding(15.dp),
            fontSize = 24.sp,
            color = Color.Black,
            //fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(15.dp))
        Text(
            text = "Здесь можно удалять старые промоакции. Красным отмечены устаревшие промоакции.",
            modifier = Modifier.fillMaxWidth().padding(15.dp),
            fontSize = 16.sp,
            color = Color.Black,
            //fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(35.dp))



        LazyColumn(){
            promos?.let {
                items(it.size){ promotion->
                    Spacer(modifier = Modifier.height(35.dp))
                    val date = promos!![promotion].end
                    val (y, m, d) = extractDateComponents(date)
                    println("День: $d Месяц: $m Год: $y")
                    var outOfDate by remember { mutableStateOf(false) }
                    // Если год окончания меньше текущего года
                    if (y < year) {
                        outOfDate = true
                    }
// Если год окончания совпадает с текущим, проверяем месяц
                    else {
                        if (m < month) {
                            outOfDate = true
                        }
                        // Если месяц окончания совпадает с текущим, проверяем день
                        else if (m == month) {
                            if (d < day) {
                                outOfDate = true
                            }
                        }
                    }
                    println("Промоакция устарела: $outOfDate")
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.height(100.dp)
                            .border(2.dp, if (!outOfDate) Color(109, 80, 255) else Color.Red, RoundedCornerShape(15.dp))
                            .clickable {
                                selectedPromo = promos!![promotion]
                                showDialog=true
                            }
                            .padding(15.dp)
                            .fillMaxWidth()
                    ){
                        Text(
                            text = promos!![promotion].name,
                            modifier = Modifier.fillMaxWidth(0.5f),
                            fontSize = 20.sp,
                            color = Color.Black,
                            //fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.width(15.dp))
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ){
                            Text(
                                text = "нач "+promos!![promotion].start,
                                fontSize = 18.sp,
                                color = Color.Black,
                                //fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "кон "+promos!![promotion].end,
                                fontSize = 18.sp,
                                color = Color.Black,
                                //fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                }
            }
        }
        val coroutineScope = rememberCoroutineScope()
        if(showDialog){
            selectedPromo?.let {
                DeletePromotionDialog(
                    promotionName = it.name,
                    onConfirm = {
                        // Perform deletion action
                        showDialog = false
                        coroutineScope.launch {
                            if(deletePromotion(selectedPromo!!)=="Success")
                            {
                                onDeletedPromo()
                            }

                        }


                    },
                    onDismiss = {
                        showDialog = false
                    }
                )
            }
        }


    }
}


@Composable
fun DeletePromotionDialog(
    promotionName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Удаление промоакции")
        },
        text = {
            Text(text = "Вы уверены что хотите удалить промоакцию: \"$promotionName\"?")
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm
            ) {
                Text("Удалить")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Отмена")
            }
        },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    )
}

suspend fun deletePromotion(
    promotion: Promotion
): String {
    val client = createHttpClient()
    try {
        val url = "https://rst-for-milenaopt.ru/delete_promotion"
        val data = mapOf(
            "name" to promotion.name,
            )

        val jsonData = Json.encodeToString(data)

        return try {
            val response: HttpResponse = client.delete(url) {
                contentType(ContentType.Application.Json)
                setBody(jsonData)  // Передача JSON-строки в запрос
            }

            val responseBody = response.bodyAsText()
            if (response.status.value == 200) {
                println("Success")
                "Success"
            } else {
                println("Ошибка при удалении ${responseBody}")
                "Ошибка при удалении"
            }
        } catch (e: Exception) {
            println("Ошибка: $e")
            "Ошибка: $e"
        }
    } catch (e: Exception) {
        println("Ошибка: $e")
        return "Ошибка: $e"
    }
}

fun extractDateComponents(dateString: String): Triple<Int, Int, Int> {
    // Разделяем строку по символу "-"
    val parts = dateString.split("-")

    // Преобразуем части в целые числа
    val year = parts[0].toInt()
    val month = parts[1].toInt()
    val day = parts[2].toInt()

    // Возвращаем значения в виде Triple (год, месяц, день)
    return Triple(year, month, day)
}