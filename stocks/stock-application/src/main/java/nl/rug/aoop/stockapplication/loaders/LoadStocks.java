package nl.rug.aoop.stockapplication.loaders;

import lombok.extern.slf4j.Slf4j;
import nl.rug.aoop.stockapplication.stock.Stocks;
import nl.rug.aoop.util.YamlLoader;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Loads the stocks from the yaml file.
 */
@Slf4j
public class LoadStocks {
    /**
     * Loads the stocks from the yaml file.
     * @param path The path to the yaml file.
     * @return The stocks.
     */
    public Stocks loadStock(String path) {
        YamlLoader loader = new YamlLoader(Path.of(path));
        Stocks loadInto;
        try {
            loadInto = loader.load(Stocks.class);
        } catch (IOException e) {
            log.error("Could not load stocks", e);
            return null;
        }
        Stocks stocks = new Stocks();
        stocks.setStocks(loadInto.getStocks());
        return stocks;
    }
}
