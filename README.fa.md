# Ntk.Android.BaseActivity

کتابخانهٔ UI مشترک اندروید برای اپ‌های محصول NTK CMS — Activityها، لایه‌ها، آداپترها و جریان احراز هویت بر پایهٔ [Ntk.Android.Base](https://github.com/akaravi/Ntk.Android.Base).

## معرفی

`Ntk.Android.BaseActivity` صفحات و انتزاع‌های قابل‌استفادهٔ مجدد را بسته‌بندی می‌کند تا هر اپ white-label فقط تم و ناوبری اختصاصی خود را پیاده کند. به ماژول `api` در `Ntk.Android.Base` وابسته است و از JitPack منتشر می‌شود.

## ماژول

| ماژول | نوع | پکیج |
|--------|-----|------|
| `app` | Android Library | `ntk.android.base` |

> توجه: `artifactId` در `build.gradle` به‌صورت تاریخی `Ntk.Android.BaseActivty` نوشته شده (غلط املایی). برای ارجاع به پروژه از نام مخزن `Ntk.Android.BaseActivity` استفاده کنید.

## قابلیت‌ها

### جریان‌های عمومی

- اسپلش و معرفی (`BaseSplashActivity`، `IntroActivity`)
- احراز هویت پیامکی / موبایل (`AuthWithSmsActivity`، `LoginMobileActivity`، `RegisterMobileActivity`)
- دربارهٔ ما، اعلان‌ها، پخش ویدیو، گالری
- انتزاع لیست / جستجو / جزئیات (`AbstractListActivity`، `AbstractSearchActivity`، `AbstractDetailActivity`)

### ماژول‌های محتوا (Activity پایه)

| حوزه | کلاس‌های پایه |
|------|----------------|
| اخبار | `BaseNewsDetail_1_Activity` و کمک‌کننده‌های لیست |
| بلاگ | `BaseBlogDetail_1_Activity` |
| مقاله | `BaseArticleDetail_1_Activity`، `BaseArticleDetail2_2_Activity` |
| زندگینامه | `BaseBiographyDetail_2_Activity` |
| تیکت | `TicketListActivity`، `NewTicketActivity`، `TicketAnswerActivity`، `FaqActivity` |
| نظرسنجی | `PolingActivity`، `PolingCategoryActivity` |
| فروشگاه | `BaseHyperShopContentDetail_1_Activity` |

### UI و زیرساخت

- کامپوننت‌های Material، انیمیشن Lottie، ExoPlayer
- Glide و Universal Image Loader
- Room (کش محلی) + RxJava 2
- Firebase Cloud Messaging، Sentry
- QR code، PhotoView، افکت Ken Burns

## پیش‌نیاز

- Android Studio
- JDK 17
- compileSdk 34، minSdk 21

## بیلد محلی

مخزن `Ntk.Android.Base` را در کنار این پروژه clone کنید و ماژول ترکیبی را در `settings.gradle` فعال کنید:

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

## پروژه‌های مرتبط

| مخزن | نقش |
|------|-----|
| [Ntk.Android.Base](https://github.com/akaravi/Ntk.Android.Base) | کلاینت API (ماژول `api`) |
| [Ntk.Android.Biography](https://github.com/akaravi/Ntk.Android.Biography) | اپ زندگینامه |
| [Ntk.Android.Estate](https://github.com/akaravi/Ntk.Android.Estate) | اپ املاک |
| [Ntk.Android.Ticketing](https://github.com/akaravi/Ntk.Android.Ticketing) | اپ تیکت |

## مجوز

اختصاصی — NTK.
