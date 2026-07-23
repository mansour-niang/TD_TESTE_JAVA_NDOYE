package com.creditengine;

public interface CentralBankPort {
    boolean isBanned(String clientId);
}
