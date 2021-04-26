package com.taixue.xiaoming.bot.test;

import com.taixue.xiaoming.bot.api.factory.normal.JsonFileSavedDataFactory;
import com.taixue.xiaoming.bot.api.picture.PictureManager;

import java.io.File;

public class PictureManagerTest {
    public static void main(String[] args) {
        PictureManager manager = new JsonFileSavedDataFactory().forFileOrProduce(
                new File("pictures.json"),
                PictureManager.class,
                PictureManager::new);
        manager.save();
    }
}
