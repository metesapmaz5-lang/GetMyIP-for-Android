# GetMyIP for Android

> Part of the **Parabellum Linux Project (PLP)**. Everything is open source.

---

## A note from us: why we were quiet

It has been a long time since you last heard from us, and we owe you an honest explanation.

PLP OS 1.0 and 1.1 were released in May 2026. Since then, **PLP OS 2.0 "Ellie"** has been in development. The code, the Debian 12 Bookworm migration and the new installer configuration are all public in the [PLP-OS repository](https://github.com/metesapmaz5-lang/PLP-OS). What is still missing is a ready-to-install ISO. We experimented with an automated ISO build, but removed it before it was ready, because we did not want to ship something we could not stand behind.

We are sorry for the long silence and for not keeping you informed. PLP is a small, independent project with no funding and no company behind it, and progress has been slower than we hoped.

Ellie is not abandoned. GetMyIP for Android is our next step, and we will share progress openly here, one release at a time.

Thank you for your patience.

*Mete Sapmaz, PLP developer*

---

## What is GetMyIP?

GetMyIP is a simple, privacy-respecting Android app that shows what the internet sees about your connection: your public IP address, your provider and whether your traffic goes through a VPN or Tor.

**Status:** 🚧 Alpha. v0.1.0-alpha has not been tested on a real device yet.

## Planned features (v1.0 "Vigil")

- Public **IPv4** and **IPv6** address, shown separately
- ISP, ASN, country and city
- Local IP address
- VPN on/off detection
- Tor detection, checked **on your device** against the Tor Project's public exit list
- IP change alerts while the app is open, plus a "your IP changed since last time" notice on launch
- IP history, stored **only on your device**
- DNS leak test
- **Learn** section: what an IP address is, IPv4 vs IPv6, and how your IP can change
- Your choice of data provider: ipleak.net (default), ifconfig.co, or your own [echoip](https://github.com/mpolden/echoip) server

Background monitoring (alerts while the app is closed) is planned for v2.

## Roadmap

| Version | What it brings | APK |
| --- | --- | --- |
| v0.1.0-alpha | IP, ISP, VPN and Tor detection | No, source only |
| **1.0.0 Stable "Vigil"** | First official release | Yes, signed |
| v2 | Background monitoring, VPN provider hints, reproducible builds | Yes, signed |
| GetMyIP for Linux | Simple desktop app | Planned |

## Releases and verification

- Only **stable** releases include an APK. The alpha is published as a source code tag only.
- Every APK is signed with our own key. Google has no access to it.
- Each release lists the **SHA-256 checksum** of the APK and the **signing certificate fingerprint**, so you can verify the file yourself.

## Updates

We recommend [Obtainium](https://github.com/ImranR98/Obtainium) for automatic updates directly from this repository's releases. Add this repository's URL in Obtainium and it will notify you about new stable versions.

GetMyIP has no built-in update checker, so the app never connects anywhere except your chosen IP provider and the Tor Project's exit list.

## Build from source

Requirements: JDK 17 or newer and the Android SDK command-line tools (`ANDROID_HOME` set).

```bash
./gradlew assembleDebug
```

The APK will be in `app/build/outputs/apk/debug/`.

Note: an APK you build yourself is signed with a different key. To switch to an official release later, uninstall your own build first.

## Privacy

- No servers, no accounts, no analytics, no ads, no tracking.
- To show your public IP, the app sends a request to the IP provider you choose. That provider sees your IP address, as any website would.
- To detect Tor, the app downloads the Tor Project's public exit list and compares it on your device. Your IP is not sent to the Tor Project.
- Your IP history never leaves your device, and you can delete it at any time.

The full privacy policy (English and Turkish, including KVKK) will be published in `PRIVACY.md`.

## Independence and funding

- GetMyIP has **no donation links, no sponsors and no affiliate links**. It receives no money from anyone.
- GetMyIP is not affiliated with ipleak.net, AirVPN, ifconfig.co or any VPN provider.
- All external links are plain official addresses, with no referral or tracking parameters.

**We support the Tor Project** and its mission for a free and open internet.

## Tech stack

Kotlin, Jetpack Compose, OkHttp and Room. No Google Play Services. Android 8.0 (API 26) or newer.

## License

GetMyIP is free software, released under the **GNU General Public License v3.0**. See [LICENSE](LICENSE).

---

**Built by** Parabellum Linux Project
**Developer:** Mete Sapmaz

Copyright © 2026 Mete Sapmaz
