import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import network.chaintech.kmp_date_time_picker.ui.datepicker.WheelDatePickerView
import network.chaintech.kmp_date_time_picker.utils.DateTimePickerView
import network.chaintech.kmp_date_time_picker.utils.WheelPickerDefaults


@Composable
fun WheelDatePickerBottomSheet() {
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf("") }

    if (showDatePicker) {
        WheelDatePickerView(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 22.dp, bottom = 26.dp),
            showDatePicker = showDatePicker,
            title = "DUE DATE",
            doneLabel = "Done",
            titleStyle = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333),
            ),
            doneLabelStyle = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight(600),
                color = Color(0xFF007AFF),
            ),
            dateTextColor = Color(0xff007AFF),
            selectorProperties = WheelPickerDefaults.selectorProperties(
                borderColor = Color.LightGray,
            ),
            rowCount = 5,
            height = 180.dp,
            dateTextStyle = TextStyle(
                fontWeight = FontWeight(600),
            ),
            dragHandle = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Spacer(modifier = Modifier.fillMaxWidth().height(10.dp))
                    Spacer(modifier = Modifier.width(70.dp).height(3.dp).background(Color.Black))
                    Spacer(modifier = Modifier.fillMaxWidth().height(10.dp))
                }


            },
            shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp),
            dateTimePickerView = DateTimePickerView.BOTTOM_SHEET_VIEW,
            onDoneClick = {
                selectedDate = it.toString()
                showDatePicker = false
            },
            onDismiss = {
                showDatePicker = false
            }
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
            color = Color.White
    ) {
        Column(
            modifier = Modifier
                .height(200.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    showDatePicker = true
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF007AFF)),
            ) {
                Text(
                    text = "Show Date Picker",
                    modifier = Modifier.background(Color.Transparent)
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    fontSize = 16.sp
                )
            }
            Text(
                text = selectedDate,
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}