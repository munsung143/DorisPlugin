package org.exam.dorisPlugin;

import java.util.List;

public class RandomTable {
    public List<RandomGroup> groups = null;

    public int weightSum;

    public void CalcSum(){
        weightSum = 0;
        for (RandomGroup group : groups){
            weightSum += group.weight;
        }
    }
}
