package com.example.pyco

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.pyco.ui.theme.PycoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PycoTheme {
                Scaffold( modifier = Modifier.fillMaxSize() ) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

//@Preview(
//    name = "Pixel 6 Pro Preview",
//    widthDp = 411,
//    heightDp = 890, showSystemUi = true,
//    device = "id:pixel_6_pro"
//)
//@Composable
//fun GreetingPreview() {
//    PycoTheme {
//        Scaffold(
//            modifier = Modifier.fillMaxSize()
//        ) {
//            Greeting(
//                name = "Android",
//                modifier = Modifier.padding(it)
//            )
//        }
//    }
//}

//the preview is wonky will test on emulator
//@Preview(
//    name = "Pixel 6 Pro Landscape Preview",
//    showBackground = true,
//    widthDp = 890,
//    heightDp = 411, device = "spec:parent=pixel_6_pro,orientation=landscape",
//    showSystemUi = true
//)
//@Composable
//fun GreetingLandscapePreview() {
//    PycoTheme {
//        Scaffold(
//            modifier = Modifier.fillMaxSize()
//        ) {
//            Greeting(
//                name = "Android",
//                modifier = Modifier.padding(it)
//            )
//        }
//    }
//}
