/*
 * Add copyright here.
 */

package labs.client;

import labs.pm.service.ProductManager;
import labs.pm.data.Product;
import labs.pm.data.Rating;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author vishalkushwaha
 * @version 1.0
 *          {@code Shop} class represents an application that manages products.
 **/
public class Shop {

    private static final Logger logger = Logger.getLogger(Shop.class.getName());

    private static void printFile(String content, Path file){
        try{
            Files.writeString(file, content, Charset.forName("utf-8"), StandardOpenOption.CREATE);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error printing file");
        }
    }

    public static void main(String[] args) {

        ServiceLoader<ProductManager> serviceLoader = ServiceLoader.load(ProductManager.class);

        ProductManager pm = serviceLoader.findFirst().get();
        AtomicInteger clientCount = new AtomicInteger(0);
        ResourceFormatter formatter = new ResourceFormatter(Locale.US);

        Callable<String> client = () -> {
            String clientId = "Client " + clientCount.incrementAndGet();
            String threadName = Thread.currentThread().getName();
            int productId = ThreadLocalRandom.current().nextInt(5) + 101;
            String languageTag = formatter.getSupportedLocales()
                    .stream()
                    .skip(ThreadLocalRandom.current().nextInt(4))
                    .findFirst()
                    .get();
            StringBuilder log = new StringBuilder();
            log.append(clientId + " " + threadName + "\n-\tstart of log\t-\n");
            log.append(
                    pm.getDiscounts()
                            .entrySet()
                            .stream()
                            .map(entry -> entry.getKey() + " " + formatter.getMoneyFormat().format(entry.getValue()))
                            .collect(Collectors.joining("\n")));

            Product product = pm.reviewProduct(productId, Rating.FOUR_STAR, "Yet another review.");
            log.append((product != null)
                    ? "\nProduct " + productId + " reviewed\n"
                    : "\nProduct " + productId + " not reviewed\n");

            log.append(clientId + " generated report for " + productId + " product");

            log.append("\n-\tend of log\t-\n");
            return log.toString();
        };

        List<Callable<String>> clients = Stream.generate(() -> client)
                .limit(5)
                .collect(Collectors.toList());

        ExecutorService executorService = Executors.newFixedThreadPool(3);
        try {
            List<Future<String>> results = executorService.invokeAll(clients);
            executorService.shutdown();
            results.stream().forEach(
                    result -> {
                        try {
                            System.out.println(result.get());
                        } catch (InterruptedException | ExecutionException e) {
                            Logger.getLogger(Shop.class.getName())
                                    .log(Level.SEVERE, "Error retreiving log " + e.getMessage());
                            e.printStackTrace();
                        }
                    });
        } catch (InterruptedException e) {
            Logger.getLogger(Shop.class.getName())
                    .log(Level.SEVERE, "Error invoking clients " + e.getMessage());
        }
    }
}
