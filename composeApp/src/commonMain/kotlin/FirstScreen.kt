import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetScaffoldDefaults
import androidx.compose.material.BottomSheetState
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.AsyncImage
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.setSingletonImageLoaderFactory
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import coil3.request.CachePolicy
import coil3.request.crossfade
import coil3.util.DebugLogger
import milenamultiplatformtry.composeapp.generated.resources.Res
import milenamultiplatformtry.composeapp.generated.resources.bibiz
import milenamultiplatformtry.composeapp.generated.resources.logo
import okio.FileSystem
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class,
    ExperimentalComposeUiApi::class, ExperimentalCoilApi::class
)
@Composable
fun First(
    onCategory:(Category)->Unit
){
    var sheetState =  rememberBottomSheetScaffoldState(
        bottomSheetState = BottomSheetState(
            initialValue = BottomSheetValue.Collapsed
        )
    )
    val screenHeightPx = getScreenSize().heightPx
    val screenWidthPx = getScreenSize().widthPx
    val ind = screenHeightPx/100 + screenWidthPx/100


    MaterialTheme {
        setSingletonImageLoaderFactory { context ->
            getAsyncImageLoader(context)
        }
        println("screenHeightPx*0.1 in dp = ${(screenHeightPx*0.1).dp}")
        println("screenHeightPx*0.1 in dp = ${(screenHeightPx*0.1)}")

        BottomSheetScaffold(

            scaffoldState = sheetState,
            sheetPeekHeight = 200.dp,//(screenHeightPx*0.1).dp,//(ind * 8.5).dp,//высота экрана умноженная на 0.1 это треть экрана
            sheetElevation = 0.dp,
            sheetContent = {
                Column(modifier = Modifier.height(30.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally){
                    Spacer(modifier = Modifier.fillMaxWidth().height(5.dp).background(Color.Transparent))
                    Spacer(modifier = Modifier.fillMaxWidth().height(5.dp).background(Color.Transparent))
                    Spacer(modifier = Modifier.width(70.dp).height(3.dp).background(Color.Black))
                    Spacer(modifier = Modifier.fillMaxWidth().height(3.dp).background(Color.Transparent))
                }

                CategoryBlocks(
                    onCategory = {category->
                        onCategory(category)
                    }
                )
            }
        ){

            var selectedPromo by remember { mutableStateOf<Promotion?>(null) }
            PromosPager(
                onPromoClick = {promo->
                    selectedPromo = promo
                }
            )
            selectedPromo?.let { PromotionDetails(it, onDismis = {selectedPromo=null}) }



            //Image(painter = painterResource(Res.drawable.bibiz),"", modifier = Modifier.size(250.dp))
        }
    }
}
fun getAsyncImageLoader(context: PlatformContext)=
    ImageLoader.Builder(context).memoryCachePolicy(CachePolicy.ENABLED).memoryCache(
        MemoryCache.Builder().maxSizePercent(context, 0.3).strongReferencesEnabled(true).build()
    ).diskCachePolicy(CachePolicy.ENABLED).networkCachePolicy(CachePolicy.ENABLED).diskCache{
        newDiskCache()
    }.crossfade(true).logger(DebugLogger()).build()

fun newDiskCache(): DiskCache {
    return DiskCache.Builder().directory(FileSystem.SYSTEM_TEMPORARY_DIRECTORY / "image_cache")
        .maxSizeBytes(1024L*1024*1024)//512MB
        .build()
}