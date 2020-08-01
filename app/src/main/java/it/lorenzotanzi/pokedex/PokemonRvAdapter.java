package it.lorenzotanzi.pokedex;

import android.content.Context;
import android.net.sip.SipSession;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class PokemonRvAdapter extends RecyclerView.Adapter<PokemonRvAdapter.ViewHolder> {

    private int pokemonItemLayout;
    Context context;
    private List<Pokemon> pokemonList;

    public PokemonRvAdapter (int layoutId, Context context){
        pokemonItemLayout = layoutId;
        this.context = context;
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
        TextView tv_pkmn_type1 = viewHolder.tv_pkmn_type1;
        TextView tv_pkmn_type2 = viewHolder.tv_pkmn_type2;
        ImageView iv_pkmn_icon = viewHolder.iv_pkmn_icon;

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.pokeball);
        requestOptions.error(R.drawable.pokeball);

        Glide.with(context)
                .setDefaultRequestOptions(requestOptions)
                .asBitmap()
                .load("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + (position+1) + ".png")
                .into(viewHolder.iv_pkmn_icon);
        String idString = pokemonList.get(position).getPkmnNum().toString();
        if (Integer.parseInt(idString) < 10){
            idString = new StringBuilder().append("#00").append(idString).toString();
        }else if (Integer.parseInt(idString) < 100){
            idString = new StringBuilder().append("#0").append(idString).toString();
        }else {
            idString = new StringBuilder().append("#").append(idString).toString();
        }
        String pkmnNameString = pokemonList.get(position).getPkmnName();
        pkmnNameString = pkmnNameString.substring(0,1).toUpperCase() + pkmnNameString.substring(1);

        tv_pkmn_num.setText(idString);
        tv_pkmn_name.setText(pkmnNameString);
        tv_pkmn_type1.setText(pokemonList.get(position).getType1());
        if (pokemonList.get(position).getType2() != null){
            tv_pkmn_type2.setText(pokemonList.get(position).getType2());
        }else{
            tv_pkmn_type2.setText("");
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
        TextView tv_pkmn_type1;
        TextView tv_pkmn_type2;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_pkmn_icon = itemView.findViewById(R.id.iv_pkmn_icon);
            tv_pkmn_name = itemView.findViewById(R.id.tv_pkmn_name);
            tv_pkmn_num = itemView.findViewById(R.id.tv_pkmn_num);
            tv_pkmn_type1 = itemView.findViewById(R.id.tv_pkmn_type1);
            tv_pkmn_type2 = itemView.findViewById(R.id.tv_pkmn_type2);
        }
    }
}
