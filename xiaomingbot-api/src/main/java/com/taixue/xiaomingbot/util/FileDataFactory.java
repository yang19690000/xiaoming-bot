package com.taixue.xiaomingbot.util;

public interface FileDataFactory<T extends SavableFile> {
    T produce();
}
