const {LCDClient, MnemonicKey, MsgExecuteContract} = require('@terra-money/terra.js');
const http = require('http');
const nodemailer =require('nodemailer');

const sendEmailAboutError = async () => {
    var transport = nodemailer.createTransport({
        host: "smtp-mail.outlook.com", 
        secureConnection: false, 
        port: 587, 
        auth: {
            user: "orca_automation@outlook.com",
            pass: "..."
        },
        tls: {
            ciphers:'SSLv3'
        }
    });

    var mailOptions = {
        from: 'orca_automation@outlook.com',
        to: 'cian.mccann2@gmail.com',
        subject: 'Error occured.',
        text: 'Program is stopped.'
    };

    let test = await transport.sendMail(mailOptions, function(error, info){
        if (error) {
          console.log(error);
        } else {
          console.log('Email sent by sendEmailAboutError().');
        }
    });
}

const sendEmailToSayNewBidCanBePlaced= async () => {
    var transport = nodemailer.createTransport({
        host: "smtp-mail.outlook.com", 
        secureConnection: false, 
        port: 587, 
        auth: {
            user: "orca_automation@outlook.com",
            pass: "..."
        },
        tls: {
            ciphers:'SSLv3'
        }
    });

    var mailOptions = {
        from: 'orca_automation@outlook.com',
        to: 'cian.mccann2@gmail.com',
        subject: 'Ready for new bid.',
        text: 'Fill occured and a bid is either 0 or less than minimumFillWithdrawlAmount.'
    };

    let test = await transport.sendMail(mailOptions, function(error, info){
        if (error) {
          console.log(error);
        } else {
          console.log('Email sent by sendEmailToSayNewBidCanBePlaced().');
        }
    });
}

const getbLunaBalance = async () => {
    const terra = new LCDClient({URL: 'https://columbus-5--lcd--full.datahub.figment.io/apikey/.../', chainID: 'columbus-5'});
    const mnemonic = '...';
    const mk = new MnemonicKey({mnemonic: mnemonic});
    const wallet = terra.wallet(mk);

    const bLunabalanceObject = await terra.wasm.contractQuery("terra1kc87mu460fwkqte29rquh4hc20m54fxwtsx7gp", { balance: { address: mk.accAddress } });
    const bLunaBalance = Object.values(bLunabalanceObject)[0];
    return bLunaBalance;
}

const getbLunaBalanceInUST = async () => {
    const terra = new LCDClient({URL: 'https://columbus-5--lcd--full.datahub.figment.io/apikey/.../', chainID: 'columbus-5'});
    const mnemonic = '...';
    const mk = new MnemonicKey({mnemonic: mnemonic});
    const wallet = terra.wallet(mk);

    const bLunabalanceObject = await terra.wasm.contractQuery("terra1kc87mu460fwkqte29rquh4hc20m54fxwtsx7gp", { balance: { address: mk.accAddress } });
    const bLunaBalance = Object.values(bLunabalanceObject)[0];
    return bLunaBalance/1000000;
}

const getbETHBalance = async () => {
    const terra = new LCDClient({URL: 'https://columbus-5--lcd--full.datahub.figment.io/apikey/.../', chainID: 'columbus-5'});
    const mnemonic = '...';
    const mk = new MnemonicKey({mnemonic: mnemonic});
    const wallet = terra.wallet(mk);

    const bETHbalanceObject = await terra.wasm.contractQuery("terra1dzhzukyezv0etz22ud940z7adyv7xgcjkahuun", { balance: { address: mk.accAddress } });
    const bETHBalance = Object.values(bETHbalanceObject)[0];
    return bETHBalance;
}

const getbETHBalanceInUST = async () => {
    const terra = new LCDClient({URL: 'https://columbus-5--lcd--full.datahub.figment.io/apikey/.../', chainID: 'columbus-5'});
    const mnemonic = '...';
    const mk = new MnemonicKey({mnemonic: mnemonic});
    const wallet = terra.wallet(mk);
    
    const bETHbalanceObject = await terra.wasm.contractQuery("terra1dzhzukyezv0etz22ud940z7adyv7xgcjkahuun", { balance: { address: mk.accAddress } });
    const bETHBalance = Object.values(bETHbalanceObject)[0];
    return bETHBalance/1000000;
}

const getUSTBalance = async () => {
    const terra = new LCDClient({URL: 'https://columbus-5--lcd--full.datahub.figment.io/apikey/.../', chainID: 'columbus-5'});
    const mnemonic = '...';
    const mk = new MnemonicKey({mnemonic: mnemonic});
    const wallet = terra.wallet(mk);

    const balances = await terra.bank.balance(mk.accAddress);
    const balancesArray = balances[0].toString().split(",");
    const index = balancesArray.findIndex(element => element.includes("uusd"));
    const balance = balancesArray[index];
    return balance.replace("uusd", "");;
}

const getUSTBalanceInUST = async () => {
    const terra = new LCDClient({URL: 'https://columbus-5--lcd--full.datahub.figment.io/apikey/.../', chainID: 'columbus-5'});
    const mnemonic = '...';
    const mk = new MnemonicKey({mnemonic: mnemonic});
    const wallet = terra.wallet(mk);

    const balances = await terra.bank.balance(mk.accAddress);
    const balancesArray = balances[0].toString().split(",");
    const index = balancesArray.findIndex(element => element.includes("uusd"));
    const balance = balancesArray[index];
    return balance.replace("uusd", "")/1000000;
}

const get_aUSTBalance = async () => {
    const terra = new LCDClient({URL: 'https://columbus-5--lcd--full.datahub.figment.io/apikey/abca9cbc52bcf6ead40e81e79488988f/', chainID: 'columbus-5'});
    const mnemonic = '...';
    const mk = new MnemonicKey({mnemonic: mnemonic});
    const wallet = terra.wallet(mk);

    const balanceObject = await terra.wasm.contractQuery("terra1hzh9vpxhsk8253se0vv5jj6etdvxu3nv8z07zu", { balance: { address: mk.accAddress } });
    const balance = Object.values(balanceObject)[0];
    return balance;
}

const get_aUSTBalanceRounded = async () => {
    const terra = new LCDClient({URL: 'https://columbus-5--lcd--full.datahub.figment.io/apikey/abca9cbc52bcf6ead40e81e79488988f/', chainID: 'columbus-5'});
    const mnemonic = '...';
    const mk = new MnemonicKey({mnemonic: mnemonic});
    const wallet = terra.wallet(mk);

    const balanceObject = await terra.wasm.contractQuery("terra1hzh9vpxhsk8253se0vv5jj6etdvxu3nv8z07zu", { balance: { address: mk.accAddress } });
    const balance = Object.values(balanceObject)[0];
    return balance/1000000;
}

const swapAllbLunaToUST = async (swapUSTToaUSTAfter) => {
    const terra = new LCDClient({URL: 'https://columbus-5--lcd--full.datahub.figment.io/apikey/.../', chainID: 'columbus-5'});
    const mnemonic = '...';
    const mk = new MnemonicKey({mnemonic: mnemonic});
    const wallet = terra.wallet(mk);

    const bLunabalanceObject = await terra.wasm.contractQuery("terra1kc87mu460fwkqte29rquh4hc20m54fxwtsx7gp", { balance: { address: mk.accAddress } });
    const bLunaBalance = Object.values(bLunabalanceObject)[0];

    const { assets } = await terra.wasm.contractQuery("terra1qpd9n7afwf45rkjlpujrrdfh83pldec8rpujgn", { pool: {} });
    const beliefPrice = (assets[0].amount / assets[1].amount).toFixed(18);

    const msg = { 
        "execute_swap_operations":{
            "max_spread": "0.02",
            "belief_price": beliefPrice,
            "operations":[
               {
                  "terra_swap":{
                        "offer_asset_info":{
                            "token":{
                                "contract_addr":"terra1kc87mu460fwkqte29rquh4hc20m54fxwtsx7gp"
                            }
                        },
                        "ask_asset_info":{
                            "native_token":{
                                "denom":"uluna"
                            }
                        }
                    }
               },
               {
                    "native_swap":{
                        "offer_denom":"uluna",
                        "ask_denom":"uusd"
                    }
               }
            ]
        }
    };

    const terraSwap = new MsgExecuteContract(
        "terra1qxv6uyan8u0a88zemfcqffdtfqq9fxy575cwgq",
        "terra1kc87mu460fwkqte29rquh4hc20m54fxwtsx7gp",
        {
            "send": {
                "contract": "terra19qx5xe6q9ll4w0890ux7lv2p4mf3csd4qvt3ex",
                "amount": bLunaBalance,
                "msg": "eyA...CAgICAgIF0KICAgICAgICB9CiAgICB9"           
            }
        }
    );
    
    var result;
    try {
        console.log("Creating tx.");
        const tx = await wallet.createAndSignTx({ msgs: [terraSwap], denoms: "uusd", gasPrices: { uusd: 0.15 }});
        console.log("Broadcasting tx.");
        result = await terra.tx.broadcast(tx);
        if (result.code != 0) {
            console.log(result);
            console.log("Transaction did not execute. Error is above.");
            sendEmailAboutError();
            return "Transaction did not execute. Code != 0.";
        } else {
            if (swapUSTToaUSTAfter) {
                var ustReceived = "";
                var rawLog = result.raw_log.split(",");
                for (var i = 1; i < rawLog.length; i++) {
                    if (rawLog[i-1].includes("swap_coin") && rawLog[i].includes("uusd")) {
                        ustReceived = rawLog[i].replace(/[^0-9]/g, '');
                        break;
                    }
                }
                if (ustReceived != "") {
                    const txHash = await swapUSTToaUST(ustReceived);
                    return txHash;
                } else {
                    console.log("Error at swapAllbLunaToUST() -> swapUSTToaUST()");
                }
                
            } else {
                console.log("Successful transaction, getting txHash");
                const txHash = await terra.tx.hash(tx);
                return txHash;
            }
        }
    } catch (error) {
        console.log(error);
        console.log("Transaction did not execute. Error is above.");
        return "Transaction did not execute. " + error;
    }
}

const swapAllbETHToUST = async (swapUSTToaUSTAfter) => {
    const terra = new LCDClient({URL: 'https://columbus-5--lcd--full.datahub.figment.io/apikey/.../', chainID: 'columbus-5'});
    const mnemonic = '...';
    const mk = new MnemonicKey({mnemonic: mnemonic});
    const wallet = terra.wallet(mk);

    const bETHbalanceObject = await terra.wasm.contractQuery("terra1dzhzukyezv0etz22ud940z7adyv7xgcjkahuun", { balance: { address: mk.accAddress } });
    const bETHBalance = Object.values(bETHbalanceObject)[0];

    const { assets } = await terra.wasm.contractQuery("terra1c0afrdc5253tkp5wt7rxhuj42xwyf2lcre0s7c", { pool: {} });
    const beliefPrice = (assets[0].amount / assets[1].amount).toFixed(18);

    const msg = { 
        "execute_swap_operations": { 
            "max_spread": "0.02",
            "belief_price": beliefPrice,
            "operations": 
            [ 
                { 
                    "terra_swap": 
                    { 
                        "offer_asset_info": 
                        { 
                            "token": 
                            { 
                                "contract_addr": "terra1dzhzukyezv0etz22ud940z7adyv7xgcjkahuun" 
                            } 
                        }, 
                        "ask_asset_info": 
                        {
                            "native_token": 
                            { 
                                "denom": "uusd" 
                            } 
                        } 
                    } 
                }   
            ] 
        }
    };

    const terraSwap = new MsgExecuteContract(
        mk.accAddress,
        "terra1dzhzukyezv0etz22ud940z7adyv7xgcjkahuun",
        {
            "send": {
                "contract": "terra19qx5xe6q9ll4w0890ux7lv2p4mf3csd4qvt3ex",
                "amount": bETHBalance,
                "msg": "eyAKICAgICAgICAiZXhlY3V0ZV9zd2FwX29wZXJhdGlvbnMiOiB7IAogICAgICAgICAgICA...CAgICAgICAgICAgXSAKICAgICAgICB9CiAgICB9"           
            }
        }
    );
    
    var result;
    try {
        const tx = await wallet.createAndSignTx({ msgs: [terraSwap], denoms: "uusd", gasPrices: { uusd: 0.15 }});
        result = await terra.tx.broadcast(tx);
        if (result.code != 0) {
            console.log(result);
            console.log("Transaction did not execute. Error is above.");
            return "Transaction did not execute. Code != 0.";
        } else {
            if (swapUSTToaUSTAfter) {
                var ustReceived = "";
                var rawLog = result.raw_log.split(",");
                for (var i = 1; i < rawLog.length; i++) {
                    if (rawLog[i-1].includes("return_amount") && rawLog[i].includes("uusd")) {
                        ustReceived = rawLog[i].replace(/[^0-9]/g, '');
                        break;
                    }
                }
                if (ustReceived != "") {
                    const txHash = await swapUSTToaUST(ustReceived);
                    return txHash;
                } else {
                    console.log("Error at swapAllbETHToUST() -> swapUSTToaUST()");
                }
                
            } else {
                const txHash = await terra.tx.hash(tx);
                return txHash;
            }
        }
    } catch (error) {
        console.log(error);
        console.log("Transaction did not execute. Error is above.");
        return "Transaction did not execute. " + error;
    }
}

const swapUSTToaUST = async (ustAmountToSwap) => {
    const terra = new LCDClient({URL: 'https://columbus-5--lcd--full.datahub.figment.io/apikey/.../', chainID: 'columbus-5'});
    const mnemonic = '...';
    const mk = new MnemonicKey({mnemonic: mnemonic});
    const wallet = terra.wallet(mk);

    // const ustBalance = await getUSTBalance();

    const { assets } = await terra.wasm.contractQuery("terra1z50zu7j39s2dls8k9xqyxc89305up0w7f7ec3n", { pool: {} });
    const beliefPrice = (assets[1].amount / assets[0].amount).toFixed(18);

    const terraSwap = new MsgExecuteContract(
        mk.accAddress,
        "terra1z50zu7j39s2dls8k9xqyxc89305up0w7f7ec3n",
        {
          swap: {
            offer_asset: { 
              amount: ustAmountToSwap, 
              info: { 
                native_token: { 
                  denom: "uusd" 
                } 
              } 
            },
          },
        },
        { uusd: ustAmountToSwap }
    );
    
    var result;
    try {
        const tx = await wallet.createAndSignTx({ msgs: [terraSwap], denoms: "uusd", gasPrices: { uusd: 0.15 }});
        result = await terra.tx.broadcast(tx);
        if (result.code != 0) {
            console.log(result);
            console.log("Transaction did not execute. Error is above.");
            return "Transaction did not execute. Code != 0.";
        } else {
            const txHash = await terra.tx.hash(tx);
            return txHash;
        }
    } catch (error) {
        console.log(error);
        console.log("Transaction did not execute. Error is above.");
        return "Transaction did not execute. " + error;
    }
}


const server = http.createServer();

server.on('request', async (req, res) => {
    let date_ob = new Date();
    console.log("\n" + date_ob);
    console.log("Request received.");
    const functionToExecute = req.url.replace('/','');

    var response;
    if (functionToExecute == "getbLunaBalance") {
        console.log("Calling getbLunaBalance().");
        response = await getbLunaBalance();
    } else if (functionToExecute == "getbLunaBalanceInUST") {
        console.log("Calling getbLunaBalanceInUST().");
        response = await getbLunaBalanceInUST();
    } else if (functionToExecute == "getbETHBalance") {
        console.log("Calling getbETHBalance().");
        response = await getbETHBalance();
    } else if (functionToExecute == "getbETHBalanceInUST") {
        console.log("Calling getbETHBalanceInUST().");
        response = await getbETHBalanceInUST();
    } else if (functionToExecute == "getUSTBalance") {
        console.log("Calling getUSTBalance().");
        response = await getUSTBalance();
    } else if (functionToExecute == "getUSTBalanceInUST") {
        console.log("Calling getUSTBalanceInUST().");
        response = await getUSTBalanceInUST();
    }else if (functionToExecute == "get_aUSTBalance") {
        console.log("Calling get_aUSTBalance().");
        response = await get_aUSTBalance();
    } else if (functionToExecute == "get_aUSTBalanceRounded") {
        console.log("Calling get_aUSTBalanceRounded().");
        response = await get_aUSTBalanceRounded();
    } else if (functionToExecute == "swapAllbLunaToUST") {
        console.log("Calling swapAllbLunaToUST().");
        response = await swapAllbLunaToUST(true);
        response = await swapAllbLunaToUST(false);
    } else if (functionToExecute == "swapAllbETHToUST") {
        console.log("Calling swapAllbETHToUST().");
        response = await swapAllbETHToUST(true);
        response = await swapAllbETHToUST(false);
    } else if (functionToExecute == "sendEmailAboutError") {
        console.log("Calling sendEmailAboutError().");
        sendEmailAboutError();
        response =  "sendEmailAboutError() was executed.";
    } else if (functionToExecute == "sendEmailToSayNewBidCanBePlaced") {
        console.log("Calling sendEmailToSayNewBidCanBePlaced().");
        sendEmailToSayNewBidCanBePlaced();
        response =  "sendEmailToSayNewBidCanBePlaced() was executed.";
    }

    // Send response
    console.log("Sending response.");
    res.setHeader("Content-Type", "text/plain");
    res.end(response.toString());
});

server.listen(3000, 'localhost', () => {
    console.log("Listening for requests...");
});