package nl.rug.aoop.traderapplication.loaders;

import lombok.extern.slf4j.Slf4j;
import nl.rug.aoop.util.YamlLoader;

import java.io.IOException;
import java.nio.file.Path;
import nl.rug.aoop.traderapplication.trader.Traders;

/**
 * Class that loads the traders from a yaml file.
 */
@Slf4j
public class LoadTraders {
    /**
     * Loads the traders from a yaml file.
     * @param path The path to the yaml file.
     * @return The traders.
     */
    public Traders loadTraders(String path) {
        YamlLoader loader = new YamlLoader(Path.of(path));
        Traders loadInto;
        try {
            loadInto = loader.load(Traders.class);
        } catch (IOException e) {
            log.error("Could not load traders", e);
            return null;
        }
        return loadInto;
    }
}