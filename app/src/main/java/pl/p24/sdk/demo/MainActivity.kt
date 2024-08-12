package pl.p24.sdk.demo

import android.os.Bundle
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import pl.p24.sdk.CardChargeModule
import pl.p24.sdk.P24
import pl.p24.sdk.card.tokenize.CardTokenizeModule
import pl.p24.sdk.demo.theme.DemoTheme
import pl.p24.sdk.module.install

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        P24.install(
            CardChargeModule,
            CardTokenizeModule
        )

        setContent {
            DemoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Navigation()
                }
            }
        }

    }
}