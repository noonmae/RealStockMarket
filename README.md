# Real Stock Market

Tired of *fake* stocks and predictable stock markets on your server? Use in-game money to buy and sell **real-world** stocks! 


## Features Overview

- View latest prices for any real-world US stock. Ex: `/sm view AAPL`
- Every command supports multiple symbols at once! Ex: `/sm view AAPL,GOOG`
- Drop-in installation, no configuration!
- Uses `sqlite` database to track purchase and sale transactions

*Note:* This plugin connects to a finance website/API to obtain required pricing information for stocks. All API calls are done on an async thread so if there's a delay or network problem, we're not going to affect performance of your server. No information about your server is transmitted aside from the IP the request comes from. View/buy/sell actions may not be possible if the API become unavailable.

## Installation

- Throw the latest .jar into your `plugins` directory. 
- Start your server once to generate config/database.

Grant permissions if you wish, but the defaults are designed responsibly.


## Permissions / Commands

*For everyone*

- `realstockmarket.view` - `/sm view (symbols)` - View latest prices of stock symbols
- `realstockmarket.buy` - `/sm buy (symbols) (quantity)` - View latest prices of stock symbols
- `realstockmarket.sell` - `/sm sell (symbols) (quantity)` - View latest prices of stock symbols

## Get Help

IRC: irc.esper.net #prism

[Source](https://github.com/prism/RealStockMarket)    
           
## Credits

This plugin was custom designed by viveleroi for the amazing *s.dhmc.us* Minecraft server.


## Authors

- viveleroi (Creator, Lead Developer)

### Metrics

This plugin utilizes Hidendra's plugin metrics system, which, if enabled, anonymously tracks the following about your server at mcstats.org: A unique identifier, server java version, online mode, plugin & server versions, OS version/name and architecture, cpu core count, player counts. 

This information is used purely to help inform our technical decisions and boost our egos with how many people are enjoying our work.

Opting out of this service can be done by editing plugins/Plugin Metrics/config.yml and changing opt-out to true.