import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.compose.SubcomposeAsyncImage
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import milenamultiplatformtry.composeapp.generated.resources.Res
import milenamultiplatformtry.composeapp.generated.resources.logo
import org.jetbrains.compose.resources.painterResource

@Composable
fun ProductUnit(
    product: Product,
    onProduct:(Product)->Unit,
    onAddToBucket:(product:Product, amount:Int)->Unit
){
    var amo = product.number.split(' ')[0].toInt()

    var amount by remember { mutableStateOf<Int>(amo) }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(200.dp).border(1.dp, Color.LightGray)
            .clickable {
                onProduct(product)
            }
            .height(525.dp)
    ) {
        Spacer(modifier = Modifier.height(15.dp))
        SubcomposeAsyncImage(
            modifier = Modifier.size(200.dp),
            model = product.images[0],
            loading = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Transparent)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(40.dp),
                        color = Color(109, 80, 255),
                        strokeWidth = 4.dp
                    )
                }
            },
            contentDescription = "sub",
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.height(5.dp))
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(15.dp)){
            Text(
                text = cropDesc(product.name),
                fontSize = 15.sp,
                color = Color.Black,
                //fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                minLines = 3,
                maxLines = 3)
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Арт: "+product.article,
                fontSize = 15.sp,
                color = Color.Gray,
                //fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Мелкий опт",
                fontSize = 15.sp,
                color = Color.Gray,
                //fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = product.priceLow+" Р/шт",
                fontSize = 15.sp,
                color = Color.Black,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Крупный опт",
                fontSize = 15.sp,
                color = Color.Gray,
                //fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = product.priceHigh+" Р/шт",
                fontSize = 15.sp,
                color = Color.Black,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(10.dp))

        }
        Row(
            modifier = Modifier.fillMaxWidth().height(40.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ){
            Icon(
                Icons.Default.Close,
                contentDescription = null,
                modifier = Modifier.size(25.dp).clickable {
                    if(amount>amo){
                        amount-=amo
                    } },
                tint = Color(109, 80, 255)
            )
            Spacer(modifier = Modifier.width(15.dp))
            Text(
                text = amount.toString(),
                fontSize = 13.sp,
                color = Color.Black,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.width(15.dp))
            Icon(
                Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(25.dp).clickable { amount+=amo },
                tint = Color(109, 80, 255)
            )
        }
        Spacer(modifier = Modifier.height(5.dp))
        Button(
            onClick = {
                onAddToBucket(product, amount)


            },
            modifier = Modifier.fillMaxWidth().height(40.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(109, 80, 255))
        ){
            Text(
                text = "В КОРЗИНУ",
                modifier = Modifier.background(Color.Transparent),
                fontSize = 12.sp,
                color = Color.White
            )
        }

    }
}

fun cropDesc(
    text:String
):String{
    return if (text.length > 41) {
        text.substring(0, 41) + "..."
    } else {
        text
    }
}

