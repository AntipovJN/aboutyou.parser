package entity;

import java.util.Objects;

public class Product {

    private Integer id;
    private String name;
    private String brand;
    private String color;
    private Double price;
    private String articleID;
    private String url;

    public Product(Integer id) {
        this.id = id;
    }

    public Product(Integer id, String name, String brand, String color, Double price) {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.color = color;
        this.price = price;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getArticleID() {
        return articleID;
    }

    public void setArticleID(String articleID) {
        this.articleID = articleID;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Product product = (Product) o;

        if (!Objects.equals(id, product.id))
            return false;
        if (getName() != null ? !getName().equals(product.getName()) : product.getName() != null)
            return false;
        if (getBrand() != null ? !getBrand().equals(product.getBrand()) : product.getBrand() != null)
            return false;
        if (getColor() != null ? !getColor().equals(product.getColor()) : product.getColor() != null)
            return false;
        if (getPrice() != null ? !getPrice().equals(product.getPrice()) : product.getPrice() != null)
            return false;
        if (getArticleID() != null ? !getArticleID().equals(product.getArticleID()) : product.getArticleID() != null)
            return false;
        return getUrl() != null ? getUrl().equals(product.getUrl()) : product.getUrl() == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getBrand() != null ? getBrand().hashCode() : 0);
        result = 31 * result + (getColor() != null ? getColor().hashCode() : 0);
        result = 31 * result + (getPrice() != null ? getPrice().hashCode() : 0);
        result = 31 * result + (getArticleID() != null ? getArticleID().hashCode() : 0);
        result = 31 * result + (getUrl() != null ? getUrl().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", brand='" + brand + '\'' +
                ", color='" + color + '\'' +
                ", price=" + price +
                ", articleID='" + articleID + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}