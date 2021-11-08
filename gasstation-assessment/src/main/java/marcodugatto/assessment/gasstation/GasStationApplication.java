package marcodugatto.assessment.gasstation;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import net.bigpoint.assessment.gasstation.GasPump;
import net.bigpoint.assessment.gasstation.GasType;
import net.bigpoint.assessment.gasstation.exceptions.GasTooExpensiveException;
import net.bigpoint.assessment.gasstation.exceptions.NotEnoughGasException;

public class GasStationApplication {
	
	private static MyGasStation myGasStation = new MyGasStation();
	
	public static void main(String[] args) {
		
		/*
		 * Gas station initialisation
		 */
		
		myGasStation.setPrice(GasType.REGULAR, 1.62);
		myGasStation.setPrice(GasType.SUPER, 1.78);
		myGasStation.setPrice(GasType.DIESEL, 1.56);
		
		myGasStation.addGasPump(new GasPump(GasType.REGULAR, 1000));
		myGasStation.addGasPump(new GasPump(GasType.REGULAR, 500));
		myGasStation.addGasPump(new GasPump(GasType.DIESEL, 1000));

		
		Runnable task1 = () -> {
		    try {
		    	System.out.println("Price to pay for transaction 1: " + myGasStation.buyGas(GasType.REGULAR, 60, 1.9));
			} catch (NotEnoughGasException e) {
				System.out.println("Not enough gas");
			} catch (GasTooExpensiveException e) {
				System.out.println("Gas too expensive");
			}
		};
		
		Runnable task2 = () -> {
		    try {
				System.out.println("Price to pay for transaction 2: " + myGasStation.buyGas(GasType.REGULAR, 30, 1.9));
			} catch (NotEnoughGasException e) {
				System.out.println("Not enough gas");
			} catch (GasTooExpensiveException e) {
				System.out.println("Gas too expensive");
			}
		};
				
		Runnable task3 = () -> {
		    try {
				System.out.println("Price to pay for transaction 3: " + myGasStation.buyGas(GasType.DIESEL, 30, 1.9));
			} catch (NotEnoughGasException e) {
				System.out.println("Not enough gas");
			} catch (GasTooExpensiveException e) {
				System.out.println("Gas too expensive");
			}
		};

		Runnable task4 = () -> {
		    try {
				System.out.println("Price to pay for transaction 4: " + myGasStation.buyGas(GasType.SUPER, 30, 1.9));
			} catch (NotEnoughGasException e) {
				System.out.println("Not enough gas");
			} catch (GasTooExpensiveException e) {
				System.out.println("Gas too expensive");
			}
		};
		
		Runnable task5 = () -> {
		    try {
				System.out.println("Price to pay for transaction 5: " + myGasStation.buyGas(GasType.REGULAR, 20, 1.9));
			} catch (NotEnoughGasException e) {
				System.out.println("Not enough gas");
			} catch (GasTooExpensiveException e) {
				System.out.println("Gas too expensive");
			}
		};
		
		Runnable task6 = () -> {
		    try {
				System.out.println("Price to pay for transaction 6: " + myGasStation.buyGas(GasType.REGULAR, 30, 1.2));
			} catch (NotEnoughGasException e) {
				System.out.println("Not enough gas");
			} catch (GasTooExpensiveException e) {
				System.out.println("Gas too expensive");
			}
		};

		
		ExecutorService executor = Executors.newCachedThreadPool();
		
		executor.submit(task1);
		executor.submit(task2);
		executor.submit(task6);
		executor.submit(task3);
		executor.submit(task4);
		executor.submit(task5);
				
		/*
		 * Shutdown executor
		 */
		try {
		    System.out.println("attempt to shutdown executor");
		    executor.shutdown();
		    executor.awaitTermination(10, TimeUnit.SECONDS);
		}
		catch (InterruptedException e) {
		    System.err.println("tasks interrupted");
		}
		finally {
		    if (!executor.isTerminated()) {
		        System.err.println("cancel non-finished tasks");
		    }
		    executor.shutdownNow();
		    System.out.println("shutdown finished");
		}
		
		System.out.println("Total revenue: " + myGasStation.getRevenue());
		System.out.println("Total number of sales: " + myGasStation.getNumberOfSales());
		System.out.println("Total number of cancellations no gas: " + myGasStation.getNumberOfCancellationsNoGas());
		System.out.println("Total number of cancellations too expensive: " + myGasStation.getNumberOfCancellationsTooExpensive());
		
	}

}
