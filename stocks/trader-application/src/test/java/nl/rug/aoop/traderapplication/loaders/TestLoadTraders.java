package nl.rug.aoop.traderapplication.loaders;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TestLoadTraders {
    private LoadTraders loadTraders;
    private String path = "stocks/data/traders.yaml";
    @BeforeEach
    public void setUp() {
        loadTraders = new LoadTraders();
    }
    @Test
    public void testConstructor() {
        assertNotNull(loadTraders);
    }
    @Test
    public void testInCorrectPath(){
        assertNull(loadTraders.loadTraders(path));
    }
}
