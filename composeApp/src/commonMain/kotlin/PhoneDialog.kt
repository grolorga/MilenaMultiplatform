import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetState
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.setSingletonImageLoaderFactory

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PhoneDialog(){
    var sheetState =  rememberBottomSheetScaffoldState(
        bottomSheetState = BottomSheetState(
            initialValue = BottomSheetValue.Collapsed
        )
    )
    val screenHeightPx = getScreenSize().heightPx
    val screenWidthPx = getScreenSize().widthPx
    val ind = screenHeightPx/100 + screenWidthPx/100


    BottomSheetScaffold(

        scaffoldState = sheetState,
        sheetPeekHeight = (screenHeightPx*0.1).dp,//(ind * 8.5).dp,//высота экрана умноженная на 0.1 это треть экрана
        sheetContent = {
            Column(modifier = Modifier.height(30.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally){
                Spacer(modifier = Modifier.fillMaxWidth().height(5.dp).background(Color.Transparent))
                Spacer(modifier = Modifier.fillMaxWidth().height(5.dp).background(Color.Transparent))
                Spacer(modifier = Modifier.width(70.dp).height(3.dp).background(Color.Black))
                Spacer(modifier = Modifier.fillMaxWidth().height(3.dp).background(Color.Transparent))
            }
            Text(
                "Телефоны",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .padding(start = 15.dp),
                fontWeight = FontWeight.SemiBold,
                fontSize = 23.sp
            )
            Spacer(modifier = Modifier.fillMaxWidth().height(2.dp).background(Color.LightGray))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colors.background
            ) {
                PhoneNumberHandler(
                    phoneNumber = "8 800 777 35 69",
                    onCopy = {
                        // Обработка события копирования
                    },
                    onDial = {
                        // Обработка события звонка
                    }
                )
            }
            Spacer(modifier = Modifier.fillMaxWidth().height(2.dp).background(Color.LightGray))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colors.background
            ) {
                PhoneNumberHandler(
                    phoneNumber = "+7 960 383 29 31",
                    onCopy = {
                        // Обработка события копирования
                    },
                    onDial = {
                        // Обработка события звонка
                    }
                )
            }
        }
    ){
        var selectedPromo by remember { mutableStateOf<Promotion?>(null) }
        PromosPager(
            onPromoClick = {promo->
                selectedPromo = promo
            }
        )
        selectedPromo?.let { PromotionDetails(it, onDismis = {selectedPromo=null}) }
    }
}