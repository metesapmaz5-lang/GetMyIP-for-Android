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

**Current version:** v1.0.0 Stable "Vigil"

## Features

- Public **IPv4** and **IPv6** address, checked separately
- ISP, ASN, country and city
- Local IP address and connection type
- VPN detection, using Android's own network information
- Tor detection, compared **on your device** against the Tor Project's public exit list
- "Your IP changed since last time" notice, and a notification when your IP changes while the app is open in the background
- IP history, stored **only on your device**
- DNS leak test
- **Learn** section: what an IP address is, IPv4 vs IPv6, how your IP can change, example tools
- Your choice of data provider: ipleak.net (default), ifconfig.co, or your own [echoip](https://github.com/mpolden/echoip) server
- English and Turkish

Background monitoring (alerts while the app is closed) is planned for v2.

## Download and verify

Get `plp-getmyip-1.0.0.apk` and its `.sha256` file from the [Releases](https://github.com/metesapmaz5-lang/GetMyIP-for-Android/releases) page.

**1. Check the file:**

```bash
sha256sum -c plp-getmyip-1.0.0.apk.sha256
```

**2. Check the signature** (`apksigner` is part of the Android SDK build tools):

```bash
apksigner verify --print-certs plp-getmyip-1.0.0.apk
```

The `SHA-256 digest` of the signer certificate must be:

```
002567cb421719a6437cb2161355ecf50f61af699a665a78eed17f33d559f740
```

Same fingerprint, colon format:

```
00:25:67:CB:42:17:19:A6:43:7C:B2:16:13:55:EC:F5:0F:61:AF:69:9A:66:5A:78:EE:D1:7F:33:D5:59:F7:40
```

Every official release is signed with this key. Google has no access to it.

## Updates

We recommend [Obtainium](https://github.com/ImranR98/Obtainium) for automatic updates directly from this repository's releases. Add this repository's URL in Obtainium and it will notify you about new stable versions.

GetMyIP has no built-in update checker.

## Roadmap

| Version | What it brings | APK |
| --- | --- | --- |
| v0.1.0-alpha | IP, ISP, VPN and Tor detection | No, source only |
| **v1.0.0 Stable "Vigil"** | First official release | Yes, signed |
| v2 | Background monitoring, VPN provider hints, reproducible builds | Yes, signed |
| GetMyIP for Linux | Simple desktop app | Planned |

## Build from source

Requirements: JDK 17 or newer and the Android SDK command-line tools (`ANDROID_HOME` set).

```bash
./gradlew assembleDebug
```

The APK will be in `app/build/outputs/apk/debug/`.

An APK you build yourself is signed with a different key. To switch to an official release later, uninstall your own build first.

## Privacy

- No servers, no accounts, no analytics, no ads, no tracking.
- To show your public IP, the app connects to the IP provider you choose. That provider sees your IP address, as any website would.
- To detect Tor, the app downloads the Tor Project's public exit list and compares it on your device. The app never asks the Tor Project about your address, but like any download, the server sees the connection.
- Your IP history never leaves your device and is excluded from Android backups. You can delete it at any time.

Full privacy policy (English and Turkish, including the KVKK information notice): [PRIVACY.md](PRIVACY.md). Every external address the app uses: [docs/external-links.md](docs/external-links.md).

## Independence and funding

- GetMyIP has **no donation links, no sponsors and no affiliate links**. It receives no money from anyone. See [docs/funding-policy.md](docs/funding-policy.md).
- GetMyIP is not affiliated with ipleak.net, AirVPN, ifconfig.co or any VPN provider. See [docs/providers.md](docs/providers.md).
- All external links are plain official addresses, with no referral or tracking parameters.

**We support the Tor Project** and its mission for a free and open internet.

## Tech stack

Kotlin, Jetpack Compose and OkHttp. No Google Play Services. Android 8.0 (API 26) or newer.

## License

GetMyIP is free software, released under the **GNU General Public License v3.0**. See [LICENSE](LICENSE).

## Contact

metesapmaz5@gmail.com

---

**Built by** Parabellum Linux Project
**Developer:** Mete Sapmaz

Copyright © 2026 Mete Sapmaz
