package pl.p24.sdk.demo

sealed class Routes(val route: String) {
    data object Main : Routes("main")
    data object ChargeCard : Routes("chargeCard")
    data object TokenizeCard : Routes("tokenizeCard")
}
