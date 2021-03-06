Message Configurations
=========================

File
--------------
messages.yml

Configuration
--------------
```YAML
#All configurable messages in TNE
# Colour characters are supported, just use the colour code preceded by an ampersand character('&')
# An alternate colouring format is also viable, just type out the name of the colour inside a less than and greater than symbol
# 
# Colour chart
# Char  |   Alt
# ------|-----------
#  &b   |  <aqua>
#  &0   |  <black>
#  &9   |  <blue>
#  &3   |  <dark_aqua>
#  &1   |  <dark_blue>
#  &8   |  <dark_gray>
#  &2   |  <dark_green>
#  &5   |  <dark_purple>
#  &4   |  <dark_red>
#  &6   |  <gold>
#  &7   |  <gray>
#  &a   |  <green>
#  &d   |  <purple>
#  &c   |  <red>
#  &f   |  <white>
#  &e   |  <yellow>
#  &k   |  <magic>
#  &l   |  <bold>
#  &o   |  <italic>
#  &r   |  <reset>
#  &m   |  <strike>
#  &n   |  <underline>

Messages:
    
    Command:
       Unable: <red>I'm sorry, but you're not allowed to use that command.
       None: <yellow>Command $command $arguments could not be found! Try using $command help.
       InActive: <red>Command $command $arguments has been deactivated! Try using $command help.
       Charge: <white>You have been charged <gold>$amount<white> for using $command.
       
    Inventory:
       Charge: <white>You have been charged <gold>$amount<white> for opening inventory of type "$type."
       NoTime: <white>You have run out of time for using inventory of type "$type."
       TimeRemoved: <white>You used $amount of time for inventory of type "$type."
       
    Credit:
        Empty: <white>You currently have no inventory time credits for type "$type."
       
    Package:
        Empty: <white>No packages to display for inventory of type "$type."
        None: <white>Package "$name" does not exist for inventory of type "$type."
        Bought: <white> Successfully purchased package "$name" for inventory of type "$type."
        Unable: <red>I'm sorry, but you have no time credits for inventory of type "$type."
    
    General:
       NoPerm: <red>I'm sorry, but you do not have permission to do that.
       Saved: <yellow>Successfully saved all TNE Data!
       NoPlayer: <red>Unable to locate player "$player"!

    Item:
      Invalid: <red>Invalid item name and/or durability combination entered.
      InvalidAmount: <red>Invalid item amount entered.
       
    Admin:
       NoBalance: <red>$player has no balance data for the world "$world"!
       NoBank: <red>$player has no bank data for the world "$world"!
       Balance: <white>$player currently has <gold>$amount <white>for world "$world"!
       ID: <white>The UUID for $player is $id.
       Exists: <red>A player with that name already exists.
       Created: <white>Successfully created account for $player.
       Purge: <white>Successfully purged all economy accounts.
       PurgeWorld: <white>Successfully purged economy accounts in $world.

    Account:
       Locked: <red>You can't do that with a locked account($player)!
       Set: <yellow>You must use /pin set before accessing your money and/or bank.
       Confirm: <yellow>You must use /pin confirm before accessing your money and/or bank.
       
    Pin:
       Set: <white>Your pin has been set successfully.
       Confirmed: <white>Your pin has been confirmed successfully.
       Already: <white>Your pin has already been confirmed.
    
    Money:
       Given: <white>You were given <gold>$amount<white>.
       Received: <white>You were paid <gold>$amount <white> by <white> $from.
       Taken: <white>$from took <gold>$amount<white> from you.
       Insufficient: <red>I'm sorry, but you do not have <gold>$amount<red>.
       Balance: <white>You currently have <gold>$amount<white> on you.
       Gave: <white>Successfully gave $player <gold>$amount<white>.
       Set: <white>Successfully set $player's balance to <gold>$amount<white>.
       Paid: <white>Successfully paid $player <gold>$amount<white>.
       Took: <white>Successfully took <gold>$amount<white> from $player.
       Negative: <red>Amount cannot be a negative value!
       SelfPay: <red>You can't pay yourself!
       NoPins: <red>Pins are disabled in this world!

    Auction:
       Start: <white>Auction has started for $item. Starting bid is <gold>$start<white>.
       Return: <white>Your items have been returned.
       FailedReturn: <white>Auctioned items have been returned due to bidder's insufficient funds.
       Won: <white>Congratulations! You won the auction for $item.
       Paid: <white>You received <gold>$amount<white> from your recent auction.
       Under: <red>Sorry, but your bid is under the minimum bid of <gold>$amount<red>.
       AntiSnipe: <white>[<green>AntiSnipe<white>]$time seconds has been added to the auction.
       Submitted: <white>Your bid of <gold>$amount <white>has been submitted for lot <green>$lot<white>.
       Bid: <white>$player has raised the auction bid to <gold>$amount<white>.
       Winner: <white>$player has won the auction for <gold>$amount<white>.
       NoWinner: <white>Auction has ended with no bidders.
       LotRequire: <red>You must specify a lot number!
       BidRequire: <red>You must enter a valid bid!
       NotActive: <red>Lot $lot is not currently up for auction.
       None: <red>There is no auction with the lot number $lot.
       End: <white>Successfully ended auction for lot <green>$lot<white>.
       NoClaim: <red>You don't have any unclaimed auction items for lot $lot.
       Claimed: <white>You have claimed your items for lot $lot.
       NoCancel: <red>You can't cancel an auction that has bids.
       Cancelled: <white>You have cancelled the auction for lot <green>$lot<white>.
       InvalidItem: <red>The item you tried auctioning is invalid.
       NoItem: <red>You do not have $amount of item "$item".
       PersonalQueue: <red>You have reached the maximum queue allowance.
       MaxQueue: <red>The auction queue is currently full.
       Queued: <white>Your auction has been added to the queue as lot <green>$lot<white>.

    Shop:
       BuyLimit: <red>Shop has reached its buy limit for this item.
       NoStock: <red>The item you wish to purchase is currently out of stock.
       NoTrade: <red>This shop currently no trade option for that item.
       NoBuy: <red>This shop currently has no buy option for that item.
       FundsLack: <red>This shop is lacking funds to purchase any more items.
       Shoppers: <red>This shop currently has the maximum number of shoppers!
       ShareNone: <red>Shop profit sharing is diabled in this world!
       ShareMax: <red>You have the max number of players profit sharing for this shop!
       Disabled: <red>Shops are disabled in this world!
       Max: <red>You already own the maximum number of shops allowed!
       Long: <red>Shop names must be no larger than 16 characters long!
       None: <red>A shop with that name doesn't exist!
       Already: <red>A shop with that name already exists!
       Created: <white> Successfully created shop "$shop".
       Permission: <red>You must be the owner of this shop to perform that action!
       WhitelistAdded: <white>Successfully added $player to your shop's whitelist.
       WhitelistRemoved: <white>Successfully removed $player from your shop's whitelist.
       BlacklistAdded: <white>Successfully added $player to your shop's blacklist.
       BlacklistRemoved: <white>Successfully removed $player from your shop's blacklist.
       Visible: <white>The shop "$shop" is no longer hidden.
       Hidden: <white>The shop "$shop" is now hidden.
       MustWhitelist: <red>You must be whitelisted to view this shop!
       ShareAdmin: <red>You can't profit share administrator shops!
       ShareGreater: <red>You can't share more than 100% of profits!
       ShareAdded: <red>Added player "$player" to the shop's profit sharing.
       ShareRemoved: <red>Removed player "$player" from the shop's profit sharing.
       ClosedBrowse: <white>The shop you were browsing has been closed!
       Closed: <white>The shop "$shop" has been closed!
       ItemNone: <white>The item "$item" could not been found in the shop "$shop".
       ItemRemoved: <white>The item "$item" has been removed from the shop "$shop".
       ItemAdded: <white>The item "$item" has been added to the shop "$shop".
       StockModified: <white>Successfully $value $amount of item "$item" for shop "$shop".
       ItemWrong: <white>Something went wrong adding item "$item" to the shop "$shop".
       ItemInvalid: <white>The item name "$item" is invalid.
       ItemTrade: <white>The trade item name "$item" is invalid.
       NotEnough: <white>You do not have $amount of item "$item".
       InvalidAmount: <white>Invalid item amount value entered.
       InvalidStock: <white>Invalid item initial stock value entered.
       InvalidTradeAmount: <white>Invalid trade item amount value entered.
       InvalidTrade: <white>The trade item name "$item" is invalid.
       InvalidCost: <white>Invalid cost format entered.
        
    
    Bank:
       Added: <white>$player has been added to your bank!
       Removed: <white>$player has been removed from your bank!
       Already: <red>You already own a bank!
       Bought: <white>Congratulations! You have successfully purchased a bank!
       Insufficient: <red>I'm sorry, but you need at least <gold>$amount<red> to create a bank.
       Overdraw: <red>I'm sorry, but the bank of $name bank does not have <gold>$amount<red>.
       None: <red>I'm sorry, but you do not own a bank. Please try /bank buy to buy one.
       NoNPC: <red>I'm sorry, but accessing banks via NPCs has been disabled in this world!
       NoSign: <red>I'm sorry, but accessing banks via signs has been disabled in this world!
       NoCommand: <red>I'm sorry, but accessing banks via /bank has been disabled in this world!
       Disabled: <red>I'm sorry, but banks are disabled in this world.
       Balance: <white>There is currently <gold>$amount<white> in the bank of $name.
       Deposit: <white>You have deposited <gold>$amount<white> into the bank of $name.
       Cost: <white>A bank is currently <gold>$amount<white>.
       Invalid: <red>I'm sorry, but you don't have access to the bank of $name!
       
    Objects:
       SignDisabled: <red>This type of sign has been disabled for this world!
       SignUse: <white>You were charged "<gold>$amount<white>" for using this sign.
       SignPlace: <white>You were charged "<gold>$amount<white>" for placing this sign.
       CraftingCharged: <white> You were charged <gold>$amount<white> for crafting $stack_size "$item".
       CraftingPaid: <white> You were given <gold>$amount<white> for crafting $stack_size "$item".
       SmeltingCharged: <white> You were charged <gold>$amount<white> for smelting $stack_size "$item".
       SmeltingPaid: <white> You were given <gold>$amount<white> for smelting $stack_size "$item".
       EnchantingCharged: <white> You were charged <gold>$amount<white> for enchanting $stack_size "$item".
       EnchantingPaid: <white> You were given <gold>$amount<white> for enchanting $stack_size "$item".
       MiningCharged: <white> You were charged <gold>$amount<white> for mining one "$name" block.
       MiningPaid: <white> You were given <gold>$amount<white> for mining one "$name" block.
       PlacingCharged: <white> You were charged <gold>$amount<white> for placing one "$name" block.
       PlacingPaid: <white> You were given <gold>$amount<white> for placing one "$name" block.
       ItemUseCharged: <white> You were charged <gold>$amount<white> for using item "$name".
       ItemUsePaid: <white> You were given <gold>$amount<white> for using item "$name".
       
    Mob:
       Killed: <white>You received $reward <white>for killing a <green>$mob<white>.
       KilledVowel: <white>You received $reward <white>for killing an <green>$mob<white>.
       NPCTag: <red>I'm sorry, but you cannot use a name tag on a villager.
    
    World:
       Change: <red>You have been charged <gold> $amount<red> for changing worlds.
       ChangeFailed: <red>I'm sorry, but you need at least <gold>$amount<red> to change worlds.
```