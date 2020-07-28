package it.lorenzotanzi.pokedex;

import android.content.Context;
import android.os.ProxyFileDescriptorCallback;
import android.os.strictmode.InstanceCountViolation;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Pokemon.class}, version = 1)
public abstract class PokemonRoomDatabase extends RoomDatabase {

    public abstract PokemonDao pokemonDao();
    private static  PokemonRoomDatabase INSTANCE;

    static Callback rdc = new Callback() {
        public void onCreate (SupportSQLiteDatabase db) {

        }
        public void onOpen (SupportSQLiteDatabase db) {
           db.execSQL("DROP TABLE IF EXISTS Pokemon");
        }
    };

    static PokemonRoomDatabase getDatabase (final Context context){
        if (INSTANCE == null){
            synchronized (PokemonRoomDatabase.class){
                if (INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), PokemonRoomDatabase.class, "pokemon_database").build();
                }
            }
        }
        return INSTANCE;
    }


}
