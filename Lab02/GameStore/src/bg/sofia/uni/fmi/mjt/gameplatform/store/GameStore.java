package bg.sofia.uni.fmi.mjt.gameplatform.store;

import bg.sofia.uni.fmi.mjt.gameplatform.store.item.StoreItem;
import bg.sofia.uni.fmi.mjt.gameplatform.store.item.filter.ItemFilter;

import java.math.BigDecimal;
import java.util.Arrays;

public class GameStore implements StoreAPI {
    private static final BigDecimal _VAN40 = new BigDecimal("40");
    private static final BigDecimal _100YO = new BigDecimal("100");

    private final StoreItem[] availableItems;
    private boolean appliedPromocode;

    public GameStore(StoreItem[] availableItems) {
        this.availableItems = Arrays.copyOf(availableItems, availableItems.length);
        this.appliedPromocode = false;
    }

    @Override
    public StoreItem[] findItemByFilters(ItemFilter[] itemFilters) {
        StoreItem[] filteredItems = new StoreItem[availableItems.length];
        int filteredItemsCount = 0;

        for (StoreItem item : availableItems) {
            boolean matchesAllFilters = true;

            for (ItemFilter filter : itemFilters) {
                if (!filter.matches(item)) {
                    matchesAllFilters = false;
                }
            }

            if (matchesAllFilters) {
                filteredItems[filteredItemsCount++] = item;
            }
        }

        return Arrays.copyOf(filteredItems, filteredItemsCount);
    }

    private BigDecimal discountedPrice(BigDecimal originalPrice, BigDecimal discount) {
        BigDecimal discountFactor = discount.divide(new BigDecimal("100"));

        BigDecimal discountAmount = originalPrice.multiply(discountFactor);
        return originalPrice.subtract(discountAmount);
    }

    @Override
    public void applyDiscount(String promoCode) {
        if (!appliedPromocode) {
            appliedPromocode = true;

            switch (promoCode) {
                case "VAN40" -> {
                    for (StoreItem item : availableItems) {
                        item.setPrice(discountedPrice(item.getPrice(), _VAN40));
                    }
                }

                case "100YO" -> {
                    for (StoreItem item : availableItems) {
                        item.setPrice(discountedPrice(item.getPrice(), _100YO));
                    }
                }
            }
        }
    }

    @Override
    public boolean rateItem(StoreItem item, int rating) {
        if (rating >= 1 && rating <= 5) {
            item.rate(rating);
            return true;
        }

        return false;
    }
}
