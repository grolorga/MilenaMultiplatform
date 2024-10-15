import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OrderedUnit(
    order:Order,
    onSelectedOrder:(order:Order)->Unit
){
    Row(modifier = Modifier.fillMaxWidth().height(100.dp).clickable {
        onSelectedOrder(order)
    },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center){
        Text(
            text = "Заказ от: "+order.date,
            fontSize = 15.sp,
            color = androidx.compose.ui.graphics.Color.Gray,
            //fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
    }
}