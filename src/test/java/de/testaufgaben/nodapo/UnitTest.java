package de.testaufgaben.nodapo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.IntStream;

/**
 * The Class for unit-testing.
 */
public class UnitTest {
	public Customer john = new Customer("John");
	public Customer jack = new Customer("Jack");
	
	public Double johnStartBudget = 200.0;
	public Double jackStartBudget = 300.0;

	public Book testBook150 = new Book("Test1", 150.0, 340, Genre.COM, "978-1861972712");
	public Book testBook250 = new Book("Test2", 250.0, 320, Genre.ADV, "978-1861972712");
	
	public Shop shop1 = new Shop("Shop1");
	public Shop shop2 = new Shop("Shop2");
	
	@BeforeEach
	public void setUp() {
		john.addToBudget(johnStartBudget);
		jack.addToBudget(jackStartBudget);
		shop1.addToCatalogue(testBook150);
		shop1.addToCatalogue(testBook250);
		shop2.addToCatalogue(testBook150);
	}
	
	@Test
	public void testBuy() {
		assertFalse(john.getLibrary().contains(testBook150));
		assertTrue(shop1.getCatalogue().contains(testBook150));
		
		john.buy(shop1, testBook150);
		assertTrue(john.getLibrary().contains(testBook150));
		assertFalse(shop1.getCatalogue().contains(testBook150));
		assertEquals(john.getBudget(), johnStartBudget - testBook150.price);
		assertEquals(shop1.getIncome(), testBook150.price);
	}
	
	@Test
	public void testBuyIfNotEnoughMoney() {
		assertFalse(john.getLibrary().contains(testBook250));
		assertTrue(shop1.getCatalogue().contains(testBook150));
		assertTrue(shop1.getCatalogue().contains(testBook250));
		
		john.buy(shop1, testBook250);
		assertFalse(john.getLibrary().contains(testBook250));
		assertTrue(shop1.getCatalogue().contains(testBook250));
		assertEquals(john.getBudget(), johnStartBudget);
		
		jack.buy(shop1, testBook250);
		assertTrue(jack.getLibrary().contains(testBook250));
		
		jack.buy(shop1, testBook150);
		assertFalse(jack.getLibrary().contains(testBook150));
		assertTrue(shop1.getCatalogue().contains(testBook150));
		assertEquals(jack.getBudget(), jackStartBudget - testBook250.price);
	}
	
	@Test
	public void testBuyIfNotInTheCatalogue() {
		assertFalse(shop2.getCatalogue().contains(testBook250));
		
		john.buy(shop2, testBook250);
		assertFalse(john.getLibrary().contains(testBook150));
	}
	
	@Test
	public void testTwoBuyAtTheSameTime() throws InterruptedException {
		Runnable c1 =
		        () -> { john.buy(shop1, testBook150); };
		Runnable c2 =
		        () -> { jack.buy(shop1, testBook150); };
		new Thread(c1).start();
		new Thread(c2).start();
		Thread.currentThread();
		// Just wait a bit until all operations are ready.
		// (could be replaced with using mocks by verifying)
		Thread.sleep(100);
		assertTrue((john.getLibrary().contains(testBook150) 
				    && !jack.getLibrary().contains(testBook150))
				|| (!john.getLibrary().contains(testBook150)
				    && jack.getLibrary().contains(testBook150)));
	}
	
	@Test
	public void testSameGenresBooks() {
		Book samegenre = new Book("Test3", 350.0, 300, Genre.ADV, "978-1861972712");
		shop1.addToCatalogue(samegenre);
		List<Book> sameGenreBooksADV = Customer.findBooksOfSameGenre(shop1, Genre.ADV);
		assertEquals(List.of(testBook250, samegenre), sameGenreBooksADV);
		List<Book> sameGenreBooksBIO = Customer.findBooksOfSameGenre(shop1, Genre.BIO);
		assertTrue(sameGenreBooksBIO.isEmpty());
	}
	
	@Test
	public void testSameBooksFromTwoShops() {
		List<Book> sameBooks = Customer.findSameBooks(shop1, shop2);
		assertEquals(List.of(testBook150), sameBooks);
		
		Book newBookADV = new Book("newAdvBook", 350.0, 300, Genre.ADV, "978-1861972712");
		shop1.addToCatalogue(newBookADV);
		shop2.addToCatalogue(newBookADV);
		sameBooks.add(newBookADV);
		assertEquals(sameBooks, Customer.findSameBooks(shop1, shop2));
	}
	
	@Test
	public void testRemoveDuplicates() {
		Book duplicate = new Book("Test1", 150.0, 340, Genre.COM, "978-1861972712");
		shop2.addToCatalogue(duplicate);
		assertEquals(shop2.getCatalogue(), List.of(testBook150, duplicate));
		assertEquals(shop2.getCatalogueWithoutDuplicates(), List.of(testBook150));
		
		Book duplicateNotFullyBook =
				new Book("Test1", 150.0, 340, Genre.COM, "978-3608963762");
		shop2.addToCatalogue(duplicateNotFullyBook);
		assertEquals(shop2.getCatalogueWithoutDuplicates(), 
				List.of(testBook150, duplicateNotFullyBook));
	}
	
	@Test
	public void testISBN() {
		Shop emptyShop = new Shop("fakeShop");
		List<String> testISBN = List.of("978-3608963762", "978-3442267747", 
				"978-758245159", "978-3841335180", "978-3442267819");
		List<Boolean> testISBNRealChecks = List.of(true, false, false, true, false);
		IntStream.range(0, testISBN.size()).forEach(i -> {
			assertEquals(Shop.isISBN13(testISBN.get(i)), testISBNRealChecks.get(i));
			
			Book fakeBook = makeFakeBook(testISBN.get(i));
			emptyShop.addToCatalogue(fakeBook);
		
			if (Shop.isISBN13(testISBN.get(i))) {
				assertTrue(emptyShop.getCatalogue().contains(fakeBook));
			} else {
				assertFalse(emptyShop.getCatalogue().contains(fakeBook));
			}
		});
	}
	
	private Book makeFakeBook(String isbn) {
		return new Book("fake", 1.0, 1, Genre.ADV, isbn);
	}
}