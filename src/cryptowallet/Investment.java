package cryptowallet;

import java.io.Serial;
import java.io.Serializable;

public record Investment(String assetID, double purchasedQuantity, double assetPrice) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1234567891234567L;

    public static Investment of(String assetID, double purchasedQuantity, double assetPrice) {
        return new Investment(assetID, purchasedQuantity, assetPrice);
    }
}
