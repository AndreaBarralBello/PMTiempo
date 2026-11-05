package com.example.tiempo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ExpandableAdaptardor extends BaseExpandableListAdapter {

    Context contexto;

    List<String> listaProvincias = new ArrayList<>();

    HashMap <String, List <String>> listaProvinciasMunicipios = new HashMap<>();

    //GRUPO = PROVINCIAS
    //CHILD = MUNICIPIOS
    //Constructor
    public ExpandableAdaptardor(Context contexto, List<String> listaProvincias, HashMap<String, List<String>> listaProvinciasMunicipios) {
        this.contexto = contexto;
        this.listaProvincias = listaProvincias;
        this.listaProvinciasMunicipios = listaProvinciasMunicipios;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return listaProvinciasMunicipios.get(listaProvincias.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(contexto);
            convertView = inflater.inflate(R.layout.list_item, parent, false);
        }
        TextView tv = convertView.findViewById(R.id.tvMunicipio);
        String provincia = listaProvincias.get(groupPosition);
        tv.setText(listaProvinciasMunicipios.get(provincia).get(childPosition));
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {

        //cuántos municipios tiene cada provincia
        //de la lista de provincias, seleccionamos esa posicion
        //y buscamos su size en el hashmap
        return listaProvinciasMunicipios.get(listaProvincias.get(groupPosition)).size();
    }
    @Override
    public Object getGroup(int groupPosition) {
        return listaProvincias.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        //cantidad del grupo: cuántas provincias
        return listaProvincias.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        //id de cada provincia
        return groupPosition;
    }
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(contexto);
            convertView = inflater.inflate(R.layout.list_group, parent, false);
        }
        TextView tv = convertView.findViewById(R.id.tvProvincia);
        tv.setText(listaProvincias.get(groupPosition));
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
