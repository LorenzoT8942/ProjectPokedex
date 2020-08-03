package it.lorenzotanzi.pokedex;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Pokemon {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "ID")
    private Integer pkmnNum;

    @ColumnInfo(name = "Name")
    private String pkmnName;

    @ColumnInfo(name = "Type1")
    private String type1;

    @ColumnInfo(name = "Type2")
    private String type2;


    public Pokemon(@NonNull Integer pkmnNum, String pkmnName, String type1, String type2) {
        this.pkmnNum = pkmnNum;
        this.pkmnName = pkmnName;
        this.type1 = type1;
        this.type2 = type2;

    }

    @NonNull
    public Integer getPkmnNum() {
        return pkmnNum;
    }

    public void setPkmnNum(@NonNull Integer pkmnNum) {
        this.pkmnNum = pkmnNum;
    }

    public String getPkmnName() {
        return pkmnName;
    }

    public void setPkmnName(String pkmnName) {
        this.pkmnName = pkmnName;
    }

    public String getType1() {
        return type1;
    }

    public void setType1(String type1) {
        this.type1 = type1;
    }

    public String getType2() {
        return type2;
    }

    public void setType2(String type2) {
        this.type2 = type2;
    }
}


