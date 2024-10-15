import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.Image
import coil3.ImageLoader
import coil3.annotation.ExperimentalCoilApi
import milenamultiplatformtry.composeapp.generated.resources.Res
import milenamultiplatformtry.composeapp.generated.resources.logo
import milenamultiplatformtry.composeapp.generated.resources.newcatalog
import org.jetbrains.compose.resources.Resource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.vectorResource

@OptIn(ExperimentalCoilApi::class)
@Composable
fun TopMenu(navController: NavController) {
    val currentScreen = remember(navController) {
        mutableStateOf("screen1") // Инициализируем текущий экран
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Color.White),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        //val img = Image(painter = painterResource(Res.drawable.logo),"", modifier = Modifier.size(66.dp).clickable {})
        BottomNavigationItem(
            //altico = painterResource(Res.drawable.newcatalog),
            icon = Icons.Default.Home,
            isSelected = currentScreen.value == "screen1"
        ){
            currentScreen.value = "screen1"
            navController.navigate("screen1")
        }
        //Spacer(modifier = Modifier.width(56.dp))
        BottomNavigationItem(
            icon = Icons.Default.Phone,
            isSelected = currentScreen.value == "screen2"
        ) {
            currentScreen.value = "screen2"
            navController.navigate("screen2")
        }

        BottomNavigationItem(
            icon = vectorResource(Res.drawable.newcatalog),//Icons.Default.Search,
            isSelected = currentScreen.value == "screen3"
        ) {
            currentScreen.value = "screen3"
            navController.navigate("screen3")
        }

        BottomNavigationItem(
            icon = Icons.Default.Person,
            isSelected = currentScreen.value == "screen4"
        ) {
            currentScreen.value = "screen4"
            navController.navigate("screen4")
        }

        BottomNavigationItem(
            icon = Icons.Default.ShoppingCart,
            isSelected = currentScreen.value == "screen5"
        ) {
            currentScreen.value = "screen5"
            navController.navigate("screen5")
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
private fun BottomNavigationItem(
    altico: Painter? = null,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,

        ) {
        if(altico == null){
            Icon(
                icon,
                contentDescription = null,
                tint = if (isSelected) {
                    Color.LightGray // Изменение цвета иконки для активной кнопки
                } else {
                    Color.Black
                },
                modifier = Modifier.size(30.dp)
            )
        }
        else{
            Image(painter = altico, "", modifier = Modifier.size(56.dp))
        }

    }
}