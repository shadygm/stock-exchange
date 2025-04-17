package nl.rug.aoop.traderapplication.trader;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;

/**
 * Helper class used to represent a list of traders for
 * loading from a yaml file.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Traders extends ArrayList<TraderData> {
}