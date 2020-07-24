package it.lorenzotanzi.pokedex;

import android.app.Application;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.IdentityHashMap;
import java.util.List;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static it.lorenzotanzi.pokedex.R.*;
import static it.lorenzotanzi.pokedex.R.integer.*;

class PokemonRepository {

    private PokemonRoomDatabase db;
    private PokemonDao pokemonDao;
    private RequestQueue requestQueue;
    private LiveData<List<Pokemon>> allPokemons;
    private MutableLiveData<List<Pokemon>> searchResults = new MutableLiveData<>();


    //CONSTRUCTOR
    PokemonRepository(Application application) {
        Log.d("REPO", "CONSTRUCTING REPOSITORY");
        db = PokemonRoomDatabase.getDatabase(application);
        Log.d("REPO", " DB obtained");
        pokemonDao = db.pokemonDao();
        Log.d("REPO", "DAO obtained");
        requestQueue = Volley.newRequestQueue(application);
        requestQueue.start();
        Log.d("REPO", "Request Queue started");

        allPokemons = pokemonDao.getAllPokemons();
        Log.d("REPO", "Pokemon list obtained");

        if (allPokemons == null) {
            Log.d("REPO", "Pokemon list is empty, fetching data...");
            searchAllPokemons();
        }
    }


    //METHODS TO BE CALLED BY VIEW-MODEL
    LiveData<List<Pokemon>> getAllPokemons(){
        return allPokemons;
    }

    MutableLiveData<List<Pokemon>> getSearchResults(){
        return searchResults;
    }

    void insert(Pokemon pokemon) {
        new InsertPokemonAsyncTask(pokemonDao).execute(pokemon);
    }

//ASYNC TASKS RESPONSIBLE FOR QUERIES

    private void asyncFinished(List<Pokemon> pokemons) {
        searchResults.setValue(pokemons);
    }

    //QUERIES THE DB AND STORES THE RESULT IN searchResults
//    private static class FindPokemonsAsyncTask extends AsyncTask<String, Void, List<Pokemon>>{
//
//        private PokemonDao asyncTaskDao;
//        private PokemonRepository delegate;
//
//        public FindPokemonsAsyncTask(PokemonDao asyncTaskDao){
//            this.asyncTaskDao = asyncTaskDao;
//        }
//
//        @Override
//        protected List<Pokemon> doInBackground(String... strings) {
//            return asyncTaskDao.findPokemon(strings[0]);
//        }
//
//        @Override
//        protected void onPostExecute(List<Pokemon> result) {
//            delegate.asyncFinished(result);
//        }
//    }


            //INSERT A POKEMON RECORD IN THE DB
    private static class InsertPokemonAsyncTask extends AsyncTask<Pokemon, Void, Void> {

        private PokemonDao asyncTaskDao;
        private PokemonRepository delegate = null;

        InsertPokemonAsyncTask(PokemonDao dao) {
            asyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Pokemon... params) {
            Log.d("ASYNCT", "Inserting pokemon");
            asyncTaskDao.insertPokemon(params[0]);
            return null;
        }
    }

    private void imgCall(String url){
        //TO DO
    }

    private void searchPokemonById (String id){
        Log.d("REPO","searching pokemon by ID");
        String url = String.format("https://pokeapi.co/api/v2/pokemon/%s/", id);
        Log.d("REPO","ID: " + id);

        Log.d("REPO", "API Request: " + url);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Gson gson = new Gson();
                        try {
                            Log.d("RESPONSE", "inserting response in DB");

                            String  newPkmnId = response.getString("id");
                            String newPkmnName = response.getString("name");
                            JSONArray newPkmnTypes = response.getJSONArray("types");
                            String Type1 = newPkmnTypes.getString(0);
                            String Type2;
                            if (newPkmnTypes.isNull(1)){
                                Type2 = null;
                            } else {
                                Type2 = newPkmnTypes.getString(1);
                            }
                            Log.d("DEB", "Id: "+newPkmnId + "Name: " + newPkmnName + "Type 1" + Type1 + "Type 2: " + Type2);
                            Pokemon pokemon = new Pokemon(newPkmnId, newPkmnName, Type1, Type2);
                            insert(pokemon);
                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        requestQueue.add(jsonObjectRequest);
    }

    private void searchAllPokemons(){
        for (int i = 1; i < 10; i++) {
            String Index = Integer.toString(i);
            String msg = "searching pokemon " + Index;
            String.format(msg, Index);
            Log.d("REPO",msg);

            searchPokemonById(Index);
        }
    }

}
