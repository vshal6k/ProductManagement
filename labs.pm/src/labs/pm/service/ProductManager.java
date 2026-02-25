/*
 * Add copyright here.
 */

package labs.pm.service;

import labs.pm.data.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

/**
 * @author vishalkushwaha
 **/
public interface ProductManager {

    public Product createProduct(int id, String name, BigDecimal price, Rating rating, LocalDate bestBefore) throws ProductManagerException;

    public Product createProduct(int id, String name, BigDecimal price, Rating rating) throws ProductManagerException;

    Product findProduct(int id) throws ProductManagerException ;

    Product reviewProduct(int id, Rating rating, String comments) throws ProductManagerException;

    Product reviewProduct(Product product, Rating rating, String comments) throws ProductManagerException;

    Map<String, BigDecimal> getDiscounts() throws ProductManagerException;
}
