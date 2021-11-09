package marcodugatto.gasstation.solution;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collection;

import net.bigpoint.assessment.gasstation.GasPump;
import net.bigpoint.assessment.gasstation.GasType;
import net.bigpoint.assessment.gasstation.exceptions.GasTooExpensiveException;
import net.bigpoint.assessment.gasstation.exceptions.NotEnoughGasException;

public class TestMyGasStation {
		
	@Test
	public void testAddGasPump() {
		MyGasStation testMyGasStation = new MyGasStation();
		Double testGasPumpAmount = 50.0;
		GasPump testGasPump = new GasPump(GasType.SUPER, testGasPumpAmount);
		testMyGasStation.addGasPump(testGasPump);
		
		Collection<GasPump> testGasPumps = testMyGasStation.getGasPumps();
		
		assertEquals(1, testMyGasStation.getGasPumps().size());
		assertEquals(GasType.SUPER, ((GasPump) testGasPumps.toArray()[0]).getGasType());
		assertEquals(50.0, ((GasPump) testGasPumps.toArray()[0]).getRemainingAmount());
	}
	
	@Test
	public void testGetGasPumps() {
		MyGasStation testMyGasStation = new MyGasStation();
		assertEquals(0, testMyGasStation.getGasPumps().size());
	}
	
	@Test
	public void testBuyGasCorrectTransaction() {
		MyGasStation testMyGasStation = new MyGasStation();
		Double testGasPumpAmount = 100.0;
		GasPump testGasPump = new GasPump(GasType.REGULAR, testGasPumpAmount);
		testMyGasStation.addGasPump(testGasPump);
		testMyGasStation.setPrice(GasType.REGULAR, 1.0);
		
		Double amountInLiters = 10.0;
		Double maxPricePerLiter = 1.1;
		
		try {
			assertEquals(10.0, testMyGasStation.buyGas(GasType.REGULAR, amountInLiters, maxPricePerLiter));			
		} catch (Exception e) {
			// Ignore
		}
	}
	
	@Test
	public void testBuyGasTooExpensiveException() {
		MyGasStation testMyGasStation = new MyGasStation();
		Double testGasPumpAmount = 100.0;
		GasPump testGasPump = new GasPump(GasType.REGULAR, testGasPumpAmount);
		testMyGasStation.addGasPump(testGasPump);
		testMyGasStation.setPrice(GasType.REGULAR, 1.0);
		
		Double amountInLiters = 10.0;
		Double maxPricePerLiter = 0.9;
		
		assertThrows(GasTooExpensiveException.class, () -> testMyGasStation.buyGas(GasType.REGULAR, amountInLiters, maxPricePerLiter));
	}
	
	@Test
	public void testBuyGasNotEnoughGasException() {
		MyGasStation testMyGasStation = new MyGasStation();
		Double testGasPumpAmount = 100.0;
		GasPump testGasPump = new GasPump(GasType.REGULAR, testGasPumpAmount);
		testMyGasStation.addGasPump(testGasPump);
		testMyGasStation.setPrice(GasType.REGULAR, 1.0);
		
		Double amountInLiters = 200.0;
		Double maxPricePerLiter = 1.1;
		
		assertThrows(NotEnoughGasException.class, () -> testMyGasStation.buyGas(GasType.REGULAR, amountInLiters, maxPricePerLiter));
	}
	
	@Test
	public void testGetRevenue() {
		MyGasStation testMyGasStation = new MyGasStation();
		Double testGasPumpAmount = 100.0;
		GasPump testGasPump = new GasPump(GasType.REGULAR, testGasPumpAmount);
		testMyGasStation.addGasPump(testGasPump);
		testMyGasStation.setPrice(GasType.REGULAR, 1.0);
		
		Double amountInLiters = 10.0;
		Double maxPricePerLiter = 1.1;
		
		assertEquals(0.0, testMyGasStation.getRevenue());
		
		try {
			testMyGasStation.buyGas(GasType.REGULAR, amountInLiters, maxPricePerLiter);			
		} catch (Exception e) {
			// Ignore
		}
		
		assertEquals(10.0, testMyGasStation.getRevenue());
	}
	
	@Test
	public void testGetNumberOfSales() {
		MyGasStation testMyGasStation = new MyGasStation();
		Double testGasPumpAmount = 100.0;
		GasPump testGasPump = new GasPump(GasType.REGULAR, testGasPumpAmount);
		testMyGasStation.addGasPump(testGasPump);
		testMyGasStation.setPrice(GasType.REGULAR, 1.0);
		
		Double amountInLiters = 10.0;
		Double maxPricePerLiter = 1.1;
		
		assertEquals(0, testMyGasStation.getNumberOfSales());
		
		try {
			testMyGasStation.buyGas(GasType.REGULAR, amountInLiters, maxPricePerLiter);			
		} catch (Exception e) {
			// Ignore
		}
		
		assertEquals(1, testMyGasStation.getNumberOfSales());
	}
	
	@Test
	public void testGetNumberOfCancellationsNoGas() {
		MyGasStation testMyGasStation = new MyGasStation();
		Double testGasPumpAmount = 100.0;
		GasPump testGasPump = new GasPump(GasType.REGULAR, testGasPumpAmount);
		testMyGasStation.addGasPump(testGasPump);
		testMyGasStation.setPrice(GasType.REGULAR, 1.0);
		
		Double amountInLiters = 200.0;
		Double maxPricePerLiter = 1.1;
		
		assertEquals(0, testMyGasStation.getNumberOfCancellationsNoGas());
		
		try {
			testMyGasStation.buyGas(GasType.REGULAR, amountInLiters, maxPricePerLiter);
		} catch (NotEnoughGasException | GasTooExpensiveException e) {
			// Ignore
		}
		
		assertEquals(1, testMyGasStation.getNumberOfCancellationsNoGas());
	}
	
	@Test
	public void testGetNumberOfCancellationsTooExpensive() {
		MyGasStation testMyGasStation = new MyGasStation();
		Double testGasPumpAmount = 100.0;
		GasPump testGasPump = new GasPump(GasType.REGULAR, testGasPumpAmount);
		testMyGasStation.addGasPump(testGasPump);
		testMyGasStation.setPrice(GasType.REGULAR, 1.0);
		
		Double amountInLiters = 10.0;
		Double maxPricePerLiter = 0.9;
		
		assertEquals(0, testMyGasStation.getNumberOfCancellationsTooExpensive());
		
		try {
			testMyGasStation.buyGas(GasType.REGULAR, amountInLiters, maxPricePerLiter);
		} catch (NotEnoughGasException | GasTooExpensiveException e) {
			// Ignore
		}
		
		assertEquals(1, testMyGasStation.getNumberOfCancellationsTooExpensive());
	}
	
	@Test
	public void testGetPrice() {
		MyGasStation testMyGasStation = new MyGasStation();
		
		assertEquals(0.0, testMyGasStation.getPrice(GasType.REGULAR));
		
		testMyGasStation.setPrice(GasType.REGULAR, 1.0);
		
		assertEquals(1.0, testMyGasStation.getPrice(GasType.REGULAR));
	}
	
	@Test
	public void testSetPrice() {
		MyGasStation testMyGasStation = new MyGasStation();
		testMyGasStation.setPrice(GasType.REGULAR, 1.0);
		
		assertEquals(1.0, testMyGasStation.getPrice(GasType.REGULAR));
	}
	
}
