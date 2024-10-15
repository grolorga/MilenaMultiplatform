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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.Month

@Composable
fun AdminPanel(
    onAddPromo:()->Unit,
    onDeletePromo:()->Unit,
    onAddProduct:()->Unit,
    onCheckOrders:()->Unit,
    onDismiss:()->Unit
){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(15.dp).verticalScroll(rememberScrollState())
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
        Text(
            text = "Панель администратора",
            modifier = Modifier.fillMaxWidth().padding(15.dp),
            fontSize = 24.sp,
            color = Color.Black,
            //fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(15.dp))
        Text(
            text = "Здесь можно модерировать контент мобильного приложения.",
            modifier = Modifier.fillMaxWidth().padding(15.dp),
            fontSize = 16.sp,
            color = Color.Black,
            //fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(35.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.height(100.dp)
                .border(2.dp, Color(109, 80, 255), RoundedCornerShape(15.dp))
                .clickable {
                    onAddPromo()
                }
                .padding(15.dp)
                .fillMaxWidth()
        ){
            Icon(
                Icons.Default.Check,
                "",
                modifier = Modifier.size(50.dp),
                tint = Color(109, 80, 255)
            )
            Spacer(modifier = Modifier.width(15.dp))
            Text(
                text = "Добавить промоакцию",
                fontSize = 24.sp,
                color = Color.Black,
                //fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(35.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.height(100.dp)
                .border(2.dp, Color(109, 80, 255), RoundedCornerShape(15.dp))
                .clickable {
                    onDeletePromo()
                }
                .padding(15.dp)
                .fillMaxWidth()
        ){
            Icon(
                Icons.Default.DateRange,
                "",
                modifier = Modifier.size(50.dp),
                tint = Color(109, 80, 255)
            )
            Spacer(modifier = Modifier.width(15.dp))
            Text(
                text = "Удалить промоакцию",
                fontSize = 24.sp,
                color = Color.Black,
                //fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        }
        Spacer(modifier = Modifier.height(35.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.height(100.dp)
                .border(2.dp, Color(109, 80, 255), RoundedCornerShape(15.dp))
                .clickable {
                    onAddProduct()
                }
                .padding(15.dp)
                .fillMaxWidth()
        ){
            Icon(
                Icons.Default.Add,
                "",
                modifier = Modifier.size(50.dp),
                tint = Color(109, 80, 255)
            )
            Spacer(modifier = Modifier.width(15.dp))
            Text(
                text = "Добавить товар",
                fontSize = 24.sp,
                color = Color.Black,
                //fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        }
        Spacer(modifier = Modifier.height(35.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.height(100.dp)
                .border(2.dp, Color(109, 80, 255), RoundedCornerShape(15.dp))
                .clickable {
                    onCheckOrders()
                }
                .padding(15.dp)
                .fillMaxWidth()
        ){
            Icon(
                Icons.Default.Favorite,
                "",
                modifier = Modifier.size(50.dp),
                tint = Color(109, 80, 255)
            )
            Spacer(modifier = Modifier.width(15.dp))
            Text(
                text = "Заявки",
                fontSize = 24.sp,
                color = Color.Black,
                //fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        }
    }
}