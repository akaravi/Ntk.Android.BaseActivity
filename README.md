# Ntk.Android.BaseActivity

Shared Android UI library for NTK CMS product apps — base Activities, layouts, adapters, and auth flows built on top of [Ntk.Android.Base](https://github.com/akaravi/Ntk.Android.Base).

## Overview

`Ntk.Android.BaseActivity` packages reusable screens and abstractions so each white-label app only implements product-specific styling and navigation. It depends on the `api` module from `Ntk.Android.Base` and is published via JitPack.

## Module

| Module | Type | Package |
|--------|------|---------|
| `app` | Android Library | `ntk.android.base` |

> Note: the Maven `artifactId` in `build.gradle` is spelled `Ntk.Android.BaseActivty` (historical typo). Use the GitHub repo name `Ntk.Android.BaseActivity` when referencing the project.

## Features

### Common flows

- Splash and intro (`BaseSplashActivity`, `IntroActivity`)
- SMS / mobile auth (`AuthWithSmsActivity`, `LoginMobileActivity`, `RegisterMobileActivity`)
- About us, notifications, video player, gallery
- Generic list / search / detail abstractions (`AbstractListActivity`, `AbstractSearchActivity`, `AbstractDetailActivity`)

### Content modules (base Activities)

| Area | Base classes |
|------|----------------|
| News | `BaseNewsDetail_1_Activity`, list/filter helpers |
| Blog | `BaseBlogDetail_1_Activity` |
| Article | `BaseArticleDetail_1_Activity`, `BaseArticleDetail2_2_Activity` |
| Biography | `BaseBiographyDetail_2_Activity` |
| Ticketing | `TicketListActivity`, `NewTicketActivity`, `TicketAnswerActivity`, `FaqActivity` |
| Polling | `PolingActivity`, `PolingCategoryActivity` |
| HyperShop | `BaseHyperShopContentDetail_1_Activity` |

### UI & infrastructure

- Material Design components, Lottie animations, ExoPlayer
- Glide + Universal Image Loader
- Room (local cache) + RxJava 2
- Firebase Cloud Messaging, Sentry crash reporting
- QR code generation, PhotoView, Ken Burns image effect

## Requirements

- Android Studio
- JDK 17 (`compileOptions` in library module)
- `compileSdk` 34, `minSdk` 21

## Local build

Clone `Ntk.Android.Base` as a sibling folder, then enable the composite module in `settings.gradle`:

```gradle
include ':baseApi'
project(':baseApi').projectDir = new File('..\\Ntk.Android.Base\\api')
```

```bash
./gradlew :app:assembleRelease
```

## JitPack

```gradle
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    api 'com.github.akaravi:Ntk.Android.BaseActivity:<tag>'
}
```

## Related projects

| Repository | Role |
|------------|------|
| [Ntk.Android.Base](https://github.com/akaravi/Ntk.Android.Base) | API client (`api` module) |
| [Ntk.Android.Biography](https://github.com/akaravi/Ntk.Android.Biography) | Biography app |
| [Ntk.Android.Estate](https://github.com/akaravi/Ntk.Android.Estate) | Estate app |
| [Ntk.Android.Ticketing](https://github.com/akaravi/Ntk.Android.Ticketing) | Ticketing app |

## License

Proprietary — NTK.
