package thread;

import com.jayway.jsonpath.internal.JsonContext;
import entity.Product;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.List;

public class ProductExtractThread extends Thread{

    protected int id;
    private JsonContext json;
    private List<Product> products;
    private int exceptions;

    public ProductExtractThread(int id, JsonContext json, List<Product> products) {
        this.id = id;
        this.json = json;
        this.products = products;
        this.exceptions = 0;
    }

    @Override
    public void run() {
        try {
            products.add(extractProductFromJson(id, json));
        } catch (IOException e) {
            exceptions++;
        }
    }

    private String generateProductURL(Product product) {
        return new StringBuffer("https://www.aboutyou.de/p/")
                .append(convertProductFieldToPartUrl(product.getBrand()))
                .append("/").append(convertProductFieldToPartUrl(product.getName()))
                .append("-").append(product.getId()).toString();
    }

    private String convertProductFieldToPartUrl(String string) {
        return string.replaceAll("/[^a-z0-9]", "")
                .replaceAll(" {2}", " ")
                .replace("'", "")
                .replace('/', '-')
                .replace(' ', '-')
                .replace("#?", "#")
                .toLowerCase();
    }

    private Product extractProductFromJson(Integer id, JsonContext json) throws IOException {
        Product product = new Product(id,
                json.read("$.attributes.name.values.label").toString(),
                json.read("$.attributes.brand.values.label").toString(),
                json.read("$.attributes.color.values.value").toString(),
                Double.valueOf(json.read("$.variants[0].price.withTax")
                        .toString()) / 100);
        product.setUrl(generateProductURL(product));
        product.setArticleID(getArticlesID(product));
        return product;
    }

    private String getArticlesID(Product product) throws IOException {
        try {
            Document document = Jsoup.connect(product.getUrl()).get();
            Element articleID = document.getElementsByAttributeValue(
                    "class", "ProductDetails__Text-d5kk8t-5 lcSOLT").first();
            return articleID.text().substring(9);
        } catch (NullPointerException e) {
            exceptions++;
            return "fail";
        }
    }

    public int getExceptions() {
        return exceptions;
    }
}
