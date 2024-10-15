import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key.Companion.R
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.setSingletonImageLoaderFactory
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import coil3.request.CachePolicy
import coil3.request.crossfade
import coil3.util.DebugLogger
import io.ktor.http.ContentType
import milenamultiplatformtry.composeapp.generated.resources.Res
import milenamultiplatformtry.composeapp.generated.resources.bibiz
import okio.FileSystem
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview



@OptIn(ExperimentalMaterialApi::class, ExperimentalCoilApi::class)
@Composable
@Preview
fun App(
    imagePicker: ImagePicker
) {
    val navController = rememberNavController()
    // Инициализация UserPreferences
    val userPreferences = remember { createUserPreferences() }
    val cartPreferences = remember { createCartPreferences()}
    // Попытка получить сохраненный логин и пароль
    val savedLogin = userPreferences.getUserLogin()
    val savedPassword = userPreferences.getUserPassword()

    clearMemoryCache()
    clearDiskCache()

    Scaffold(
        topBar = { TopMenu(navController = navController) }
    ) {

        Navigation(navController = navController, userPreferences = userPreferences,
            imagePicker = imagePicker, cartPreferences = cartPreferences)
    }
}


@Composable
fun Navigation(navController: NavController, userPreferences: UserPreferences, imagePicker: ImagePicker,
               cartPreferences: CartPreferences) {


    val navHostController = remember(navController) {
        navController as NavHostController
    }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }

    NavHost(
        navController = navHostController, startDestination = "screen1", modifier = Modifier
            .fillMaxSize()
            //.padding(top = 56.dp)
    ) {

        composable("screen1") {

            First(
                onCategory = {category ->
                    selectedCategory = category
                    //navController.navigate("screen3")
                    navHostController.navigate("screen3")
                    //navHostController.navigate("screen3")

                    //navController.navigate("screen3" )
                }
            )
        }
        composable("screen2") {
            PhoneDialog()
            //tryDecode()
            //TryGetImage()
            //PdfGenerationScreen()
        }
        composable("screen3") { backStackEntry ->
            val categoryName = backStackEntry.arguments?.getString("categoryName")
            println("Имя категории : $categoryName")
            val savedLogin = userPreferences.getUserLogin()
            var selected = selectedCategory?.name
            selectedCategory = null
            CatalogWithFilter(selected, savedLogin, cartPreferences)
        }
        composable("screen4") {
            //todo сделать авторизованного пользователя, если нет, то на вход
            // Попытка получить сохраненный логин и пароль
            val savedLogin = userPreferences.getUserLogin()
            val savedPassword = userPreferences.getUserPassword()
            if (savedLogin != null && savedPassword != null) {
                // Если данные сохранены, направляем на "кабинет"
                LaunchedEffect(Unit) {
                    navController.navigate("kabinet")
                }
            }

            Auth(
                onSuccess = {login,password->
                    // Сохраняем логин и пароль при успешной авторизации
                    userPreferences.saveUserCredentials(login = login, password = password)
                    navController.navigate("kabinet")
                            },
                onRegistr = {navController.navigate("registration")}
            )
        }
        composable("registration"){
            Registration(
                onAuth = {navController.navigate("screen4")}
            )
        }
        composable("kabinet"){

            personalKab(
                userPreferences,
                onHistory = {
                    navController.navigate("history")
                },
                onPersonalData = {
                    navController.navigate("personalData")
                },
                onAdminPanel = {
                    navController.navigate("adminPanel")
                }
            )
        }
        composable("history") {
            History(
                userPreferences,
                onDismiss = {navController.navigate("kabinet")})
        }
        composable("personalData") {
            PersonalData(
                userPreferences,
                cartPreferences,
                onUpdatedUserData = {
                    navController.navigate("screen1")
                },
                onDismiss = {navController.navigate("kabinet")})
        }
        composable("adminPanel") {
            AdminPanel(
                onAddPromo = {
                    navController.navigate("addPromo")
                },
                onDeletePromo = {
                    navController.navigate("deletePromo")
                },
                onAddProduct = {
                    navController.navigate("addProduct")
                },
                onCheckOrders = {
                    navController.navigate("orders")
                },
                onDismiss = {navController.navigate("kabinet")}
            )
        }
        composable("orders"){
            CkeckOrders(
                userPreferences,
                onCencelled = {
                    navController.navigate("kabinet")
                },
                onDismiss = {navController.navigate("adminPanel")})
        }
        composable("addProduct"){
            AddProduct(
                onProductAdded = {
                    navController.navigate("kabinet")
                },
                onDismiss = {navController.navigate("adminPanel")}
            )
        }
        composable("addPromo"){
            UploadPromotion(
                imagePicker = imagePicker,
                onAddedPromo = {
                    navController.navigate("kabinet")
                },
                onDismiss={
                    navController.navigate("adminPanel")
                }
            )
        }
        composable("deletePromo"){
            DeletePromotion(
                onDeletedPromo = {
                    navController.navigate("deletePromo")
                },
                onDismiss = {
                    navController.navigate("adminPanel")
                }
            )
        }
        composable("screen5") {

            Cart(
                cartPreferences,
                userPreferences,
                onOffered = {
                    navController.navigate("kabinet")
                })
        }
    }
}