import androidx.compose.ui.interop.LocalUIViewController
import androidx.compose.ui.window.ComposeUIViewController

fun MainViewController() = ComposeUIViewController { App(
    imagePicker = ImagePickerFactory(LocalUIViewController.current).createPicker()
) }