package nl.rug.aoop.traderapplication.managers;

import nl.rug.aoop.model.TraderDataModel;
import nl.rug.aoop.model.TraderModel;
import nl.rug.aoop.traderapplication.trader.TraderData;
import nl.rug.aoop.traderapplication.trader.Traders;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;

/**
 * Class that manages the trader model.
 */
public class TraderManager {

    /**
     * Initializes the trader model.
     * @param listTraders The list of traders to initialize the model with (from the yaml file).
     * @return The list of trader models.
     */
    public static List<TraderDataModel> initTraderModel(Traders listTraders) {
        if(listTraders == null) {
            return new CopyOnWriteArrayList<>();
        }

        List<TraderDataModel> traderDataModelList = new CopyOnWriteArrayList<>();
        for(TraderData td : listTraders) {
            TraderModel traderModel = new TraderModel(td.getId(), td.getName(),
                    td.getFunds(), td.getStockPortfolio().getOwnedShares());
            traderDataModelList.add(traderModel);
        }

        if(!traderDataModelList.isEmpty()) {
            return traderDataModelList;
        } else {
            return null;
        }
    }
}
