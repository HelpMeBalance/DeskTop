package org.example.models;

import java.util.Objects;

public class Formulairerep {
        private int id;
    private int idrep;

    public Formulairerep(int id, int idrep) {
        this.id = id;
        this.idrep = idrep;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdrep() {
        return idrep;
    }

    public void setIdrep(int idrep) {
        this.idrep = idrep;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Formulairerep)) return false;
        Formulairerep that = (Formulairerep) o;
        return getId() == that.getId() && getIdrep() == that.getIdrep();
    }

}
