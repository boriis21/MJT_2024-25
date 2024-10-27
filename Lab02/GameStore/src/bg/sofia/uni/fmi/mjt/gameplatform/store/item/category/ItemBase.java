package bg.sofia.uni.fmi.mjt.gameplatform.store.item.category;

import bg.sofia.uni.fmi.mjt.gameplatform.store.item.StoreItem;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

public abstract class ItemBase implements StoreItem {
    private String title;
    private BigDecimal price;
    private LocalDateTime releaseDate;
    private double ratingsSum;
    private int ratingsCount;

    public ItemBase(String title, BigDecimal price, LocalDateTime releaseDate) {
        this.title = title;
        this.price = price;
        this.releaseDate = releaseDate;
        this.ratingsSum = 0.0;
        this.ratingsCount = 0;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public BigDecimal getPrice() {
        return price.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public double getRating() {
        if (ratingsCount == 0) {
            return 0.0;
        }

        return (double) Math.round((ratingsSum / ratingsCount) * 100) / 100;
    }

    @Override
    public LocalDateTime getReleaseDate() {
        return releaseDate;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public void setReleaseDate(LocalDateTime releaseDate) {
        this.releaseDate = releaseDate;
    }

    @Override
    public void rate(double rating) {
        ratingsSum += rating;
        ratingsCount++;
    }
}
