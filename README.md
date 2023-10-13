### Terra Blockchain
Terra is an open-source blockchain payment platform for an algorithmic stablecoin, which is a cryptocurrency that automatically tracks the price of currencies or other assets. The Terra blockchain enables users to instantly spend, save, trade, or exchange Terra stablecoins. Overall, Terra aims to bridge the gap between traditional finance and the world of cryptocurrencies, offering a stable and efficient means of transacting value on the blockchain.

### Terra.js
Terra.js is a JavaScript SDK for writing applications that interact with the Terra blockchain from either Node.js, the browser, or React Native environments, providing simple abstractions over core data structures, serialization, key management, and API request generation.

Features:
- Written in TypeScript, with type definitions.
- Versatile support for key management solutions.
- Works in Node.js, the browser, and React Native.
- Exposes the Terra API through LCDClient.
- Parses responses into native JavaScript types.

### Orca dAPP
ORCA is the world's first public marketplace for liquidated collateral. Using Orca, you can participate in buying assets at a discount. Orca was originally launched as a dApp on Terra before Terra collapsed. At the time, it offered 3 assets: bEth, bLuna, and sAVAX. Orca offered a way to pick up bETH, bLuna, and sAVAX at discounts of up to 30%. 
[Orca Website](https://orca.kujira.network/)

# This Repository
I loved using Orca and found it functioned as the developers intended. Back then, Orca would fill all bids looking for a 4% discount on an asset like bLuna multiple times a week. Bids looking for a discount of 20% or more were rarely getting filled; how often bids were filled would depend on market volatility. I identified a profit opportunity - if I could pick up an asset like bLuna at a discount of 4% and immediately sell it back to UST (the equivalent of USD on Terra), I could profit from the discount of around 4%. The problem was that Orca's functionality ended when the bid was filled. Mainly through Selenium in Java, Terra.js, and Node.js, I was able to craft an application that, when started, would:
- Make a record of all bids placed for all assets, the size of each bid in UST/USD, and their selected discount.
- Monitor all bids to check if a bid was filled.
- If a bid of the correct size was filled, withdraw the asset acquired at a discount from Orca to my Terra wallet.
- Swap that asset in my Terra wallet back into UST/USD though the TerraSwap exchange router to profit from the discount.
- Calculate and display all information about the bid, transaction, and profit made/lost.
- Continue to run until all bids on each asset are filled, while displaying a UI element/JFrame that allowed for bids to be reset without having to stop the program.

Note: I have only included source files in this repository; this repository does not include packages and libraries. The Terra blockchain essentially collapsed in May of 2022. The Orca dApp is still available but has since migrated to the Kujira ecosystem. Any code in this repository needs to be updated to reflect these updates. The Orca UI has changed, and they no longer offer discounts on assets like bLuna and bEth.
