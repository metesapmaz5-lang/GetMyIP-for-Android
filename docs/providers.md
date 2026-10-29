# IP data providers

GetMyIP is not affiliated with any provider listed here and receives nothing from them.

## Criteria

A provider can be listed if it:

- works over HTTPS
- needs no API key or account
- returns the IP address as JSON
- can be reached over both IPv4 and IPv6
- does not require any tracking from the app

Anyone can propose a provider that meets these criteria with a pull request.

## Providers

| Provider | Operator | Notes |
| --- | --- | --- |
| ipleak.net (default) | AirVPN | Chosen as default because it is actively maintained and reliable. Also used for the DNS leak test. |
| ifconfig.co | echoip project | Runs [echoip](https://github.com/mpolden/echoip), open source (BSD-3-Clause). |
| Your own echoip server | You | No third party sees your IP address. |
