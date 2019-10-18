package service;

import entity.Product;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public interface AboutYouParserService {

    List<Product> parseProducts(int countPages) throws InterruptedException;

    AtomicInteger getExceptions();

    int getConnections();
}
