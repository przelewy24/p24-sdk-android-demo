package pl.p24.sdk.demo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import pl.p24.sdk.Environment
import pl.p24.sdk.card.tokenize.TOSCheckbox
import pl.p24.sdk.card.tokenize.TermsOfServiceConfig
import pl.p24.sdk.card.tokenize.TokenizationMode
import pl.p24.sdk.card.tokenize.TokenizedCard
import pl.p24.sdk.card.tokenize.api.TokenizationCallback
import pl.p24.sdk.card.tokenize.api.TokenizeCardRequestBuilder
import pl.p24.sdk.demo.theme.DemoTheme
import java.util.UUID

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TokenizeCardScreen(navController: NavHostController) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Tokenize card")
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

            var checkedTokenizationPermanent by remember { mutableStateOf(true) }
            var checkedTokenizationTemporary by remember { mutableStateOf(true) }

            var expanded by remember { mutableStateOf(false) }
            var environment by remember { mutableStateOf(Environment.Production) }
            var merchantId by remember { mutableStateOf("157122") }
            var sessionId by remember { mutableStateOf(randomString()) }
            var crc by remember { mutableStateOf("1234") }
            var sign = getSign(
                merchantId = merchantId.toIntOrNull() ?: 0,
                sessionId = sessionId,
                crc = crc
            )

            var statusBackground by remember { mutableStateOf(Color.Transparent) }
            var statusText by remember { mutableStateOf("") }

            Column(modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(all = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)) {
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

                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = merchantId,
                        onValueChange = { merchantId = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        label = {
                            Text("MerchantId")
                        }
                    )
                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = sessionId,
                        onValueChange = { sessionId = it },
                        label = {
                            Text("SessionId")
                        },
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    sessionId = randomString()
                                }
                            ) {
                                Icon(
                                    Icons.Filled.Refresh,
                                    contentDescription = "Refresh sessionId",
                                )
                            }
                        }
                    )
                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = crc,
                        onValueChange = { crc = it },
                        label = {
                            Text("CRC")
                        }
                    )
                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = sign,
                        onValueChange = {},
                        label = {
                            Text("Sign")
                        }
                    )
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(),
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(8.dp)
                        ) {
                            Text(
                                "Tokenization modes"
                            )
                            Row {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        "Permanent",
                                        fontSize = 14.sp
                                    )
                                    Checkbox(
                                        checked = checkedTokenizationPermanent,
                                        onCheckedChange = { checkedTokenizationPermanent = it }
                                    )
                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        "Temporary",
                                        fontSize = 14.sp
                                    )
                                    Checkbox(
                                        checked = checkedTokenizationTemporary,
                                        onCheckedChange = { checkedTokenizationTemporary = it }
                                    )
                                }
                            }
                        }
                    }
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        enabled = merchantId.isNotEmpty() && sessionId.isNotEmpty() && crc.isNotEmpty(),
                        onClick = {

                            statusBackground = Color.Blue
                            statusText = "Process pending"

                            startProcess(
                                environment = environment,
                                merchantId = merchantId.toInt(),
                                sessionId = sessionId,
                                sign = sign,
                                tokenizationModes = buildList {
                                    if (checkedTokenizationPermanent) add(TokenizationMode.Permanent)
                                    if (checkedTokenizationTemporary) add(TokenizationMode.Temporary)
                                },
                                callback = object : TokenizationCallback {
                                    override fun onStarted() {
                                        statusBackground = Color.Yellow
                                        statusText = "Process started"
                                    }

                                    override fun onSuccess(tokenizedCard: TokenizedCard) {
                                        statusBackground = Color.Green
                                        statusText = "Process success -> tokenizedCard: \n$tokenizedCard"
                                    }

                                    override fun onFailed(errors: List<String>)  {
                                        statusBackground = Color.Red
                                        statusText = "Process failed -> errors: \n$errors"
                                    }

                                    override fun onCanceled() {
                                        statusBackground = Color.Gray
                                        statusText = "Process canceled"
                                    }
                                }
                            )
                        }
                    ) {
                        Text(text = "Start")
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .background(statusBackground),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = statusText,
                            color = Color.Black,
                            fontSize = 20.sp
                        )
                    }
            }
        }
    )
}

private fun startProcess(
    environment: Environment,
    merchantId: Int,
    sessionId: String,
    sign: String,
    tokenizationModes: List<TokenizationMode>,
    callback: TokenizationCallback,
) {
    TokenizeCardRequestBuilder()
        .environment(environment)
        .merchantId(merchantId)
        .sessionId(sessionId)
        .sign(sign)
        .allowedTokenizationModes(tokenizationModes)
        .callback(callback)
        .termsOfServiceConfig(TermsOfServiceConfig(
            checkbox = TOSCheckbox(
                isChecked = true
            )
        ))
        .build()
        .start()
}

private fun randomString() = UUID.randomUUID().toString()

private fun getSign(merchantId: Int, sessionId: String, crc: String): String {
    return """
        {
            "merchantId": $merchantId,
            "sessionId": $sessionId,
            "crc": $crc
        }
    """.trimIndent().toSHA384()
}

@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    DemoTheme {
        TokenizeCardScreen(rememberNavController())
    }
}