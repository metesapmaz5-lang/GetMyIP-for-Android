# Changelog

## v1.0.0 "Vigil" (2026-10-29)

### Added
- Choice of IP data provider: ipleak.net (default), ifconfig.co or your own echoip server
- IPv4 and IPv6 are checked separately with every provider
- Settings and About screens
- IP history, stored only on the device (up to 500 entries, can be cleared)
- "Your IP changed since last time" notice when the app opens
- Notification when the IP changes while the app is open in the background (addresses hidden on the lock screen)
- DNS leak test (ipleak.net)
- Learn section: what an IP address is, IPv4 and IPv6, how your IP changes, example tools, VPN or Tor
- `docs/external-links.md`: every external address the app uses
- First launch screens: welcome, privacy summary, notification permission
- Privacy policy and KVKK information notice, in the app and in `PRIVACY.md`
- App icon
- Debug builds only: the last crash is shown on the next launch and can be copied
- Nothing is copied to Android cloud backups or device transfers

### Changed
- Network requests now use OkHttp

## v0.1.0-alpha

> Not tested on a real device. Build check only.

### Added
- Project skeleton: Kotlin, Jetpack Compose, Android Gradle Plugin 9.4, Gradle 9.6
- Public IPv4 and IPv6 address via ipleak.net
- ISP, ASN and location
- Local IP address and connection type
- VPN detection using Android's own network information
- Tor detection against the Tor Project's public exit list, checked on the device
- Automatic refresh when the network changes
- English and Turkish
