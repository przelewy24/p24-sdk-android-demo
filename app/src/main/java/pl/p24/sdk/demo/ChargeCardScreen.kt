package pl.p24.sdk.demo

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pl.p24.sdk.Environment
import pl.p24.sdk.P24
import pl.p24.sdk.card.charge.api.CardRequest
import pl.p24.sdk.card.charge.api.CardRequestEvent
import pl.p24.sdk.card.charge.api.cardRequest
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import pl.p24.sdk.demo.theme.DemoTheme

@OptIn(ExperimentalMaterialApi::class)
@Composable
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
fun ChargeCardScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Charge card")
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        content = { padding ->
            var token by remember { mutableStateOf("") }
            var expanded by remember { mutableStateOf(false) }
            var environment by remember { mutableStateOf(
                Environment.Production) }
            var event by remember { mutableStateOf<CardRequestEvent?>(null) }
            var request by remember { mutableStateOf<CardRequest?>(null) }
            val processIsRunning = mapEventToProcessIsRunning(event)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = token,
                    onValueChange = { token = it },
                    label = {
                        Text("TOKEN")
                    }
                )
                ExposedDropdownMenuBox(
                    modifier = Modifier.fillMaxWidth(),
                    expanded = expanded,
                    onExpandedChange = {
                        expanded = !expanded
                    }
                ) {
                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = environment.name,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        Environment.values().forEach { value ->
                            DropdownMenuItem(
                                onClick = {
                                    environment = value
                                    expanded = false
                                }
                            ) {
                                Text(text = value.name)
                            }
                        }
                    }
                }
                if (event != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .background(mapEventToBackgroundColor(event!!)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = mapEventToMessage(event!!),
                            fontSize = 20.sp
                        )
                    }

                }
                if (processIsRunning) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(48.dp)
                    )
                }
                Spacer(modifier = Modifier.weight(1.0f))
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = token.isNotBlank() && !processIsRunning,
                    onClick = {
                        request = P24.cardRequest()
                            .token(token)
                            .environment(environment)
                            .callback { e -> event = e }
                            .build()
                            .also(CardRequest::start)
                    }) {
                    Text("Start")
                }
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = processIsRunning,
                    onClick = {
                        request?.cancel()
                    }) {
                    Text("Cancel")
                }
            }

        })

}

private fun mapEventToMessage(event: CardRequestEvent): String = when(event) {
    CardRequestEvent.Need3DS -> "3DS Needed"
    CardRequestEvent.ProcessCanceled -> "Process canceled"
    CardRequestEvent.ProcessCompleted -> "Process completed"
    CardRequestEvent.ProcessError -> "Process error"
    CardRequestEvent.ProcessStarted -> "Process started"
}

private fun mapEventToBackgroundColor(event: CardRequestEvent): Color = when(event) {
    CardRequestEvent.Need3DS -> Color.Yellow
    CardRequestEvent.ProcessCanceled -> Color.LightGray
    CardRequestEvent.ProcessCompleted -> Color.Green
    CardRequestEvent.ProcessError -> Color.Red
    CardRequestEvent.ProcessStarted -> Color.Yellow
}

private fun mapEventToProcessIsRunning(event: CardRequestEvent?): Boolean = when(event) {
    CardRequestEvent.Need3DS -> true
    CardRequestEvent.ProcessCanceled -> false
    CardRequestEvent.ProcessCompleted -> false
    CardRequestEvent.ProcessError -> false
    CardRequestEvent.ProcessStarted -> true
    null -> false
}

@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    DemoTheme {
        ChargeCardScreen(rememberNavController())
    }
}
