package server.coinapi.dto;

import com.google.gson.annotations.SerializedName;

public record Asset(@SerializedName("asset_id") String assetID, String name,
                    @SerializedName("type_is_crypto") int typeIsCrypto, String data_start, String data_end,
                    String data_quote_start, String data_quote_end, String data_orderbook_start,
                    String data_orderbook_end, String data_trade_start, String data_trade_end,
                    String data_symbols_count, String volume_1hrs_usd, String volume_1day_usd, String volume_1mth_usd,
                    @SerializedName("price_usd") double price, String id_icon) {
}
