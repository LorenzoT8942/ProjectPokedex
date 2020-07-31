package it.lorenzotanzi.pokedex;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import java.util.List;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

class PokemonRepository {

    private PokemonDao pokemonDao;
    private RequestQueue requestQueue;
    private LiveData<List<Pokemon>> allPokemons;
    private MutableLiveData<List<Pokemon>> searchResults = new MutableLiveData<>();


    //CONSTRUCTOR
    PokemonRepository(Application application) {
        Log.d("REPO", "CONSTRUCTING REPOSITORY");
        PokemonRoomDatabase db = PokemonRoomDatabase.getDatabase(application);
        Log.d("REPO", " DB obtained");
        pokemonDao = db.pokemonDao();
        Log.d("REPO", "DAO obtained");
        requestQueue = Volley.newRequestQueue(application);
        requestQueue.start();
        Log.d("REPO", "Request Queue started");
        allPokemons = pokemonDao.getAllPokemons();
        Log.d("REPO", "Pokemon list obtained from DB");
        initDatabase();
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

    void initDatabase(){
        new checkDatabaseAT(pokemonDao).execute(requestQueue);
    }

//ASYNC TASKS RESPONSIBLE FOR QUERIES

    private void asyncFinished(List<Pokemon> pokemons) {
        searchResults.setValue(pokemons);
    }

    private void checkDbFinished(Integer result){
        int checkDB = result;
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

    //QUERIES THE DB TO CHECK IF IT EMPTY OR NOT
    private static class checkDatabaseAT extends AsyncTask<RequestQueue, Void, /*List<Pokemon>*/Integer > {

        private PokemonDao asyncTaskDao;
        private PokemonRepository delegate;

        public checkDatabaseAT(PokemonDao asyncTaskDao) {
            this.asyncTaskDao = asyncTaskDao;
        }

//        @Override
//        protected List<Pokemon> doInBackground(Void... string) {
//            //return asyncTaskDao.checkDatabase();
//        }

        @Override
        protected Integer doInBackground(RequestQueue... requestQueues) {
            if (asyncTaskDao.checkDatabase().length < 1) {
                for (int i = 1; i < 150; i++) {
                    String Index = Integer.toString(i);
                    String msg = "searching pokemon " + Index;
                    String.format(msg, Index);
                    Log.d("REPO", msg);

                    Log.d("REPO", "searching pokemon by ID");
                    String url = String.format("https://pokeapi.co/api/v2/pokemon/%s/", Integer.toString(i));
                    Log.d("REPO", "ID: " + Integer.toString(i));

                    Log.d("REPO", "API Request: " + url);

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Gson gson = new Gson();
                                    try {
                                        Log.d("RESPONSE", "inserting response in DB");

                                        String newPkmnId = response.getString("id");
                                        String newPkmnName = response.getString("name");
                                        JSONArray newPkmnTypes = response.getJSONArray("types");
                                        JSONObject jsonType1 = newPkmnTypes.getJSONObject(0);
                                        String sType1 = jsonType1.getJSONObject("type").getString("name");
                                        String sType2;
                                        if (newPkmnTypes.isNull(1)) {
                                            sType2 = null;
                                        } else {
                                            sType2 = newPkmnTypes.getJSONObject(1).getJSONObject("type").getString("name");

                                        }
                                        Log.d("DEB", "Id: " + newPkmnId + "Name: " + newPkmnName + "Type 1" + sType1 + "Type 2: " + sType2);
                                        Pokemon pokemon = new Pokemon(Integer.parseInt(newPkmnId), newPkmnName, sType1, sType2);
                                        new InsertPokemonAsyncTask(asyncTaskDao).execute(pokemon);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                }
                            });
                    requestQueues[0].add(jsonObjectRequest);
                }
            }
            return null;
        }
    }




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
}
