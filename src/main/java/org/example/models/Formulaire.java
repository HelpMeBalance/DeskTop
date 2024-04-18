package org.example.models;

import java.util.Objects;

public class Formulaire {

    private  int userid;
    private int idrend;
    private  int id;


    public Formulaire(int idint,int  userid, int idrend) {
        this.userid = userid;
        this.idrend = idrend;
        this.id = id;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public int getIdrend() {
        return idrend;
    }

    public void setIdrend(int idrend) {
        this.idrend = idrend;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Formulaire() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Formulaire)) return false;
        Formulaire that = (Formulaire) o;
        return getUserid() == that.getUserid() && getIdrend() == that.getIdrend() && getId() == that.getId() ;
    }
}
