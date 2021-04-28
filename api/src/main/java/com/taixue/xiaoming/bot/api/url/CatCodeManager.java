package com.taixue.xiaoming.bot.api.url;

import java.util.List;

public interface CatCodeManager {
    String requireRecordedCatCode(String catCode);

    List<String> listCatCodes(String string);
}
