
import com.google.gson.GsonBuilder;
import entity.Product;
import factory.AboutYouParserServiceFactory;
import factory.FileWriterServiceFactory;
import org.apache.log4j.Logger;
import service.AboutYouParserService;
import service.FileWriterService;

import java.util.List;
import java.util.Scanner;

public class App {

    private final static Logger logger = Logger.getLogger(App.class);

    public static void main(String[] args) throws InterruptedException {
        long time = System.currentTimeMillis();
        int countOfPages = 0;
        while (countOfPages <= 0) {
                countOfPages = scanCountOfPages();
        }
        System.out.println("Wait...");
        AboutYouParserService aboutYouParserService = AboutYouParserServiceFactory.getInstance();
        List<Product> productList = aboutYouParserService.parseProducts(countOfPages);

        FileWriterService fileWriterService = FileWriterServiceFactory.getInstance();
        fileWriterService.writeToFile("output/output.json",
                new GsonBuilder().create().toJson(productList));

        logger.info(String.format("Was parsed %s products", productList.size()));
        logger.info(String.format("Was created %s connections"
                , aboutYouParserService.getConnections()));
        logger.info(String.format("Was arising %s exceptions"
                , aboutYouParserService.getExceptions()));
        logger.info(String.format("%s seconds", (System.currentTimeMillis() - time) / 1000));
    }

    private static int scanCountOfPages() {
        try {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextInt();
    }catch (RuntimeException e){
        System.out.println("enter digit biggest then 0");
    }
        return 0;
    }
}
