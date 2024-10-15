import androidx.compose.foundation.background
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
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun PromotionDetails(
    promotion: Promotion,
    onDismis:()->Unit
){
    Box(modifier = Modifier.fillMaxSize().background(Color(.2f, .2f,.2f, 0.9f )).clickable {
        onDismis()
    }){
        Column(
            modifier = Modifier.fillMaxHeight(0.6f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = promotion.name,
                modifier = Modifier.fillMaxWidth().padding(15.dp),
                fontSize = 24.sp,
                color = Color.White,
                //fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(30.dp))
            Text(
                text = promotion.start+" - "+promotion.end,
                modifier = Modifier.fillMaxWidth().padding(15.dp),
                fontSize = 20.sp,
                color = Color.White,
                //fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(30.dp))
            Text(
                text = promotion.description,
                modifier = Modifier.fillMaxWidth().padding(15.dp),
                fontSize = 24.sp,
                color = Color.White,
                //fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(30.dp))
        }

    }

}