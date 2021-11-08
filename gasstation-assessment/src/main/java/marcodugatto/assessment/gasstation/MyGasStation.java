package marcodugatto.assessment.gasstation;

import java.util.Collection;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

import net.bigpoint.assessment.gasstation.GasPump;
import net.bigpoint.assessment.gasstation.GasStation;
import net.bigpoint.assessment.gasstation.GasType;
import net.bigpoint.assessment.gasstation.exceptions.GasTooExpensiveException;
import net.bigpoint.assessment.gasstation.exceptions.NotEnoughGasException;

/**
 * This is an implementation of the GasStation interface.
 *
 */

class MyGasStation implements GasStation {
	
	/**
	 * All the gas pumps present in this gas station.
	 */
	private ArrayList<GasPump> gasPumps = new ArrayList<GasPump>();
	
	/**
	 * The costs of the various types of gas present in this station.
	 */
	private HashMap<GasType, Double> gasPrices = new HashMap<>();
	
	/**
	 * The total revenue generated.
	 */
	private double totalRevenue;
	
	/**
	 * Number of successful sales.
	 */
	private int numberOfSales;
	
	/**
	 * Number of cancelled transactions due to not enough gas being available.
	 */
	private int numberOfCancellationsNoGas;
	
	/**
	 * Number of cancelled transactions due to the gas being more expensive than what the customer wanted to pay.
	 */
	private int numberOfCancellationsTooExpensive;
			
	/**
	 * Lock to manage concurrency on pumps
	 */
	private ArrayList<ReentrantLock> pumpLocks = new ArrayList<ReentrantLock>(); 
	
	/**
	 * Lock to manage concurrency on the gas station statistics (i.e. number of sales)
	 */
	private ReentrantLock stationStatisticsLock = new ReentrantLock();
	
	
	
	public void addGasPump(GasPump pump) {
		gasPumps.add(pump);
		
		ReentrantLock currentLock = new ReentrantLock(); 
		pumpLocks.add(currentLock);
	}

	/**
	 * Makes a deep copy of the gasPumps Collection.
	 */
	public Collection<GasPump> getGasPumps() {
		ArrayList<GasPump> returnCollection = new ArrayList<GasPump>(gasPumps.size());
		for (GasPump gasPump : gasPumps) {
			returnCollection.add(new GasPump(gasPump.getGasType(), gasPump.getRemainingAmount()));
		}
		return returnCollection;
	}	
	
	/**
	 * 
	 *  
	 */
	public double buyGas(GasType type, double amountInLiters, double maxPricePerLiter)
			throws NotEnoughGasException, GasTooExpensiveException {
		
		System.out.println("New gas pump transaction! " + amountInLiters + "L of " + type.toString());
		
		// Check if maxPricePerLiter > gas type price
				//		If so throw GasTooExpensiveException
				//		Increase numberOfCancellationTooExpensive
				
				// Check for a GasPump of the GasType type that is not being used
				//		If there's one free, start a Thread and lock the pump
				//			Check if there's enough gas in the pump
				//				If so call pumpGas
				//				Else unlock the pump and Throw NotEnoughGasException @TODO this can be optimised
				
				//		Else wait for a pump to unlock
		
		
		if(maxPricePerLiter < this.getPrice(type)) {
			stationStatisticsLock.lock();
			numberOfCancellationsTooExpensive++;
			stationStatisticsLock.unlock();
			throw new GasTooExpensiveException();
		}
		
		
		/*
		 * ArrayList to keep track of the availability of gas for the current transaction in the different pumps
		 */
		ArrayList<Boolean> usablePumps = new ArrayList<Boolean>();
		for (int i = 0; i < gasPumps.size(); i++) {
			usablePumps.add(true);
		}
		
		Boolean transactionMade = false;
		
		for (int i = 0; i < gasPumps.size() && !transactionMade ; i++) {
						
			// Find first gas pump with correct gas type and right amount of gas available
			if(gasPumps.get(i).getGasType() == type) {
								
				// Pump had enough gas last time we checked
				if(usablePumps.get(i)) {
					
					
					// Enough gas for the transaction
					if(gasPumps.get(i).getRemainingAmount() > amountInLiters) {
												
						// If pump is available do the transaction
						if(pumpLocks.get(i).tryLock()) {
							
							try {
								
								System.out.println("Executing transaction on pump " + i + "!");
								
								gasPumps.get(i).pumpGas(amountInLiters);
								
							} finally {
								transactionMade = true;
								
								System.out.println("Transaction on pump " + i + " finished!");
								
								pumpLocks.get(i).unlock();
							}
							
							
							if(transactionMade) {
								
								stationStatisticsLock.lock();
																
								numberOfSales++;
								
								Double priceToPay = amountInLiters * getPrice(type);
								
								totalRevenue += priceToPay;
								
								stationStatisticsLock.unlock();
								
								return priceToPay;
							}
							
						}
						
					}
					// Not enough gas in this pump for the transaction
					else {
						usablePumps.set(i, false);
					}
					
				}
				
			}
			// The gas type of this pump is not the one required
			else {
				usablePumps.set(i, false);
			}
			
			
			
			/*
			 * If we are at the end of the pumps collection and still haven't made the transaction,
			 * check if there are any pumps available for another round of checks,
			 * otherwise throw NotEnoughGasException 
			 */
			
			if(i == gasPumps.size()-1 && !transactionMade) {
				
				Boolean pumpsStillAvailable = false;
				
				for (int j = 0; j < usablePumps.size() && !pumpsStillAvailable; j++) {
					
					if(usablePumps.get(j)) {
						pumpsStillAvailable = true;
						i = 0;
					}
					
				}
				
				if(!pumpsStillAvailable) {
					stationStatisticsLock.lock();
					numberOfCancellationsNoGas++;
					stationStatisticsLock.unlock();
					throw new NotEnoughGasException();
				}
				
				
			}
			
			
		}
		
		return 0.0;
		
		
		
	}

	public double getRevenue() {
		return totalRevenue;
	}

	public int getNumberOfSales() {
		return numberOfSales;
	}

	public int getNumberOfCancellationsNoGas() {
		return numberOfCancellationsNoGas;
	}

	public int getNumberOfCancellationsTooExpensive() {
		return numberOfCancellationsTooExpensive;
	}

	public double getPrice(GasType type) {
		double price = gasPrices.get(type);
		return price;
	}

	public void setPrice(GasType type, double price) {
		
		System.out.println("Setting price of " + type.toString() + " gas.");
		
		gasPrices.put(type, price);
	}
	
		
}
