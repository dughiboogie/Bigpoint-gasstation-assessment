package marcodugatto.gasstation.solution;

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
	
	
	/**
	 * Add a gas pump to this station.
	 * This is used to set up this station.
	 * 
	 * @param pump
	 *            the gas pump
	 */
	public void addGasPump(GasPump pump) {
		gasPumps.add(pump);
		
		ReentrantLock currentLock = new ReentrantLock(); 
		pumpLocks.add(currentLock);
	}

	/**
	 * Get a deep copy of all gas pumps that are currently associated with this gas station.
	 * 
	 * @return A collection of all gas pumps.
	 */
	public Collection<GasPump> getGasPumps() {
		ArrayList<GasPump> returnCollection = new ArrayList<GasPump>(gasPumps.size());
		for (GasPump gasPump : gasPumps) {
			returnCollection.add(new GasPump(gasPump.getGasType(), gasPump.getRemainingAmount()));
		}
		return returnCollection;
	}	
	
	/**
	 * Simulates a customer wanting to buy a specific amount of gas.
	 * 
	 * @param type
	 *            The type of gas the customer wants to buy
	 * @param amountInLiters
	 *            The amount of gas the customer wants to buy. Nothing less than this amount is acceptable!
	 * @param maxPricePerLiter
	 *            The maximum price the customer is willing to pay per liter
	 * @return the price the customer has to pay for this transaction
	 * @throws NotEnoughGasException
	 *             Should be thrown in case not enough gas of this type can be provided
	 *             by any single {@link GasPump}.
	 * @throws GasTooExpensiveException
	 *             Should be thrown if gas is not sold at the requested price (or any lower price)
	 */
	public double buyGas(GasType type, double amountInLiters, double maxPricePerLiter)
			throws NotEnoughGasException, GasTooExpensiveException {
		
		System.out.println("New gas pump transaction! " + amountInLiters + "L of " + type.toString());
		
		/*
		 * First of all check if the max price that the customer wants to pay is less than the price of the gas type he wants.
		 * If so, increase "numberOfCancellationsTooExpensive" and throw the corresponding exception. 
		 */
		if(maxPricePerLiter < this.getPrice(type)) {
			stationStatisticsLock.lock();
			numberOfCancellationsTooExpensive++;
			stationStatisticsLock.unlock();
			throw new GasTooExpensiveException();
		}
		
		/*
		 * Local list of the pumps present in the gas station, used to keep track of the availability of gas for the current transaction in every pump.
		 * Every element is initialised to true, to try to make the transaction on every pump. 
		 */
		ArrayList<Boolean> usablePumps = new ArrayList<Boolean>();
		for (int i = 0; i < gasPumps.size(); i++) {
			usablePumps.add(true);
		}
		
		/*
		 * Local variable to keep track of the transaction status.
		 */
		Boolean transactionMade = false;
		
		/*
		 * Iterate on the gas pumps collection to find a free pump to make the transaction on.
		 */
		for (int i = 0; i < gasPumps.size() && !transactionMade ; i++) {
						
			/*
			 * If the current gas pump hasn't the correct type of gas for this transaction set the corresponding 
			 * element in "usablePumps" to false to avoid checking again for this pump availability.
			 */
			if(gasPumps.get(i).getGasType() == type) {
								
				/*
				 * Check if this pump wasn't already set as unavailable due to lack of enough gas.
				 */
				if(usablePumps.get(i)) {

					/*
					 * If this pump has enough gas for the current transaction keep going, otherwise 
					 * set the corresponding element in "usablePumps" to false to avoid checking again for this pump availability.
					 */
					if(gasPumps.get(i).getRemainingAmount() >= amountInLiters) {
												
						/*
						 * If the current pump is free for a transaction, lock it to prevent other threads from using it and execute the transaction.
						 * Otherwise go on with the for loop and check the next pump.
						 */
						if(pumpLocks.get(i).tryLock()) {
							
							try {
								gasPumps.get(i).pumpGas(amountInLiters);
								
							} finally {
								/*
								 * When the transaction has finished unlock the pump for other threads to eventually use it.
								 */
								transactionMade = true;
								pumpLocks.get(i).unlock();
							}
							
							/*
							 * If the transaction has been completed lock the station statistics to update them.
							 * If the statistics are already locked by another thread wait for it to unlock them and then do the update.
							 */
							if(transactionMade) {
								
								Double priceToPay = amountInLiters * getPrice(type);
								
								stationStatisticsLock.lock();
								numberOfSales++;
								totalRevenue += priceToPay;
								stationStatisticsLock.unlock();
								
								return priceToPay;
							}
						}
					}
					/*
					 * If there's not enough gas in this pump for the current transaction set the availability to false.
					 */
					else {
						usablePumps.set(i, false);
					}
				}
			}
			/*
			 * If this pump hasn't the correct type of gas for the current transaction set the availability to false.
			 */
			else {
				usablePumps.set(i, false);
			}
			
			/*
			 * If this is the last gas pump and the current transaction is still pending check if there are any pumps available to try again.
			 * If so, reset the gas pumps iterator and try again for all the available pumps.  
			 * Otherwise, increase "numberOfCancellationsNoGas" and throw the corresponding exception. 
			 */
			if(i == gasPumps.size() - 1 && !transactionMade) {
				
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
		
		/*
		 * Default return value, application should never get here.
		 */
		return 0.0;
	}

	/**
	 * @return the total revenue generated
	 */
	public double getRevenue() {
		return totalRevenue;
	}

	/**
	 * Returns the number of successful sales. This should not include cancelled sales.
	 * 
	 * @return the number of sales that were successful
	 */
	public int getNumberOfSales() {
		return numberOfSales;
	}

	/**
	 * @return the number of cancelled transactions due to not enough gas being available
	 */
	public int getNumberOfCancellationsNoGas() {
		return numberOfCancellationsNoGas;
	}

	/**
	 * Returns the number of cancelled transactions due to the gas being more expensive than what the customer wanted to pay
	 * 
	 * @return the number of cancelled transactions
	 */
	public int getNumberOfCancellationsTooExpensive() {
		return numberOfCancellationsTooExpensive;
	}

	/**
	 * Get the price for a specific type of gas
	 * 
	 * @param type
	 *            the type of gas
	 * @return the price per liter for this type of gas, or 0.0 if the price for this gas is not set
	 */
	public double getPrice(GasType type) {
		if(gasPrices.containsKey(type)) {
			double price = gasPrices.get(type);			
			return price;
		}
		
		return 0.0;
	}

	/**
	 * Set a new price for a specific type of gas
	 * 
	 * @param type
	 *            the type of gas
	 * @param price
	 *            the new price per liter for this type of gas
	 */
	public void setPrice(GasType type, double price) {
		
		System.out.println("Setting price of " + type.toString() + " gas.");
		
		gasPrices.put(type, price);
	}
	
}
