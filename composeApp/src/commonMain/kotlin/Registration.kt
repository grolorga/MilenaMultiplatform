import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun Registration(
    onAuth:()->Unit
){
    Box(modifier = Modifier
        .fillMaxSize().background(Color.LightGray),
        contentAlignment = Alignment.Center){
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(15.dp).fillMaxWidth().verticalScroll(rememberScrollState()).requiredHeight(10000.dp)){
            Text(
                text = "Регистрация",
                modifier = Modifier.fillMaxWidth().padding(15.dp),
                fontSize = 24.sp,
                color = Color.Black,
                //fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(15.dp))
            Text(
                text = "Зарегистрируйтесь, чтобы использовать все возможности личного кабинета: отслеживание заказов, настройку подписки, связи с социальными сетями и другие. Мы никогда и ни при каких условиях не разглашаем личные данные клиентов. Контактная информация будет использована только для оформления заказов и более удобной работы приложения.",
                modifier = Modifier.fillMaxWidth().padding(15.dp),
                fontSize = 13.sp,
                color = Color.Black,
                //fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(35.dp))
            Column(horizontalAlignment = Alignment.Start){

                val coroutineScope = rememberCoroutineScope()
                var email by remember { mutableStateOf("") }
                var password by remember { mutableStateOf("") }
                var password2 by remember { mutableStateOf("") }
                var name by remember { mutableStateOf("") }
                var phone by remember { mutableStateOf("") }
                var errorName by remember { mutableStateOf(false) }
                var errorPassword by remember { mutableStateOf(false) }
                var errorPassword2 by remember { mutableStateOf(false) }
                var errorEmail by remember { mutableStateOf(false) }
                var errorPhone by remember { mutableStateOf(false) }
                Text(
                    text = "Фамилия Имя Отчество",
                    modifier = Modifier.fillMaxWidth().padding(bottom = 15.dp),
                    fontSize = 18.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Left
                )


                OutlinedTextField(
                    textStyle = TextStyle(fontSize = 18.sp),
                    value = name,
                    onValueChange = { name = it },
                    isError = errorName,  // Пример обработки ошибок
                    modifier = Modifier
                        .fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done
                    ),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        unfocusedBorderColor= Color.Black,
                        errorBorderColor = Color.Red
                    )
                )
                Spacer(modifier = Modifier.height(15.dp))
                Text(
                    text = "E-mail",
                    modifier = Modifier.fillMaxWidth().padding(bottom = 15.dp),
                    fontSize = 18.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Left
                )
                OutlinedTextField(
                    textStyle = TextStyle(fontSize = 18.sp),
                    value = email,
                    onValueChange = { email = it },
                    isError = errorEmail,  // Пример обработки ошибок
                    modifier = Modifier
                        .fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done
                    ),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        unfocusedBorderColor= Color.Black,
                        errorBorderColor = Color.Red
                    )
                )
                Spacer(modifier = Modifier.height(15.dp))
                Text(
                    text = "Телефон",
                    modifier = Modifier.fillMaxWidth().padding(bottom = 15.dp),
                    fontSize = 18.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Left
                )
                OutlinedTextField(
                    textStyle = TextStyle(fontSize = 18.sp),
                    value = phone,
                    onValueChange = { phone = it },
                    isError = errorPhone,  // Пример обработки ошибок
                    modifier = Modifier
                        .fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done
                    ),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        unfocusedBorderColor= Color.Black,
                        errorBorderColor = Color.Red
                    )
                )
                Spacer(modifier = Modifier.height(15.dp))
                Text(
                    text = "Пароль",
                    modifier = Modifier.fillMaxWidth().padding(bottom = 15.dp),
                    fontSize = 18.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Left
                )
                OutlinedTextField(
                    textStyle = TextStyle(fontSize = 18.sp),
                    value = password,
                    onValueChange = { password = it },
                    isError = errorPassword,  // Пример обработки ошибок
                    modifier = Modifier
                        .fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done
                    ),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        unfocusedBorderColor= Color.Black,
                        errorBorderColor = Color.Red
                    )
                )
                Spacer(modifier = Modifier.height(15.dp))
                Text(
                    text = "Подтверждение пароля",
                    modifier = Modifier.fillMaxWidth().padding(bottom = 15.dp),
                    fontSize = 18.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Left
                )
                OutlinedTextField(
                    textStyle = TextStyle(fontSize = 18.sp),
                    value = password2,
                    onValueChange = { password2 = it },
                    isError = errorPassword2,  // Пример обработки ошибок
                    modifier = Modifier
                        .fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done
                    ),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        unfocusedBorderColor= Color.Black,
                        errorBorderColor = Color.Red
                    )
                )
                Spacer(modifier = Modifier.height(15.dp))
                Row(modifier = Modifier.fillMaxWidth().height(60.dp), horizontalArrangement = Arrangement.Center){
                    Button(
                        shape = RoundedCornerShape(5.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(109, 80, 255)),
                        modifier = Modifier
                            .fillMaxWidth(0.48f)
                            .fillMaxHeight(),
                        onClick = {
                            onAuth()
                        }
                    ) {
                        Text(
                            text = "ВОЙТИ",
                            modifier = Modifier
                                .fillMaxWidth(),
                            fontSize = 15.sp,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.width(15.dp))

                    Button(
                        shape = RoundedCornerShape(5.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
                        border = BorderStroke(1.dp, Color(109, 80, 255)),
                        elevation = ButtonDefaults.elevation(0.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        onClick = {
                            if (name=="")
                                errorName = true
                            else
                                if(email=="")
                                    errorEmail = true
                                else
                                    if(phone =="")
                                        errorPhone = true
                                    else
                                        if(password!=password2) {
                                            errorPassword = true
                                            errorPassword2 = true
                                        }
                                        else
                                            coroutineScope.launch {
                                                if(registration(name, email, phone, password) == "Success")
                                                {
                                                    onAuth()
                                                }
                                            }

                        }
                    ) {
                        Text(
                            text = "РЕГИСТРАЦИЯ",
                            modifier = Modifier
                                .fillMaxWidth(),
                            fontSize = 15.sp,
                            color = Color(109, 80, 255),
                            textAlign = TextAlign.Center
                        )
                    }

                }


            }


        }
    }
}