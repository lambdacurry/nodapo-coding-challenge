package de.testaufgaben.nodapo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class Customer.
 */
public class Customer {
	private static final Logger LOG = LoggerFactory.getLogger(Customer.class);
	
	public final String name;
	private Double budget = 0.0;
	private List<Book> library = new ArrayList<>();
	
	/**
	 * Instantiates a new customer.
	 *
	 * @param name the name
	 */
	public Customer(String name) {
		this.name = name;
	}
	
	/**
	 * Gets the budget.
	 *
	 * @return the budget
	 */
	public Double getBudget() {
		return budget;
	}

	/**
	 * Gets the library.
	 *
	 * @return the library
	 */
	public List<Book> getLibrary() {
		return library;
	}
	
	/**
	 * Adds money to the budget.
	 *
	 * @param amount the amount
	 */
	public void addToBudget(Double amount) {
		budget += amount;
	}

	/**
	 * Buy the book in the shop.
	 *
	 * @param shop the shop
	 * @param book the book
	 */
	public void buy(Shop shop, Book book) {
		synchronized (shop.getCatalogue()) {
			if (book.price <= budget) {
				Book newBook = shop.getCatalogue().stream()
						           .filter(entry -> entry.equals(book))
					               .findFirst()
					               .orElse(null);
				if (newBook != null) {
					library.add(book);
					budget -= book.price;
					shop.getCatalogue().remove(book);
					shop.pay(book.price);
					LOG.info("{} was succesfully bought by {} from the shop {}", 
							book.toString(), name, shop.name);
				} else {
					LOG.error("No {} in the {} shop", book.toString(), shop.name);
				}
			} else {
				LOG.error("Not enough money to buy the {}", book.toString());
			}
		}
	}
	
	/**
	 * Find books in one shop of the same genre.
	 *
	 * @param shop the shop
	 * @param genre the genre
	 * @return books of the same genre
	 */
	public static List<Book> findBooksOfSameGenre(Shop shop, Genre genre) {
		synchronized (shop.getCatalogue()) {
			List<Book> sameGenreBooks = shop.getCatalogue().stream()
											.filter(book -> book.genre.equals(genre))
											.collect(Collectors.toList());
			LOG.info("Following books of the same genre {} in shop {} were founded: {}",
					genre.toString(), shop.name, sameGenreBooks);
			return sameGenreBooks;
		}
	}
	
	/**
	 * Find same books in two different shops.
	 *
	 * @param shopFirst the shop first
	 * @param shopSecond the shop second
	 * @return books, which could be found in both shops
	 */
	public static List<Book> findSameBooks(Shop shopFirst, Shop shopSecond) {
		synchronized (shopFirst.getCatalogue()) {
			synchronized (shopSecond.getCatalogue()) {
				List<Book> sameBooks = shopFirst.getCatalogue().stream()
			               .filter(book -> shopSecond.getCatalogue().contains(book))
			               .collect(Collectors.toList());
				LOG.info("Following same books in shops {} and {} were founded: {}",
						shopFirst.name, shopSecond.name, sameBooks);
				return sameBooks;
			}
		}
	}
}
