package com.onlinefooddeliverymanagement.OnlineFoodDeliveryManagement.service;

import com.onlinefooddeliverymanagement.OnlineFoodDeliveryManagement.model.Menu_And_Food_Item;
import com.onlinefooddeliverymanagement.OnlineFoodDeliveryManagement.util.FileHelper;
import java.io.IOException;

public class MenuItemService {

    private final String filePath;

    public MenuItemService(String filePath) {
        this.filePath = filePath;
    }

    public void addMenuItem(Menu_And_Food_Item item) throws IOException {
        FileHelper.appendLine(filePath, item.toFileString());
    }
}