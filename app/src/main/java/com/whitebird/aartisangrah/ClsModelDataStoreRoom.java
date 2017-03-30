package com.whitebird.aartisangrah;

/**
 * Created by girish on 22/1/17.
 */

public class ClsModelDataStoreRoom {

    //Store the list of god aarti and bhajans
    private String listOfGodComponent;
    private String lyrics;
    private int imagesOfGodComponent;



    public ClsModelDataStoreRoom(String listOfGodComponent, int imagesOfGodsModelCl,String lyrics) {
        this.listOfGodComponent = listOfGodComponent;
        this.imagesOfGodComponent = imagesOfGodsModelCl;
        this.lyrics = lyrics;
    }

    public String getLyrics(){
        return lyrics;
    }
    public String getListOfStringComponent(){
        return listOfGodComponent;
    }

    public int getImageOfComponents(){
        return imagesOfGodComponent;
    }
    public void setListOfGodComponent(String listOfGodComponent){
        this.listOfGodComponent = listOfGodComponent;
    }
}
