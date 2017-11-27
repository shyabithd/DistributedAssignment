/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package distributionassignment.support;

import java.util.ArrayList;

/**
 *
 * @author shyabithd
 */
public class FileDetail {
    
    FileDetail(String fileName) {
        this.fileName = fileName;
        this.commentList = new ArrayList<>();
        this.rate = -1;
        this.rateCount = 1;
    }
    
    public void setFileRate(double rate, int rateCount) {
        this.rateCount = rateCount;
        if(this.rate == -1) {
            this.rate = rate;
        } else {
            this.rate =((this.rate*(this.rateCount-1) + rate)/this.rateCount);
        }
        this.rateCount++;
    }
    
    public int getFileRateCount() {
        return this.rateCount;
    }
    public void addComment(String Comment) {
        if (this.commentList.contains(Comment) == false) {
            this.commentList.add(Comment);
        }
    }
    public String getFileName() {
        return fileName;
    }
    
    public double getFileRate() {
        return rate;
    }
    
    public ArrayList<String> getCommentList() {
        return commentList;
    }
    private String fileName;
    private double rate;
    private ArrayList<String> commentList;
    private int rateCount;
}
