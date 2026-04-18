package org.exam.dorisPlugin;

import org.bukkit.inventory.ItemStack;

public class ItemSetResult {
    public boolean success;
    public String message;
    public boolean sync;

    public ItemSetResult(boolean success, String message, boolean sync){
        this.message = message;
        this.sync = sync;
        this.success = success;
    }

    public ItemSetResult setSuccess(String message){
        this.message = message;
        this.success = true;
        return this;
    }
    public ItemSetResult setFail(String message){
        this.message = message;
        this.success = false;
        return this;
    }

}
