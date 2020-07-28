package it.lorenzotanzi.pokedex;

import java.util.List;

import javax.xml.namespace.QName;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface PokemonDao {

    @Insert
    void insertPokemon(Pokemon pokemon);

    @Query("SELECT * FROM Pokemon ORDER BY ID")
    LiveData<List<Pokemon>> getAllPokemons();

    @Query("SELECT * FROM Pokemon WHERE name LIKE ':name%'")
    List<Pokemon> findPokemon();

    @Query("SELECT * FROM Pokemon LIMIT 1")
    Pokemon[] checkDatabase();
}
