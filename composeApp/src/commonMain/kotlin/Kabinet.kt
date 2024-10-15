import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun personalKab(
    userPreferences:UserPreferences,
    onPersonalData:()->Unit,
    onHistory:()->Unit,
    onAdminPanel:()->Unit
){
    var userData by remember { mutableStateOf<User?>(null) }
    LaunchedEffect(true){
        userData = userPreferences.getUserLogin()?.let { getUser(it) }
        println(userData)
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(15.dp).fillMaxWidth().verticalScroll(rememberScrollState()).requiredHeight(10000.dp)) {
        Text(
            text = "Личный кабинет",
            modifier = Modifier.fillMaxWidth().padding(15.dp),
            fontSize = 24.sp,
            color = Color.Black,
            //fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
        Row(horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth().height(200.dp)){
            Box(contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxHeight()
                    .border(2.dp,shape = RoundedCornerShape(10.dp), color = Color.Gray)
                    .fillMaxWidth(0.48f)
                    .clickable {
                        onPersonalData()
                    })
            {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = Color(109, 80, 255)
                    )
                    Text(
                        text = "Личные данные",
                        modifier = Modifier.fillMaxWidth(0.7f).padding(15.dp),
                        fontSize = 18.sp,
                        color = Color.Black,
                        minLines = 2,
                        //fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                }

            }
            Spacer(modifier = Modifier.width(15.dp))
            Box(contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxHeight()
                    .border(2.dp,shape = RoundedCornerShape(10.dp), color = Color.Gray)
                    .fillMaxWidth()
                    .clickable {
                        onHistory()
                    })
            {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ){

                    Icon(
                        Icons.Default.List,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = Color(109, 80, 255)
                    )
                    Text(
                        text = "История заказов",
                        modifier = Modifier.fillMaxWidth(0.7f).padding(15.dp),
                        fontSize = 18.sp,
                        color = Color.Black,
                        minLines = 2,
                        //fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                }

            }
        }
        Spacer(modifier = Modifier.height(15.dp))
        userData?.let{
            if (it.is_admin){
                Row(horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth().height(200.dp)){
                    Box(contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxHeight()
                            .border(2.dp,shape = RoundedCornerShape(10.dp), color = Color.Gray)
                            .fillMaxWidth(0.48f)
                            .clickable {
                                onAdminPanel()
                            })
                    {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ){

                            Icon(
                                Icons.Default.Settings,
                                contentDescription = null,
                                modifier = Modifier.size(80.dp),
                                tint = Color(109, 80, 255)
                            )
                            Text(
                                text = "Панель админа",
                                modifier = Modifier.fillMaxWidth(0.7f).padding(15.dp),
                                fontSize = 18.sp,
                                color = Color.Black,
                                minLines = 2,
                                //fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center
                            )
                        }

                    }
                }
            }
        }


    }
}