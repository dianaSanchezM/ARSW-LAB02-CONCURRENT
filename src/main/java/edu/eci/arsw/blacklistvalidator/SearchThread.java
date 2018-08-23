/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.blacklistvalidator;

import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author 2108310
 */
public class SearchThread extends Thread{
    private int from;
    private int to;
    private String host;
    private AtomicInteger numOcur;
    private ArrayList<Integer> blackListOcur;
    private HostBlacklistsDataSourceFacade skds=HostBlacklistsDataSourceFacade.getInstance();
    private int blackListChecked;

    public SearchThread(int from, int to, String host, AtomicInteger numOcur) {
        this.from = from;
        this.to = to;
        this.host = host;
        this.numOcur =numOcur;
        blackListOcur = new ArrayList<>();
        blackListChecked = 0;
    }

    @Override
    public void run() {
        while (from<to && numOcur.get()<HostBlackListsValidator.getBLACK_LIST_ALARM_COUNT()){
            blackListChecked++;
            if (skds.isInBlackListServer(from, host)){
                numOcur.getAndIncrement();
                blackListOcur.add(from);
            }
            from++;
        }
        
    }

    public ArrayList<Integer> getBlackListOcur() {
        return blackListOcur;
    }

    public void setBlackListOcur(ArrayList<Integer> blackListOcur) {
        this.blackListOcur = blackListOcur;
    }

    public int getBlackListChecked() {
        return blackListChecked;
    }
    
}
