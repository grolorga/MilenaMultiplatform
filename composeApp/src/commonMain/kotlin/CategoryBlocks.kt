import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import milenamultiplatformtry.composeapp.generated.resources.Res
import milenamultiplatformtry.composeapp.generated.resources.logot
import org.jetbrains.compose.resources.vectorResource

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun CategoryBlocks(
    onCategory:(Category)->Unit
){
    var categories by remember { mutableStateOf<List<Category>>(emptyList()) }
    LaunchedEffect(Unit) {
        categories = fetchCategories()
        println(categories.size)
    }
    val screenHeightPx = getScreenSize().heightPx
    val screenWidthPx = getScreenSize().widthPx
    val ind = screenHeightPx/100 + screenWidthPx/100
    Column(modifier = Modifier.height((ind*25).dp).padding(15.dp).background(Color.Transparent).verticalScroll(
        rememberScrollState()
    )) {
        if(categories.isNotEmpty()){
            Box(modifier = Modifier.fillMaxWidth().height((ind*7).dp).shadow(20.dp).clickable {
                onCategory(categories[0])
            }){
                AsyncImage(modifier = Modifier.fillMaxWidth().height((ind*7).dp),
                    model = categories[0].image,
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
                Column(){
                    Spacer(modifier = Modifier.height((ind*5.3).dp))
                    Text(
                        text=categories[0].name,
                        fontSize = 20.sp,
                        color = Color.White,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.height(40.dp).fillMaxWidth().padding(10.dp)

                    )
                }
            }
            Spacer(modifier = Modifier.fillMaxWidth().height(15.dp))
            Row(modifier = Modifier.fillMaxWidth().height((ind*7).dp)){
                Box(modifier = Modifier.fillMaxWidth(0.5f).shadow(20.dp).clickable {
                    onCategory(categories[1])
                }){
                    AsyncImage(modifier = Modifier.fillMaxWidth().height((ind*7).dp),
                        model = categories[1].image,
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                    Column(){
                        Spacer(modifier = Modifier.height((ind*5).dp))
                        Text(
                            text=categories[1].name,
                            fontSize = 20.sp,
                            color = Color.White,
                            textAlign = TextAlign.Start,
                            softWrap = true,
                            modifier = Modifier.height(80.dp).fillMaxWidth().padding(start = 10.dp)

                        )
                    }
                }

                Spacer(modifier = Modifier.width(15.dp))
                Box(modifier = Modifier.fillMaxWidth().shadow(20.dp).clickable {
                    onCategory(categories[2])
                }){
                    AsyncImage(modifier = Modifier.fillMaxWidth().height((ind*7).dp),
                        model = categories[2].image,
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                    Column(){
                        Spacer(modifier = Modifier.height((ind*5).dp))
                        Text(
                            text=categories[2].name,
                            fontSize = 20.sp,
                            color = Color.White,
                            textAlign = TextAlign.Start,
                            softWrap = true,
                            modifier = Modifier.height(80.dp).fillMaxWidth().padding(start = 10.dp)

                        )
                    }
                }

            }
            Spacer(modifier = Modifier.fillMaxWidth().height(15.dp))
            ///*
            Row(modifier = Modifier.fillMaxWidth().height((ind*7).dp)){
                Box(modifier = Modifier.fillMaxWidth(0.45f).shadow(20.dp).clickable {
                    onCategory(categories[3])
                }){
                    AsyncImage(modifier = Modifier.fillMaxWidth().height((ind*7).dp),
                        model = categories[3].image,
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                    Column(){
                        Spacer(modifier = Modifier.height((ind*5).dp))
                        Text(
                            text=categories[3].name,
                            fontSize = 20.sp,
                            color = Color.White,
                            textAlign = TextAlign.Start,
                            softWrap = true,
                            modifier = Modifier.height(80.dp).fillMaxWidth().padding(start = 10.dp)

                        )
                    }
                }

                Spacer(modifier = Modifier.width(15.dp))
                Box(modifier = Modifier.fillMaxWidth().shadow(20.dp).clickable {
                    onCategory(categories[4])
                }){
                    AsyncImage(modifier = Modifier.fillMaxWidth().height((ind*7).dp),
                        model = categories[4].image,
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                    Column(){
                        Spacer(modifier = Modifier.height((ind*5).dp))
                        Text(
                            text=categories[4].name,
                            fontSize = 20.sp,
                            color = Color.White,
                            textAlign = TextAlign.Start,
                            softWrap = true,
                            modifier = Modifier.height(80.dp).fillMaxWidth().padding(start = 10.dp)

                        )
                    }
                }

            }
            Spacer(modifier = Modifier.fillMaxWidth().height(15.dp))
            /*
            Box(modifier = Modifier.fillMaxWidth().height((ind*7).dp).shadow(20.dp).clickable {
                onCategory(categories[5])
            }){
                AsyncImage(modifier = Modifier.fillMaxWidth().height((ind*7).dp),
                    model = categories[5].image,
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
                Column(){
                    Spacer(modifier = Modifier.height((ind*5.3).dp))
                    Text(
                        text=categories[5].name,
                        fontSize = 20.sp,
                        color = Color.White,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.height(40.dp).fillMaxWidth().padding(10.dp)

                    )
                }
            }

             */
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
}