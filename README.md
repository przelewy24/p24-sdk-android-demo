## A demo project that shows how to integrate with the P24 SDK

### Prepare project:

1. Add repository

```kotlin
repositories {
    maven {
        url uri("https://maven.pkg.github.com/przelewy24/p24-sdk-android")
        credentials {
            username = "..."
            password = "..."
        }
    }
}
```

2. Add dependencies

```kotlin
    implementation("pl.p24.sdk:core:x.x.x")
    implementation("pl.p24.sdk:card-charge:x.x.x")
    implementation("pl.p24.sdk:card-tokenize:x.x.x")
```

3. Install modules

```kotlin
P24.install(
    CardChargeModule,
    CardTokenizeModule
)
```