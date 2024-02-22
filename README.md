<div align='center'>
	<img src='fastlane/metadata/android/en-US/images/icon.png' alt='Lunar Launcher' width='100' height='100'>
    <h2>Lunar Launcher</h2>
    <p>
        <img src='https://img.shields.io/badge/Android-3DDC84?style=flat-square&logo=android&logoColor=white'>
        <img src='https://img.shields.io/badge/SDK-26-vibrant?style=flat-square'>
        <a href='https://github.com/iamrasel/lunar-launcher/blob/main/LICENSE'><img src='https://img.shields.io/badge/License-GPL%20v3-blue?style=flat-square'></a>
        <br>
		<a href='https://hosted.weblate.org/engage/lunar-launcher/'><img src='https://img.shields.io/weblate/progress/lunar-launcher?style=flat-square&label=Translated&color=4DCCAA' alt='Oversettelsesstatus' /></a>
        <a href='https://github.com/iamrasel/lunar-launcher/actions'><img src='https://img.shields.io/github/actions/workflow/status/iamrasel/lunar-launcher/ci_push.yml?branch=main&style=flat-square&label=Build'></a>
	<a href='https://github.com/iamrasel/lunar-launcher/releases/latest'><img src='https://img.shields.io/github/downloads/iamrasel/lunar-launcher/total?style=flat-square&label=Downloads'></a>
    </p>
	<a href="https://www.buymeacoffee.com/iamrasel"><img src="https://img.buymeacoffee.com/button-api/?text=Buy me a coffee&emoji=&slug=iamrasel&button_colour=FFDD00&font_colour=000000&font_family=Cookie&outline_colour=000000&coffee_colour=ffffff" /></a>
</div>

## Features
<details><summary>Global</summary>

- [x] Appearances
  - [x] Material Design 3
  - [x] Material You
  - [x] Day/night theme
  - [x] Wallpaper with color filter support
- [x] Double tap: lock/sleep
  - [x] Accessibility (SDK >= 28)
  - [x] Device admin
  - [x] Root
- [x] Swipe down: expand notification panel
- [ ] Yet to decide

</details>
<details><summary>Home</summary>

- [x] Battery status
  - [x] Circular percentage indicator
  - [x] Animation while charging
- [x] Time
  - [x] 12/24 format
- [x] Date
- [x] Weather
  - [x] Provider: OpenWeatherMap
  - [x] Celsius/Fahrenheit
- [x] Todo
  - [x] Add, delete, edit, copy
  - [ ] Auto destructive todo with notify
  - [x] 0–7 items in home screen
  - [x] Access lock

</details>
<details><summary>App Drawer</summary>

- [x] Quick search
- [x] Launch from search
- [x] Launch in freeform mode
- [x] Total apps count
- [ ] Gesture search
- [ ] App grid with icon (alternative)
- [ ] App appearance
- [ ] App renaming
- [ ] App vault
- [x] Detailed app info

</details>
<details><summary>Feeds</summary>

- [x] Device stats
- [x] News feed
  - [x] RSS
  - [ ] Atom
- [x] Widget host

</details>
<details><summary>Quick Access</summary>

- [x] Favourite apps (<=6)
- [x] Favourite contacts and URLs (<=6)
- [x] Control system value
  - [x] Brightness
  - [x] Sound

</details>

## Screenshots
As per version 14, click on any image to enlarge it. \
To know more, explore and see for yourself.

<table>
	<tr>
		<td><img src='fastlane/metadata/android/en-US/images/phoneScreenshots/1.png' width='240'></td>
		<td><img src='fastlane/metadata/android/en-US/images/phoneScreenshots/2.png' width='240'></td>
		<td><img src='fastlane/metadata/android/en-US/images/phoneScreenshots/3.png' width='240'></td>
		<td><img src='fastlane/metadata/android/en-US/images/phoneScreenshots/4.png' width='240'></td>
	</tr>
	<tr>
		<td><img src='fastlane/metadata/android/en-US/images/phoneScreenshots/5.png' width='240'></td>
		<td><img src='fastlane/metadata/android/en-US/images/phoneScreenshots/6.png' width='240'></td>
		<td><img src='fastlane/metadata/android/en-US/images/phoneScreenshots/7.png' width='240'></td>
		<td><img src='fastlane/metadata/android/en-US/images/phoneScreenshots/8.png' width='240'></td>
	</tr>
</table>

## Downloads
To get updated with the latest build, head over to the [Actions](https://github.com/iamrasel/lunar-launcher/actions) tab and choose the latest workflow build from there.
<div align='center'>

<a href='https://github.com/iamrasel/lunar-launcher/releases/latest'><img src='https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white'></a>
<a href='https://f-droid.org/packages/rasel.lunar.launcher'><img src='https://img.shields.io/badge/F_Droid-1976d2?style=for-the-badge&logo=f-droid&logoColor=white'></a>

</div>

This app is also available on Google's Play Store, which is forked and published by [Vedansh Nigam](https://github.com/vednig). \
If you don't have any issue with installing apps from Play Store, you can go ahead.
<div align='center'>

<a href='https://play.google.com/store/apps/details?id=rasel.lunar.launcher'><img src='https://img.shields.io/badge/Play_Store-34A853?style=for-the-badge&logo=google-play&logoColor=white'></a>

</div>

## Permissions
- `android.permission.ACCESS_NETWORK_STATE` — Check for active network connections
- `android.permission.CALL_PHONE` — Place calls
- `android.permission.EXPAND_STATUS_BAR` — Expand the notification panel
- `android.permission.INTERNET` — Fetch weather and feed data
- `android.permission.READ_EXTERNAL_STORAGE` — Fetch images to set as wallpaper (SDK < 33)
  - `android.permission.READ_MEDIA_IMAGES` — Fetch images to set as wallpaper (SDK 33+)
- `android.permission.REQUEST_DELETE_PACKAGES` — Uninstall APKs
- `android.permission.SET_WALLPAPER` — Set the wallpaper
- `android.permission.WRITE_SETTINGS` — Change system values like brightness

## Community
If you want to share your thoughts with me or other users, join the Lunar Launcher's users community.
<div align='center'>

<a href='https://github.com/iamrasel/lunar-launcher/discussions'><img src='https://img.shields.io/badge/Discussions-333333?style=for-the-badge&logo=github'></a>
<a href='https://t.me/LunarLauncher_chats'><img src='https://img.shields.io/badge/Telegram-2CA5E0?style=for-the-badge&logo=telegram&logoColor=white'></a>

</div>

## Translations
Help [translate the app on Hosted Weblate](https://hosted.weblate.org/engage/lunar-launcher). \
<a href='https://hosted.weblate.org/engage/lunar-launcher/'>
	<img src='https://hosted.weblate.org/widgets/lunar-launcher/-/multi-blue.svg' alt='Translation status' />
</a>

## Credits
- [https://github.com/cachapa/ExpandableLayout](https://github.com/cachapa/ExpandableLayout)

## Donation
Please support the development by donating. Lunar Launcher is gratis, copylefted libre app, \
it needs your support to keep it gratis and alive. \
Purchasing is not a requirement, donations are. 😊

<div align='center'>

| ![](https://img.shields.io/badge/Buy_Me_A_Coffee-FFDD00?style=flat-square&logo=buy-me-a-coffee&logoColor=black) | [iamrasel](https://www.buymeacoffee.com/iamrasel) |
|:---------------------------------------------------------------------------------------------------------------:|:-------------------------------------------------:|
|         ![](https://img.shields.io/badge/Bitcoin-000000?style=flat-square&logo=bitcoin&logoColor=white)         |   `bc1qaps45fcd9uhqgr5w2k8cxphs07ht5a7ne9af7z`    |
|        ![](https://img.shields.io/badge/Ethereum-3C3C3D?style=flat-square&logo=Ethereum&logoColor=white)        |   `0x651Fc30Ad9293593aC571A0594Fb53417F76f896`    |

</div>

There is also another way to support, just go through this [Amazon affiliate link](https://amzn.to/44krAw9) while shopping there. \
It doesn't cost you anything extra, which is a win-win for the both of us. 😉
