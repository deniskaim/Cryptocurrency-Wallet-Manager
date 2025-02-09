package cryptowallet.offers;

public record Offering(String assetID, String assetName, double price) {

    public static Offering of(String assetID, String assetName, double price) {
        if (assetID == null || assetID.isBlank()) {
            throw new IllegalArgumentException("assetID cannot be null or blank!");
        }
        if (assetName == null || assetName.isBlank()) {
            throw new IllegalArgumentException("name cannot be null or blank!");
        }
        if (Double.compare(price, 0d) <= 0) {
            throw new IllegalArgumentException("price cannot be below 0.00 USD!");
        }

        return new Offering(assetID, assetName, price);
    }
}
