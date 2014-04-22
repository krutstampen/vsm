/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cybercom.svp.machine.dto;

/**
 *
 * @author hajoh1
 */
public class ColorBlock {

    private int numOfStitches;
    private int stichNumber = 0;
    private String threadName;
    private String threadColor;
    private String threadManufacturer;
    private String threadNickname;
    private int threadNumber;

    public ColorBlock(int numOfStitches,
            String threadName, String threadColor, String threadManufacturer, String threadNickname, int threadNumber
    ) {
        this.numOfStitches = numOfStitches;
        this.threadName = threadName;
        this.threadColor = threadColor;
        this.threadManufacturer = threadManufacturer;
        this.threadNickname = threadNickname;
        this.threadNumber = threadNumber;

    }

    /**
     * @return the numOfStitches
     */
    public int getNumOfStitches() {
        return numOfStitches;
    }

    /**
     * @param numOfStitches the numOfStitches to set
     */
    public void setNumOfStitches(int numOfStitches) {
        this.numOfStitches = numOfStitches;
    }

    /**
     * @return the threadName
     */
    public String getThreadName() {
        return threadName;
    }

    /**
     * @param threadName the threadName to set
     */
    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    /**
     * @return the threadColor
     */
    public String getThreadColor() {
        return threadColor;
    }

    /**
     * @param threadColor the threadColor to set
     */
    public void setThreadColor(String threadColor) {
        this.threadColor = threadColor;
    }

    /**
     * @return the threadManufacturer
     */
    public String getThreadManufacturer() {
        return threadManufacturer;
    }

    /**
     * @param threadManufacturer the threadManufacturer to set
     */
    public void setThreadManufacturer(String threadManufacturer) {
        this.threadManufacturer = threadManufacturer;
    }

    /**
     * @return the threadNickname
     */
    public String getThreadNickname() {
        return threadNickname;
    }

    /**
     * @param threadNickname the threadNickname to set
     */
    public void setThreadNickname(String threadNickname) {
        this.threadNickname = threadNickname;
    }

    /**
     * @return the threadNumber
     */
    public int getThreadNumber() {
        return threadNumber;
    }

    /**
     * @param threadNumber the threadNumber to set
     */
    public void setThreadNumber(int threadNumber) {
        this.threadNumber = threadNumber;
    }

    /**
     * @return the stichNumber
     */
    public int getStichNumber() {
        return stichNumber;
    }

    /**
     * @param stichNumber the stichNumber to set
     */
    public void setStichNumber(int stichNumber) {
        this.stichNumber = stichNumber;
    }

    public void increase(int stitches) {
        stichNumber += stitches;

        stichNumber = (stichNumber > numOfStitches) ? numOfStitches : stichNumber;
    }

    public boolean isReady() {
        return (stichNumber >= numOfStitches);
    }

    public void reset() {
        stichNumber = 0;
    }

}
