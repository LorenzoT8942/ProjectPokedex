package it.lorenzotanzi.pokedex;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class PokemonRvAdapter extends RecyclerView.Adapter<PokemonRvAdapter.ViewHolder> { // PROBLEMA QUI

    private Map<String, String> colors = new HashMap<>();
    private int pokemonItemLayout;
    Context context;
    private List<Pokemon> pokemonList;

    public PokemonRvAdapter (int layoutId, Context context){
        pokemonItemLayout = layoutId;
        this.context = context;
        colors.put("Normal","#A8A77A");
        colors.put("Fire","#EE8130");
        colors.put("Water","#6390F0");
        colors.put("Electric","#F7D02C");
        colors.put("Grass","#7AC74C");
        colors.put("Ice","#96D9D6");
        colors.put("Fighting","#C22E28");
        colors.put("Poison","#A33EA1");
        colors.put("Ground","#E2BF65");
        colors.put("Flying","#A98FF3");
        colors.put("Psychic","#F95587");
        colors.put("Bug","#A6B91A");
        colors.put("Rock","#B6A136");
        colors.put("Ghost","#735797");
        colors.put("Dragon","#6F35FC");
        colors.put("Dark","#705746");
        colors.put("Steel","#B7B7CE");
        colors.put("Fairy","#D685AD");

    }

    public void setPokemonList(List<Pokemon> pokemons){
        pokemonList = pokemons;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PokemonRvAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_pokemon_detail, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PokemonRvAdapter.ViewHolder viewHolder, int position) {
        TextView tv_pkmn_num = viewHolder.tv_pkmn_num;
        TextView tv_pkmn_name = viewHolder.tv_pkmn_name;
        ImageView iv_pkmn_icon = viewHolder.iv_pkmn_icon;
        View cardView = viewHolder.cardView;
        ImageView iv_pkmn_type1 = viewHolder.iv_pkmn_type1;
        ImageView iv_pkmn_type2 = viewHolder.iv_pkmn_type2;

        String idString = pokemonList.get(position).getPkmnNum().toString();
        String pkmnNameString = pokemonList.get(position).getPkmnName();
         String type1str = pokemonList.get(position).getType1();
         String type2str = pokemonList.get(position).getType2();
         String type1col = colors.get(type1str);

        //REQUEST .PNG OF THE POKEMON SPRITE TO SET
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.pokeball);
        requestOptions.error(R.drawable.pokeball);
        Glide.with(context)
                .setDefaultRequestOptions(requestOptions)
                .asBitmap()
                .load("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + (position + 1) + ".png")
                .into(iv_pkmn_icon);

        //STRING ADJUSTMENTS
        if (Integer.parseInt(idString) < 10) idString = new StringBuilder().append("#00").append(idString).toString();
        else if (Integer.parseInt(idString) < 100) idString = new StringBuilder().append("#0").append(idString).toString();
        else idString = new StringBuilder().append("#").append(idString).toString();
        pkmnNameString = pkmnNameString.substring(0, 1).toUpperCase() + pkmnNameString.substring(1);

        //BINDING OF POKEMON INFO AND CREATION OF GRADIENT BACKGROUND
        tv_pkmn_num.setText(idString);
        tv_pkmn_name.setText(pkmnNameString);

        String _type1str = type1str.substring(0,1).toLowerCase() + type1str.substring(1);
        int id = context.getResources().getIdentifier(_type1str , "drawable", context.getPackageName());
        iv_pkmn_type1.setImageResource(id);
        Log.d("ADAPTER", "Pokemon " + idString + "drawable 1 id:" + Integer.toString(id));




        //SE IL POKEMON IN POSIZIONE position HA UN TIPO 2 ALLORA VIENE SETTATO IL TESTO DELLA TEXT VIEW
        //tv_pkmn_type2 E VIENE CREATA UNA NUOVA ISTANZA DI GradientDrawable CHE VERRA' USATA COME
        //BACKGROUND DELLA CARD VIEW

        if (type2str == null) {
            /*SE INVECE IL POKEMON HA UN SOLO TIPO VIENE SETTATO IL TESTO DELLA TEXT VIEW tv_pkmn_type2 A UNA STRINGA VUOTA
             * E VIENE SETTATO LO SFONDO DELLA CARD VIEW AL COLORE DELL'UNICO TIPO RELATIVO AL POKEMON*/
            Log.d("ADAPTER", "COLORS:" + type1str + " " + type1col + ", " + type2str + " null");
            iv_pkmn_type2.setVisibility(View.INVISIBLE);
            int backgroundColor = Color.parseColor(type1col);
            cardView.setBackgroundColor(backgroundColor);

        } else {
            String type2col = colors.get(type2str);
            iv_pkmn_type2.setVisibility(View.VISIBLE);

            String _type2str = type2str.substring(0,1).toLowerCase() + type2str.substring(1);
            int id2 = context.getResources().getIdentifier(_type2str , "drawable", context.getPackageName());
            Log.d("ADAPTER", "Pokemon " + idString + "drawable 2 id:" + Integer.toString(id2));
            int[] gradientColors = {Color.parseColor((type1col)), Color.parseColor(type2col)}; // PROBLEMA QUI
            Log.d("ADAPTER", "Pokemon " + idString + " COLORS:" + type1str + " " +  type1col + ", " + type2str + " "+ type2col);
            GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, gradientColors);
            gd.setGradientType(GradientDrawable.LINEAR_GRADIENT);
            gd.setCornerRadius(30);
            cardView.setBackground(gd);
            iv_pkmn_type2.setImageResource(id2);
        }
    }

    @Override
    public int getItemCount() {
        return pokemonList == null? 0 : pokemonList.size();
    }



    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView iv_pkmn_icon;
        TextView tv_pkmn_num;
        TextView tv_pkmn_name;

        View cardView;
        ImageView iv_pkmn_type1;
        ImageView iv_pkmn_type2;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_pkmn_icon = itemView.findViewById(R.id.iv_pkmn_icon);
            tv_pkmn_name = itemView.findViewById(R.id.tv_pkmn_name);
            tv_pkmn_num = itemView.findViewById(R.id.tv_pkmn_num);
            cardView = itemView.findViewById(R.id.cl_card);
            iv_pkmn_type1 = itemView.findViewById(R.id.iv_pkmn_type1);
            iv_pkmn_type2 = itemView.findViewById(R.id.iv_pkmn_type2);
        }
    }
}
