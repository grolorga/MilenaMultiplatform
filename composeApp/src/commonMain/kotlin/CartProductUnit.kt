import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage

@Composable
fun CartProductUnit(
    product: Product,
    amount: Int,
    type: String, // Новый параметр для определения типа цен
    onDeleteProduct: (product: Product) -> Unit,
    onLowerAmount: (amo: Int, product: Product) -> Unit,
    onHigherAmount: (amo: Int, product: Product) -> Unit
) {
    val amo = product.number.toInt()
    val displayPrice = if (type == "Крупный опт") product.priceHigh.toInt() else product.priceLow.toInt()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    )
    {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = product.images[0],
                "",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(100.dp)
                    .width(75.dp)
            )
            Text(
                text = product.description,
                fontSize = 15.sp,
                color = Color.Black,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth(0.5f).height(100.dp)
            )
            Column(
                modifier = Modifier
                    .width(40.dp)
                    .height(100.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Top
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = null,
                    modifier = Modifier
                        .size(25.dp)
                        .clickable {
                            onDeleteProduct(product)
                        },
                    tint = Color.Gray
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .width(100.dp)
                    .height(30.dp)
                    .border(1.dp, Color.Gray),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = null,
                    modifier = Modifier
                        .size(25.dp)
                        .clickable {
                            if (amount > amo) {
                                onLowerAmount(amo, product)
                            }
                        },
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
                    modifier = Modifier
                        .size(25.dp)
                        .clickable {
                            onHigherAmount(amo, product)
                        },
                    tint = Color(109, 80, 255)
                )
            }
            // Используем displayPrice для отображения цены в зависимости от типа цен
            Text(
                text = (displayPrice * amount).toString()+" Р",
                fontSize = 13.sp,
                color = Color.Black,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )

        }
        Spacer(modifier = Modifier.fillMaxWidth(0.5f).height(10.dp))
        Row(modifier = Modifier.fillMaxWidth().height(50.dp)){
            Text(
                text = displayPrice.toString()+" Р/шт",
                fontSize = 11.sp,
                color = Color.Black,
                fontWeight = FontWeight.Thin,
                textAlign = TextAlign.Left
            )
            Spacer(modifier = Modifier.width(15.dp))
            Text(
                text = "Арт: "+product.article,
                fontSize = 11.sp,
                color = Color.Black,
                fontWeight = FontWeight.Thin,
                textAlign = TextAlign.Left,
                modifier = Modifier.fillMaxWidth()
            )
        }


        Spacer(modifier = Modifier.fillMaxWidth(0.5f).height(10.dp))
        Spacer(modifier = Modifier.fillMaxWidth(0.9f).height(1.dp).background(Color.Gray))
        Spacer(modifier = Modifier.fillMaxWidth(0.5f).height(10.dp))
    }
}
