import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.key.Key.Companion.R
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.compose.SubcomposeAsyncImage
import io.ktor.util.decodeBase64Bytes
import io.ktor.util.decodeBase64String
import io.ktor.utils.io.core.toByteArray
import kotlinx.coroutines.coroutineScope
import milenamultiplatformtry.composeapp.generated.resources.Res
import milenamultiplatformtry.composeapp.generated.resources.logo
import milenamultiplatformtry.composeapp.generated.resources.logot
import okio.ByteString
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.vectorResource
import kotlin.io.encoding.Base64

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PromosPager(
    onPromoClick:(Promotion)->Unit
){
    var promos by remember { mutableStateOf<List<Promotion>?>(emptyList()) }
    LaunchedEffect(true){
        promos=fetchPromos()
    }
    /*var listOfImg = listOf("https://doomwiki.org/w/images/5/54/PUSS_XX_Titlepic.png",
                "https://i.pinimg.com/550x/5f/33/ee/5f33ee4eaa5d94d32dd1bc2517899c62.jpg",
                "https://i.pinimg.com/236x/a9/ec/da/a9ecda87f09e96b6ec28af112b0d222c.jpg")*/
    val screenHeightPx = getScreenSize().heightPx
    val screenWidthPx = getScreenSize().widthPx
    val ind = screenHeightPx/100 + screenWidthPx/100
    var isLoading by remember { mutableStateOf(true) }

    var listOfImg by remember { mutableStateOf<List<String?>>(emptyList()) }
    if(promos?.isNotEmpty() == true){
        promos!!.forEach { promo->//Promos это должны быть полученные из базы акции
            listOfImg += promo.image
        }
        val pagerState = rememberPagerState(
            initialPage = 0,
            initialPageOffsetFraction = 0f){
            promos!!.size//Сколько будет точек
        }


        val indicatorScrollState = rememberLazyListState()

        LaunchedEffect(key1 = pagerState.currentPage, block = {
            val currentPage = pagerState.currentPage
            val size = indicatorScrollState.layoutInfo.visibleItemsInfo.size
            val lastVisibleIndex =
                indicatorScrollState.layoutInfo.visibleItemsInfo.last().index
            val firstVisibleItemIndex = indicatorScrollState.firstVisibleItemIndex
            var maxi = if (currentPage - 1 > 0) currentPage-1 else 0
            if (currentPage > lastVisibleIndex - 1) {
                indicatorScrollState.animateScrollToItem(currentPage - size + 2)
            } else if (currentPage <= firstVisibleItemIndex + 1) {
                indicatorScrollState.animateScrollToItem(maxi)
            }
        })

        Box(modifier = Modifier
            .fillMaxWidth().background(Color.Transparent).height(500.dp),//((screenHeightPx*0.26).dp),
            contentAlignment = Alignment.BottomCenter){
            HorizontalPager(state = pagerState){mainIndex->



                SubcomposeAsyncImage(
                    model = listOfImg[mainIndex],
                    contentDescription = null,
                    modifier = Modifier
                        .width(screenWidthPx.dp)
                        .height((screenHeightPx * 0.27).dp)
                        .clickable {
                            onPromoClick(promos!![pagerState.currentPage])
                        },
                    loading = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Transparent)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .size(50.dp),
                                color = Color(109, 80, 255),
                                strokeWidth = 4.dp
                            )
                        }
                    },
                    error = {
                        // Простой текст или иконка для обработки ошибок
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Transparent),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Ошибка загрузки", color = Color.Red)
                        }
                    },
                    contentScale = ContentScale.Crop,
                    onSuccess = {
                        isLoading = false
                    }
                )








            }

            Column(
                modifier = Modifier.height(110.dp).fillMaxWidth(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ){
                Row(modifier = Modifier.fillMaxWidth().padding(start = 15.dp)){
                    Icon(
                        Icons.Default.Check,
                        "",
                        modifier = Modifier.size(25.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = promos!![pagerState.currentPage].start+" - "+ promos!![pagerState.currentPage].end,
                        modifier = Modifier.fillMaxWidth().padding(start = 5.dp),
                        fontSize = 19.sp,
                        color = Color.White,

                        textAlign = TextAlign.Start)
                }

                Text(
                    text = promos!![pagerState.currentPage].name,
                    modifier = Modifier.fillMaxWidth().padding(15.dp),
                    fontSize = 23.sp,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Start)
            }

            LazyRow(
                state = indicatorScrollState,
                modifier = Modifier
                    .height(50.dp)
                    .width(((6 + 16) * 2 + 3 * (10 + 16)).dp)
                //.background(Color.Cyan)
                ,
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(promos!!.size) { iteration ->//Здесь тоже сколько точек должно быть
                    val color = if (pagerState.currentPage == iteration) Color.DarkGray else Color.LightGray
                    item(key = "item$iteration") {
                        val currentPage = pagerState.currentPage
                        val firstVisibleIndex by remember { derivedStateOf { indicatorScrollState.firstVisibleItemIndex } }
                        val lastVisibleIndex = indicatorScrollState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                        val size by animateDpAsState(
                            targetValue = if (iteration == currentPage) {
                                10.dp
                            } else if (iteration in firstVisibleIndex + 1..lastVisibleIndex - 1) {
                                10.dp
                            } else {
                                6.dp
                            }
                        )
                        Box(
                            modifier = Modifier
                                .padding(8.dp)
                                .background(color, CircleShape)
                                .size(
                                    size
                                )
                        )
                    }
                }
            }
            //Spacer(modifier = Modifier.fillMaxWidth().height(40.dp).background(Color.Yellow).shadow(10.dp))

        }
    }
    else{
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(50.dp),
                color = Color(109, 80, 255),
                strokeWidth = 4.dp
            )
        }
    }

}

fun fixBase64Padding(encodedString: String): String {
    val missingPadding = (4 - (encodedString.length % 4)) % 4
    return encodedString + "=".repeat(missingPadding)
}

fun decodeBase64String(encodedString: String): ByteArray? {
    return try {
        fixBase64Padding(encodedString).decodeBase64Bytes()
    } catch (e: IllegalArgumentException) {
        println("Failed to decode Base64 string. Error: ${e.message}")
        null
    }
}

