package marcodugatto.assessment.gasstation;

import java.util.Collection;
import java.util.HashMap;
import java.util.ArrayList;

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
	
	
	public void addGasPump(GasPump pump) {
		gasPumps.add(pump);
	}

	/**
	 * Makes a deep copy of the gasPumps Collection.
	 * 
	 * @TODO @DUGHI.BOOGIE TEST THIS
	 */
	public Collection<GasPump> getGasPumps() {
		ArrayList<GasPump> returnCollection = new ArrayList<GasPump>(gasPumps.size());
		for (GasPump gasPump : gasPumps) {
			returnCollection.add(new GasPump(gasPump.getGasType(), gasPump.getRemainingAmount()));
		}
		return returnCollection;
	}
	
	/**
	 * @TODO manage multithreading
	 * @TODO manage gas type not present (maybe add new GasTypeNotPresentException?)
	 *  
	 */
	public double buyGas(GasType type, double amountInLiters, double maxPricePerLiter)
			throws NotEnoughGasException, GasTooExpensiveException {

		// Every gas pump simulates a single thread
		
		
		
		
		return 0;
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
		gasPrices.put(type, price);
	}

}
