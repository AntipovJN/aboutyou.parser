package service.impl;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.internal.JsonContext;
import entity.Product;
import org.apache.log4j.Logger;
import service.AboutYouParserService;
import thread.ProductExtractThread;
import thread.pool.WorkQueue;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class AboutYouParserServiceImpl implements AboutYouParserService {

    private final Logger logger = Logger.getLogger(AboutYouParserServiceImpl.class);

    private final String FIRST_PART_API_URL_PRODUCT_LIST =
            "https://api-cloud.aboutyou.de/v1/products?with=attributes%3Akey%28brand%" +
                    "7Cname%7CquantityPerPack%7CplusSize%7Csustainability%7CcolorDetail%" +
                    "29%2CadvancedAttributes%3Akey%28materialCompositionTextile%7Csiblings%" +
                    "29%2Cvariants%2Cvariants.attributes%3Akey%28shopSize%" +
                    "7CcategoryShopFilterSizes%7Ccup%7Ccupsize%7CvendorSize%" +
                    "7Clength%7Cdimension3%7Csort%29%2Cimages.attributes%3Alegacy%28false%29%" +
                    "3Akey%28imageNextDetailLevel%7CimageBackground%7CimageFocus%7CimageGender%" +
                    "7CimageType%7CimageView%29%2Ccategories%2CpriceRange&filters%5Bcategory%" +
                    "5D=20290&sortDir=desc&sortScore=category_scores&sortChannel=" +
                    "etkp&page=2&perPage=";

    private final String SECOND_PART_API_URL_PRODUCT_LIST =
            "&campaignKey=px&shopId=139";

    private final String FIRST_PART_API_URL_SPECIFIC_PRODUCT =
            "https://api-cloud.aboutyou.de/v1/products/";

    private final String SECOND_PART_API_URL_SPECIFIC_PRODUCT =
            "?with=attributes%2CadvancedAttributes%3Akey%28modelHeight" +
                    "%7CbulletPoints%7CmaterialCompositionTextile%7CmodelMeasurements" +
                    "%7Csiblings%29%2Cvariants%2Cvariants.attributes%3Akey%28shopSize" +
                    "%7Ccup%7Ccupsize%7CvendorSize%7Clength%7Cdimension3%7Csort" +
                    "%7CwornByModel%29%2Cvariants.advancedAttributes%3Akey" +
                    "%28variantCrosssellings%7CmodelHeight%29%2Cimages.attributes" +
                    "%3Alegacy%28false%29%3Akey%28imageNextDetailLevel" +
                    "%7CimageBackground%7CimageFocus%7CimageGender%7CimageType" +
                    "%7CimageView%29%2Ccategories%2Csiblings%2Csiblings.attributes" +
                    "%3Akey%28brand%7Cname%7CquantityPerPack%7CplusSize%7CcolorDetail" +
                    "%29%2Csiblings.advancedAttributes%3Akey%28materialCompositionTextile" +
                    "%7Csiblings%29%2Csiblings.variants%2Csiblings.variants.attributes" +
                    "%3Akey%28shopSize%7Ccup%7Ccupsize%7CvendorSize%7Clength%7Cdimension3" +
                    "%7Csort%29%2Csiblings.images.attributes%3Alegacy%28false%29%3Akey" +
                    "%28imageNextDetailLevel%7CimageBackground%7CimageFocus%7CimageGender" +
                    "%7CimageType%7CimageView%29%2Csiblings.priceRange" +
                    "%2CpriceRange&campaignKey=px&shopId=139";

    private List<Integer> pageCount;
    private List<Product> productList;
    private volatile AtomicInteger exceptions;
    private volatile List<JsonContext> jsons;
    private volatile Set<Integer> idSet;
    private WorkQueue workQueue;
    private int connections;

    public AboutYouParserServiceImpl() {
        this.pageCount = new ArrayList<>();
        this.productList = new ArrayList<>();
        this.exceptions = new AtomicInteger(0);
        this.connections = 0;
        this.jsons = new ArrayList<>();
        this.idSet = new HashSet<>();
        this.workQueue = new WorkQueue(4);
    }

    @Override
    public List<Product> parseProducts(int countPages) throws InterruptedException {
        pageCount.add(countPages*102);
        jsons = getJsonContextList(pageCount, FIRST_PART_API_URL_PRODUCT_LIST,
                SECOND_PART_API_URL_PRODUCT_LIST);
        idSet.addAll(getProductsId(jsons));
        jsons = getJsonContextList(idSet, FIRST_PART_API_URL_SPECIFIC_PRODUCT,
                SECOND_PART_API_URL_SPECIFIC_PRODUCT);
        List<Integer> idList = new ArrayList<>(idSet);
        for (int i = 0; i < idList.size(); i++) {
            ProductExtractThread productExtractThread = new ProductExtractThread(
                    idList.get(i), jsons.get(i), productList);
            workQueue.execute(productExtractThread);
            exceptions.getAndAdd(productExtractThread.getExceptions());
            connections++;
        }
        while (productList.size() != idList.size()) {
            Thread.sleep(1000);
        }
        return productList;
    }

    private List<JsonContext> getJsonContextList(Collection<Integer> collection, String firstPartUrl,
                                                 String secondPartUrl) {
        List<JsonContext> jsonContexts = new ArrayList<>();
        for (Integer id : collection) {
            try {
                jsonContexts.add(getJsonContext(firstPartUrl, secondPartUrl, id));
            } catch (IOException e) {
                logger.error(String.format("Failed get json from url %s%s%s",
                        firstPartUrl,id,secondPartUrl));
                exceptions.getAndAdd(1);
            }
            connections++;
        }
        return jsonContexts;
    }

    private Set<Integer> getProductsId(List<JsonContext> jsons) {
        Set<Integer> setId = new LinkedHashSet<>();
        for (JsonContext json : jsons) {
            setId.addAll(json.read("$.entities[*].id"));
            setId.addAll(json.read("$.entities[*]." +
                    "advancedAttributes.siblings.values[*].fieldSet[0].[*].productId"));
        }
        return setId;
    }

    private JsonContext getJsonContext(String firstPartUrl, String secondPartUrl,
                                       Integer id) throws IOException {
        StringBuffer stringBuffer;
        stringBuffer = new StringBuffer(firstPartUrl);
        URL obj = new URL(stringBuffer.append(id).append(secondPartUrl).toString());
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
        connection.setRequestMethod("GET");
        return (JsonContext) JsonPath.parse(obj);
    }

    public AtomicInteger getExceptions() {
        return exceptions;
    }

    public int getConnections() {
        return connections;
    }
}
