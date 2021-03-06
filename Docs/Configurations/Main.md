Main Configurations
=========================

File
--------------
config.yml

Configuration
--------------
```YAML
Core:
    
    #Whether or not to enable UUID support(results may vary if turned off).
    UUID: true
    
    #Whether or not players should have different balances, banks, etc in different worlds
    Multiworld: false
    
    #The initial balance for accounts.
    #Will be used if no world-specific configurations are found if multiworld is enabled
    Balance: 200.0

    #Whether or not to shorten money amounts
    #Example: 2100 would be 2.1k
    Shorten: true
    
    #whether or not to enable plugin metrics for TNE
    Metrics: true

    #All configurations relating to account pins
    Pins:

        #Whether or not account pins should be enabled
        Enabled: true

        #Whether or not players must set their pin before using money-related functions
        Force: true

    #All configurations relating to TNE commands
    Commands:

        #Whether or not players should be able to use /pay instead of /money pay
        PayShort: true

        #Whether or not players should be able to use /balance instead of /money
        BalanceShort: true
    
    #All configurations relating to update checking
    Update:
    
        #Whether or not TNE should check if the server is using the latest build
        Check: true
        
        #Whether or not admins(anyone with perm. node tne.admin) should be notified on login if TNE is outdated.
        Notify: true

    Transactions:

        #Whether or not to track transaction history.
        Track: true

        #The timezone to use for transactions.
        Timezone: US/Eastern

    #All configurations relating to the data auto saver
    AutoSaver:

        #Whether or not the auto saver is enabled(will auto save player data)
        Enabled: true

        #The interval at which the auto saver will save data(in seconds)
        Interval: 600

    #All configurations relating to currency.
    Currency:

        #The character to use as the decimal place holder.
        Decimal: .

        #Would you like to use an item as the currency?
        ItemCurrency: false
        
        #If you want to use an item, which one?(Please use the name of the item)
        #Example: GOLD_INGOT
        ItemMajor: GOLD_INGOT
        
        #The minor item currency item.
        #Example: for USD this might be quarters or dimes
        ItemMinor: IRON_INGOT

        #The name of the major currency
        #Example: Dollars
        MajorName:

            #The singular name. Example: Dollar
            Single: Dollar

            #The plural name. Example: Dollars
            Plural: Dollars
            
        #The name of the minor currency
        #Example: Cents
        MinorName:

            #The singular name. Example: Cent
            Single: Cent

            #The plural name. Example: Cents
            Plural: Cents

    #All configurations relating to the TNE Auctions System.
    Auctions:

        #Whether or not auctions are enabled
        Enabled: true

        #The cost to start an auction.
        Cost: 10

        #Whether or not world-based auctions are allowed.
        AllowWorld: true

        #Whether or not multiple auctions can be run at once
        Multiple: false

        #The maximum amount of simultaneous auctions at once.
        MaxMultiple: 3

        #The maximum number of auctions a single player can have in the queue.
        PersonalQueue: 3

        #The maximum number of auctions able to be in the queue for a single world.
        MaxQueue: 10

        #The maximum start price for auctions
        MaxStart: 2000

        #The minimum start price for auctions
        MinStart: 1

        #The max increment amount for auctions
        MaxIncrement: 1000

        #The minimum increment amount for auctions
        MinIncrement: 1

        #The max amount of time an auction can last(Seconds).
        MaxTime: 60

        #The minimum time an auction can last(Seconds).
        MinTime: 30

        #whether or not anti-snipe is enabled
        AntiSnipe: true

        #The time in seconds
        SnipePeriod: 30

        #The time in seconds the anti-snipe system should add on
        SnipeTime: 20

        #Whether or not periodic announcements should be made about the current auction(s)
        Announce: true

        #The interval for auction announcements if they're enabled
        Interval: 10

        #Whether or not the final <CountdownTime> seconds should be announced.
        Countdown: true

        #The time in seconds that should be classified as the final countdown period.
        CountdownTime: 10

    #All configurations relating to the TNE Shop System
    Shops:

        #Whether or not shops are enabled
        Enabled: true

        #The cost to create a shop.
        Cost: 10.00

        #The max amount of shops a single player can own.
        Max: 5

        #The number of rows a shop has.( minimum is 1, maximum is 6)
        #1 row = 9 slots
        Rows: 3

        #The max amount of people that can browse a single shop at a time.
        Shoppers: 10

        #All configurations relating to shop money sharing.
        Shares:

            #Whether or not money sharing is enabled
            Enabled: true

            #The max amount of people that can share money for a shop.
            Max: 3

    #All configurations relating to the TNE Sign System
    Signs:

        #All configurations relating to bank signs.
        Bank:

            #Whether or not players can use bank signs to view their banks
            Enabled: false

            #The cost to make a bank sign, if the player has valid permissions.
            Place: 20.0

            #The cost to use a bank sign, if the player has valid permissions.
            Use: 20.0

        #All configurations relating to shop signs.
        Shop:

            #Whether or not shop signs are enabled.
            Enabled: false

            #The cost to make a shop sign, if the player has valid permissions.
            Place: 20.0

            #The cost to use a shop sign, if the player has valid permissions.
            Use: 20.0

    #All configurations relating to player death.
    Death:

        #Whether or not players who die lose all their money
        Lose: false


    #All configurations relating to banks.
    Bank:

        #Whether or not banks are enabled.
        Enabled: true

        #Whether or not players can use a command to access their banks.
        Command: true
        
        #Whether or not testificates named "Banker" allow access to player banks.
        NPC: false
        
        #Whether or not players can use bank balances to pay dues when personal balance is short.
        Connected: false

        #How much is costs to open a bank account.
        Cost: 20.0

        #The number of rows a bank has.( minimum is 1, maximum is 6)
        #1 row = 9 slots
        Rows: 3

        #All configurations relating to bank gold interest.
        Interest:

            #Whether or not interest is enabled.
            Enabled: false

            #The interest rate in decimal form.
            Rate: 0.2

            #The interval at which interest is gained(in seconds)
            Interval: 1800
            
            
    #All configurations relating to worlds
    World:
       
       #Whether or not changing worlds costs money
       EnableChangeFee: false
       
       #How much it costs to change worlds if ChangeFee is enabled
       ChangeFee: 5.0


    #All configurations relating to the database
    Database:

        #The database type to use. Current options: FlatFile, MySQL, H2.
        Type: FlatFile
            
        #The prefix to use for TheNewEconomy MySQL and H2 Tables
        Prefix: TNE
        
        #Whether or not to backup your database automatically before converting to newer versions of TNE
        Backup: true

        #All configurations relating to the FlatFile Database
        FlatFile:

            #The file to which all the data will be saved
            File: economy.tne
            
        #All configurations relating to the MySQL Database
        MySQL:
            
            #The MySQL host
            Host: localhost
            
            #The MySQL port
            Port: 3306
            
            #The MySQL database
            Database: TheNewEconomy
            
            #Your MySQL user's name
            User: user
            
            #Your MySQL user's password
            Password: password

        #All configurations relating to the H2 Database
        H2:
            #The H2 Database file.
            File: Economy
            
        #All configurations relating to the SQLite Database
        #No Longer Supported
        #For backwards support purposes only.
        SQLite:
            
            #The SQLite Database File
            File: economy.db
```