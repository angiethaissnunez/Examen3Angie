package com.example.examen3angie;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.example.examen3angie.Configuracion.Medicamentos;

import java.util.ArrayList;

public class Adaptador extends BaseAdapter {

    private Context context;
    private ArrayList<Medicamentos> listItem;
    private ArrayList<Medicamentos> filterlist;
    private CustomFilter filter;

   /* public Adaptador(Context context, ArrayList<Medicamentos> listItem, ArrayList<Medicamentos> filterlist, CustomFilter filter) {
        this.context = context;
        this.listItem = listItem;
        this.filterlist = filterlist;
        this.filter = filter;
    }*/

    public Adaptador(ActivityListaMed context, ArrayList<Medicamentos> listItem) {
        this.context = context;
        this.listItem = listItem;
        this.filterlist = listItem;

    }

    @Override
    public int getCount() { return listItem.size(); }

    @Override
    public Object getItem(int i) { return listItem.get(i); }

    @Override
    public long getItemId(int i) { return 0; }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        Medicamentos item = (Medicamentos) getItem(i);

        view = LayoutInflater.from(context).inflate(R.layout.itemmed, null);


        TextView titulo =(TextView) view.findViewById(R.id.itemTitulo);
        TextView Tiempo =(TextView) view.findViewById(R.id.itemNota);
        TextView id =(TextView) view.findViewById(R.id.itemObjetId);

        titulo.setText(item.toString());
        Tiempo.setText(item.getTiempo());
        id.setText(item.getId()+"");



        return view;
    }

    //Filter
    /********************************/

    class CustomFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            FilterResults filterResults = new FilterResults();

            if(charSequence != null && charSequence.length()>0){

                charSequence = charSequence.toString().toUpperCase();

                ArrayList<Medicamentos> filters = new ArrayList<Medicamentos>();

                for(int i = 0;i < filterlist.size(); i++){

                    if(filterlist.get(i).getDescripcion().toUpperCase().contains(charSequence)){

                        filters.add(filterlist.get(i));
                    }
                }

                filterResults.count = filters.size();
                filterResults.values = filters;

            }else {

                filterResults.count = filterlist.size();
                filterResults.values = filterlist;
            }

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

            listItem = (ArrayList<Medicamentos>) filterResults.values;
            notifyDataSetChanged();
        }
    }

    public Filter getFilter(){

        if(filter == null){
            filter = new CustomFilter();
        }

        return filter;
    }

    public ArrayList<Medicamentos> getFilterlist(){
        return filterlist;
    }

}
