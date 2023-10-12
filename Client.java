package orca_automation_client;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Client {
	
	public static WebDriver driver = null;
	
	public static ArrayList<String> coins = new ArrayList<String>();
	public static ArrayList<JCheckBox> coinCheckboxes = new ArrayList<JCheckBox>();
	public static HashMap<String, Float> fillRemainingInitialMap = new HashMap<String, Float>();
	public static HashMap<String, Float> balanceThatCanBeWithdrawnInitialMap = new HashMap<String, Float>();
	public static HashMap<String, Float> untransactedFillTotalMap = new HashMap<String, Float>();
	
	public static float walletBalanceUSTInitial;
	
	static String fillInfoMessage = "";

	public static void main(String[] args) throws InterruptedException, AWTException, IOException {
		// Create panel for resetting bids
		JFrame frame = createJFrame();
		JPanel panel = createJPanel();
        JCheckBox bLunaCheckbox = createCheckbox("bLuna");
        JCheckBox bETHCheckbox = createCheckbox("bETH");
        JCheckBox sAVAXCheckbox = createCheckbox("sAVAX");
        panel.add(bLunaCheckbox);
        panel.add(bETHCheckbox);  
        panel.add(sAVAXCheckbox); 
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
        
		// Start browser with Terra Station with tabs extension installed and create the log file
		ChromeOptions options = new ChromeOptions();
		options.addExtensions(new File("terrastation.crx"));
		options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
		options.addArguments("--disable-notifications");
		DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setCapability(ChromeOptions.CAPABILITY, options);
		options.merge(capabilities);
		driver = new ChromeDriver(options);
		driver.manage().window().maximize();
		createFreshLogFile();
		
		// Settings
		coins.add("bLuna");
        coinCheckboxes.add(bLunaCheckbox);
		coins.add("bETH");
        coinCheckboxes.add(bETHCheckbox);
        coins.add("sAVAX");
        coinCheckboxes.add(sAVAXCheckbox);
		int minimumFillExecutionAmount = 70;
		
		// Connect wallet to ORCA and get initial UST balance
		driver.navigate().to("https://orca.kujira.app/markets/terra/anchor/bLuna");
		waitForORCAPageLoad();
		waitForORCAWalletConnection();
		walletBalanceUSTInitial = Float.parseFloat(executeServerFunction("getUSTBalanceInUST"));
		
		// Initialize starting values 
		for (String coin : coins) { 		      
			switchPageTo(coin);
			fillRemainingInitialMap.put(coin, getFillRemaining());
			balanceThatCanBeWithdrawnInitialMap.put(coin, getBalanceThatCanBeWithdrawn());
			untransactedFillTotalMap.put(coin, 0F);
	    }
		
		// Print starting values 
		for( String coin : coins) { 
			System.out.println(coin + " fillRemainingInitial = " + fillRemainingInitialMap.get(coin));
			System.out.println(coin + " balanceThatCanBeWithdrawnInitial = " + balanceThatCanBeWithdrawnInitialMap.get(coin));
	    }
		System.out.println("walletBalanceUSTInitial = " + walletBalanceUSTInitial);

		boolean triedRefreshRecovery = false;
		while (true) {
			try {
				String coinPage = getCurrentCoinPage();
				
				float fillRemainingInitial = (float) 0.0;
				fillRemainingInitial = fillRemainingInitialMap.get(coinPage);
				float fillRemainingCheck = getFillRemaining();
				if (fillRemainingCheck > fillRemainingInitial) {
					fillRemainingInitialMap.replace(coinPage, fillRemainingCheck);
					fillRemainingInitial = fillRemainingCheck;
				}
				
				float balanceThatCanBeWithdrawnInitial = (float) 0.0;
				balanceThatCanBeWithdrawnInitial = balanceThatCanBeWithdrawnInitialMap.get(coinPage);
				float balanceThatCanBeWithdrawnCheck = getBalanceThatCanBeWithdrawn();
				
				if ((fillRemainingInitial != -1 && fillRemainingInitial != 0  && fillRemainingCheck != -1.0 && fillRemainingCheck != 0.0) 
						|| fillRemainingInitial != -1 && fillRemainingInitial != 0 && fillRemainingCheck == 0.0 && balanceThatCanBeWithdrawnCheck > balanceThatCanBeWithdrawnInitial) {
							
					String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
					System.out.println("\nChecking for " + coinPage + " fill - " + timeStamp + ".");
					System.out.println("fillRemainingInitial = " + fillRemainingInitial + ".");
					System.out.println("fillRemainingCheck = " + fillRemainingCheck + ".");
					System.out.println("balanceThatCanBeWithdrawnInitial = " + balanceThatCanBeWithdrawnInitial + ".");
					System.out.println("balanceThatCanBeWithdrawnCheck = " + balanceThatCanBeWithdrawnCheck + ".");
		
					boolean fillOccurred = fillRemainingInitial != fillRemainingCheck && balanceThatCanBeWithdrawnCheck > balanceThatCanBeWithdrawnInitial;
					if (fillOccurred) {
						fillInfoMessage = "\n\nChecking for " + coinPage + " fill - " + timeStamp + "." +
								"\nfillRemainingInitial = " + fillRemainingInitial + "." +
									"\nfillRemainingCheck = " + fillRemainingCheck + "." +
										"\nbalanceThatCanBeWithdrawnInitial = " + balanceThatCanBeWithdrawnInitial + "." +
											"\nbalanceThatCanBeWithdrawnCheck = " + balanceThatCanBeWithdrawnCheck + ".";	
						
						System.out.println("Fill occurred. ");
						fillInfoMessage = fillInfoMessage + "\nFill occurred. ";
						
						boolean executeOnFill = false;
						float fillSize = fillRemainingInitial-fillRemainingCheck;
						if (fillSize >= minimumFillExecutionAmount) {
							System.out.println("fillSize >= minimumFillWithdrawlAmount. Executing on fill.");
							fillInfoMessage = fillInfoMessage + "\nfillSize >= minimumFillWithdrawlAmount. Executing on fill.";
							executeOnFill = true;
						} else {
							float untransactedFillTotal = untransactedFillTotalMap.get(coinPage);
							untransactedFillTotal = untransactedFillTotal + fillSize;
							if (untransactedFillTotal > minimumFillExecutionAmount) {
								System.out.println("untransactedFillTotal > minimumFillWithdrawlAmount now. Executing on fill.");
								fillInfoMessage = fillInfoMessage + "\nuntransactedFillTotal > minimumFillWithdrawlAmount now. Executing on fill.";
								executeOnFill = true;
							}
						}
						if (executeOnFill) {
							walletBalanceUSTInitial = Float.parseFloat(executeServerFunction("getUSTBalanceInUST"));
							System.out.println("Withdrawing " + coinPage + " from ORCA.");
							fillInfoMessage = fillInfoMessage + "\nWithdrawing " + coinPage + " from ORCA.";
							withdrawFromORCA();
							
							System.out.println("Fill was for " + fillSize + "UST worth of " + coinPage + ". Triggering sell.");
							fillInfoMessage = fillInfoMessage + "\nFill was for " + fillSize + "UST worth of " + coinPage + ". Triggering sell.";
							
							boolean sellTransactionComplete = false;
							String serverResult = executeServerFunction("swapAll" + coinPage + "ToUST");
							if (serverResult.contains("Transaction did not execute.") && !serverResult.contains("Error: Request failed with status code 500")) { 
								// Should be really checking message: 'timed out waiting for tx to be included in a block', but could I increase the timeout?
								System.out.println(serverResult); 
								fillInfoMessage = fillInfoMessage + "\n" + serverResult + ".";
								System.out.println("Transaction will try to be completed again next time."); 
								fillInfoMessage = fillInfoMessage + "\nTransaction will try to be completed again next time.";
							} else {
								sellTransactionComplete = true;
								System.out.println("Transaction hash = " + serverResult + ".");
								fillInfoMessage = fillInfoMessage + "\nTransaction hash = " + serverResult + ".";
							}
							
							if (sellTransactionComplete) {
								float walletBalanceUSTNew = Float.parseFloat(executeServerFunction("getUSTBalanceInUST"));
								float walletBalanceChange = walletBalanceUSTNew - walletBalanceUSTInitial;
								float walletBalanceProfitFromFill = walletBalanceChange - fillSize;
								
								System.out.println("walletBalanceUSTInitial = " + walletBalanceUSTInitial + ".");
								fillInfoMessage = fillInfoMessage + "\nwalletBalanceUSTInitial = " + walletBalanceUSTInitial + ".";
								System.out.println("walletBalanceUSTNew = " + walletBalanceUSTNew + ".");
								fillInfoMessage = fillInfoMessage + "\nwalletBalanceUSTNew = " + walletBalanceUSTNew + ".";
								System.out.println("After that " + fillSize + "UST fill. My UST balance changed by " + walletBalanceChange + ".");
								fillInfoMessage = fillInfoMessage + "\nAfter that " + fillSize + "UST fill. My UST balance changed by " + walletBalanceChange + ".";
								System.out.println("Therefore, the profit on this fill (with fees disregarded) was " + walletBalanceProfitFromFill + "UST.");
								fillInfoMessage = fillInfoMessage + "\nTherefore, the profit on this fill (with fees disregarded) was " + walletBalanceProfitFromFill + "UST.";

								System.out.println("Note: For this " + fillSize + "UST worth of " + coinPage + " fill, " + untransactedFillTotalMap.get(coinPage) + "UST of that fill was previously untransacted.");	
								fillInfoMessage = fillInfoMessage + "\nNote: For this " + fillSize + "UST worth of " + coinPage + " fill, " + untransactedFillTotalMap.get(coinPage) + "UST of that fill was previously untransacted.";
								
								System.out.println("Updating " + coinPage + " untransactedFillTotal to 0.0.");
								fillInfoMessage = fillInfoMessage + "\nUpdating " + coinPage + " untransactedFillTotal to 0.0.";
								untransactedFillTotalMap.replace(coinPage, 0.0F);
								
								System.out.println("Updating " + coinPage +  " balanceThatCanBeWithdrawnInitial to 0.0.");
								fillInfoMessage = fillInfoMessage + "\nUpdating " + coinPage +  " balanceThatCanBeWithdrawnInitial to 0.0.";
								balanceThatCanBeWithdrawnInitialMap.replace(coinPage, 0.0F);
							}		
						} else {
							System.out.println("Fill was only for " + fillSize + "UST worth of " + coinPage + ". Not withdrawing.");
							fillInfoMessage = fillInfoMessage + "Fill was only for " + fillSize + "UST worth of " + coinPage + ". Not withdrawing.";
							
							System.out.println(coinPage + "  untransactedFillTotal was " + untransactedFillTotalMap.get(coinPage) + "UST.");
							fillInfoMessage = fillInfoMessage + "\n" + coinPage + " untransactedFillTotal was " + untransactedFillTotalMap.get(coinPage) + "UST.";
							untransactedFillTotalMap.replace(coinPage, untransactedFillTotalMap.get(coinPage) + fillSize);
							System.out.println(coinPage + " untransactedFillTotal is now " + untransactedFillTotalMap.get(coinPage) + "UST.");
							fillInfoMessage = fillInfoMessage + "\n" + coinPage + " untransactedFillTotal is now " + untransactedFillTotalMap.get(coinPage) + "UST.";
							
							System.out.println("Updating " + coinPage + " balanceThatCanBeWithdrawnInitial  to " + balanceThatCanBeWithdrawnCheck + ".");
							fillInfoMessage = fillInfoMessage + "\nUpdating " + coinPage + " balanceThatCanBeWithdrawnInitial  to " + balanceThatCanBeWithdrawnCheck + ".";
							balanceThatCanBeWithdrawnInitialMap.replace(coinPage, balanceThatCanBeWithdrawnCheck);
						}
						
						float fillRemainingAfterFill = getFillRemaining();
						if (fillRemainingAfterFill < minimumFillExecutionAmount) {
							System.out.println("fillRemainingAfterFill is less than minimumFillWithdrawlAmount. Updating " + coinPage +  " fillRemainingInitial to 0.0.");
							fillInfoMessage = fillInfoMessage + "\nfillRemainingAfterFill is less than minimumFillWithdrawlAmount. Updating " + coinPage +  " fillRemainingInitial to 0.0.";
							fillRemainingInitialMap.replace(coinPage, 0.0F);
							executeServerFunction("sendEmailToSayNewBidCanBePlaced");
							writeToLogFile(fillInfoMessage);
							System.exit(0);
						} else {
							System.out.println("Updating " + coinPage  + " fillRemainingInitial to " + fillRemainingAfterFill);
							fillInfoMessage = fillInfoMessage + "\nUpdating " + coinPage  + " fillRemainingInitial to " + fillRemainingAfterFill;
							fillRemainingInitialMap.replace(coinPage, fillRemainingAfterFill);
						}
						
						writeToLogFile(fillInfoMessage);
						fillInfoMessage = "";
					} else {
						System.out.println("No new fill has occurred.");
					}		
				}
				
				
				boolean switchedToNextPage = false;
				int currentPageIndex = coins.indexOf(coinPage);
				int nextPageIndex;
				if (currentPageIndex == coins.size()-1) {
					nextPageIndex = 0;
				} else {
					nextPageIndex = currentPageIndex + 1;
				}
				while (!switchedToNextPage) {
					if (fillRemainingInitialMap.get(coins.get(nextPageIndex)) != -1.0 && 
							fillRemainingInitialMap.get(coins.get(nextPageIndex)) != 0.0) {
						switchedToNextPage = true;
						switchPageTo(coins.get(nextPageIndex));
					} else {
						if (coinCheckboxes.get(nextPageIndex).isSelected()) {
							coinCheckboxes.get(nextPageIndex).setSelected(false);
							switchedToNextPage = true;
							switchPageTo(coins.get(nextPageIndex));
							fillRemainingInitialMap.replace(coins.get(nextPageIndex), getFillRemaining());
							balanceThatCanBeWithdrawnInitialMap.replace(coins.get(nextPageIndex), getBalanceThatCanBeWithdrawn());
						}
					}
					
					if (!switchedToNextPage) {
						if (currentPageIndex == coins.size()-1) {
							currentPageIndex = 0;
							nextPageIndex = 1;
						} else {
							currentPageIndex++;
							nextPageIndex++;
						}
					}
				}
				
				triedRefreshRecovery = false;
				Thread.sleep(1000);
			}
			catch (Exception e) {	
				if (!triedRefreshRecovery) {
					triedRefreshRecovery = true;
					try {
						driver.navigate().refresh();
						waitForORCAPageLoad();
					} catch (Exception e2) {
						System.out.println("\nError occurred and recovery attempt failed (e2). Exiting program. Error:");
						System.out.println(e2.getMessage());
						e.printStackTrace();
						String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
						System.out.println("Error occurred at " + timeStamp + ".\n");
						executeServerFunction("sendEmailAboutError");
						System.exit(0);
					}
				} else {
					System.out.println("\nError occurred and recovery attempt failed (e1). Exiting program. Error:");
					System.out.println(e.getMessage());
					e.printStackTrace();
					String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
					System.out.println("Error occurred at " + timeStamp + ".\n");
					executeServerFunction("sendEmailAboutError");
					System.exit(0);
				}
			}
		}
	}
		
	public static String executeServerFunction(String functionToCall) throws InterruptedException, IOException {
		URL url = new URL("http://localhost:3000/" + functionToCall);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");
		
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer content = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
		    content.append(inputLine);
		}
		in.close();
		return content.toString();
	}
	
	public static void waitForORCAPageLoad() throws InterruptedException {
		WebDriverWait wait = new WebDriverWait(driver, 500);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("button--solid-teal")));
		
		boolean myTotalActiveBidsSet = false;
		while (!myTotalActiveBidsSet) {
			try {
				WebElement myTotalActiveBids = driver.findElement(By.className("order-md-9"));
				if (myTotalActiveBids.findElement(By.xpath(".//h4")).getAttribute("innerHTML") != "") {
					myTotalActiveBidsSet = true;
				} else {
					Thread.sleep(1000);
				}
			} 
			catch (Exception e) {
			}
		}

		Thread.sleep(8000);
	}
	
	public static void switchPageTo(String coin) throws InterruptedException {
		driver.navigate().to("https://orca.kujira.app/markets/terra/anchor/" + coin);
		waitForORCAPageLoad();
	}
	
	public static String getCurrentCoinPage() {
		String coinPage = driver.getCurrentUrl();
		coinPage = coinPage.substring(coinPage.lastIndexOf("/") + 1);
		return coinPage;
	}
	
	public static void waitForORCAWalletConnection() throws InterruptedException, AWTException {
		System.out.println("Connect wallet to ORCA first.");
		
		boolean walletConnected = false;
		while (!walletConnected) {
			WebDriverWait wait = new WebDriverWait(driver, 500);
			List<WebElement> walletConnectedElement = driver.findElements(By.cssSelector(".button--wallet-connected"));
			if (walletConnectedElement.size() > 0) {
				walletConnected = true;
			} else {
				Thread.sleep(1000);
			}
		}
		System.out.println("Connected.");
	}
	
	public static float getFillRemaining() {
		try {
			WebElement bidsTable = driver.findElement(By.className("table--mybids"));
			List<WebElement> bidsTableRows = bidsTable.findElements(By.xpath(".//tr"));
			WebElement bidsTableRow = bidsTableRows.get(bidsTableRows.size()-1);
			List<WebElement> bidsTableColumns = bidsTableRow.findElements(By.xpath(".//td"));
			WebElement bidsTableColumnBidRemaining = bidsTableColumns.get(2);	
			List<WebElement> bidsTableColumnBidRemainingSpan = bidsTableColumnBidRemaining.findElements(By.xpath(".//span"));
			WebElement WebidsTableColumnBidRemainingSpanBeforeDecimal = bidsTableColumnBidRemainingSpan.get(0);
			WebElement WebidsTableColumnBidRemainingSpanAfterDecimal = bidsTableColumnBidRemainingSpan.get(1);		
			String beforeDecimal = WebidsTableColumnBidRemainingSpanBeforeDecimal.getAttribute("innerHTML");
			String afterDecimal = WebidsTableColumnBidRemainingSpanAfterDecimal.getAttribute("innerHTML");
			String fillRemainingString = beforeDecimal + afterDecimal;
			fillRemainingString = fillRemainingString.replace(",", "");
			float fillRemaining = Float.parseFloat(fillRemainingString);
			return fillRemaining;
		} catch (Exception e) {
			return (float) -1.0;
		}
	}
	
	public static float getBalanceThatCanBeWithdrawn() throws InterruptedException {
		WebElement withdrawalDiv = driver.findElement(By.className("withdrawal"));
		List<WebElement> withdrawalDivH3Elements = withdrawalDiv.findElements(By.xpath(".//h3"));
		WebElement withdrawalDivH3Element = withdrawalDivH3Elements.get(0);
		List<WebElement> withdrawalDivH3ElementSpans = withdrawalDivH3Element.findElements(By.xpath(".//span"));
		WebElement withdrawalDivH3ElementSpanBeforeDecimal = withdrawalDivH3ElementSpans.get(0);
		WebElement WebidsTableColumnBidRemainingSpanAfterDecimal = withdrawalDivH3ElementSpans.get(1);
		String beforeDecimal = withdrawalDivH3ElementSpanBeforeDecimal.getAttribute("innerHTML");
		String decimalPlaces = WebidsTableColumnBidRemainingSpanAfterDecimal.getAttribute("innerHTML");
		String balanceToWithdrawString = beforeDecimal + decimalPlaces;
		float balanceToWithdraw = Float.parseFloat(balanceToWithdrawString);
		return balanceToWithdraw;
	}
	
	public static void withdrawFromORCA() throws InterruptedException, AWTException {		
		// Click bottom of ORCA to focus and in case of a pop-up 
		Robot bot = new Robot();		
		bot.mouseMove(800, 700);
		Thread.sleep(500);
	    bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
	    bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
	    Thread.sleep(500);
		
	    boolean stillClaimingFundsAfter10Seconds = false;
	    while (!stillClaimingFundsAfter10Seconds) {
	    	driver.findElement(By.className("button--solid-teal")).click();
	    	Thread.sleep(10000);
		
			try {
				// Check that the "Claiming Funds" message is there.
				WebElement claimingFundsMessageElement = driver.findElement(By.xpath("//*[text()='Claiming Funds']"));
				stillClaimingFundsAfter10Seconds = true;
			} catch (Exception e) 
			{
				System.out.println("Error occurred. Claiming Funds message is gone before calling completeTransactionPost(). Trying again.");
				fillInfoMessage = fillInfoMessage + "\nError occurred. Claiming Funds message is gone before calling completeTransactionPost(). Trying again.";
				driver.navigate().refresh();
				waitForORCAPageLoad();
			}
	    }
		
		completeTransactionPost(true);
		
		boolean withdrawalComplete = false;
		System.out.println("Waiting for withdrawal transaction to complete.");
		fillInfoMessage = fillInfoMessage + "\nWaiting for withdrawal transaction to complete.";
		while (!withdrawalComplete) {		
			float balanceThatCanBeWithdrawnCheck = getBalanceThatCanBeWithdrawn();
			if (balanceThatCanBeWithdrawnCheck != 0.0) {
				Thread.sleep(500);
			} else {
				withdrawalComplete = true;
			}
		}
		
		System.out.println("Withdrawal from ORCA complete.");
		fillInfoMessage = fillInfoMessage + "\nWithdrawal from ORCA complete.";
	}
	
	public static void completeTransactionPost(boolean onOrcaNotTerraSwap) throws InterruptedException, AWTException {
		System.out.println("Completing transaction post.");
		fillInfoMessage = fillInfoMessage + "\nCompleting transaction post.";
		Robot bot = new Robot();
		bot.keyPress (80); 
		bot.keyPress (65); 
		bot.keyPress (83); 
		bot.keyRelease(83); 
		bot.keyPress (83); 
		bot.keyPress (49); 
		bot.keyPress (50); 
		bot.keyPress (51); 
		bot.keyPress (52); 
		bot.keyPress (53); 
		bot.keyPress (54); 
		Thread.sleep(1000);
		bot.keyPress (9);
		bot.keyRelease(9);
		Thread.sleep(1000);
		bot.keyPress (10);
		bot.keyRelease(10);
	}

	public static void createFreshLogFile() throws InterruptedException, IOException {
	    File myObj = new File("logs.txt"); 
	    myObj.delete();
		
		myObj = new File("logs.txt");
	    myObj.createNewFile();	    
	}
	
	public static void writeToLogFile(String message) throws InterruptedException, IOException {
		FileWriter fw = null; 
		BufferedWriter bw = null; 
		PrintWriter pw = null;
		
		fw = new FileWriter("logs.txt", true); 
		bw = new BufferedWriter(fw); 
		pw = new PrintWriter(bw); 
		pw.println(message); 
		pw.flush(); 

		pw.close(); 
		bw.close(); 
		fw.close(); 
	}
	
	public static JFrame createJFrame() {
		JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("Bid Reset Window");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        return frame;
	}
	
	public static JPanel createJPanel() {
		// Define the panel to hold the checkbox    
        JPanel panel = new JPanel();
        // Set up the title for the panel
        panel.setBorder(BorderFactory.createTitledBorder("Check checkbox for the coin the bid was reset on:"));
        return panel;
	}
	
	public static JCheckBox createCheckbox(String label) {
		JCheckBox checkbox = new JCheckBox(label);
		return checkbox;
	}
}
