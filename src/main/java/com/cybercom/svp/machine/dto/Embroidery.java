/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cybercom.svp.machine.dto;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author hajoh1
 */
public class Embroidery {

    private List<ColorBlock> colorBlocks = new ArrayList<>();

    public Embroidery() {
        setupEmbroidery();
    }

    private void setupEmbroidery() {

        colorBlocks.add(new ColorBlock(2000, "2001", "Color1", "Manu1", "Nick1", 1));
        colorBlocks.add(new ColorBlock(1150, "2010", "Color2", "Manu1", "Nick1", 1));
        colorBlocks.add(new ColorBlock(3430, "2011", "Color3", "Manu1", "Nick1", 1));
        colorBlocks.add(new ColorBlock(5550, "2101", "Color4", "Manu1", "Nick1", 1));
        colorBlocks.add(new ColorBlock(3200, "2004", "Color5", "Manu1", "Nick1", 1));
        colorBlocks.add(new ColorBlock(1440, "2006", "Color6", "Manu1", "Nick1", 1));
        colorBlocks.add(new ColorBlock(2320, "2008", "Color7", "Manu1", "Nick1", 1));

        for (ColorBlock cb : colorBlocks) {
            cb.reset();
            designNumStitches += cb.getNumOfStitches();

        }

    }

    private int currentBlock = 0;
    private String designImg = "aaaaaaa";
    private int designNumStitches;

    /**
     * @return the colorBlocks
     */
    public List<ColorBlock> getColorBlocks() {
        return colorBlocks;
    }

    /**
     * @return the currentBlock
     */
    public int getCurrentBlock() {
        return currentBlock;
    }

    /**
     * @return the designImg
     */
    public String getDesignImg() {
        return designImg;
    }

    /**
     * @return the designNumStitches
     */
    public int getDesignNumStitches() {
        return designNumStitches;
    }

    /**
     * @return the designStitchNumber
     */
    public int getDesignStitchNumber() {
        int designStitchNumber = 0;
        for (ColorBlock cb : colorBlocks) {

            designStitchNumber += cb.getStichNumber();

        }

        return designStitchNumber;
    }

    public void increase(int stitches) throws EmbroideryFinishException {
        if (!colorBlocks.get(currentBlock).isReady()) {
            colorBlocks.get(currentBlock).increase(stitches);
        } else {
            currentBlock++;
            if (currentBlock >= colorBlocks.size()) {
                throw new EmbroideryFinishException();
            }
        }
    }

    public static class EmbroideryFinishException extends Exception {

        public EmbroideryFinishException() {
        }
    }

}
