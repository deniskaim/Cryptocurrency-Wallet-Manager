package cryptowallet.offers;

import java.util.List;

public record CryptoCatalog(List<Offering> offerings) {

    private static final String HEADER_MESSAGE = "Available Cryptocurrencies:" + System.lineSeparator();

    public static CryptoCatalog of(List<Offering> offerings) {
        return new CryptoCatalog(offerings);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder(HEADER_MESSAGE);
        for (int i = 0; i < offerings.size(); i++) {
            Offering offering = offerings.get(i);
            result.append(offering.assetID()).append(", ")
                .append(offering.assetName()).append(": ")
                .append(offering.price()).append(" USD");
            if (i != offerings.size() - 1) {
                result.append(System.lineSeparator());
            }
        }
        return result.toString();
    }
}
