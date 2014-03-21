# Real Stock Market

Tired of *fake* stocks and predictable stock markets on your server? Use in-game money to buy and sell **real-world** stocks! 

![RealStockMarket](http://dhmc.us.s3.amazonaws.com/realstockmarket_1.jpg)

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
- `realstockmarket.portfolio.others` - `/sm portfolio (player)` - View another player's portfolio

![RealStockMarket](http://dhmc.us.s3.amazonaws.com/realstockmarket_2.jpg)


## Get Help

IRC: irc.esper.net #prism

[Source](https://github.com/prism/RealStockMarket)    
           
## Credits

This plugin was custom designed by viveleroi for the amazing *s.dhmc.us* Minecraft server.


## Authors

- viveleroi (Creator, Lead Developer)

## Donate to Vive

[![alt text][2]][1]

  [1]: https://www.paypal.com/cgi-bin/webscr?return=http%3A%2F%2Fdev.bukkit.org%2Fserver-mods%2Fprism%2F&cn=Add+special+instructions+to+the+addon+author%28s%29&business=botsko%40gmail.com&bn=PP-DonationsBF%3Abtn_donateCC_LG.gif%3ANonHosted&cancel_return=http%3A%2F%2Fdev.bukkit.org%2Fserver-mods%2Fprism%2F&lc=US&item_name=Prism+%28from+Bukkit.org%29&cmd=_donations&rm=1&no_shipping=1&currency_code=USD
  [2]: http://botsko.s3.amazonaws.com/paypal_donate.gif

I'm viveleroi, author of RealStockMarket and other plugins like Prism, Oracle, Darmok, Craftys, DarkMythos, InventoryToolkit, and more. There's no pay in making plugins but it's rewarding knowing you all use them - so please help bridge the gap and donate to cover my own time and money investment.

So please, *make a donation and make it easier for me to continue with these amazing plugins*.


### Metrics

This plugin utilizes Hidendra's plugin metrics system, which, if enabled, anonymously tracks the following about your server at mcstats.org: A unique identifier, server java version, online mode, plugin & server versions, OS version/name and architecture, cpu core count, player counts. 

This information is used purely to help inform our technical decisions and boost our egos with how many people are enjoying our work.

Opting out of this service can be done by editing plugins/Plugin Metrics/config.yml and changing opt-out to true.


### Development Builds

We offer access to [development builds](http://dhmc.us:8080/job/RealStockMarket/) through our Jenkins server. Use these only if you're comfortable using development builds. These have not yet been reviewed by Bukkit staff but are what we'll include in our next release submission.

