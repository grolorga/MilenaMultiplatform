import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.SubcomposeAsyncImage
import milenamultiplatformtry.composeapp.generated.resources.Res
import milenamultiplatformtry.composeapp.generated.resources.logo
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProductDetails(
    product: Product,
    onDismiss: () -> Unit,
    onAddToBucket:(product:Product, amount:Int)->Unit
) {
    val screenHeightPx = getScreenSize().heightPx
    val screenWidthPx = getScreenSize().widthPx
    var amo = product.number.split(' ')[0].toInt()
    var amount by remember { mutableStateOf<Int>(amo) }

    var listOfImg = product.images

    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f
    ) {
        listOfImg.size
    }

    val indicatorScrollState = rememberLazyListState()

    LaunchedEffect(key1 = pagerState.currentPage) {
        val currentPage = pagerState.currentPage
        val size = indicatorScrollState.layoutInfo.visibleItemsInfo.size
        val lastVisibleIndex = indicatorScrollState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
        val firstVisibleItemIndex = indicatorScrollState.firstVisibleItemIndex
        val maxi = if (currentPage - 1 > 0) currentPage - 1 else 0
        if (currentPage > lastVisibleIndex - 1) {
            indicatorScrollState.animateScrollToItem(currentPage - size + 2)
        } else if (currentPage <= firstVisibleItemIndex + 1) {
            indicatorScrollState.animateScrollToItem(maxi)
        }
    }
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier.fillMaxSize().background(Color.White).verticalScroll(scrollState)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height((screenHeightPx * 0.25).dp)
                .background(Color.Transparent),
            contentAlignment = Alignment.BottomCenter
        ) {
            HorizontalPager(state = pagerState) { mainIndex ->
                SubcomposeAsyncImage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height((screenHeightPx * 0.25).dp),
                    model = listOfImg[mainIndex],
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
                    contentDescription = "",
                    contentScale = ContentScale.Crop
                )
            }

            LazyRow(
                state = indicatorScrollState,
                modifier = Modifier
                    .height(50.dp)
                    .width(((6 + 16) * 2 + 3 * (10 + 16)).dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(listOfImg.size) { iteration ->
                    val color = if (pagerState.currentPage == iteration) Color.DarkGray else Color.LightGray
                    item(key = "item$iteration") {
                        val currentPage = pagerState.currentPage
                        val firstVisibleIndex by remember { derivedStateOf { indicatorScrollState.firstVisibleItemIndex } }
                        val lastVisibleIndex = indicatorScrollState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                        val size by animateDpAsState(
                            targetValue = if (iteration == currentPage) 10.dp else 6.dp
                        )
                        Box(
                            modifier = Modifier
                                .padding(8.dp)
                                .background(color, CircleShape)
                                .size(size)
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.TopStart
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "",
                    modifier = Modifier
                        .size(40.dp)
                        .clickable { onDismiss() },
                    tint = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = product.description,
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            fontSize = 22.sp,
            color = Color.Black,
            textAlign = TextAlign.Left
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = "Арт: "+product.article,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal =  15.dp),
            fontSize = 15.sp,
            color = Color.Gray,
            textAlign = TextAlign.Left
        )
        Spacer(modifier = Modifier.fillMaxWidth().height(10.dp))
        Spacer(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color.Gray))
        Spacer(modifier = Modifier.fillMaxWidth().height(5.dp))
        Text(
            text = "Мелкий опт (от 10 до 33 тыс.Р)",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal =  15.dp),
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Gray,
            textAlign = TextAlign.Left
        )
        Spacer(modifier = Modifier.fillMaxWidth().height(5.dp))
        Text(
            text = product.priceLow+" Р/шт",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal =  15.dp),
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black,
            textAlign = TextAlign.Left
        )
        Spacer(modifier = Modifier.fillMaxWidth().height(5.dp))
        Text(
            text = "Крупный опт (от 33 тыс.Р)",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp),
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Gray,
            textAlign = TextAlign.Left
        )
        Spacer(modifier = Modifier.fillMaxWidth().height(5.dp))
        Text(
            text = product.priceHigh+" Р/шт",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal =  15.dp),
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black,
            textAlign = TextAlign.Left
        )
        Spacer(modifier = Modifier.fillMaxWidth().height(10.dp))
        Spacer(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color.Gray))
        Spacer(modifier = Modifier.fillMaxWidth().height(5.dp))


        Text(
            text = "Характеристики",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal =  15.dp),
            fontSize = 17.sp,
            color = Color.Black,
            textAlign = TextAlign.Left
        )
        if (product.selectedOption != ""){
            Spacer(modifier = Modifier.fillMaxWidth().height(5.dp))
            Text(
                text = "Категория - "+product.selectedOption,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal =  15.dp),
                fontSize = 17.sp,
                color = Color.Gray,
                textAlign = TextAlign.Left
            )
        }
        if (product.selectedSizes.joinToString(", ") != ""){
            Spacer(modifier = Modifier.fillMaxWidth().height(5.dp))
            Text(
                text = "Размеры - "+product.selectedSizes.joinToString(", "),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal =  15.dp),
                fontSize = 17.sp,
                color = Color.Gray,
                textAlign = TextAlign.Left
            )
        }
        if (product.selectedColors.joinToString(", ") != ""){
            Spacer(modifier = Modifier.fillMaxWidth().height(5.dp))
            Text(
                text = "Цвета - "+product.selectedColors.joinToString(", "),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal =  15.dp),
                fontSize = 17.sp,
                color = Color.Gray,
                textAlign = TextAlign.Left
            )
        }
        if (product.selectedMaterials.joinToString(", ") != ""){
            Spacer(modifier = Modifier.fillMaxWidth().height(5.dp))
            Text(
                text = "Материалы - "+product.selectedMaterials.joinToString(", "),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal =  15.dp),
                fontSize = 17.sp,
                color = Color.Gray,
                textAlign = TextAlign.Left
            )
        }
        if (product.number !=""){
            Spacer(modifier = Modifier.fillMaxWidth().height(5.dp))
            Text(
                text = "Количество в упаковке - "+product.number,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal =  15.dp),
                fontSize = 17.sp,
                color = Color.Gray,
                textAlign = TextAlign.Left
            )
        }

        if (product.country !=""){
            Spacer(modifier = Modifier.fillMaxWidth().height(5.dp))
            Text(
                text = "Страна - "+product.country,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal =  15.dp),
                fontSize = 17.sp,
                color = Color.Gray,
                textAlign = TextAlign.Left
            )
        }
        Spacer(modifier = Modifier.fillMaxWidth().height(5.dp))
        Row(
            modifier = Modifier.fillMaxWidth().border(2.dp, Color.LightGray),
            horizontalArrangement = Arrangement.Center
        ){
            Row(
                modifier = Modifier.fillMaxWidth(0.5f).height(60.dp).padding(horizontal = 15.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
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

                Text(
                    text = amount.toString(),
                    fontSize = 15.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
                Icon(
                    Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(25.dp).clickable { amount+=amo },
                    tint = Color(109, 80, 255)
                )
            }

            Button(
                onClick = {
                    onAddToBucket(product, amount)
                },
                modifier = Modifier.fillMaxWidth().height(60.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(109, 80, 255))
            ){
                Text(
                    text = "В КОРЗИНУ",
                    modifier = Modifier.background(Color.Transparent),
                    color = Color.White,
                    fontSize = 12.sp
                )
            }
        }
    }
}
