package de.testaufgaben.nodapo;

import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class Shop.
 */
public class Shop {
	private static final Logger LOG = LoggerFactory.getLogger(Shop.class);

	public final String name;
	private Double income = 0.0;
	private List<Book> catalogue = Collections.synchronizedList(new ArrayList<>());
	
	/**
	 * Instantiates a new shop.
	 *
	 * @param name the name
	 */
	public Shop(String name) {
		this.name = name;
	}

	/**
	 * Gets the income.
	 *
	 * @return the income
	 */
	public Double getIncome() {
		return income;
	}
	
	/**
	 * Gets the catalogue.
	 *
	 * @return the catalogue
	 */
	public List<Book> getCatalogue() {
		return catalogue;
	}
	
	/**
	 * Gets the catalogue without duplicates.
	 *
	 * @return the catalogue without duplicates
	 */
	public List<Book> getCatalogueWithoutDuplicates() {
		return catalogue.stream().distinct().collect(Collectors.toList());
	}

	/**
	 * Pay for a book (increase shop's amount).
	 *
	 * @param sum the book's price
	 */
	public void pay(Double sum) {
		income += sum; 
	}
	
	/**
	 * Adds the book to shop's catalogue.
	 *
	 * @param book the book
	 */
	public void addToCatalogue(Book book) {
		if (isISBN13(book.isbn)) {
			catalogue.add(book);
			LOG.info("Book {} was succesfully added to the catalogue of shop {}",
					book, name);
		} else {
			LOG.error("Book {} has wrong ISBN13", book.toString());
		}
	}
	
	/**
	 * Checks if the number is real isbn13.
	 *
	 * @param input tested number
	 * @return true, if is isbn13
	 */
	public static boolean isISBN13(String input) {
	    String digits = input.replaceAll("[\\-]", "");
	    if (digits.length() != 13 || !digits.matches("[0-9]+")) {
	        return false;
	    }
	    List<Integer> digitsList = CharBuffer.wrap(digits.toCharArray()).chars()
	              .mapToObj(ch -> Character.getNumericValue(ch))
	              .collect(Collectors.toList());
	    Integer even = 0;
	    Integer odd = 0;
	    for(int i = 0; i < digitsList.size()/2; i++) {
	        even += digitsList.get(2 * i);
	        odd += digitsList.get(2 * i + 1);
	    }
	    return digitsList.get(digitsList.size() - 1) == 
	    		(10 - (even + 3 * odd) % 10) % 10;
	}
}
