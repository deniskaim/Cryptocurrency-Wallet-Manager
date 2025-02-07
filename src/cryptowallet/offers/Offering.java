package cryptowallet.offers;

public record Offering(String assetID, double price) {

    public static Offering of(String assetID, double price) {
        if (assetID == null || assetID.isBlank()) {
            throw new IllegalArgumentException("assetID cannot be null or blank!");
        }
        if (Double.compare(price, 0d) <= 0) {
            throw new IllegalArgumentException("price cannot be below 0.00 USD!");
        }

        return new Offering(assetID, price);
    }
}
