package Tp.model;

import java.sql.Connection;

import Tp.dao.ObjetBDD;

public class ClientTop3 extends ObjetBDD {
    private String NomClient;
    private int nombre;

    public ClientTop3() {
        this.setNomTable("v_ClientTop3");
    }

    public String getNomClient() {
        return NomClient;
    }

    public void setNomClient(String nomClient) {
        NomClient = nomClient;
    }

    public int getNombre() {
        return nombre;
    }

    public void setNombre(int nombre) {
        this.nombre = nombre;
    }

    public ClientTop3[] getClientTop3(Connection con) throws Exception{
        ObjetBDD[] lc=new ClientTop3().Find(con);
        ClientTop3[] lt=new ClientTop3[lc.length];
        System.arraycopy(lc, 0, lt, 0, lc.length);
        return lt;
    }

}
